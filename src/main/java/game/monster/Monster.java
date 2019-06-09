package game.monster;

import game.GameClient;
import game.character.MapleCharacter;
import game.map.MapObject;
import packet.MobPacket;
import utils.data.PacketWriter;

public class Monster extends MapObject {
    private short fh;
    private int mid;
    private byte stance;
    private long maxhp, hp;
    int summoner;

    public Monster(int mid) {
        maxhp = 1000;
        hp = 1000;
        this.mid = mid;
    }

    @Override
    public void sendSpawnData(MapleCharacter chr) {
        //chr.writePacket(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0, true));
    }

    @Override
    public void sendDestroyData(MapleCharacter chr) {

    }

    public void writeSpawnMonster(PacketWriter pw) {
        pw.write(0);
        pw.writeInt(oid);
        pw.write(1);
        pw.writeInt(mid);
        addForcedMobStat(pw);
        addMonsterInformation(pw);
    }

    public void addForcedMobStat(PacketWriter pw) {
        pw.write(0);
        pw.write(new byte[12]);
    }

    public void addMonsterInformation(PacketWriter pw) {

        pw.writeShort(position.x);
        pw.writeShort(position.y);
        pw.write(5);
        pw.writeShort(fh);
        pw.writeShort(fh);
        if(summoner != 0) {
            //handle summoned mob
        }
        else {
            pw.writeShort(-1); //some spawntype
        }
        pw.write(-1);
        if(hp > Integer.MAX_VALUE) {
            pw.writeInt(Integer.MAX_VALUE);
        } else {
            pw.writeInt((int) hp);
        }

        pw.writeInt(0);

        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);

        pw.writeInt(0);
        pw.write(0);

        pw.writeInt(-1);
        pw.writeInt(-1);
        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0x64); // monster scale
        pw.writeInt(-1);
        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeShort(0);
    }

}
