package game.map;

import game.GameClient;
import game.character.MapleCharacter;
import org.apache.mina.core.session.IoSession;

import java.awt.Point;

public abstract class MapObject {
    protected int oid;
    protected final Point position = new Point();

    public void setPosition(int x, int y) {
       position.x = x;
       position.y = y;
    }

    public void setPosition(Point p) {
        position.x = p.x;
        position.y = p.y;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getRange() {
        return 800;
    }

    public abstract void sendSpawnData(MapleCharacter chr);

    public abstract void sendDestroyData(MapleCharacter chr);
}
