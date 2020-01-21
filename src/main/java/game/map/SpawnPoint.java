package game.map;

import game.character.MapleCharacter;
import game.monster.Monster;
import game.monster.MonsterFactory;
import packet.CWvsContext;
import packet.MobPacket;
import server.ChannelServer;
import utils.Logging;
import utils.parsing.MapleData;

import java.awt.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnPoint{

    private final int mid;
    private final Point pos;
    private long nextPossibleSpawn;
    private int mobTime, fh, f;
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private final String msg;
    private ScheduledFuture<?> spawnSchedule;

    public SpawnPoint(MapleData info) {
        this.mid = MapleData.getInt(info.getChildByPath("id"), 0);;
        this.pos = new Point(MapleData.getInt(info.getChildByPath("x")), MapleData.getInt(info.getChildByPath("y")));
        this.fh = MapleData.getInt(info.getChildByPath("fh"), 0);
        this.f = MapleData.getInt(info.getChildByPath("f"), 0);
        int mobTime = MapleData.getInt(info.getChildByPath("mobTime"), 0);
        this.mobTime = (mobTime < 0 ? -1 : (mobTime * 1000));
        this.msg = null;
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    public final Point getPosition() {
        return pos;
    }

    public void spawnMonster(MapleMap map) {
        ChannelServer.timers[map.channel].schedule(() -> {
            try {
                ChannelServer.processors[map.channel].queue.put((Runnable) () -> {
                    Monster mob;
                    try {
                        mob = MonsterFactory.getMonster(mid);
                    } catch (RuntimeException e) {
                        map.broadcastMessage(CWvsContext.sendMessage(5, "Error: " + e.getMessage()));
                        return;
                    }
                    if (mob == null) {
                        map.broadcastMessage(CWvsContext.sendMessage(5, "Mob does not exist"));
                        return;
                    }
                    map.spawnMonsterOnPoint(mob, pos);
                    if (map.chars.size() > 0) {
                        //TODO: change to assign controller to nearest char
                        MapleCharacter chr = (MapleCharacter) map.chars.values().toArray()[0];
                        chr.writePacket(MobPacket.controlMonster(mob, false));
                        chr.controlMonster(mob, false);
                    }
                    mob.setPosition(pos);
                    mob.setLifePosition(f, fh);
                    //TODO: Handle kanna
                    if (msg != null) {
                        //TODO: broadcast spawn msg
                        //map.broadcastMessage(CWvsContext.broadcastMsg(6, msg));
                    }
                });
            }
            catch (InterruptedException e) {
                Logging.log(e.getMessage());
            }
        }, 5000);
    }

    public boolean blacklisted() {
        switch(mid) {
            case 9300736:
                return true;
            default:
                return false;
        }
    }
}
