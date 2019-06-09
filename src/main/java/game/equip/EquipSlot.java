package game.equip;

public enum EquipSlot {
    CAP(-1),
    FACE_ACCESSORY(-2),
    EYE_ACCESSORY(-3),
    EARRINGS(-4),
    TOP(-5),
    OVERCOAT(-5),
    PANTS(-6),
    SHOES(-7),
    GLOVES(-8),
    SECONDARY(-10),
    CAPE(-9),
    RING1(-12),
    RING2(-13),
    RING3(-15),
    RING4(-16),
    PENDANT(-17),
    PENDANT2(-65),
    BELT(-50),
    MEDAL(-49),
    SHOULDER(-51),
    BADGE(-56),
    EMBLEM(-61),
    TOTEM1(-5000),
    TOTEM2(-5001),
    TOTEM3(-5002),
    ANDROID(-53),
    HEART(-54),
    WEAPON(-11),
    TAMING_MOB(-18),
    SADDLE(-19);

    public int slot;

    private EquipSlot(int slot) {
        this.slot = slot;
    }
}
