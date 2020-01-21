package game.character;

import constants.Jobs;
import constants.Skill;
import game.skill.SkillEntry;
import game.skill.SkillFactory;
import utils.DatabaseConnection;
import utils.Logging;
import utils.data.PacketWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CharacterSkills {
    public int[] sp;
    Map<Integer, SkillEntry> skills = new HashMap<>();

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

    public int saveSp(PreparedStatement ps, int index) throws SQLException {
        StringBuilder sps = new StringBuilder();
        for (int i = 0; i < sp.length; i++) {
            sps.append(sp[i]);
            sps.append(",");
        }
        String out = sps.toString();
        ps.setString(++index, out.substring(0, out.length() - 1));
        return index;
    }

    public void save(int chrId) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO skills (skill_id, character_id, skill_level, master_level) VALUES (?, ?, ?, ?) " +
                                      "ON DUPLICATE KEY UPDATE skill_level = VALUES(skill_level), master_level = VALUES(master_level)");
            for(Map.Entry<Integer, SkillEntry> se : skills.entrySet()) {
                ps.setInt(1, se.getKey());
                ps.setInt(2, chrId);
                ps.setInt(3, se.getValue().slv);
                ps.setInt(4, se.getValue().masterlevel);
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            ps.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public void addCharSp(PacketWriter pw, Jobs.Job job) {
        if(Jobs.Job.isSeparatedSp(job)) {
            int size = getRemainingSpSize();
            pw.write(size);
            for(int i = 0; i < sp.length; i++) {
                if(sp[i] > 0) {
                    pw.write(i);
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

    public void addSkill(int skillId) {
        int slv = 0;
        if(Skill.isDefaultedSkill(skillId)) {
            slv = 1;
        }
        skills.put(skillId, new SkillEntry(slv, SkillFactory.getSkill(skillId).maxLevel));
    }

    public void giveSp(int book, int amount) {
        sp[book] += amount;
    }

    public boolean distributeSp(int skillId, int amount) {
        int book = skillBook(skillId);
        int curSp = sp[book];
        if(amount > curSp) {
            //ban
            return false;
        }
        SkillEntry target = skills.get(skillId);
        if(target == null || target.slv + amount > target.masterlevel) {
            //ban
            return false;
        }
        target.slv += amount;
        sp[book] -= amount;
        return true;
    }

    public void loadSkills(Connection con, int chrId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM skills WHERE character_id = ?");
        ps.setInt(1, chrId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            skills.put(rs.getInt("skill_id"), new SkillEntry(rs.getInt("skill_level"), rs.getInt("master_level")));
        }
        ps.close();
        rs.close();
    }

    public int getSkillLevel(int skillId) {
        SkillEntry se = skills.get(skillId);
        if(se == null) {
            return -1;
        }
        return se.slv;
    }

    public void writeSkillInfo(PacketWriter pw) {
        pw.write(1);
        pw.writeShort(skills.size());
        for(Map.Entry<Integer, SkillEntry> entry : skills.entrySet()) {
            pw.writeInt(entry.getKey());
            pw.writeInt(entry.getValue().slv);
            pw.writeLong(0);
            int masterLevel = entry.getValue().masterlevel;
            if(masterLevel > 0) {
                pw.writeInt(masterLevel);
            }
        }
        pw.writeShort(0);
    }

    public void writeUpdateSkills(PacketWriter pw) {
        pw.writeShort(skills.size());
        for(Map.Entry<Integer, SkillEntry> entry : skills.entrySet()) {
            pw.writeInt(entry.getKey());
            pw.writeInt(entry.getValue().slv);
            pw.writeInt(entry.getValue().masterlevel);
            pw.writeLong(0);
        }
        pw.write(0x0);
    }

    public void writeUpdateSp(PacketWriter pw) {
        pw.write(getRemainingSpSize());
        for (int i = 0; i < sp.length; i++) {
            if (sp[i] > 0) {
                pw.write(i);
                pw.writeInt(sp[i]);
            }
        }
    }

    public static int skillBook(int skillId) {
        if((skillId / 10000) % 100 == 0) {
            return 1;
        }
        return (skillId / 10000) % 10 + 2;
    }
}
