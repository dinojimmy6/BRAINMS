package packet;

import java.util.HashMap;
import java.util.Map;

public enum SendPacketOpcode {
    // Login Opcodes
    LOGIN_STATUS(0x00),
    WORLD_INFO(0x01),
    CHAR_LIST(0x06),
    FINISH_LOGIN(0x07),
    CHECK_NAME_RESPONSE(0x0A),
    NEW_CHARACTER(0x0B),
    DELETE_CHAR(0x0C),
    AUTH_RESPONSE(0x17),
    PIN_RESPONSE(0x18),
    HOTFIX_RESPONSE(0x24),
    CHANNEL_SELECT(0x26),
    AUTH_SERVER(0x2F),

    //CWvsContext
    SERVER_MESSAGE(0x82),

    //CStage
    SET_FIELD(0x1AC),

    //CUserPool::OnUserCommonPacket
    CHAT(0x206),

    //CMobPool
    SPAWN_MONSTER(0x38C);

    private short value;
    private static Map<Short, SendPacketOpcode> map = new HashMap<Short, SendPacketOpcode>();

    static {
        for (SendPacketOpcode s : SendPacketOpcode.values()) {
            map.put(s.getValue(), s);
        }
    }

    SendPacketOpcode(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

    public static SendPacketOpcode getName(int value) {
        return map.get((short)value);
    }
}