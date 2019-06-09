package game.map;

import game.character.MapleCharacter;
import game.monster.Monster;
import packet.MobPacket;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MapleMap {
    private int mapId;
    private Map<Integer, MapleCharacter> chars;
    private Map<Integer, Monster> monsters;
    private int nextOid = 500000;
    public MapFootholds fhs;

    public MapleMap(int mapId, MapFootholds fhs) {
        this.mapId = mapId;
        chars = new HashMap<>();
        monsters = new HashMap<>();
        this.fhs = fhs;
    }

    public void addChar(MapleCharacter chr) {
        chars.put(chr.id, chr);
    }

    public void spawnMonsterOnPoint(Monster monster, Point p) {
        monster.setOid(nextOid++);
        monster.setPosition(fhs.calcPointBelow(p));
        monsters.put(monster.oid, monster);
        broadcastMessage(MobPacket.spawnMonster(monster));
    }

    public void broadcastMessage(MapleCharacter src, byte[] packet) {
        for(MapleCharacter chr : chars.values()) {
            if(chr.inRange(src.getPosition())) {
                chr.writePacket(packet);
            }
        }
    }

    public void broadcastMessage(byte[] packet) {
        for(MapleCharacter chr : chars.values()) {
            chr.writePacket(packet);
        }
    }
}
