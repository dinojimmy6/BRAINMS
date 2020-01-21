package packet;

import game.character.MapleCharacter;
import game.movement.LifeMovementFragment;
import utils.data.PacketWriter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static byte[] spawnPlayer(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        chr.writeSpawnPlayer(pw);
        return pw.getPacket();
    }

    public static byte[] removePlayer(int cid) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        pw.writeInt(cid);

        return pw.getPacket();
    }

    public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        pw.writeInt(cid);
        pw.writeInt(0);
        pw.writePos(startPos);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(pw);
        }
        return pw.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.UPDATE_PARTY_MEMBER_HP.getValue());
        pw.writeInt(cid);
        pw.writeInt(curhp);
        pw.writeInt(maxhp);
        return pw.getPacket();
    }
}
