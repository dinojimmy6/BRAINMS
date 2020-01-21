package game.skill;

import utils.parsing.MapleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skill {
    public int id, maxLevel;
    private Map<String, List<Integer>> info = new HashMap<>();

    public Skill(int id, final MapleData data) {
        this.id = id;
        MapleData common = data.getChildByPath("common");
        maxLevel = MapleData.getInt(common.getChildByPath("maxLevel"), 1);

        for(int level = 1; level <= maxLevel; level++) {
            addValue("time", MapleData.evaluate(common.getChildByPath("time"), "x", level));
            addValue("attackCount", MapleData.evaluate(common.getChildByPath("attackCount"), "x", level));
            addValue("mobCount", MapleData.evaluate(common.getChildByPath("mobCount"), "x", level));
            addValue("damage", MapleData.evaluate(common.getChildByPath("damage"), "x", level));
            addValue("mpCon", MapleData.evaluate(common.getChildByPath("mpCon"), "x", level));
            addValue("pad", MapleData.evaluate(common.getChildByPath("padX"), "x", level));
            addValue("pad", MapleData.evaluate(common.getChildByPath("indiePad"), "x", level));
            addValue("mad", MapleData.evaluate(common.getChildByPath("madX"), "x", level));
            addValue("pdd", MapleData.evaluate(common.getChildByPath("pddX"), "x", level));
            addValue("damAbsorbShieldR", MapleData.evaluate(common.getChildByPath("damAbsorbShieldR"), "x", level));
            addValue("mhpR", MapleData.evaluate(common.getChildByPath("mhpR"), "x", level));
            addValue("speed", MapleData.evaluate(common.getChildByPath("psdSpeed"), "x", level));
            addValue("jump", MapleData.evaluate(common.getChildByPath("psdJump"), "x", level));
            addValue("str", MapleData.evaluate(common.getChildByPath("strX"), "x", level));
            addValue("dex", MapleData.evaluate(common.getChildByPath("dexX"), "x", level));
            addValue("int", MapleData.evaluate(common.getChildByPath("intX"), "x", level));
            addValue("luk", MapleData.evaluate(common.getChildByPath("lukX"), "x", level));
            addValue("v", MapleData.evaluate(common.getChildByPath("v"), "x", level));
            addValue("x", MapleData.evaluate(common.getChildByPath("x"), "x", level));
            addValue("y", MapleData.evaluate(common.getChildByPath("y"), "x", level));
            addValue("z", MapleData.evaluate(common.getChildByPath("z"), "x", level));
            addValue("mastery", MapleData.evaluate(common.getChildByPath("mastery"), "x", level));
            addValue("asrR", MapleData.evaluate(common.getChildByPath("asrR"), "x", level));
            addValue("terR", MapleData.evaluate(common.getChildByPath("terR"), "x", level));
            addValue("minCrit", MapleData.evaluate(common.getChildByPath("criticalDamageMin"), "x", level));
            addValue("pdR", MapleData.evaluate(common.getChildByPath("pdR"), "x", level));
            addValue("ied", MapleData.evaluate(common.getChildByPath("ignoreMobpdpR"), "x", level));
            addValue("damR", MapleData.evaluate(common.getChildByPath("damR"), "x", level));
            addValue("damR", MapleData.evaluate(common.getChildByPath("indieDamR"), "x", level));
            addValue("cr", MapleData.evaluate(common.getChildByPath("cr"), "x", level));
            addValue("dot", MapleData.evaluate(common.getChildByPath("dot"), "x", level));
            addValue("dotTime", MapleData.evaluate(common.getChildByPath("dotTime"), "x", level));
        }
    }

    public int getTime(int level) {
        return info.get("time").get(level - 1);
    }

    public int getDamage(int level) {
        return info.get("damage").get(level - 1);
    }

    public int getMpCon(int level) {
        return info.get("mpCon").get(level - 1);
    }

    public int getPad(int level) {
        return info.get("pad").get(level - 1);
    }

    public int getMad(int level) {
        return info.get("mad").get(level - 1);
    }

    public int getPdd(int level) {
        return info.get("pdd").get(level - 1);
    }

    public int getDamAbsorbShieldR(int level) {
        return info.get("damAbsorbShieldR").get(level - 1);
    }

    public int getMhpR(int level) {
        return info.get("mhpR").get(level - 1);
    }

    public int getSpeed(int level) {
        return info.get("speed").get(level - 1);
    }

    public int getJump(int level) {
        return info.get("jump").get(level - 1);
    }

    public int getStr(int level) {
        return info.get("str").get(level - 1);
    }

    public int getDex(int level) {
        return info.get("dex").get(level - 1);
    }

    public int getInt(int level) {
        return info.get("int").get(level - 1);
    }

    public int getLuk(int level) {
        return info.get("luk").get(level - 1);
    }

    public int getV(int level) {
        return info.get("v").get(level - 1);
    }

    public int getX(int level) {
        return info.get("x").get(level - 1);
    }

    public int getY(int level) {
        return info.get("y").get(level - 1);
    }

    public int getZ(int level) {
        return info.get("z").get(level - 1);
    }

    public int getMastery(int level) {
        return info.get("mastery").get(level - 1);
    }

    public int getAsrR(int level) {
        return info.get("asrR").get(level - 1);
    }

    public int getTerR(int level) {
        return info.get("terR").get(level - 1);
    }

    public int getMinCrit(int level) {
        return info.get("minCrit").get(level - 1);
    }

    public int getPdR(int level) {
        return info.get("pdR").get(level - 1);
    }

    public int getIed(int level) {
        return info.get("ied").get(level - 1);
    }

    public int getDamR(int level) {
        return info.get("damR").get(level - 1);
    }

    public int getCr(int level) {
        return info.get("cr").get(level - 1);
    }

    public int getDot(int level) {
        return info.get("dot").get(level - 1);
    }

    public int getDotTime(int level) {
        return info.get("dotTime").get(level - 1);
    }

    public int getMobCount(int level) {
        return info.get("mobCount").get(level - 1);
    }

    public int getAttackCount(int level) {
        return info.get("attackCount").get(level - 1);
    }


    private void addValue(String name, int val) {
        if(val == -1) {
            return;
        }
        List<Integer> list = info.get(name);
        if(list != null) {
            list.add(val);
        }
        else {
            list = new ArrayList<>(maxLevel);
            list.add(val);
            info.put(name, list);
        }
    }
}
