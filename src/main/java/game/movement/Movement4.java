package game.movement;

import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;

import java.awt.Point;

public class Movement4 extends AbstractLifeMovement {
    
    public Movement4(LittleEndianAccessor lea, byte command) {
        super();
        this.command = command;
        this.position = new Point(0, 0);
        
        bMoveAction = lea.readByte();
        tElapse = lea.readShort();
        bForcedStop = lea.readByte();
    }

    @Override
    public void serialize(PacketWriter lew) {
        lew.write(getCommand());
        lew.write(getMoveAction());
        lew.writeShort(getDuration());
        lew.write(getForcedStop());
    }

}
