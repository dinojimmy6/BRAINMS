package game.character;

import constants.Jobs;
import utils.Logging;
import utils.data.PacketWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterSkills {
    int[] sp;

    public CharacterSkills(ResultSet rs) {
        sp = new int[10];
        try {
            String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < sp.length; i++) {
                this.sp[i] = Integer.parseInt(sp[i]);
            }
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public CharacterSkills() {
        sp = new int[10];
    }

    public int save(PreparedStatement ps, int index) throws SQLException {
        StringBuilder sps = new StringBuilder();
        for (int i = 0; i < sp.length; i++) {
            sps.append(sp[i]);
            sps.append(",");
        }
        String out = sps.toString();
        ps.setString(++index, out.substring(0, out.length() - 1));
        return index;
    }

    public void addCharSp(PacketWriter pw, Jobs.Job job) {
        if(Jobs.Job.isSeparatedSp(job)) {
            int size = getRemainingSpSize();
            pw.write(size);
            for(int i = 0; i < sp.length; i++) {
                if(sp[i] > 0) {
                    pw.write(i + 1);
                    pw.writeInt(sp[i]);
                }
            }
        }
        else {
            pw.writeShort(sp[job.getRank()]);
        }
    }

    private int getRemainingSpSize() {
        int ret = 0;
        for(int i = 0; i < sp.length; i++) {
            if(sp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }
}
