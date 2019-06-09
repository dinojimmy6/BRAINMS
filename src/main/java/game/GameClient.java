package game;

import game.character.MapleCharacter;
import game.handlers.PacketHandler;
import org.apache.mina.core.session.IoSession;
import packet.LoginPacket;
import packet.RecvPacketOpcode;
import packet.SendPacketOpcode;
import server.ChannelServer;
import server.LoginServer;
import utils.data.LittleEndianAccessor;

import java.util.concurrent.SynchronousQueue;

public class GameClient {
    IoSession session;
    MapleCharacter chr;
    int channel;
    SynchronousQueue<MapleCharacter> delivered;
    public GameClient(IoSession session) {
        this.session = session;
        this.channel = 0;
        delivered = new SynchronousQueue<>();
    }

    public void handleCharLogin(LittleEndianAccessor lea) {
        lea.readInt(); // this could be the world or account
        final int charId = lea.readInt();
        if(LoginServer.characters.get(session.getRemoteAddress().toString().split(":")[0]) != charId) {
            session.closeNow();
            return;
        }
        if(ChannelServer.characters.get(charId) != null) {
            //do change channel
        }
        else {
            chr = new MapleCharacter(charId, session);
            try {
                ChannelServer.processors[channel].queue.put(chr);
            }
            catch(InterruptedException e) {}
        }
    }

    public void handlePacket(RecvPacketOpcode header, LittleEndianAccessor lea) throws InterruptedException {
        switch(header) {
            case CHAR_LOGIN:
                handleCharLogin(lea);
                break;
            case AUTH_REQUEST:
                session.write(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getValue() ^ lea.readInt()));
                break;
            case ON_ATTACK: {
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleAttack(chr);
                });
                break;
            }
            case GENERAL_CHAT: {
                lea.skip(4);
                String text = lea.readMapleAsciiString();
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleGeneralChat(chr, text);
                });
                break;
            }
        }
    }

    public void disconnect() {
        LoginServer.logins.remove(chr.accountId);
    }
}
