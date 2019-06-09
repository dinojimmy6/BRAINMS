package game.handlers;

import packet.RecvPacketOpcode;
import utils.data.LittleEndianAccessor;

public interface PacketHandler {
    public void handle(RecvPacketOpcode opcode, LittleEndianAccessor lea);
}
