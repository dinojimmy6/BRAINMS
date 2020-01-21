package packet;

import java.util.HashMap;
import java.util.Map;

public enum RecvPacketOpcode {
    // Login Opcodes
    CLIENT_CONNECT(0x67),
    LOGIN(0x69),
    CHAR_LIST_REQUEST(0x6A),
    CHECK_PIN(0x6B),
    REDISPLAY_WORLD_INFO(0x72),
    CHECK_NAME(0x74),
    CREATE_CHAR_REQUEST(0x7D),
    DELETE_CHAR_REQUEST(0x80),
    AUTH_REQUEST(0x86),
    HOTFIX(0x98),
    CHANNEL_SELECT_REQUEST(0x9D),
    WORLD_INFO_REQUEST(0xA2),
    CREATE_PIN(0xA6),
    CHANGE_PIC_REQUEST(0xAA),
    AUTH_SERVER(0xAB),
    BUTTON_PRESS(0x24B),
    //Game
    MOVE_PLAYER(0xBE),
    CHAR_LOGIN(0x6E),
    ON_MELEE_ATTACK(0xC3),
    GENERAL_CHAT(0xCB),
    DISTRIBUTE_SP(0x130),
    SPECIAL_MOVE(0x131),
    MOVE_LIFE(0x339),
    UNHANDLED(0x9999);

    private short value;
    private static Map<Short, RecvPacketOpcode> map = new HashMap<Short, RecvPacketOpcode>();

    static {
        for (RecvPacketOpcode r : RecvPacketOpcode.values()) {
            map.put(r.getValue(), r);
        }
    }

    RecvPacketOpcode(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

    public static RecvPacketOpcode getName(int value) {
        RecvPacketOpcode ret = map.get((short)value);
        if(ret != null) {
            return map.get((short) value);
        }
        else {
            return UNHANDLED;
        }
    }
}
