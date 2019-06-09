package game.character;

import utils.Logging;
import utils.data.PacketWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterStats {
    public int str, dex, int_, luk, maxhp, maxmp, hp, mp, ap;
    public long exp;
    public int fame;

    public CharacterStats(ResultSet rs) {
        try {
            this.str = rs.getInt("str");
            this.dex = rs.getInt("dex");
            this.int_ = rs.getInt("int");
            this.luk = rs.getInt("luk");
            this.maxhp = rs.getInt("maxhp");
            this.maxmp = rs.getInt("maxmp");
            this.hp = rs.getInt("hp");
            this.mp = rs.getInt("mp");
            this.ap = rs.getInt("ap");
            this.exp = rs.getLong("exp");
            this.fame = rs.getInt("fame");
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public CharacterStats() {
       str = 4;
       dex = 4;
       int_ = 4;
       luk = 4;
       maxhp = 50;
       maxmp = 50;
       hp = 50;
       mp = 50;
       exp = 0;
       fame = 0;
    }

    public int save(PreparedStatement ps, int index) throws SQLException {
        ps.setInt(++index, str);
        ps.setInt(++index, dex);
        ps.setInt(++index, int_);
        ps.setInt(++index, luk);
        ps.setInt(++index, maxhp);
        ps.setInt(++index, maxmp);
        ps.setInt(++index, hp);
        ps.setInt(++index, mp);
        ps.setInt(++index, ap);
        ps.setLong(++index, exp);
        ps.setInt(++index, fame);
        return index;
    }

    public void addCharStats(PacketWriter pw) {
        pw.writeShort(str);
        pw.writeShort(dex);
        pw.writeShort(int_);
        pw.writeShort(luk);
        pw.writeInt(hp);
        pw.writeInt(maxhp);
        pw.writeInt(mp);
        pw.writeInt(maxmp);
        pw.writeShort(ap);
    }

    public void addMisc(PacketWriter pw) {
        pw.writeLong(exp);
        pw.writeInt(fame);
    }
}
