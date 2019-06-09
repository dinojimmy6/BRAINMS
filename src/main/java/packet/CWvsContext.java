package packet;

import utils.data.PacketWriter;

public class CWvsContext {
    public static byte[] sendMessage(int type, String message) {
        return sendMessage(type, 0, message, false);
    }

    private static byte[] sendMessage(int type, int channel, String message, boolean megaEar) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SERVER_MESSAGE.getValue());
        pw.write(type);
        if (type == 4) {
            pw.write(1);
        }
        if ((type != 23) && (type != 24)) {
            pw.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                pw.write(channel - 1);
                pw.write(megaEar ? 1 : 0);
                break;
            case 9:
                pw.write(channel - 1);
                break;
            case 12:
                pw.writeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                pw.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            case 24:
                pw.writeShort(0);
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
        }
        return pw.getPacket();
    }
}
