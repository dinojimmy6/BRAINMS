package server;

import constants.ServerConfig;
import encryption.ClientSocket;
import game.GameClient;
import game.LoginClient;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import packet.LoginPacket;
import packet.RecvPacketOpcode;
import utils.HexTool;
import utils.data.ByteArrayByteStream;
import utils.data.LittleEndianAccessor;
import utils.Logging;
import utils.Randomizer;

public class ChannelServerHandler extends IoHandlerAdapter {
    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        final String address = session.getRemoteAddress().toString().split(":")[0];
        final short port = Short.parseShort(session.getServiceAddress().toString().split(":")[1]);
        final byte ivRecv[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        final byte ivSend[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        ClientSocket<GameClient> client = new ClientSocket<>(ivRecv, ivSend, new GameClient(session));
        session.write(LoginPacket.getHello(ServerConfig.MAPLE_VERSION, ivSend, ivRecv));
        session.setAttribute(ClientSocket.KEY, client);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 150);
        session.getConfig().setIdleTime(IdleStatus.WRITER_IDLE, 150);
        Logging.log("Login Connection Established " + address + ":" + port);
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        ClientSocket<GameClient> socket = (ClientSocket) session.getAttribute(ClientSocket.KEY);
        if (socket != null) {
            socket.client.disconnect();
            session.removeAttribute(ClientSocket.KEY);
        }
        final String address = session.getRemoteAddress().toString().split(":")[0];
        final short port = Short.parseShort(session.getServiceAddress().toString().split(":")[1]);
        Logging.log("Connection Closed " + address + ":"+port);
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        final String address = session.getRemoteAddress().toString().split(":")[0];
        final short port = Short.parseShort(session.getServiceAddress().toString().split(":")[1]);
        Logging.log("Connection went Idle " + address + ":"+port);
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {
        final LittleEndianAccessor lea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
        if (lea.available() < 2) {
            return;
        }
        ClientSocket<GameClient> cs = (ClientSocket) session.getAttribute(ClientSocket.KEY);
        int opcode = lea.readShort();
        //Logging.log("[Recv] (" + HexTool.getOpcodeToString(opcode) + ") " + lea.toString());
        try {
            cs.client.handlePacket(RecvPacketOpcode.getName(opcode), lea);
        }
        catch (Exception e) {
            Logging.exceptionLog(e);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        final LittleEndianAccessor lea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) message));

        short code = lea.readShort();

        String bytes = lea.toString(false);
        byte[] hex = HexTool.getByteArrayFromHexString(bytes);
        String hexString = new String(hex, "ASCII");

        //Logging.log("[Sent] " + code + ": " + bytes);
    }
}