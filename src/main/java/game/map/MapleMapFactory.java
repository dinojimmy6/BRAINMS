package game.map;

import utils.parsing.MapleData;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import game.map.MapFootholds;

public class MapleMapFactory {
    public static MapleMap getMap(int mapId) {
        MapleData info = null;
        try {
            info = new MapleData(new FileInputStream("wz/map/" + mapId + ".img.xml"));
        }
        catch (FileNotFoundException e) {
            return null;
        }
        MapleMap map = new MapleMap(mapId, new MapFootholds(info));
        buildSpawnPoints(map, info);
        map.initMap();
        return map;
    }

    private static void buildSpawnPoints(MapleMap map, MapleData root) {
        for (MapleData lifeRoot : root.getChildByPath("life")) {
            String type = MapleData.getString(lifeRoot.getChildByPath("type"));
            if(!type.equals("m")) {
                continue;
            }
            map.addSpawnPoint(new SpawnPoint(lifeRoot));
            MapleData.getInt(lifeRoot.getChildByPath("mobTime"), 0);
        }
    }
}
