package encryption;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class PacketDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        final ClientSocket client = (ClientSocket) session.getAttribute(ClientSocket.KEY);
        if(client == null) {
            return false;
        }
        if(client.pLength == -1) {
            if (in.remaining() >= 4) {
                final int packetHeader = in.getInt();
                if (!client.recvCrypto.checkPacket(packetHeader)) {
                    session.closeNow();
                    return false;
                }
                client.pLength = MapleAESOFB.getPacketLength(packetHeader);
            }
            else {
                return false;
            }
        }
        if (in.remaining() >= client.pLength) {
            final byte decryptedPacket[] = new byte[client.pLength];
            in.get(decryptedPacket, 0, client.pLength);
            client.pLength = -1;
            client.recvCrypto.crypt(decryptedPacket, decryptedPacket, 0);
            out.write(decryptedPacket);
            return true;
        }
        return false;
    }
}
