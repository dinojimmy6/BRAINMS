package packet;

import game.monster.Monster;
import game.movement.LifeMovementFragment;
import utils.data.PacketWriter;

import java.awt.*;
import java.util.List;

public class MobPacket {
    public static byte[] spawnMonster(Monster monster) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        monster.writeSpawnMonster(pw);
        return pw.getPacket();
    }

    public static byte[] killMonster(int oid, int animation) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        pw.writeInt(oid);
        pw.write(animation);
        if (animation == 4) {
            pw.writeInt(-1);
        }
        return pw.getPacket();
    }

    public static byte[] controlMonster(Monster monster, boolean aggro) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        pw.write(aggro ? 2 : 1);

        pw.writeInt(monster.oid);

        pw.write(1); // 1 = Control normal, 5 = Control none?
        pw.writeInt(monster.mid);

        monster.addForcedMobStat(pw);
        monster.addMonsterInformation(pw);

        return pw.getPacket();
    }

    public static byte[] stopControllingMonster(Monster monster) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        pw.write(0);
        pw.writeInt(monster.oid);
        return pw.getPacket();
    }

    public static byte[] moveMonster(boolean useSkill, int skill, int skillId, int slv, int option, int oid, Point xy, List<LifeMovementFragment> moves) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        pw.writeInt(oid);

        pw.write(useSkill ? 1 : 0);
        pw.write(skill);
        pw.write(skillId);
        pw.write(slv);
        pw.writeShort(option);

        pw.write(0); // unk3 == null ? 0 : unk3.size());

        /*if (unk3 != null) {
            for (Pair i : unk3) {
                pw.writeShort(((Integer) i.left).intValue());
                pw.writeShort(((Integer) i.right).intValue());
            }
        }*/

        pw.write(0); // unk2 == null ? 0 : unk2.size());

        /*if (unk2 != null) {
            for (Integer i : unk2) {
                pw.writeShort(i.intValue());
            }
        }*/

        pw.writeInt(0); // EncodedGatherDuration
        pw.writePos(xy);
        pw.writeShort(0); // vx
        pw.writeShort(0); // vy

        serializeMovementList(pw, moves);
        pw.write(0); // ...
        return pw.getPacket();
    }

    public static byte[] moveMonsterResponse(int oid, short moveId, boolean useSkills, int skillId, int skillLevel) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        pw.writeInt(oid);
        pw.writeShort(moveId);
        pw.write(useSkills ? 1 : 0);
        pw.writeInt(0); // ...
        pw.writeInt(skillId); // ...
        pw.write(skillLevel);
        pw.writeInt(0);
        return pw.getPacket();
    }

    public static void serializeMovementList(PacketWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }
}
