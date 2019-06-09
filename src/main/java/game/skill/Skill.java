package game.skill;

import utils.parsing.MapleData;
import tools.Eval;

import java.util.ArrayList;

public class Skill {
    private int id, maxLevel, mobCount, attackCount, damage, mpCon;
    public Skill(int id, final MapleData data) {
        this.id = id;
        MapleData common = data.getChildByPath("common");
        mobCount = MapleData.getInt(common.getChildByPath("mobCount"), 1);
        attackCount = MapleData.getInt(common.getChildByPath("attackCount"), 1);
        maxLevel = MapleData.getInt(common.getChildByPath("maxLevel"), 1);
        for(int level = 0; level < maxLevel; level++) {
            damage = MapleData.evaluate(common.getChildByPath("damage"), "x", level);
            mpCon = MapleData.evaluate(common.getChildByPath("mpCon"), "x", level);
        }
    }
}
