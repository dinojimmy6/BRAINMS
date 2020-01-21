package game.skill;

import utils.parsing.MapleData;
import constants.Jobs.Job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillFactory {
    public static Map<Integer, Skill> skills = new HashMap<>();
    public static Map<Job, List<Skill>> jobSkills = new HashMap<>();

    public static void loadAllSkills() {
        String path = "wz/skill";
        MapleData info = null;
        try {
            File dir = new File(path);
            File[] listings = dir.listFiles();
            for(File file : listings) {
                info = new MapleData(new FileInputStream(file));
                for (MapleData skillRoot : info.getChildByPath("skill")) {
                    int skillId = Integer.parseInt(skillRoot.getName());
                    skills.put(skillId, new Skill(skillId, skillRoot));
                }
            }
        }
        catch (FileNotFoundException e) {
            return;
        }
    }

    public static void loadAllSkillsByJob() {

    }

    public static Skill getSkill(int skillId) {
        return skills.get(skillId);
    }
}
