package game.map;

import game.character.MapleCharacter;
import game.monster.Monster;
import packet.MobPacket;
import server.ChannelServer;
import utils.Randomizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapleMap {
    private int mapId;
    public Map<Integer, MapleCharacter> chars;
    private Map<Integer, Monster> monsters;
    private int nextOid = 500000;
    public int channel;
    public MapFootholds fhs;
    public ArrayList<SpawnPoint> spawnPoints;

    public MapleMap(int mapId, MapFootholds fhs) {
        this.mapId = mapId;
        chars = new HashMap<>();
        monsters = new HashMap<>();
        this.fhs = fhs;
        this.spawnPoints = new ArrayList<SpawnPoint>();
    }

    public void addSpawnPoint(SpawnPoint pt) {
        spawnPoints.add(pt);
    }

    public void initMap() {
        for(SpawnPoint spawnPoint : spawnPoints) {
            spawnPoint.spawnMonster(this);
        }
    }

    public void addChar(MapleCharacter chr) {
        chars.put(chr.getId(), chr);
    }

    public void onCharEnterMap(MapleCharacter chr) {
        for(Monster monster : monsters.values()) {
            monster.sendSpawnData(chr);
            if(monster.getController() == null) {
                chr.controlMonster(monster, false);
            }
        }
        for(MapleCharacter notify : chars.values()) {
            chr.sendSpawnData(notify);
            notify.sendSpawnData(chr);
        }
    }

    public void onCharLeaveMap(MapleCharacter chr) {
        chars.remove(chr.getId());
        chr.leaveMap();
        for(MapleCharacter notify : chars.values()) {
            chr.sendDestroyData(notify);
        }
    }

    public void updateMonsterController(Monster monster) {
        int mincontrolled = -1;
        MapleCharacter newController = null;
        for(MapleCharacter chr : chars.values()) {
            if(chr.getControlledSize() < mincontrolled || mincontrolled == -1) {
                mincontrolled = chr.getControlledSize();
                newController = chr;
            }
        }
        if (newController != null) {
            newController.controlMonster(monster, false);
        }
        else {
            monster.setController(null);
        }
    }


    public void spawnMonsterOnPoint(Monster monster, Point p) {
        monster.setOid(nextOid++);
        monster.setPosition(fhs.calcPointBelow(p));
        monster.map = this;
        monsters.put(monster.oid, monster);
        broadcastMessage(MobPacket.spawnMonster(monster));
    }

    public void moveMonster(Monster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        for (MapleCharacter mc : chars.values()) {
            //updateMapObjectVisibility(mc, monster);
        }
    }

    public void setRespawn() {
        spawnPoints.get(Randomizer.nextInt(spawnPoints.size())).spawnMonster(this);
    }

    public void broadcastMessage(MapleCharacter src, byte[] packet) {
        for(MapleCharacter chr : chars.values()) {
            if(chr.inRange(src.getPosition()) ) {
                chr.writePacket(packet);
            }
        }
    }

    public void broadcastMessage(byte[] packet) {
        for(MapleCharacter chr : chars.values()) {
            chr.writePacket(packet);
        }
    }

    public MapleCharacter getChrFromMap(int chrId) {
        return chars.get(chrId);
    }

    public void removeMonster(int oid) {
        monsters.remove(oid);
    }

    public Monster getMonster(int oid) {
        return monsters.get(oid);
    }
}
