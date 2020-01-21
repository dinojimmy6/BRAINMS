package game.skill;

import game.character.MapleCharacter;
import game.monster.Monster;

/**
 * Created by jimmy on 10/16/2017.
 */
public class AttackSkillHandler {
    protected int skillId;
    protected MapleCharacter chrFrom;
    protected Skill skill;
    protected int slv;
    public AttackSkillHandler(int skillId, int slv, MapleCharacter chrFrom) {
        skill = SkillFactory.getSkill(skillId);
        this.slv = slv;
        this.skillId = skillId;
        this.chrFrom = chrFrom;
    }

    public void consumeResource() {
        //mse.drainMP(chrFrom);
    }

    public void onAttackCast(AttackInfo attack) {}

    public void onAttackHit() {}

    //public void onAttackMobResponse(List<AttackPair> pairs) {}

    public void showAttackEffect(Monster monster) {}

    public void onAttackPerMob(Monster monster) {}

    public void onAttackDamage(long totalDamage) {}

    public void afterAttack() {}

    public void getExpectedDamage(AttackInfo ai) {
        ai.printExpectedDamage(chrFrom, skill.getDamage(slv), null);
    }
}
