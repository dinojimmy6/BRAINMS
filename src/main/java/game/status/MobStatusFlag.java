package game.status;

import utils.data.PacketWriter;

public enum MobStatusFlag {
    PAD(0x80000000, 0),
    PDR(0x40000000, 0),
    MAD(0x20000000, 0),
    MDR(0x10000000, 0),
    ACC(0x8000000, 0),
    EVA(0x4000000, 0),
    SPEED(0x2000000, 0),
    STUN(0x1000000, 0),
    FREEZE(0x800000, 0),
    POISON(0x400000, 0),
    SEAL(0x200000, 0),
    DARKNESS(0x100000, 0),
    POWER_UP(0x80000, 0),
    MAGIC_UP(0x40000, 0),
    PGUARD_UP(0x20000, 0),
    MGUARD_UP(0x10000, 0),
    WEAPON_IMMUNITY(0x8000, 0),
    MAGIC_IMMUNITY(0x4000, 0),
    SHADOW_WEB(0x2000, 0),
    HARD_SKIN(0x1000, 0),
    AMBUSH(0x800, 0),
    VENOM(0x400, 0),
    BLIND(0x200, 0),
    SEAL_SKILL(0x100, 0),
    DAZZLE(0x80, 0),
    PCOUNTER(0x40, 0),
    MCOUNTER(0x20, 0),
    TOSS(0x10, 0),
    BODY_PRESSURE(0x8, 0),
    WEAKNESS(0x4, 0),
    SHOWDOWN(0x2, 0),
    MAGIC_CRASH(0x1, 0),

    DAMAGED_ELEM_ATTR(0x80000000, 1),
    DARK(0x40000000, 1),
    MYSTERY(0x20000000, 1),
    ADD_DAM_PARTY(0x10000000, 1),
    HIT_CRI_DAM_R(0x8000000, 1),
    FATALITY(0x4000000, 1),
    LIFTING(0x2000000, 1),
    DEADLY_CHARGE(0x1000000, 1),
    SMITE(0x800000, 1),
    ADD_DAM_SKILL(0x400000, 1),
    PUNCTURE(0x200000, 1),
    DODGE_BODY_ATTACK(0x100000, 1),
    DEBUFF_HEALING(0x80000, 1),
    ADD_DAM_SKILL2(0x40000, 1),
    BODY_ATTACK(0x20000, 1),
    TEMP_MOVE_ABILITY(0x10000, 1),
    FIX_DAM_RBUFF(0x8000, 1),
    ELEMENT_DARKNESS(0x4000, 1),
    AREA_INSTALL_BY_HIT(0x2000, 1),
    BMAGE_DEBUFF(0x1000, 1),
    JAGUAR_PROVOKE(0x800, 1),
    JAGUAR_BLEEDING(0x400, 1),
    DARK_LIGHTNING(0x200, 1),
    PINK_BEAN_FLOWER_POT(0x100, 1),
    PVP_HELENA(0x80, 1),
    PSYCHIC_LOCK(0x40, 1),
    PSYCHIC_LOCK_COOL_TIME(0x20, 1),
    PSYCHIC_GROUND_MARK(0x10, 1),
    POWER_IMMUNE(0x8, 1),
    PSYCHIC_FORCE(0x4, 1),
    MULTI_PMDR(0x2, 1),
    ELEMENT_RESET_BY_SUMMON(0x1, 1),
    HYPNOTIZE(0x12345, 1),

    BAHA_ADD_DAM(0x80000000, 2),
    BOSS_PROP_PLUS(0x40000000, 2),
    MULTI_DAM_SKILL(0x20000000, 2),
    RW_LIFT_PRESS(0x10000000, 2),
    RW_CHOPPING_HAMMER(0x8000000, 2),
    MOBSTAT_MOD1(0x4000000, 2),
    MOBSTAT_MOD2(0x2000000, 2),
    TIME_BOMB(0x1000000, 2),
    TREASURE(0x800000 , 2),
    ADD_EFFECT(0x400000, 2),
    INVINCIBLE(0x200000, 2),
    EXPLOSION(0x100000, 2),
    HANGOVER(0x80000, 2),
    DOT(0x40000, 2),
    BALOGDISABLE(0x20000, 2),
    EXCHANGE_ATTACK(0x10000, 2),
    MOBSTAT_MOD3(0x8000, 2),
    MOBSTAT_STRING(0x4000, 2),
    SOUL_EXPLOSION(0x2000, 2),
    SEPARATE_SOUL_P(0x1000, 2),
    SEPARATE_SOUL_C(0x800, 2),
    EMBER(0x400, 2),
    TRUE_SIGHT(0x200, 2),
    LASER(0x100, 2),
    DOOM(0x10000, 1),
    DAMAGE_IMMUNITY(0x200000, 1),
    VENOMOUS_WEAPON(0x1000000, 1), //BURN
    WEAPON_DAMAGE_REFLECT(0x20000000, 1),
    MAGIC_DAMAGE_REFLECT(0x40000000, 1),
    NEUTRALISE(0x2, 2), // first int on v.87 or else it won't work.
    IMPRINT(0x4, 2),
    MONSTER_BOMB(0x8, 2),
    TRIANGULATION(0x8000, 2),
    STING_EXPLOSION(0x10000, 2),
    STACKABLE_DOT(0x40000, 2),
    FLAME_HAZE(-1, -1),
    BIND(-2, -2),
    REWARD_BUFF(-3, -3),
    THREATEN(-4, -4),
    FREEZINGSTACK(-5, -5),
    DEFENSEREDUCE(-6, -6),
    DEATHMARK(-7, -7),
    DRAGONSTRIKE(-8, -8),
    EMPTY(0x8000000, 1, true),
    SUMMON(0x80000000, 1, true), //all summon bag mobs have.
    EMPTY_1(0x20, 2, false), //chaos
    EMPTY_2(0x40, 2, true),
    EMPTY_3(0x80, 2, true),
    EMPTY_4(0x100, 2, true), //jump
    EMPTY_5(0x200, 2, true),
    EMPTY_6(0x400, 2, true),
    EMPTY_7(0x2000, 2, true),;
    static final long serialVersionUID = 0L;
    private final int i;
    private final int first;
    private final boolean end;

    private MobStatusFlag(int i, int first) {
        this.i = i;
        this.first = first;
        this.end = false;
    }

    private MobStatusFlag(int i, int first, boolean end) {
        this.i = i;
        this.first = first;
        this.end = end;
    }

    public int getPosition() {
        return first;
    }

    public boolean isEmpty() {
        return end;
    }

    public int getValue() {
        return i;
    }

    public void writeSingleMask(PacketWriter pw) {
        for (int i = 0; i <= 2;i++) {
            pw.writeInt(i == getPosition() ? getValue() : 0);
        }
    }

    /*public static final MapleDisease getLinkedDisease(final MonsterStatus skill) {
        switch (skill) {
            case STUN:
            case SHADOW_WEB:
                return MapleDisease.STUN;
            case POISON:
            case VENOMOUS_WEAPON:
                return MapleDisease.POISON;
            case SEAL:
            case MAGIC_CRASH:
                return MapleDisease.SEAL;
            case FREEZE:
                return MapleDisease.FREEZE;
            case DARKNESS:
                return MapleDisease.DARKNESS;
            case SPEED:
                return MapleDisease.SLOW;
        }
        return null;
    }*/
}
