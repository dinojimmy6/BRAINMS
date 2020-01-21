package game.movement;

import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;

import java.awt.Point;

public class Movement8 extends AbstractLifeMovement {

    public Movement8(LittleEndianAccessor lea, byte command) {
        super();
        this.command = command;
        this.position = new Point(0, 0);
        
        this.bStat = lea.readByte();
    }
    
    @Override
    public void serialize(PacketWriter lew) {
        lew.write(getCommand());
        lew.write(getBStat());
    }

}
