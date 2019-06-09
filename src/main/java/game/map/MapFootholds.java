package game.map;

import utils.parsing.MapleData;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MapFootholds {
    public List<Foothold> footholds = new LinkedList<>();
    private Point lBound = new Point();
    private Point uBound = new Point();

    public MapFootholds(MapleData info) {
        Foothold fh;
        for (MapleData footRoot : info.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    Point p1 = new Point(MapleData.getInt(footHold.getChildByPath("x1"), 0), MapleData.getInt(footHold.getChildByPath("y1")));
                    Point p2 = new Point(MapleData.getInt(footHold.getChildByPath("x2"), 0), MapleData.getInt(footHold.getChildByPath("y2")));
                    short prev = (short) MapleData.getInt(footHold.getChildByPath("prev"), 0);
                    short next = (short) MapleData.getInt(footHold.getChildByPath("next"), 0);
                    fh = new Foothold(p1, p2, prev, next, Integer.parseInt(footHold.getName()));
                    addFoothold(fh);
                }
            }
        }
    }

    public Foothold findBelow(Point p) {
        final List<Foothold> xMatches = new LinkedList<>();
        for (final Foothold fh : footholds) {
            if (fh.p1.x <= p.x && fh.p2.x >= p.x) {
                xMatches.add(fh);
            }
        }
        Collections.sort(xMatches);
        for (final Foothold fh : xMatches) {
            if(fh.p1.x <= p.x && fh.p2.x >= p.x) {
                if (!fh.isWall() && !fh.isFlat()) {
                    if (fh.getDeltaY(p) >= p.y) {
                        return fh;
                    }
                } else if (!fh.isWall()) {
                    if (fh.p1.y >= p.y) {
                        return fh;
                    }
                }
            }
        }
        return null;
    }

    public Point calcPointBelow(Point p) {
        Foothold fh = findBelow(p);
        if (fh == null) {
            return null;
        }
        int dropY = fh.p1.y;
        if (!fh.isWall() && !fh.isFlat()) {
            dropY = fh.getDeltaY(p);
        }
        return new Point(p.x, dropY);
    }

    private void addFoothold(Foothold fh) {
        if (fh.p1.x < lBound.x) {
            lBound.x = fh.p1.x;
        }
        if (fh.p2.x > uBound.x) {
            uBound.x = fh.p2.x;
        }
        if (fh.p1.y < lBound.y) {
            lBound.y = fh.p1.y;
        }
        if (fh.p2.y > uBound.y) {
            uBound.y = fh.p2.y;
        }
        footholds.add(fh);
    }

    public static class Foothold implements Comparable<Foothold> {
        private final Point p1;
        private final Point p2;
        private short next, prev;
        public final int id;

        public Foothold(Point p1, Point p2, short prev, short next, int id) {
            this.p1 = p1;
            this.p2 = p2;
            this.prev = prev;
            this.next = next;
            this.id = id;
        }

        public boolean isWall() {
            return p1.x == p2.x;
        }

        public boolean isFlat() {
            return p1.y == p2.y;
        }

        public int getDeltaY(Point p) {
            final double s1 = Math.abs(p2.y - p1.y);
            final double s2 = Math.abs(p2.x - p1.x);
            final double s4 = Math.abs(p.x - p1.x);
            final double alpha = Math.atan(s2 / s1);
            final double beta = Math.atan(s1 / s2);
            final double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
            if (p2.y < p1.y) {
                return p1.y - (int) s5;
            } else {
                return p1.y + (int) s5;
            }
        }

        @Override
        public int compareTo(Foothold fh) {
            if (p2.y < fh.p1.y) {
                return -1;
            } else if (p1.y > fh.p2.y) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Foothold)) {
                return false;
            }
            final Foothold oth = (Foothold) o;
            return oth.p1.y == p1.y && oth.p2.y == p2.y && oth.p1.x == p1.x && oth.p2.x == p2.x && id == oth.id;
        }
    }
}

