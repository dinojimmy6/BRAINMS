package game.equip;

public enum WeaponType {
    ROD(1.0f, 30),
    NOT_A_WEAPON(1.43f, 20),
    BOW(1.3f, 15),
    CLAW(1.75f, 15),
    CANE(1.3f, 15),
    DAGGER(1.3f, 20),
    CROSSBOW(1.35f, 15),
    AXE1H(1.2f, 20),
    SWORD1H(1.2f, 20),
    BLUNT1H(1.2f, 20),
    AXE2H(1.34f, 20),
    SWORD2H(1.34f, 20),
    BLUNT2H(1.34f, 20),
    POLE_ARM(1.49f, 20),
    SPEAR(1.49f, 20),
    STAFF(1.0f, 25),
    WAND(1.0f, 25),
    KNUCKLE(1.7f, 20),
    GUN(1.5f, 15),
    CANNON(1.35f, 15),
    DUAL_BOW(1.35f, 15), //beyond op
    MAGIC_ARROW(2.0f, 15),
    CARTE(2.0f, 15),
    KATARA(1.3f, 20),
    BIG_SWORD(1.3f, 15),
    LONG_SWORD(1.3f, 15);

    private final float damageMultiplier;
    private final int baseMastery;

    private WeaponType(final float maxDamageMultiplier, int baseMastery) {
        this.damageMultiplier = maxDamageMultiplier;
        this.baseMastery = baseMastery;
    }

    public final float getMaxDamageMultiplier() {
        return damageMultiplier;
    }

    public final int getBaseMastery() {
        return baseMastery;
    }

    public static WeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) {
            case 21:
                return ROD;
            case 30:
                return SWORD1H;
            case 31:
                return AXE1H;
            case 32:
                return BLUNT1H;
            case 33:
                return DAGGER;
            case 34:
                return KATARA;
            case 35:
                return MAGIC_ARROW; // can be magic arrow or cards
            case 36:
                return CANE;
            case 37:
                return WAND;
            case 38:
                return STAFF;
            case 40:
                return SWORD2H;
            case 41:
                return AXE2H;
            case 42:
                return BLUNT2H;
            case 43:
                return SPEAR;
            case 44:
                return POLE_ARM;
            case 45:
                return BOW;
            case 46:
                return CROSSBOW;
            case 47:
                return CLAW;
            case 48:
                return KNUCKLE;
            case 49:
                return GUN;
            case 52:
                return DUAL_BOW;
            case 53:
                return CANNON;
            case 56:
                return BIG_SWORD;
            case 57:
                return LONG_SWORD;
        }
        return NOT_A_WEAPON;
    }
};
