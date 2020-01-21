package game.character;

import constants.Jobs.Job;
import game.equip.EquipSlot;
import game.equip.WeaponType;
import game.item.Equip;
import game.skill.Hero;
import game.skill.Paladin;
import game.skill.Skill;
import game.skill.SkillFactory;
import utils.Logging;
import utils.data.PacketWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CharacterStats {
    private int curHp, curMp;
    public int str, dex, int_, luk, maxhp, maxmp, hp, mp, ap, mastery;
    public long exp;
    public int fame;

    public int e_watk, e_matk, e_wdef, e_mdef, e_acc, e_eva, e_speed, e_jump, e_str, e_dex, e_int, e_luk, e_hp, e_mp,
               e_hpR, e_mpR, e_watkR, e_matkR;

    public int p_watk, p_matk, p_wdef, p_mdef, p_acc, p_eva, p_speed, p_jump, p_str, p_dex, p_int, p_luk, p_hp, p_mp,
               p_watkR, p_matkR, p_wdefR, p_mdefR, p_accR, p_evaR, p_hpR, p_mpR, p_damAbsorbR, p_asrR, p_terR, p_damR,
               p_bossDamR, p_critRate, p_minCrit, p_maxCrit, p_finalDmg = 1, p_ied = 1;

    public int b_watk, b_matk, b_wdef, b_mdef, b_acc, b_eva, b_speed, b_jump, b_str, b_dex, b_int, b_luk, b_hp, b_mp,
            b_watkR, b_matkR, b_wdefR, b_mdefR, b_accR, b_evaR, b_hpR, b_mpR, b_damAbsorbR, b_asrR, b_terR, b_damR,
            b_bossDamR, b_critRate, b_minCrit, b_maxCrit, b_finalDmg = 1, b_ied = 1;

    public int buffDuration;

    public double maxBaseDmg, minBaseDmg;

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

    public CharacterStats(CharacterStats cs) {
        str = cs.str;
        dex = cs.dex;
        int_ = cs.int_;
        luk = cs.luk;
        maxhp = cs.maxhp;
        maxmp = cs.maxmp;
        hp = cs.hp;
        mp = cs.mp;
        ap = cs.ap;
        mastery = cs.mastery;
        exp = cs.exp;
        fame = cs.fame;

        e_watk = cs.e_watk;
        e_matk = cs.e_matk;
        e_wdef = cs.e_wdef;
        e_mdef = cs.e_mdef;
        e_acc = cs.e_acc;
        e_eva = cs.e_eva;
        e_speed = cs.e_speed;
        e_jump = cs.e_jump;
        e_str = cs.e_str;
        e_dex = cs.e_dex;
        e_int = cs.e_int;
        e_luk = cs.e_luk;
        e_hp = cs.e_hp;
        e_mp = cs.e_mp;
        e_hpR = cs.e_hpR;
        e_mpR = cs.e_mpR;

        p_watk = cs.p_watk;
        p_matk = cs.p_matk;
        p_wdef = cs.p_wdef;
        p_mdef = cs.p_mdef;
        p_acc = cs.p_acc;
        p_eva = cs.p_eva;
        p_speed = cs.p_speed;
        p_jump = cs.p_jump;
        p_str = cs.p_str;
        p_dex = cs.p_dex;
        p_int = cs.p_int;
        p_luk = cs.p_luk;
        p_hp = cs.p_hp;
        p_mp = cs.p_mp;
        p_watkR = cs.p_watkR;
        p_matkR =  p_wdefR;
        p_mdefR = cs.p_mdefR;
        p_accR = cs.p_accR;
        p_evaR = cs.p_evaR;
        p_hpR = cs.p_hpR;
        p_mpR = cs.p_mpR;
        p_damAbsorbR = cs.p_damAbsorbR;
        p_asrR = cs.p_asrR;
        p_terR = cs.p_terR;
        p_damR = cs.p_damR;
        p_bossDamR = cs.p_bossDamR;
        p_critRate = cs.p_critRate;
        p_minCrit = cs.p_minCrit;
        p_maxCrit = cs.p_maxCrit;
        p_finalDmg = cs.p_finalDmg;
        p_ied = cs.p_ied;

        b_watk = cs.b_watk;
        b_matk = cs.b_matk;
        b_wdef = cs.b_wdef;
        b_mdef = cs.b_mdef;
        b_acc = cs.b_acc;
        b_eva = cs.b_eva;
        b_speed = cs.b_speed;
        b_jump = cs.b_jump;
        b_str = cs.b_str;
        b_dex = cs.b_dex;
        b_int = cs.b_int;
        b_luk = cs.b_luk;
        b_hp = cs.b_hp;
        b_mp = cs.b_mp;
        b_watkR = cs.b_watkR;
        b_matkR =  b_wdefR;
        b_mdefR = cs.b_mdefR;
        b_accR = cs.b_accR;
        b_evaR = cs.b_evaR;
        b_hpR = cs.b_hpR;
        b_mpR = cs.b_mpR;
        b_damAbsorbR = cs.b_damAbsorbR;
        b_asrR = cs.b_asrR;
        b_terR = cs.b_terR;
        b_damR = cs.b_damR;
        b_bossDamR = cs.b_bossDamR;
        b_critRate = cs.b_critRate;
        b_minCrit = cs.b_minCrit;
        b_maxCrit = cs.b_maxCrit;
        b_finalDmg = cs.b_finalDmg;
        b_ied = cs.b_ied;
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

    public void getBaseDmg(int weaponId, int job) {
        WeaponType weapon = WeaponType.getWeaponType(weaponId);
        int mainstat, secondarystat;
        boolean mage = Job.isMage(job);
        double bonusWeaponMultiplier = 0f;
        switch (weapon) {
            case BOW:
            case CROSSBOW:
            case GUN:
                mainstat = getTotalDex();
                secondarystat = getTotalStr();
                break;
            case DAGGER:
            case KATARA:
            case CLAW:
            case CANE:
                mainstat = getTotalLuk();
                secondarystat = getTotalDex() + getTotalStr();
                break;
            case STAFF:
            case WAND:
                mainstat = getTotalInt();
                secondarystat = getTotalLuk();
                if(job <= 232) {
                    bonusWeaponMultiplier = .2f;
                }
                break;
            case SWORD1H:
            case SWORD2H:
            case AXE1H:
            case AXE2H:
                if(job == 112 || job == 111 || job == 110) {
                    bonusWeaponMultiplier = .1f;
                }
            default:
                if (mage) {
                    mainstat = getTotalInt();
                    secondarystat = getTotalLuk();
                } else {
                    mainstat = getTotalStr();
                    secondarystat = getTotalDex();
                }
                break;
        }
        double weaponMultiplier = weapon.getMaxDamageMultiplier() + bonusWeaponMultiplier;
        maxBaseDmg = weaponMultiplier * (4 * mainstat + secondarystat) * (getTotalWatk() / 100.0f) + .5;
        minBaseDmg = Math.max(getMastery() / 100.0 * maxBaseDmg, 1.0);
    }

    public void calculateStats(MapleCharacter chr) {
        updateEquipStats(chr);
        int weaponId = chr.equips.getWeaponId();
        if(weaponId != 0) {
            getBaseDmg(weaponId, chr.getJobId());
        }
    }

    private void resetEquipStats() {
        e_watk = 0;
        e_matk = 0;
        e_wdef = 0;
        e_mdef = 0;
        e_acc = 0;
        e_eva = 0;
        e_speed = 0;
        e_jump = 0;
        e_str = 0;
        e_dex = 0;
        e_int = 0;
        e_luk = 0;
        e_hp = 0;
        e_mp = 0;
        e_hpR = 0;
        e_mpR = 0;
    }

    public void updateEquipStats(MapleCharacter chr) {
        resetEquipStats();
        for(EquipSlot es : chr.equips.equips.keySet()) {
            Equip e = chr.equips.equips.get(es);
            e_str += e.str;
            e_dex += e.dex;
            e_int += e.int_;
            e_luk += e.luk;
            e_hp += e.hp;
            e_mp += e.mp;
            e_watk += e.watk;
            e_matk += e.matk;
            e_wdef += e.wdef;
            e_mdef += e.mdef;
            e_acc += e.acc;
            e_eva += e.eva;
            e_speed += e.speed;
            e_jump += e.jump;
        }
    }

    private void resetPassiveStats() {
        p_watk = 0;
        p_matk = 0;
        p_wdef = 0;
        p_mdef = 0;
        p_acc = 0;
        p_eva = 0;
        p_speed = 0;
        p_jump = 0;
        p_str = 0;
        p_dex = 0;
        p_int = 0;
        p_luk = 0;
        p_hp = 0;
        p_mp = 0;
        p_watkR = 0;
        p_matkR = 0;
        p_mdefR = 0;
        p_accR = 0;
        p_evaR = 0;
        p_hpR = 0;
        p_mpR = 0;
        p_damAbsorbR = 0;
        p_asrR = 0;
        p_terR = 0;
        p_damR = 0;
        p_bossDamR = 0;
        p_critRate = 0;
        p_minCrit = 0;
        p_maxCrit = 0;
        p_finalDmg = 1;
        p_ied = 1;
        mastery = 0;
    }

    public void handlePassiveSkills(MapleCharacter chr) {
        resetPassiveStats();
        Skill sk;
        int slv;
        switch (chr.getJob()) {
            case Warrior:
            case Fighter:
            case Crusader:
            case Hero:
                sk = SkillFactory.getSkill(Hero.IronBody);
                slv = chr.cSkills.getSkillLevel(Hero.IronBody);
                if (slv > 0) {
                    p_hpR += sk.getMhpR(slv);
                    p_damAbsorbR += sk.getDamAbsorbShieldR(slv);
                    p_wdef += sk.getPdd(slv);
                }
                sk = SkillFactory.getSkill(Hero.WarriorMastery);
                slv = chr.cSkills.getSkillLevel(Hero.WarriorMastery);
                if (slv > 0) {
                    p_speed += sk.getSpeed(slv);
                    p_jump += sk.getJump(slv);
                    p_hp += (slv + 5) * chr.getLevel();
                }
                sk = SkillFactory.getSkill(Hero.PhysicalTraining);
                slv = chr.cSkills.getSkillLevel(Hero.PhysicalTraining);
                if (slv > 0) {
                    p_str += sk.getStr(slv);
                    p_dex += sk.getDex(slv);
                }
                sk = SkillFactory.getSkill(Hero.WeaponMastery);
                slv = chr.cSkills.getSkillLevel(Hero.WeaponMastery);
                if (slv > 0) {
                    p_finalDmg *= sk.getPdR(slv) / 100.0 + 1;
                }
                sk = SkillFactory.getSkill(Hero.Endure);
                slv = chr.cSkills.getSkillLevel(Hero.Endure);
                if (slv > 0) {
                    p_asrR += sk.getAsrR(slv);
                    p_terR += sk.getTerR(slv);
                }
                sk = SkillFactory.getSkill(Hero.CombatMastery);
                slv = chr.cSkills.getSkillLevel(Hero.CombatMastery);
                if (slv > 0) {
                    p_ied *= 1 - sk.getIed(slv) / 100.0;
                }
                sk = SkillFactory.getSkill(Hero.AdvancedFinalAttack);
                slv = chr.cSkills.getSkillLevel(Hero.AdvancedFinalAttack);
                if (slv > 0) {
                    p_watk += chr.cSkills.getSkillLevel(Hero.AdvancedFinalAttackFerocity) > 0 ?
                            sk.getPad(slv) + 20 : sk.getPad(slv);
                }
                break;
            case Page:
            case WhiteKnight:
            case Paladin:
                sk = SkillFactory.getSkill(Hero.IronBody);
                slv = chr.cSkills.getSkillLevel(Hero.IronBody);
                if (slv > 0) {
                    p_hpR += sk.getMhpR(slv);
                    p_damAbsorbR += sk.getDamAbsorbShieldR(slv);
                    p_wdef += sk.getPdd(slv);
                }
                sk = SkillFactory.getSkill(Hero.WarriorMastery);
                slv = chr.cSkills.getSkillLevel(Hero.WarriorMastery);
                if (slv > 0) {
                    p_speed += sk.getSpeed(slv);
                    p_jump += sk.getJump(slv);
                    p_hp += (slv + 5) * chr.getLevel();
                }
                sk = SkillFactory.getSkill(Paladin.WeaponMastery);
                slv = chr.cSkills.getSkillLevel(Paladin.WeaponMastery);
                if (slv > 0) {
                    mastery += sk.getMastery(slv);
                    p_acc += sk.getX(slv);
                }
                sk = SkillFactory.getSkill(Paladin.PhysicalTraining);
                slv = chr.cSkills.getSkillLevel(Paladin.PhysicalTraining);
                if (slv > 0) {
                    p_str += sk.getStr(slv);
                    p_dex += sk.getDex(slv);
                }
                sk = SkillFactory.getSkill(Paladin.Achilles);
                slv = chr.cSkills.getSkillLevel(Paladin.Achilles);
                if (slv > 0) {
                    p_damAbsorbR += sk.getY(slv);
                }
                sk = SkillFactory.getSkill(Paladin.ShieldMastery);
                slv = chr.cSkills.getSkillLevel(Paladin.ShieldMastery);
                if (slv > 0 && chr.equips.equips.get(EquipSlot.SECONDARY.slot) != null) {
                    p_watk += sk.getY(slv);
                    p_asrR += sk.getAsrR(slv);
                    p_terR += sk.getTerR(slv);
                    p_wdefR += sk.getX(slv);
                    p_mdefR += sk.getX(slv);
                }
                sk = SkillFactory.getSkill(Paladin.HighPaladin);
                slv = chr.cSkills.getSkillLevel(Paladin.HighPaladin);
                if (slv > 0 && chr.equips.equips.get(EquipSlot.WEAPON.slot) != null) {
                    mastery += sk.getMastery(slv) - 55;
                    p_minCrit += sk.getMinCrit(slv);
                }
                break;
        }
    }

    private void resetBuffStats() {
        b_watk = 0;
        b_matk = 0;
        b_wdef = 0;
        b_mdef = 0;
        b_acc = 0;
        b_eva = 0;
        b_speed = 0;
        b_jump = 0;
        b_str = 0;
        b_dex = 0;
        b_int = 0;
        b_luk = 0;
        b_hp = 0;
        b_mp = 0;
        b_watkR = 0;
        b_matkR = 0;
        b_mdefR = 0;
        b_accR = 0;
        b_evaR = 0;
        b_hpR = 0;
        b_mpR = 0;
        b_damAbsorbR = 0;
        b_asrR = 0;
        b_terR = 0;
        b_damR = 0;
        b_bossDamR = 0;
        b_critRate = 0;
        b_minCrit = 0;
        b_maxCrit = 0;
        b_finalDmg = 1;
        b_ied = 1;
    }

    public void handleBuffStats(MapleCharacter chr) {
        resetBuffStats();
        switch (chr.getJob()) {
            case Fighter:
            case Crusader:
            case Hero:
                if(chr.registeredBuffs.get(Hero.ComboAttack) != null) {
                    int stacks = chr.registeredBuffs.get(Hero.ComboAttack).getStatValue() - 1;
                    b_watk += 2 * stacks;
                    if (chr.cSkills.getSkillLevel(Hero.AdvancedCombo) > 0) {
                        Skill sk = SkillFactory.getSkill(Hero.AdvancedCombo);
                        int slv = chr.cSkills.getSkillLevel(Hero.AdvancedCombo);
                        int perStack = sk.getV(slv);
                        b_finalDmg *= (chr.cSkills.getSkillLevel(Hero.AdvancedComboAttackReinforce) > 0 ?
                                       stacks * (perStack + 2) : stacks * perStack) / 100.0 + 1;
                        if (chr.cSkills.getSkillLevel(Hero.AdvancedComboAttackBossRush) > 0) {
                            b_bossDamR += 2 * stacks / 100.0;
                        }
                    }
                    else if (chr.cSkills.getSkillLevel(Hero.ComboSynergy) > 0) {
                        Skill sk = SkillFactory.getSkill(Hero.ComboSynergy);
                        int slv = chr.cSkills.getSkillLevel(Hero.ComboSynergy);
                        int perStack = sk.getDamR(slv);
                        b_finalDmg *= stacks * perStack;
                    }
                }
                if(chr.registeredBuffs.get(Hero.Rage) != null) {
                    Skill sk = SkillFactory.getSkill(Hero.Rage);
                    int slv = chr.cSkills.getSkillLevel(Hero.Rage);
                    b_damAbsorbR += sk.getX(slv);
                }
                if(chr.registeredBuffs.get(Hero.WeaponBooster) != null) {
                    b_maxCrit += 20;
                }
                if(chr.registeredBuffs.get(Hero.Enrage) != null) {
                    Skill sk = SkillFactory.getSkill(Hero.Enrage);
                    int slv = chr.cSkills.getSkillLevel(Hero.Enrage);
                    b_finalDmg *= sk.getX(slv) / 100.0 + 1;
                    b_minCrit += sk.getY(slv);
                }
                if(chr.registeredBuffs.get(Hero.CryValhalla) != null) {
                    b_watk += 50;
                }
                break;
            case Page:
            case WhiteKnight:
            case Paladin:
                if(chr.registeredBuffs.get(Paladin.ElementalCharge) != null) {
                    int stacks = chr.registeredBuffs.get(Paladin.ElementalCharge).getStatValue();
                    if (chr.cSkills.getSkillLevel(Paladin.AdvancedCharge) > 0) {
                        b_damR += 5 * stacks / 100.0;
                        b_watk += 12 * stacks;
                    } else {
                        b_watk += 8 * stacks;
                    }
                    b_asrR += 2 * stacks;
                }
                if(chr.registeredBuffs.get(Paladin.DivineShield) != null &&
                    chr.registeredBuffs.get(Paladin.DivineShield).getStatValue() > 0) {
                    b_watk += 2 * chr.cSkills.getSkillLevel(Paladin.DivineShield);
                }
                if(chr.registeredBuffs.get(Paladin.ParashockGuard) != null) {
                    b_watk += 2 * chr.cSkills.getSkillLevel(Paladin.ParashockGuard);
                }
                if(chr.registeredBuffs.get(Paladin.Blast) != null) {
                    Skill sk = SkillFactory.getSkill(Paladin.Blast);
                    int slv = chr.cSkills.getSkillLevel(Paladin.Blast);
                    b_ied *= 1 - sk.getIed(slv) / 100.0;
                    b_finalDmg *= sk.getDamR(slv) / 100.0 + 1;
                    b_critRate+= sk.getCr(slv) + chr.cSkills.getSkillLevel(Paladin.BlastCriticalChance) > 0 ? 20 : 0;
                }
                if(chr.registeredBuffs.get(Paladin.ElementalForce) != null) {
                    b_watkR += chr.cSkills.getSkillLevel(Paladin.ElementalForce);
                }
                break;
        }
        if (chr.registeredBuffs.get(Hero.Rage) != null) {
            b_watk += chr.registeredBuffs.get(Hero.Rage).getStatValue();
        }
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

    private int getTotalStr() {
        return str + e_str + p_str + b_str;
    }

    private int getTotalDex() {
        return dex + e_dex + p_dex + b_dex;
    }

    private int getTotalInt() {
        return int_ + e_int + p_int + b_int;
    }

    private int getTotalLuk() {
        return luk + e_luk + p_luk + b_luk;
    }

    private int getTotalWatk() {
        return (int) ((e_watk + p_watk + b_watk) * (e_watkR + p_watkR + b_watkR + 100) / 100.0);
    }

    private int getTotalMatk() {
        return (int) ((e_matk + p_matk + b_matk) * (e_matkR + p_matkR + b_matkR + 100) / 100.0);
    }

    public int getTotalHp() {
        return (maxhp + e_hp + p_hp + b_hp) * (e_hpR + p_hpR + b_hpR);
    }

    public int getCritRate() {
        return 5 + p_critRate + b_critRate;
    }

    public int getMinCrit() {
        int finMinCrit = 20 + p_minCrit + b_minCrit;
        int finMaxCrit = 50 + p_maxCrit + b_maxCrit;
        return Math.min(finMinCrit, finMaxCrit);
    }

    public int getMaxCrit() {
        int finMinCrit = 20 + p_minCrit + b_minCrit;
        int finMaxCrit = 50 + p_maxCrit + b_maxCrit;
        return Math.max(finMinCrit, finMaxCrit);
    }

    public int getMastery() {
        return mastery + 20;
    }


    public boolean drainMp(int drain) {
        if(mp >= drain) {
            mp -= drain;
            return true;
        }
        return false;
    }

    public int getCurHp() {
        return hp;
    }

    public int getCurMp() {
        return mp;
    }

    public int getMaxHp() {
        return hp;
    }

    public int getMaxMp() {
        return mp;
    }
}
