package constants;

public class Flags {
    public enum CharFlags {
        CHARSTATS(0x1),
        MESO(0x2),
        ITEMSLOTEQUIP(0x4),
        ITEMSLOTUSE(0x8),
        ITEMSLOTSETUP(0x10),
        ITEMSLOTETC(0x20),
        ITEMSLOTCASH(0x40),
        ITEMSLOTSIZE(0x80),
        SKILL(0x100),
        QUEST(0x200),
        MINIGAME(0x400),
        COUPLE(0x800),
        HYPERROCK(0x1000),
        COMPLETEDQUEST(0x4000),
        COOLDOWN(0x8000),
        MONSTERBOOK(0x10000),
        MONSTERBOOKCOVER(0x20000),
        QUESTEX(0x40000),
        CHARSKIN(0x100000),
        JAGUAR(0x200000),
        ITEMPOT(0x800000),
        COREAURA(0x1000000),
        EXPCONSUMEITEM(0x2000000),
        SHOPBUYLIMIT(0x4000000),
        CHOSENSKILLS(0x10000000),
        STOLENSKILLS(0x20000000),
        INNERABILITY(0x80000000),
        UNKNOWN(0x40000000),
        HONOR(0x100000000L),
        ANGELICBUSTER(0x800000000L),
        AVATAR(0x200000000000L),
        ZERO(0x80000000000L),
        UNK1(0x400000000000000L),
        FAMILIAR(0x80000000000000L);

        private final long flag;

        private CharFlags(long flag) {
            this.flag = flag;
        }

        public long getFlag() {
            return flag;
        }

        public static boolean checkMask(long mask, CharFlags check) {
            return (mask & check.getFlag()) != 0;
        }

        public static long defaultMask() {
            return CHARSTATS.flag | ITEMSLOTEQUIP.flag | MESO.flag | QUESTEX.flag;
        }
    }

    public enum Stat {
        SKIN(0x1), // byte
        FACE(0x2), // int
        HAIR(0x4), // int
        LEVEL(0x10), // byte
        JOB(0x20), // short
        STR(0x40), // short
        DEX(0x80), // short
        INT(0x100), // short
        LUK(0x200), // short
        HP(0x400), // int
        MAXHP(0x800), // int
        MP(0x1000), // int
        MAXMP(0x2000), // int
        AVAILABLEAP(0x4000), // short
        AVAILABLESP(0x8000), // short (depends)
        EXP(0x10000), // int
        FAME(0x20000), // int
        MESO(0x40000), // int
        PET(0x180008), // Pets: 0x8 + 0x80000 + 0x100000  [3 longs]
        FATIGUE(0x80000), // byte
        CHARISMA(0x100000), // ambition int
        INSIGHT(0x200000),
        WILL(0x400000), // int
        CRAFT(0x800000), // dilligence, int
        SENSE(0x1000000), // empathy, int
        CHARM(0x2000000), // int
        TRAIT_LIMIT(0x20000000), // 12 bytes
        VIRTUE(0x400000000L);
        private final long i;

        private Stat(long i) {
            this.i = i;
        }

        public long getValue() {
            return i;
        }

        public static Stat getByValue(final long value) {
            for (final Stat stat : Stat.values()) {
                if (stat.i == value) {
                    return stat;
                }
            }
            return null;
        }
    }
}
