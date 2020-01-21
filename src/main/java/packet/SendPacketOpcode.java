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
    UPDATE_STATS(0x49),
    GIVE_BUFF(0x4A),
    CANCEL_BUFF(0x4B),
    UPDATE_SKILLS(0x4E),
    PARTY_OPERATION(0x72),
    SERVER_MESSAGE(0x82),
    SET_EVENT_NAME_TAG(0x118),

    //CStage
    SET_FIELD(0x1AC),
    CANCEL_FOREIGN_BUFF(0x28F),

    //CUserPool::OnUserCommonPacket
    SPAWN_PLAYER(0x204),
    REMOVE_PLAYER_FROM_MAP(0x205),
    CHAT(0x206),

    //CUserPool::OnUserRemotePacket
    MOVE_PLAYER(0x279),
    UPDATE_PARTY_MEMBER_HP(0x290),

    //CMobPool
    SPAWN_MONSTER(0x38C),
    KILL_MONSTER(0x38D),
    SPAWN_MONSTER_CONTROL(0x38E),
    MOVE_MONSTER(0x392),
    MOVE_MONSTER_RESPONSE(0x393),
    APPLY_MOB_STATUS(0x395),
    CANCEL_MOB_STATUS(0x396);


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