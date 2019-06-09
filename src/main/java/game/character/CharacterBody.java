package game.character;

import utils.Logging;
import utils.data.PacketWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterBody {
    public byte gender, skinColor;
    public int face, hair, hairColor, faceMarking;

    public CharacterBody(byte gender, byte skinColor, int face, int hair, int faceMarking) {
        this.gender = gender;
        this.skinColor = skinColor;
        this.face = face;
        this.hair = hair;
        this.faceMarking = faceMarking;
    }

    public CharacterBody(ResultSet rs) throws SQLException {
        gender = rs.getByte("gender");
        skinColor = rs.getByte("skincolor");
        face = rs.getInt("face");
        hair = rs.getInt("hair");
        faceMarking = rs.getInt("facemarking");
    }

    public int save(PreparedStatement ps, int index) throws SQLException {
        ps.setByte(++index, gender);
        ps.setByte(++index, skinColor);
        ps.setInt(++index, face);
        ps.setInt(++index, hair);
        ps.setInt(++index, faceMarking);
        return index;
    }

    public void addCharBody(PacketWriter pw) {
        pw.write(gender);
        pw.write(skinColor);
        pw.writeInt(face);
        pw.writeInt(hair);
        pw.write(-1);
        pw.write(0);
        pw.write(0);
    }

    public void addFaceMarking(PacketWriter pw) {
        pw.writeInt(faceMarking);
    }
}
