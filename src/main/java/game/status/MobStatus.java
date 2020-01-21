package game.status;

import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import server.ChannelServer;
import utils.Logging;
import utils.data.PacketWriter;

import java.util.concurrent.ScheduledFuture;

public abstract class MobStatus implements Runnable{
    protected Monster monster;
    protected MapleCharacter controller;
    protected MapleCharacter charFrom;
    protected MapleMap map;
    protected int oid;
    protected ScheduledFuture<?> schedule;
    protected int value;

    public MobStatus(Monster monster, MapleCharacter charFrom) {
        this.monster = monster;
        this.controller = monster.getController();
        this.charFrom = charFrom;
        this.map = monster.map;
        this.oid = monster.oid;
    }

    public MapleCharacter getCharFrom() {
        return charFrom;
    }

    @Override
    public void run() {
        try {
            ChannelServer.processors[monster.map.channel].queue.put((Runnable) () -> {
                if (monster.isAlive()) {
                    cancelStatus();
                }
            });
        } catch (InterruptedException e) {
            Logging.log(e.getMessage());
        }

    }

    public void cancelSchedule() {
        if(schedule != null && schedule.cancel(false)) {
            schedule = null;
        }
    }

    public abstract void applyStatus(MapleCharacter chr);

    public abstract void cancelStatus();

    public abstract void addStatus();

    public abstract void removeStatus();

    public void applyServerEffect() {};

    public int getValue() {
        return value;
    }

    public boolean isMobBuff() {
        return false;
    }

    protected void writeStatus(MapleCharacter chr, PacketWriter pw) {
        if(chr == null) {
            map.broadcastMessage(pw.getPacket());
        }
        else {
            chr.writePacket(pw.getPacket());
        }
    }
}
