package packet;

import constants.Jobs;
import constants.ServerConfig;
import game.character.MapleCharacter;
import utils.data.PacketWriter;

import java.util.List;

public class LoginPacket {
    public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(15);
        pw.writeShort(mapleVersion);
        pw.writeMapleAsciiString(ServerConfig.MAPLE_PATCH);
        pw.write(recvIv);
        pw.write(sendIv);
        pw.write(8);
        pw.write(0);
        return pw.getPacket();
    }

    public static final byte[] hotfixResponse() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.HOTFIX_RESPONSE.getValue());
        pw.write(1);
        return pw.getPacket();
    }

    public static byte[] authServer() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.AUTH_SERVER.getValue());
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] sendAuthResponse(int response) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.AUTH_RESPONSE.getValue());
        pw.writeInt(response);
        return pw.getPacket();
    }

    public static byte[] loginSuccess(String name, int accId) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        pw.write(0);
        pw.write(0);
        pw.writeInt(0);
        pw.writeMapleAsciiString(name); // 174.1
        pw.writeInt(accId);
        pw.write(0);
        pw.write(0);
        pw.writeInt(2);
        pw.writeInt(0);
        pw.write(1);
        pw.writeMapleAsciiString(name);
        pw.write(3);
        pw.write(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeInt(0x1A); // The amount of characters available in total - 3?
        getAvailableJobs(pw);
        pw.write(0); // 174.1
        pw.writeInt(-1); // 174.1
        pw.write(1);
        pw.write(1);
        pw.writeLong(0);
        return pw.getPacket();
    }
//
//    public static final byte[] sendAllowedCreation() {
//        PacketWriter pw = new PacketWriter();
//        pw.writeShort(0x2D);
//        getAvailableJobs(pw);
//        return pw.getPacket();
//    }
//
    private static void getAvailableJobs(PacketWriter pw) {
        pw.write(Jobs.enableJobs ? 1 : 0); //toggle
        pw.write(Jobs.jobOrder); // Job Order (orders are located in lib)
        for (Jobs.LoginJob j : Jobs.LoginJob.values()) {
            pw.write(j.getFlag());
            pw.writeShort(j.getFlag());
        }
    }
//
//    /**
//     * Send a packet to confirm authentication was successful.
//     * @param c
//     * @return
//     */
//    public static final byte[] getSecondAuthSuccess(MapleClient c) {
//        PacketWriter pw = new PacketWriter();
//        pw.writeShort(SendPacketOpcode.LOGIN_SECOND.getValue());
//        pw.write(0); // request
//        pw.writeInt(c.getAccID());
//        pw.write(0);
//        pw.write(0);
//        pw.writeInt(0);
//        pw.writeInt(0);
//        pw.write(0);
//        pw.writeMapleAsciiString(c.getAccountName());
//        pw.write(0);
//        pw.write(0);
//        pw.writeLong(0);
//        pw.writeMapleAsciiString(c.getAccountName());
//        pw.writeLong(Randomizer.nextLong());
//        pw.writeInt(28);
//        pw.writeLong(Randomizer.nextLong());
//        /*for(byte b = 0; b < 3; b++) {
//            if(b == 1)
//                pw.writeInt(28);
//            pw.writeLong(Randomizer.nextLong());
//        }*/
//        pw.writeMapleAsciiString("");
//        getAvailableJobs(pw);
//        pw.write(0);
//        pw.writeInt(-1);
//        return pw.getPacket();
//    }
//
    public static final byte[] loginFail(int reason) {
        PacketWriter pw = new PacketWriter(16);
        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        pw.write(reason);
        pw.write(0);
        pw.writeInt(0);
        return pw.getPacket();
    }
//    /*
//     * location: UI.wz/Login.img/Notice/text
//     * reasons:
//     * useful:
//     * 32 - server under maintenance check site for updates
//     * 35 - your computer is running thirdy part programs close them and play again
//     * 36 - due to high population char creation has been disabled
//     * 43 - revision needed your ip is temporary blocked
//     * 75-78 are cool for auto register
//
//     */
//
//    public static byte[] getTempBan(long timestampTill, byte reason) {
//        PacketWriter pw = new PacketWriter(17);
//
//        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
//        pw.write(2);
//        pw.write(0);
//        pw.writeInt(0);
//        pw.write(reason);
//        pw.writeLong(timestampTill);
//
//        return pw.getPacket();
//    }

    public static final byte[] deleteChar(int charId, int state) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.DELETE_CHAR.getValue());
        pw.writeInt(charId);
        pw.write(state);

        return pw.getPacket();
    }

    public static byte[] pinError() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PIN_RESPONSE.getValue());
        pw.write(0x14);
        return pw.getPacket();
    }
//
//    public static byte[] enableRecommended(int world) {
//        PacketWriter pw = new PacketWriter();
//        pw.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
//        pw.writeInt(world);
//        return pw.getPacket();
//    }
//
//    public static byte[] sendRecommended(int world, String message) {
//        PacketWriter pw = new PacketWriter();
//        pw.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
//        pw.write(message != null ? 1 : 0);
//        if (message != null) {
//            pw.writeInt(world);
//            pw.writeMapleAsciiString(message);
//            System.out.println("my msg: " + message);
//        }
//        return pw.getPacket();
//    }
//
    public static byte[] worldInfo() {
        PacketWriter pw = new PacketWriter();
        int serverId = 1;
        pw.writeShort(SendPacketOpcode.WORLD_INFO.getValue());
        pw.write(serverId);
        String worldName = "MapleSto";
        pw.writeMapleAsciiString(worldName);
        pw.write(5);
        pw.writeMapleAsciiString("Hello bumbo");
        pw.writeShort(100);
        pw.writeShort(100);
        pw.write(0);
        int lastChannel = 4;
        pw.write(lastChannel);

        for (int i = 1; i <= lastChannel; i++) {
            pw.writeMapleAsciiString(worldName + "-" + i);
            pw.writeInt(2); // load -60 = 100%
            pw.write(serverId);
            pw.writeShort(i - 1);
        }
        pw.writeShort(0);
        pw.writeInt(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] worldInfoEnd() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WORLD_INFO.getValue());
        pw.write(0xFF);
        pw.write(0); // boolean disable cash shop and trade msg
        pw.write(0); // 174.1
        pw.write(0); // 174.1

        return pw.getPacket();
    }

    public static byte[] channelSelect(int status) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CHANNEL_SELECT.getValue());
        pw.write(status);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] charList(List<MapleCharacter> characters, int picStatus) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHAR_LIST.getValue());
        pw.write(0); // nDay
        pw.writeMapleAsciiString("normal"); // 174.1
        pw.writeInt(0); // 174.1 ref count?

        pw.write(1); // burning event block?

        // character locations
        pw.writeInt(0); // ?

        // timestamp?
        pw.writeLong(0); // 174.1

        // has the list of characters been edited
        pw.write(0);

        pw.writeInt(0); // ? (nSecond)

        pw.write(characters.size());
        for (MapleCharacter chr : characters) {
            chr.writeGWCharacterStats(pw);
            chr.writeAvatarLook(pw);
            pw.write(0);

//            boolean ranking = !GameConstants.isGM(character.getJob()) && character.getLevel() >= 30;
//            pw.write(ranking);
//            if (ranking) {
//                pw.writeInt(character.getRank());
//                pw.writeInt(character.getRankMove());
//                pw.writeInt(character.getJobRank());
//                pw.writeInt(character.getJobRankMove());
//            }
            pw.write(0);
        }
        pw.write(picStatus); //pic
        pw.write(0);
        pw.writeInt(4); //chr slots

        pw.writeInt(0); // buy character count?
        pw.writeInt(-1); // event new char job

        /*
        pw.writeInt(6111);

        // Something to do with time.
        pw.writeInt(0); // hidword
        pw.writeInt(0); // lodword

        pw.write(0); // rename count?

        pw.write(0); // ?
        */

        pw.writeReversedLong(0); //time
        pw.write(0); // the amount of allowed name changes
        pw.write(new byte[5]);
        return pw.getPacket();
    }
//
//    public static byte[] getAccountName(String name) {
//        PacketWriter pw = new PacketWriter();
//        pw.writeShort(0x10F);
//        pw.writeLong(0);
//        pw.writeMapleAsciiString(name);
//        return pw.getPacket();
//    }
//
    public static byte[] writeNewCharEntry(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.NEW_CHARACTER.getValue());
        pw.write(0);
        chr.writeGWCharacterStats(pw);
        chr.writeAvatarLook(pw);
        return pw.getPacket();
    }

    public static byte[] checkNameResponse(String name, boolean nameUsed) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHECK_NAME_RESPONSE.getValue());
        pw.writeMapleAsciiString(name);
        pw.write(nameUsed ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] finishLogin(int channel, int charId) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.FINISH_LOGIN.getValue());
        pw.write(0); // request
        pw.write(0);
        byte[] svr = new byte[] {8, 31, 99, ((byte) 141)};
        byte[] chat = new byte[] {8, 31, 99, ((byte) 133)};
        // maple server ip
        pw.write(svr);
        pw.writeShort(channel + 8584);
        // chat server ip
        pw.write(chat);
        pw.writeShort(8785);
        pw.writeInt(charId);
        pw.write(0);
        // argument ?
        pw.writeInt(0);
        pw.write(0);
        // shutdown ? (timestamp)
        pw.writeLong(0);
        return pw.getPacket();
    }
//
//    public static byte[] enableSpecialCreation(int accid, boolean enable) {
//        PacketWriter pw = new PacketWriter();
//
//        pw.writeShort(SendPacketOpcode.SPECIAL_CREATION.getValue());
//        pw.writeInt(accid);
//        pw.write(enable ? 0 : 1);
//        pw.write(0);
//
//        return pw.getPacket();
//    }
}
