package game.movement;

import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;

import java.awt.Point;

public class Movement5 extends AbstractLifeMovement {

    public Movement5(LittleEndianAccessor lea, byte command) {
        super();
        this.command = command;
        
        short x = lea.readShort();
        short y = lea.readShort();
        position = new Point(x, y);
        
        fh = lea.readShort();
        
        bMoveAction = lea.readByte();
        tElapse = lea.readShort();
        bForcedStop = lea.readByte();
    }

    @Override
    public void serialize(PacketWriter lew) {
        lew.write(getCommand());
        lew.writePos(getPosition());
        lew.writeShort(getFh());
        lew.write(getMoveAction());
        lew.writeShort(getDuration());
        lew.write(getForcedStop());
    }

}
