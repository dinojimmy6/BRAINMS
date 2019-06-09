package game.map;

import utils.parsing.MapleData;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import game.map.MapFootholds;

public class MapleMapFactory {
    public static MapleMap getMap(int mapId) {
        MapleData info = null;
        try {
            info = new MapleData(new FileInputStream(mapId + ".img.xml"));
        }
        catch (FileNotFoundException e) {
            return null;
        }
        MapleMap map = new MapleMap(mapId, new MapFootholds(info));
        return map;
    }
}
