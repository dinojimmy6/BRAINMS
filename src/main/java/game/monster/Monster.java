package game.monster;

import game.GameClient;
import game.character.MapleCharacter;
import game.map.AnimatedMapObject;
import game.map.MapObject;
import game.status.StackableDOT;
import packet.MobPacket;
import utils.data.PacketWriter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Monster extends AbstractLife {
    public final int mid;
    // stats
    public long maxhp, hp;
    public int level;

    int summoner;
    public boolean controllerHasAggro = false;
    public long lastControlTime;
    private WeakReference<MapleCharacter> controller = new WeakReference<>(null);
    private Map<Integer, Map<Integer, StackableDOT>> dots = new HashMap<>();

    public Monster(int mid) {
        maxhp = 60000000000L;
        hp = 60000000000L;
        this.mid = mid;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public void sendSpawnData(MapleCharacter chr) {
        chr.writePacket(MobPacket.spawnMonster(this));
    }

    @Override
    public void sendDestroyData(MapleCharacter chr) {

    }

    public void damage(MapleCharacter chr, long damage, boolean updateAttackTime, int lastSkill, boolean shouldShowHp) {
        long rDamage = Math.max(0, Math.min(damage, hp));
        hp -= rDamage;
        if(hp <= 0) {
            killMonster();
        }
    }

    public void killMonster() {
        map.broadcastMessage(MobPacket.killMonster(oid, 1));
        map.removeMonster(oid);
        map.setRespawn();
    }

    public void addDot(StackableDOT status, int skillId) {
        int charId = status.getCharFrom().oid;
        if(!dots.containsKey(charId)) {
            dots.put(charId, new HashMap<>());
        }
        dots.get(charId).put(skillId, status);
    }

    public void removeDot(MapleCharacter charFrom, int skillId) {
        int charId = charFrom.oid;
        if(!dots.containsKey(charId)) {
            return;
        }
        StackableDOT dot = dots.get(charId).remove(skillId);
        if(dot != null) {
            dot.cancelSchedule();
        }
    }

    public ArrayList<StackableDOT> getDots(MapleCharacter charFrom) {
        int charId = charFrom.oid;
        ArrayList<StackableDOT> allDots = new ArrayList();
        if(dots.containsKey(charId)) {
            for (StackableDOT s : dots.get(charId).values()) {
                allDots.add(s);
            }
        }
        return allDots;
    }

    public ArrayList<StackableDOT> getAllDots() {
        ArrayList<StackableDOT> allDots = new ArrayList();
        for(Map<Integer, StackableDOT> charDot : dots.values()) {
            for(StackableDOT dot : charDot.values()) {
                allDots.add(dot);
            }
        }
        return allDots;
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
        pw.write(1);
        pw.writeInt(maxhp > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) maxhp);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(10);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(5);
        pw.writeInt(0); //use default speed always
        pw.writeInt(1);
        pw.writeInt(0); // nUserCount
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

    public MapleCharacter getController() {
        return controller.get();
    }

    public void setController(MapleCharacter controller) {
        this.controller = new WeakReference<>(controller);
    }

    public void switchController(MapleCharacter newController, boolean immediateAggro) {
        MapleCharacter controller = getController();
        if (controller == newController) {
            return;
        } else if (controller != null) {
            controller.stopControllingMonster(this);
            controller.writePacket(MobPacket.stopControllingMonster(this));
            //monster.writeAllStatus(controller);
        }
        newController.controlMonster(this, immediateAggro);
        setController(newController);
        if (immediateAggro) {
            controllerHasAggro = true;
        }
        updateLastControl();
    }

    public void updateLastControl() {
        lastControlTime = System.currentTimeMillis();
    }

    public long getLastControlTime() {
        return lastControlTime;
    }
}
