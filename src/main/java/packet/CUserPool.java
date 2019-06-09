package packet;

import utils.data.PacketWriter;

public class CUserPool {
    public static byte[] writeChat(int cid, String text) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CHAT.getValue());
        pw.writeInt(cid);
        pw.write(0);
        pw.writeMapleAsciiString(text);
        pw.write(1);
        pw.write(0);
        pw.write(-1);
        return pw.getPacket();
    }
}
