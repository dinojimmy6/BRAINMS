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
            return CHARSTATS.flag | ITEMSLOTEQUIP.flag;
        }
    }
}
