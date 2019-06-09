package game.equip;

public enum EquipType {
    CAP(100, EquipSlot.CAP),
    FACE_ACCESSORY(101, EquipSlot.FACE_ACCESSORY),
    EYE_ACCESSORY(102, EquipSlot.EYE_ACCESSORY),
    EARRINGS(103, EquipSlot.EARRINGS),
    TOP(104, EquipSlot.TOP),
    OVERCOAT(105, EquipSlot.OVERCOAT),
    PANTS(106, EquipSlot.PANTS),
    SHOES(107, EquipSlot.SHOES),
    GLOVES(108, EquipSlot.GLOVES),
    SHIELD(109, EquipSlot.SECONDARY),
    KATARA(134, EquipSlot.SECONDARY),
    SECONDARY(135, EquipSlot.SECONDARY),
    CAPE(110, EquipSlot.CAPE),
    RING(111, EquipSlot.RING1, EquipSlot.RING2, EquipSlot.RING3, EquipSlot.RING4),
    PENDANT(112, EquipSlot.PENDANT, EquipSlot.PENDANT2),
    BELT(113, EquipSlot.BELT),
    MEDAL(114, EquipSlot.MEDAL),
    SHOULDER(115, EquipSlot.SHOULDER),
    BADGE(118, EquipSlot.BADGE),
    EMBLEM(119, EquipSlot.EMBLEM),
    TOTEM(120, EquipSlot.TOTEM1, EquipSlot.TOTEM2, EquipSlot.TOTEM3),
    ANDROID(166, EquipSlot.ANDROID),
    HEART(167, EquipSlot.HEART),

    SHINING_ROD(121, EquipSlot.WEAPON),
    DEMON_SWORD(123, EquipSlot.WEAPON),
    PSY_LIMITER(126, EquipSlot.WEAPON),
    ONE_HANDED_SWORD(130, EquipSlot.WEAPON),
    ONE_HANDED_AXE(131, EquipSlot.WEAPON),
    ONE_HANDED_BLUNT_WEAPON(132, EquipSlot.WEAPON),
    DAGGER(133, EquipSlot.WEAPON),
    CANE(136, EquipSlot.WEAPON),
    WAND(137, EquipSlot.WEAPON),
    STAFF(138, EquipSlot.WEAPON),

    FISTS(139, EquipSlot.WEAPON), // placeholder item for having no "knuckler"
    TWO_HANDED_SWORD(140, EquipSlot.WEAPON),
    TWO_HANDED_AXE(141, EquipSlot.WEAPON),
    TWO_HANDED_BLUNT_WEAPON(142, EquipSlot.WEAPON),
    SPEAR(143, EquipSlot.WEAPON),
    POLEARM(144, EquipSlot.WEAPON),
    BOW(145, EquipSlot.WEAPON),
    CROSSBOW(146, EquipSlot.WEAPON),
    CLAW(147, EquipSlot.WEAPON),
    KNUCKLER(148, EquipSlot.WEAPON),
    GUN(149, EquipSlot.WEAPON),

    TAMING_MOB(190, EquipSlot.TAMING_MOB),
    SADDLE(191, EquipSlot.SADDLE),
    SPECIAL_TAMING_MOB(193, EquipSlot.TAMING_MOB),
    CASH_WEAPON(170, EquipSlot.WEAPON),
    CASH_ITEM;

    private int prefix;
    private EquipSlot[] allowed;

    private EquipType() {
        prefix = 0;
    }

    private EquipType(int pre, EquipSlot... in) {
        prefix = pre;
        allowed = in;
    }

    public int getPrefix() {
        return prefix;
    }

    public EquipSlot getAllowed() {
        return allowed[0];
    }

    public boolean isTwoHanded() {
        return prefix >= 139 && prefix <= 149;
    }

    public boolean isAllowed(int slot, boolean cash) {
        if (allowed != null) {
            for (EquipSlot allow : allowed) {
                if(cash) {
                    if(slot == allow.slot - 100 || slot == allow.slot - 1199)
                        return true;
                }
                else {
                    if(slot == allow.slot)
                        return true;
                }
            }
        }
        return cash;
    }


    public static EquipType getFromItemId(int id) {
        int prefix = id / 10000;
        if (prefix != 0) {
            for (EquipType c : values()) {
                if (c.getPrefix() == prefix) {
                    return c;
                }
            }
        }
        return CASH_ITEM;
    }
}
