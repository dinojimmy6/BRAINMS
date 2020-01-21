package game.item;

import utils.parsing.MapleData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ItemFactory {
    private static Map<Integer, Item> cache = new HashMap<>();

    public static void loadAllEquips() {
        String[] paths = {"wz/Equip/Weapon", "wz/Equip/Longcoat", "wz/Equip/Shoes"};
        MapleData info = null;
        try {
            for(String path  : paths) {
                File dir = new File(path);
                File[] listings = dir.listFiles();
                for (File file : listings) {
                    info = new MapleData(new FileInputStream(file));
                    String name = file.getName();
                    name = name.substring(0, name.length() - 8);
                    int itemId = Integer.parseInt(name);
                    cache.put(itemId, new Equip(info));
                }
            }
        }
        catch (FileNotFoundException e) {
            return;
        }
    }

    public static Item getItem(int itemId) {
        Item cachedItem = cache.get(itemId);
        if(cachedItem != null) {
            return cachedItem.copy();
        }
        return null;
    }
}
