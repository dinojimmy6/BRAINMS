package game.skill;

import utils.parsing.MapleData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class SkillFactory {
    public static Map<Integer, Skill> skills = new HashMap<>();

    public static void loadAllSkills() {
        String path = "wz/skill/100.img.xml";
        MapleData info = null;
        try {
            info = new MapleData(new FileInputStream(path));
        }
        catch (FileNotFoundException e) {
            return;
        }
        for (MapleData skillRoot : info.getChildByPath("skill")) {
            int skillId = Integer.parseInt(skillRoot.getName());
            skills.put(skillId, new Skill(skillId, skillRoot));
        }
    }
}
