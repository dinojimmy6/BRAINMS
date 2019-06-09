package encryption;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

class PacketEncoder implements ProtocolEncoder {
    @Override
    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        final ClientSocket client = (ClientSocket) session.getAttribute(ClientSocket.KEY);
        if (client != null) {
            final byte[] initial = ((byte[]) message);
            final byte[] ret = new byte[initial.length + 4];
            final byte[] header = client.sendCrypto.getPacketHeader(initial.length);
            client.sendCrypto.crypt(initial, ret, 4);
            System.arraycopy(header, 0, ret, 0, 4);
            out.write(IoBuffer.wrap(ret));
        }
        else {
            out.write(IoBuffer.wrap((byte[]) message));
        }
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // nothing to do
    }
}
