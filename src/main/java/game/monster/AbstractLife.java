package game.monster;

import game.map.AnimatedMapObject;

public abstract class AbstractLife extends AnimatedMapObject {
    public int f;
    public boolean hide = false;
    public int fh, originFh;
    public int cy;
    public int rx0;
    public int rx1;
    public String ctype;
    public int mtime;

    public void setLifePosition(int f, int fh) {
        this.cy = position.y;
        this.rx0 = position.x - 50;
        this.rx1 = position.x + 50;
        this.fh = fh;
        this.f = f;
    }
}
