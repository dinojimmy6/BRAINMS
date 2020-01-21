package game.skill;

import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import game.status.MobStatus;
import game.status.StackableDOT;
import packet.CWvsContext;
import server.ChannelServer;
import utils.Randomizer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Paladin {
    public static final int WeaponMastery = 1200000;
    public static final int FinalAttack = 1200002;
    public static final int PhysicalTraining = 1200009;
    public static final int ElementalCharge = 1200014;
    public static final int Booster = 1201004;
    public static final int FlameCharge = 1201011;
    public static final int BlizzardCharge = 1201012;
    public static final int CloseCombat = 1201013;

    public static final int ShieldMastery = 1210001;
    public static final int Achilles = 1210015;
    public static final int LightningCharge = 1211008;
    public static final int DivineShield = 1210016;
    public static final int HpRecovery = 1211010;
    public static final int CombatOrders = 1211011;
    public static final int Rush = 1211012;
    public static final int Threaten = 1211013;
    public static final int ParashockGuard = 1211014;


    public static final int AdvancedCharge = 1220010;
    public static final int PowerStance = 1220017;
    public static final int HighPaladin = 1220018;
    public static final int MapleWarrior = 1221000;
    public static final int DivineCharge = 1221004;
    public static final int Blast = 1221009;
    public static final int HeavensHammer = 1221011;
    public static final int HerosWill = 1221012;
    public static final int MagicCrash = 1221014;
    public static final int ElementalForce = 1221015;
    public static final int Guardian = 1221016;

    public static final int ThreatenPersist = 1220043;
    public static final int ThreatenOpportunity = 1220044;
    public static final int ThreatenReinforce = 1220045;
    public static final int BlastReinforce = 1220046;
    public static final int BlastCriticalChance = 1220047;
    public static final int BlastExtraStrike = 1220048;
    public static final int HeavensHammerReinforce = 1220049;
    public static final int HeavensHammerExtraStrike = 1220050;
    public static final int HeavensHammerCooldownCutter = 1220051;

    public static final int SmiteShield = 1221052;
    public static final int EpicAdventure = 1221053;
    public static final int Sacrosanctity = 1221054;

    public static final Integer[] job2 = {WeaponMastery, FinalAttack, PhysicalTraining, ElementalCharge, Booster,
                                          FlameCharge, BlizzardCharge, CloseCombat};
    public static final Integer[] job3 = {ShieldMastery, Achilles, LightningCharge, DivineShield, HpRecovery, CombatOrders,
                                          Rush, Threaten, ParashockGuard};
    public static final Integer[] job4 = {AdvancedCharge, PowerStance, HighPaladin, MapleWarrior, HerosWill, DivineCharge, Blast,
                                          HeavensHammer, MagicCrash, ElementalForce, Guardian};
    public static final Integer[] hypers = {ThreatenPersist, ThreatenOpportunity, ThreatenReinforce, BlastReinforce,
                                            BlastCriticalChance, BlastExtraStrike, HeavensHammerReinforce,
                                            HeavensHammerExtraStrike, HeavensHammerCooldownCutter, SmiteShield,
                                            EpicAdventure, Sacrosanctity};

    @SkillHandler(skillId = Booster)
    public static class Booster extends BuffSkillHandler {
        public Booster(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(Booster, slv, chrFrom, chrTo);
            Skill sk = SkillFactory.getSkill(Booster);
            buffStats.put(BuffStat.Booster, Collections.singletonList(sk.getX(slv)));
            registerEffect();
            giveBuff();
        }
    }

    @SkillHandler(skillId = Blast)
    public static class Blast extends BuffSkillHandler {
        public Blast(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(Blast, slv, chrFrom, chrTo);
            Map<Integer, BuffSkillHandler> buffs = this.chrTo.registeredBuffs;
            if(!buffs.containsKey(Blast) && buffs.containsKey(ElementalCharge) && buffs.get(ElementalCharge).getStatValue() == 5) {
                buffs.get(ElementalCharge).cancelSchedule();
                buffs.get(ElementalCharge).cancelBuff();
                Skill sk = SkillFactory.getSkill(ElementalCharge);
                buffStats.put(BuffStat.IndieCr, Collections.singletonList(sk.getCr(slv)));
                buffStats.put(BuffStat.IndiePMdR, Collections.singletonList(sk.getDamR(slv)));
                buffStats.put(BuffStat.IndieIgnoreMobpdpR, Collections.singletonList(sk.getIed(slv)));
                registerEffect();
                giveBuff();
            }
        }
    }

    @SkillHandler(skillId = ElementalForce)
    public static class ElementalForce extends BuffSkillHandler {
        public ElementalForce(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(ElementalForce, slv, chrFrom, chrTo);
            Skill sk = SkillFactory.getSkill(ElementalForce);
            buffStats.put(BuffStat.IndiePADR, Collections.singletonList(sk.getDamR(slv)));
            buffStats.put(BuffStat.EVA, Collections.singletonList(1));
            registerEffect();
            giveBuff();
        }
    }

    @SkillHandler(skillId = Sacrosanctity)
    public static class Sacrosanctity extends BuffSkillHandler {
        public Sacrosanctity(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(Sacrosanctity, slv, chrFrom, chrTo);
            buffStats.put(BuffStat.NotDamaged, Collections.singletonList(1));
            registerEffect();
            giveBuff();
            showBuffAll();
            //chrTo.giveCooldown(Sacrosanctity, mse.getCooldown(chrTo));
        }

        @Override
        public boolean hasVisibleEffect() {
            return true;
        }
    }

    @SkillHandler(skillId = ElementalCharge, preregister = true)
    public static class ElementalCharge extends BuffSkillHandler {
        public ElementalCharge(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(ElementalCharge, slv, chrFrom, chrTo);
            buffDuration = 0;
            getChrTo().registerBuff(this);
        }

        @Override
        public void giveOnSkillDamage(int skillId) {
            cancelSchedule();
            buffDuration = 30000;
            setStatValue(getStatValue() < 5 ? getStatValue() + 1 : getStatValue());
            int atk;
            int damR;
            if(chrTo.cSkills.getSkillLevel(AdvancedCharge) > 0) {
                atk = 12;
                damR = 5 * getStatValue();
            }
            else {
                atk = 8;
                damR = 0;
            }
            buffStats.put(BuffStat.ElementalCharge,
                          Arrays.asList(damR, getStatValue(), atk * getStatValue(), 2 * getStatValue(), 2 * getStatValue()));

            giveBuff();
            updateCharacterStats();
            schedule = ChannelServer.timers[channel].schedule(this, buffDuration);
        }

        @Override
        public void cancelBuff() {
            setStatValue(0);
            chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), getChrTo()));
            updateCharacterStats();
        }
    }

//    @SkillHandler(skillId = CombatOrders)
//    public static class CombatOrders extends BuffSkillHandler {
//        public CombatOrders(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(CombatOrders, slv, map, chrFrom, chrTo);
//            Skill sk = SkillFactory.getSkill(CombatOrders);
//            buffStats.put(BuffStat.CombatOrders, Collections.singletonList(sk.getX(slv)));
//            chrTo.stats.combatOrders = sk.getX(slv);
//            registerEffect();
//            giveBuff();
//            setStatValue(sk.getX(slv));
//            if(chrFrom == chrTo) {
//                List<MapleCharacter> chrsToPartyBuff = getChrsToPartyBuff();
//                for (MapleCharacter chrToPartyBuff : chrsToPartyBuff) {
//                    new CombatOrders(slv, map, chrFrom, chrToPartyBuff);
//                    chrToPartyBuff.getClient().getSession().write(CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, CombatOrders, slv,false));
//                    chrToPartyBuff.getMap().broadcastMessage(chrToPartyBuff, CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, CombatOrders, slv, true), false);
//                }
//            }
//        }
//
//        @Override
//        public void cancelBuff() {
//            chrTo.unregisterBuff(this);
//            chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), chrTo));
//            map.broadcastMessage(chrTo, CWvsContext.cancelForeignBuff(chrTo.id, buffStats), false);
//            chrTo.getStat().combatOrders = 0;
//            chrTo.getStat().recalcLocalStats(chrTo);
//            cancelLock.writeLock().unlock();
//        }
//    }

    @SkillHandler(skillId = ParashockGuard)
    public static class ParashockGuard extends BuffSkillHandler {
        private int slv;
        public ParashockGuard(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(ParashockGuard, slv, chrFrom, chrTo);
            this.slv = slv;
            buffStats.put(BuffStat.KnightsAura, Arrays.asList(1, 1));
            if(chrFrom == chrTo && chrFrom.getParty() == null) {
                buffStats.put(BuffStat.IndiePAD, Collections.singletonList(skill.getPad(slv)));
                buffStats.put(BuffStat.IndiePDDR, Collections.singletonList(skill.getPdR(slv)));
                registerEffect();
                giveBuff();
                showBuffAll();
            }
            else if(chrFrom == chrTo) {
                buffStats.put(BuffStat.IndiePAD, Collections.singletonList(skill.getPad(slv)));

//                for (PartyCharacter partyChr : chrFrom.getParty().getMembers()) {
//                    MapleCharacter chrToPartyBuff = chrFrom.getMap().getCharacterById(partyChr.getId());
//                    if(chrToPartyBuff != null  && chrToPartyBuff.getJob() / 10 != 12) {
//                        new ParashockGuard(slv, map, chrFrom, chrToPartyBuff);
//                        chrToPartyBuff.getMap().broadcastMessage(chrToPartyBuff, CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, ParashockGuard, slv, true), true);
//                    }
//                }
                registerEffect();
                giveBuff();
                showBuffAll();
            }
            else if(chrTo.getJobId() / 10 != 12){
                schedule = ChannelServer.timers[chrTo.getChannel()].schedule(this, 45000);
                buffStats.put(BuffStat.DamageReduce, Collections.singletonList(skill.getZ(slv)));
                registerEffect();
                giveBuff();
                showBuffAll();
            }
        }

        @Override
        public void run() {
            reapplyBuff();
        }

        public void reapplyBuff() {
//            if(chrFrom != chrTo) {
//                if (chrTo.getParty() != null &&
//                    chrTo.getParty().getMemberById(chrFrom.getId()) != null &&
//                    chrTo.getMap().getCharacterById(chrFrom.getId()) != null) {//source of buff is still in party and in map
//                    schedule = Timer.BuffTimer.getInstance().schedule(this, 45000);
//                } else {
//                    cancelBuff();
//                }
//            }
        }

        @Override
        public boolean hasVisibleEffect() {
            return true;
        }

        @Override
        public void cancelBuff() {
            chrTo.unregisterBuff(this);
            chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), chrTo));
            chrTo.broadcastMessage(CWvsContext.cancelForeignBuff(chrTo.getId(), buffStats));
            updateCharacterStats();
            if(chrFrom == chrTo && chrFrom.getParty() != null) {
//                for (PartyCharacter partyChr : chrFrom.getParty().getMembers()) {
//                    MapleCharacter chrToPartyBuff = chrFrom.getMap().getCharacterById(partyChr.getId());
//                    if(chrToPartyBuff != null  && chrToPartyBuff.getJob() / 10 != 12) {
//                        BuffSkillHandler bsh = chrToPartyBuff.getRegisteredBuffs().get(ParashockGuard);
//                        if(bsh!= null) {
//                            bsh.cancelBuff();
//                        }
//                    }
//                }
            }
        }
    }

//    @SkillHandler(skillId = DivineShield, preregister = true)
//    public static class DivineShield extends BuffSkillHandler {
//        public DivineShield(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(DivineShield, slv, chrFrom, chrTo);
//            setStatValue(-1);
//            chrTo.registerBuff(this);
//        }
//
//        @Override
//        public void giveOnTakeDamage(int damage, int monsterId) {
//            BuffSkillHandler bsh = getChrTo().registeredBuffs.get(DivineShield);
//            if(bsh == null) {
//                return;
//            }
//            int hitsLeft = bsh.getStatValue();
//            if (hitsLeft != -1) {
//                if (hitsLeft > 1) {
//                    bsh.setStatValue(hitsLeft - 1);
//                } else {
//                    //cancel divine shield and unregister
//                    if (bsh.cancelSchedule()) {
//                        bsh.cancelBuff();
//                    }
//                }
//            } else {
//                //check cd and apply the buff if cd is up
//                if (!bsh.getChrTo().skillIsCooling(DivineShield)) {
//                    bsh.setStatValue(bsh.getChrTo().getTotalSkillLevel(DivineShield));
//                    buffStats.put(BuffStat.BlessingArmor, Collections.singletonList(mse.info.get(MapleStatInfo.y)));
//                    buffStats.put(BuffStat.BlessingArmorIncPAD, Collections.singletonList(mse.info.get(MapleStatInfo.epad)));
//                    schedule = ChannelServer.timers[chrTo.channel].schedule(this, buffDuration);
//                    giveBuff();
//                }
//            }
//        }
//
//        @Override
//        public void cancelBuff() {
//            setStatValue(-1);
//            chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), getChrTo()));
//            chrTo.addCooldown(DivineShield, System.currentTimeMillis(), 30000);
//        }
//    }
//
//    @SkillHandler(skillId = HpRecovery)
//    public static class HpRecovery extends BuffSkillHandler {
//        public HpRecovery(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(HpRecovery, slv, chrFrom, chrTo);
//            if(chrTo.registeredBuffs.get(HpRecovery) == null) {
//                setStatValue(10);
//            }
//            else {
//                int maxRestStack = skill.getX(slv);
//                int curStack = chrTo.registeredBuffs.get(HpRecovery).getStatValue();
//                if(curStack < maxRestStack) {
//                    int added = curStack + 10;
//                    setStatValue(added > maxRestStack ? maxRestStack : added);
//                }
//                else {
//                    setStatValue(maxRestStack);
//                }
//            }
//            buffStats.put(BuffStat.Restoration, Collections.singletonList(getStatValue()));
//            registerEffect();
//            giveBuff();
//            chrTo.addHP((int) ((mse.info.get(MapleStatInfo.x) + 10 - getStatValue()) / 100.0 * chrTo.getStat().getCurrentMaxHp()));
//        }
//    }
//
//    @SkillHandler(skillId = Guardian)
//    public static class Guardian extends BuffSkillHandler {
//        public Guardian(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(Guardian, slv, chrFrom, chrTo);
//            buffDuration = buffDuration * 2;
//            buffStats.put(MapleBuffStat.SpiritGuard, Collections.singletonList(1));
//            buffStats.put(MapleBuffStat.NotDamaged, Collections.singletonList(1));
//
//            if(chrFrom == chrTo) {
//                chrTo.giveCooldown(Guardian, mse.getCooldown(chrTo));
//                chrTo.getClient().getSession().write(CWvsContext.enableActions());
//                MapleCharacter toRevive = getClosestDeadMember();
//                if(toRevive != null) {
//                    new Guardian(slv, chrFrom, toRevive);
//                    toRevive.getMap().broadcastMessage(toRevive, CField.EffectPacket.showPartyBuffEffect(toRevive, Guardian, slv, true), true);
//                    registerEffect();
//                    giveBuff();
//                }
//            }
//            else {
//                final Map<MapleStat, Long> hpUpdate = new EnumMap<>(MapleStat.class);
//                chrTo.getStat().setHp(chrTo.getStat().getCurrentMaxHp(), chrTo);
//                hpUpdate.put(MapleStat.HP, Long.valueOf(chrTo.getStat().getHp()));
//                chrTo.getClient().getSession().write(CWvsContext.updatePlayerStats(hpUpdate, true, chrTo));
//                registerEffect();
//                giveBuff();
//            }
//        }
//
//        private MapleCharacter getClosestDeadMember() {
//            final Rectangle bounds = mse.calculateBoundingBox(chrFrom.getTruePosition(), chrFrom.isFacingLeft());
//            final List<MapleMapObject> affecteds = chrFrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
//            MapleCharacter closest = null;
//            double closestDistance = 99999999999.0;
//            for (final MapleMapObject affectedObj : affecteds) {
//                final MapleCharacter affected = (MapleCharacter) affectedObj;
//                if (!affected.isAlive() && affected != chrFrom && (chrFrom.getParty() != null && affected.getParty() != null && chrFrom.getParty().getId() == affected.getParty().getId())) {
//                    double distance = affected.getPosition().distanceSq(chrFrom.getPosition());
//                    if(distance < closestDistance) {
//                        closestDistance = distance;
//                        closest = affected;
//                    }
//                }
//            }
//            return closest;
//        }
//    }
//
//    @SkillHandler(skillId = HerosWill)
//    public static class HerosWill extends BuffSkillHandler {
//        public HerosWill(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(HerosWill, slv, chrFrom, chrTo);
//            chrTo.dispelDebuffs(1);
//            chrTo.giveCooldown(HerosWill, mse.getCooldown(chrFrom));
//            chrTo.getClient().getSession().write(CWvsContext.enableActions());
//        }
//    }
//
//    @SkillHandler(skillId = MapleWarrior)
//    public static class MapleWarrior extends BuffSkillHandler {
//        public MapleWarrior(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(MapleWarrior, slv, chrFrom, chrTo);
//            key = CommonClass.MapleWarrior;
//            buffStats.put(MapleBuffStat.BasicStatUp, Collections.singletonList(mse.getX()));
//            registerEffect();
//            giveBuff();
//            chrTo.getClient().getSession().write(CWvsContext.enableActions());
//            if(chrFrom == chrTo) {
//                List<MapleCharacter> chrsToPartyBuff = getChrsToPartyBuff();
//                for (MapleCharacter chrToPartyBuff : chrsToPartyBuff) {
//                    new Bowmaster.MapleWarrior(slv, chrFrom, chrToPartyBuff);
//                    chrToPartyBuff.getClient().getSession().write(CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, MapleWarrior, slv,false));
//                    chrToPartyBuff.getMap().broadcastMessage(chrToPartyBuff, CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, MapleWarrior, slv, true), false);
//                }
//            }
//        }
//    }
//
//    @SkillHandler(skillId = EpicAdventure)
//    public static class EpicAdventure extends BuffSkillHandler {
//        public EpicAdventure(int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(EpicAdventure, slv, chrFrom, chrTo);
//            buffStats.put(BuffStat.IndiePADR, Collections.singletonList(10));
//            buffStats.put(BuffStat.IndieMADR, Collections.singletonList(10));
//            registerEffect();
//            giveBuff();
//            if (chrFrom == chrTo) {
//                chrTo.giveCooldown(EpicAdventure, mse.getCooldown(chrTo));
//                List<MapleCharacter> chrsToPartyBuff = getChrsToPartyBuff();
//                for (MapleCharacter chrToPartyBuff : chrsToPartyBuff) {
//                    if (chrToPartyBuff.getJob() < 600) {
//                        new EpicAdventure(slv, chrFrom, chrToPartyBuff);
//                        chrToPartyBuff.getClient().getSession().write(CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, EpicAdventure, slv, false));
//                        chrToPartyBuff.getMap().broadcastMessage(chrToPartyBuff, CField.EffectPacket.showPartyBuffEffect(chrToPartyBuff, EpicAdventure, slv, true), false);
//
//                    }
//                }
//            }
//        }
//    }
//
//    @SkillHandler(skillIds = {Hero.SlashBlast, CloseCombat, Rush}, skillType = 1)
//    public static class OtherAttacks extends AttackSkillHandler {
//        public OtherAttacks(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(skillId, slv, chrFrom);
//        }
//
//        @Override
//        public void onAttackPerMob(Monster monster) {
//            if(monster != null) {
//                if (Randomizer.isSuccess(80)) {
//                    int weaponType = GameConstants.getWeaponTypeInt(chrFrom.getEquips(false).get((byte) -11));
//                    chrFrom.getClient().getSession().write(CField.finalAttack(skillId, FinalAttack, weaponType, monster.getObjectId()));
//                }
//            }
//        }
//    }
//
//    @SkillHandler(skillIds = HeavensHammer, skillType = 1)
//    public static class HeavensHammer extends AttackSkillHandler {
//        public HeavensHammer(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(skillId, slv, chrFrom);
//            chrTo.giveCooldown(HeavensHammer, chrFrom.getTotalSkillLevel(HeavensHammerCooldownCutter) > 0 ?
//                    mse.getCooldown(chrFrom) / 6 : mse.getCooldown(chrFrom) / 3);
//        }
//    }
//
//    @SkillHandler(skillIds = SmiteShield, skillType = 1)
//    public static class SmiteShield extends AttackSkillHandler {
//        public SmiteShield(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(SmiteShield, slv, chrFrom);
//            chrTo.giveCooldown(SmiteShield, mse.getCooldown(chrFrom));
//        }
//    }
//
    public static class Charge extends AttackSkillHandler {
        public Charge(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(skillId, slv, chrFrom);
        }

        @Override
        public void onAttackHit() {
            BuffSkillHandler bsh = chrFrom.registeredBuffs.get(ElementalCharge);
            if(bsh != null) {
                bsh.giveOnSkillDamage(skillId);
            }
        }

        @Override
        public void onAttackPerMob(Monster monster) {
            //if(monster != null) {
            //    if (Randomizer.isSuccess(80)) {
            //        int weaponType = GameConstants.getWeaponTypeInt(chrFrom.getEquips(false).get((byte) -11));
            //        chrFrom.getClient().getSession().write(CField.finalAttack(skillId, FinalAttack, weaponType, monster.getObjectId()));
            //    }
            //}
        }
    }

    @SkillHandler(skillId = FlameCharge, skillType = 1)
    public static class FlameCharge extends Charge {
        public FlameCharge(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(skillId, slv, chrFrom, chrTo);
        }

        @Override
        public void getExpectedDamage(AttackInfo ai) {
            int bonusSkillDmg = 0;
            int combatOrdersLvl = chrFrom.cSkills.getSkillLevel(CombatOrders);
            if(combatOrdersLvl > 0) {
                bonusSkillDmg += SkillFactory.getSkill(CombatOrders).getDamR(combatOrdersLvl);
            }
            int elementalForceLvl = chrFrom.cSkills.getSkillLevel(ElementalForce);
            if(elementalForceLvl > 0) {
                bonusSkillDmg += SkillFactory.getSkill(ElementalForce).getX(elementalForceLvl);
            }
            ai.printExpectedDamage(chrFrom, skill.getDamage(slv) + bonusSkillDmg, null);
        }

        @Override
        public void onAttackPerMob(Monster monster) {
            super.onAttackPerMob(monster);
            MobStatus status = new StackableDOT(monster, FlameCharge, slv, chrFrom);
            status.applyStatus(null);
        }
    }

    @SkillHandler(skillId = BlizzardCharge, skillType = 1)
    public static class BlizzardCharge extends Charge {
        public BlizzardCharge(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(skillId, slv, chrFrom, chrTo);
        }

        @Override
        public void getExpectedDamage(AttackInfo ai) {
            ArrayList<AttackInfo.MonsterDamageMods> mods = new ArrayList<>();
            for(AttackInfo.MonsterDamage md : ai.monsters) {
                Monster monster = chrFrom.getMap().getMonster(md.oid);
                AttackInfo.MonsterDamageMods mod = new AttackInfo.MonsterDamageMods();
                if(!monster.getDots(chrFrom).isEmpty()) {
                    mod.bonusTD += skill.getZ(slv) - 100;
                }
                mods.add(mod);
                int bonusSkillDmg = 0;
                int combatOrdersLvl = chrFrom.cSkills.getSkillLevel(CombatOrders);
                if(combatOrdersLvl > 0) {
                    bonusSkillDmg += SkillFactory.getSkill(CombatOrders).getDamR(combatOrdersLvl);
                }
                int elementalForceLvl = chrFrom.cSkills.getSkillLevel(ElementalForce);
                if(elementalForceLvl > 0) {
                    bonusSkillDmg += SkillFactory.getSkill(ElementalForce).getX(elementalForceLvl);
                }
                ai.printExpectedDamage(chrFrom, skill.getDamage(slv) + bonusSkillDmg, mods);
            }
        }
    }

    @SkillHandler(skillId = LightningCharge, skillType = 1)
    public static class LightningCharge extends Charge {
        public LightningCharge(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(skillId, slv, chrFrom, chrTo);
        }

        @Override
        public void getExpectedDamage(AttackInfo ai) {
            ArrayList<AttackInfo.MonsterDamageMods> mods = new ArrayList<>();
            for(AttackInfo.MonsterDamage md : ai.monsters) {
                Monster monster = chrFrom.getMap().getMonster(md.oid);
                AttackInfo.MonsterDamageMods mod = new AttackInfo.MonsterDamageMods();
                //if(monster.getStatus(MonsterStatus.SPEED, chr) != null) {
                //    mod.bonusTD += skill.getZ(slv) - 100;
                //}
                mods.add(mod);
                int bonusSkillDmg = 0;
                int elementalForceLvl = chrFrom.cSkills.getSkillLevel(ElementalForce);
                if(elementalForceLvl > 0) {
                    bonusSkillDmg += SkillFactory.getSkill(ElementalForce).getX(elementalForceLvl);
                }
                ai.printExpectedDamage(chrFrom, skill.getDamage(slv) + bonusSkillDmg, mods);
            }
        }
    }

    @SkillHandler(skillId = DivineCharge, skillType = 1)
    public static class DivineCharge extends Charge {
        public DivineCharge(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
            super(skillId, slv, chrFrom, chrTo);
        }

        @Override
        public void getExpectedDamage(AttackInfo ai) {
            ArrayList<AttackInfo.MonsterDamageMods> mods = new ArrayList<>();
            for(AttackInfo.MonsterDamage md : ai.monsters) {
                Monster monster = chrFrom.getMap().getMonster(md.oid);
                AttackInfo.MonsterDamageMods mod = new AttackInfo.MonsterDamageMods();
                //if(monster.getStatus(MonsterStatus.STUN, chr) != null) {
                //    mod.bonusTD += skill.getZ(slv) - 100;
                //}
                mods.add(mod);
                ai.printExpectedDamage(chrFrom, skill.getDamage(slv), mods);
            }
        }
    }


//
//    @SkillHandler(skillId = Blast, skillType = 1)
//    public static class BlastAttack extends AttackSkillHandler {
//        public BlastAttack(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
//            super(Blast, slv, chrFrom);
//        }
//
//        @Override
//        public void afterAttack() {
//            SkillManager.manageSpecialMove(Blast, mse.getLevel(), chrFrom, chrFrom);
//        }
//
//        @Override
//        public void onAttackPerMob(Monster monster) {
//            if(monster != null) {
//                if (Randomizer.isSuccess(80)) {
//                    int weaponType = GameConstants.getWeaponTypeInt(chrFrom.getEquips(false).get((byte) -11));
//                    chrFrom.getClient().getSession().write(CField.finalAttack(skillId, FinalAttack, weaponType, monster.getObjectId()));
//                }
//            }
//        }
//    }
}
