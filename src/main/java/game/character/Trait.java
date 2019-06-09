package game.character;

import game.character.MapleCharacter;


public class Trait {

    public static enum TraitType {
        AMBITION(0x100000),
        INSIGHT(0x200000),
        WILLPOWER(0x400000),
        DILIGENCE(0x800000),
        EMPATHY(0x1000000),
        CHARM(0x2000000);

        final int flag;

        private TraitType(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }

        public static TraitType getByQuestName(String q) {
            String qq = q.substring(0, q.length() - 3); //e.g. charmEXP, charmMin
            for (TraitType t : TraitType.values()) {
                if (t.name().equals(qq)) {
                    return t;
                }
            }
            return null;
        }
    }

    private final TraitType type;
    private int totalExp = 0, localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;

    public Trait(TraitType t) {
        this.type = t;
    }

    public void setExp(int e) {
        this.totalExp = e;
        this.localTotalExp = e;
        //recalcLevel();
    }
//
//    public void addExp(int e) {
//        this.totalExp += e;
//        this.localTotalExp += e;
//        if (e != 0) {
//            recalcLevel();
//        }
//    }

//    public void addExp(int e, MapleCharacter c) {
//        addTrueExp(e * c.getClient().getChannelServer().getTraitRate(), c);
//    }
//
//    public void addTrueExp(int e, MapleCharacter c) {
//        if (e != 0) {
//            this.totalExp += e;
//            this.localTotalExp += e;
//            //c.updateSingleStat(type.stat, totalExp);
//            //c.getClient().getSession().write(InfoPacket.showTraitGain(type, e));
//            recalcLevel();
//        }
//    }
//
//    public boolean recalcLevel() {
//        if (totalExp < 0) {
//            totalExp = 0;
//            localTotalExp = 0;
//            level = 0;
//            exp = 0;
//            return false;
//        }
//        final int oldLevel = level;
//        for (byte i = 0; i < 100; i++) {
//            if (GameConstants.getTraitExpNeededForLevel(i) > localTotalExp) {
//                exp = (short) (GameConstants.getTraitExpNeededForLevel(i) - localTotalExp);
//                level = (byte) (i - 1);
//                return level > oldLevel;
//            }
//        }
//        exp = 0;
//        level = 100;
//        totalExp = GameConstants.getTraitExpNeededForLevel(level);
//        localTotalExp = totalExp;
//        return level > oldLevel;
//    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int getLocalTotalExp() {
        return localTotalExp;
    }

    public void addLocalExp(int e) {
        this.localTotalExp += e;
    }

    public void clearLocalExp() {
        this.localTotalExp = totalExp;
    }

    public TraitType getType() {
        return type;
    }
}
