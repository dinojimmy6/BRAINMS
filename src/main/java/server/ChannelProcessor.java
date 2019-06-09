package server;

import game.character.MapleCharacter;
import game.map.MapleMap;
import game.map.MapleMapFactory;
import game.random.CRand32;
import packet.CStage;
import packet.CUserPool;
import utils.command.CommandProcessor;
import utils.data.LittleEndianAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChannelProcessor implements Runnable {
    int channel;
    public BlockingQueue<Object> queue;
    public Map<Integer, MapleMap> maps;

    public ChannelProcessor(int channel) {
        this.channel = channel;
        queue = new LinkedBlockingQueue<>();
        maps = new HashMap<>();
    }

    @Override
    public void run() {
        while(true) {
            Object o = null;
            try {
                o = queue.take();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(o instanceof Runnable) {
                ((Runnable) o).run();
            }
            else if(o instanceof LittleEndianAccessor) {

            }
            else if(o instanceof MapleCharacter) {
                placeChar((MapleCharacter)o);
            }
        }
    }

    public MapleMap findMap(MapleCharacter chr) {
        return maps.get(chr.mapId);
    }

    public void placeChar(MapleCharacter chr) {
        MapleMap map = maps.get(chr.mapId);
        if(map == null) {
            map = MapleMapFactory.getMap(chr.mapId);
            maps.put(chr.mapId, map);
        }
        map.addChar(chr);
        chr.writePacket(CStage.getWarpToMap(chr, channel, chr.mapId, 0, true));
    }

    public void handleGeneralChat(MapleCharacter chr, String text) {
        if (text.length() < 1 || (text.length() >= 80)) {
            //            //            //autoban
            return;
        }
        MapleMap chrMap = findMap(chr);
        if(chrMap == null) {
            return;
        }
        if (CommandProcessor.processCommand(chr, chrMap, text)){
            return;
        }
        //handle muted
        chrMap.broadcastMessage(chr, CUserPool.writeChat(chr.id, text));
    }

    public void handleAttack(MapleCharacter chr) {
        int numRand = 11;
        long rand[] = new long[numRand];
        int index = 0;
        for (int i = 0; i < numRand; i++) {
            rand[i] = chr.rand.random();
        }
        double damage = CRand32.getRand(rand[3], 12, 2) * .9 * 1.1;
        if(CRand32.getRand(rand[4], 100, 0) < 5) {
            int critDamage = (int) CRand32.getRand(rand[5], 50, 20);
            damage += critDamage / 100.0 * (int) damage;
            System.out.println("CRITICAL");
        }
        System.out.println("prediction: " + damage);

    }
}
