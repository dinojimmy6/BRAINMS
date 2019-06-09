package packet;

import game.monster.Monster;
import utils.data.PacketWriter;

public class MobPacket {
    public static byte[] spawnMonster(Monster monster) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        monster.writeSpawnMonster(pw);
        return pw.getPacket();
    }
}
