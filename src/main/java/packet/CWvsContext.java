package packet;

import game.character.MapleCharacter;
import game.skill.BuffSkillHandler;
import game.skill.BuffStat;
import game.skill.Paladin;
import utils.Logging;
import utils.data.PacketWriter;
import constants.Flags.Stat;
import world.Party;
import world.PartyCharacter;
import world.PartyOperation;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CWvsContext {
    public static byte[] sendMessage(int type, String message) {
        return sendMessage(type, 0, message, false);
    }

    private static byte[] sendMessage(int type, int channel, String message, boolean megaEar) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SERVER_MESSAGE.getValue());
        pw.write(type);
        if (type == 4) {
            pw.write(1);
        }
        if ((type != 23) && (type != 24)) {
            pw.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                pw.write(channel - 1);
                pw.write(megaEar ? 1 : 0);
                break;
            case 9:
                pw.write(channel - 1);
                break;
            case 12:
                pw.writeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                pw.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            case 24:
                pw.writeShort(0);
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
        }
        return pw.getPacket();
    }

    public static byte[] updateSkills(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
        pw.write(0);
        pw.write(0);
        pw.write(0);
        chr.cSkills.writeUpdateSkills(pw);
        return pw.getPacket();
    }

    public static byte[] updateStats(Map<Stat, Long> stats, MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        pw.write(1);
        long mask = 0L;
        for (Stat statUpdate : stats.keySet()) {
            mask |= statUpdate.getValue();
        }
        pw.writeLong(mask);
        for (final Map.Entry<Stat, Long> statUpdate : stats.entrySet()) {
            switch (statUpdate.getKey()) {
                case SKIN:
                case LEVEL:
                case FATIGUE:
                    pw.write((statUpdate.getValue()).byteValue());
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                    pw.writeShort((statUpdate.getValue()).shortValue());
                    break;
                case JOB:
                    pw.writeShort((statUpdate.getValue()).shortValue());
                    pw.writeShort(0);
                    break;
                case AVAILABLESP:
//                    if (GameConstants.isSeparatedSp(chr.getJob())) {
                        chr.cSkills.writeUpdateSp(pw);
//                    } else {
//                        pw.writeShort(5);
//                    }
                    break;
                case EXP:
                case MESO:
                    pw.writeLong((statUpdate.getValue()).longValue());
                    pw.write(-1);
                    pw.writeInt(0);
                    break;
                case TRAIT_LIMIT:
                    Long statup = stats.get(Stat.CHARISMA);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    statup = stats.get(Stat.INSIGHT);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    statup = stats.get(Stat.WILL);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    statup = stats.get(Stat.CRAFT);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    statup = stats.get(Stat.SENSE);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    statup = stats.get(Stat.CHARM);
                    pw.writeShort(statup != null ? statup.shortValue() : 0);
                    pw.write(0);
                    pw.writeLong(0);
                    break;
                case PET:
                    pw.write(-1);
                    pw.write(0);
                    pw.write(0);
                    pw.writeLong((statUpdate.getValue()).intValue());
                    pw.writeLong((statUpdate.getValue()).intValue());
                    pw.writeLong((statUpdate.getValue()).intValue());
                    break;
                case VIRTUE:
                    pw.writeLong((statUpdate.getValue()).longValue());
                    break;
                case CHARM: // also other trait values?
                    pw.write(statUpdate.getValue().byteValue()); //LOBYTE(nCharmOld) = CInPacket::Decode1(retaddr);
                default:
                    pw.writeInt((statUpdate.getValue()).intValue());
            }
        }
        pw.write(0);
        pw.write(0);
        pw.write(0);
        pw.write(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] setEventNameTag(int[] titles) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_EVENT_NAME_TAG.getValue());
        for (int i = 0; i < 5; i++) {
            pw.writeMapleAsciiString("");
            if (titles.length < i + 1) {
                pw.write(-1);
            } else {
                pw.write(titles[i]);
            }
        }

        return pw.getPacket();
    }
    
    //BuffPackets
    public static byte[] giveBuff(int sourceId, int buffDuration, Map<BuffStat, List<Integer>> buffStats, MapleCharacter chrTo) {
        Set<BuffStat> buffStatsSet = buffStats.keySet();
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());

        writeBuffMask(pw, buffStatsSet);

        int indieCount = 0;
        int dynamicIndex = 0;
        for(BuffStat buffStat : buffStatsSet) {
            if(buffStat.isIndie()) {
                indieCount++;
            }
        }
        for(Map.Entry<BuffStat, List<Integer>> stat : buffStats.entrySet()) {
            if (stat.getKey().isIndie() || stat.getKey().isTwoState() || !stat.getKey().isStandard()) continue;
            if (stat.getKey().isEnDecode4Byte()) {
                pw.writeInt(stat.getValue().get(0));
            }
            else {
                pw.writeShort(stat.getValue().get(0));
            }
            pw.writeInt(sourceId);
            pw.writeInt(buffDuration);

            if (stat.getKey() == BuffStat.SoulMP) {
                pw.writeInt(stat.getValue().get(1)); // xSoulMP
                pw.writeInt(stat.getValue().get(2)); // rSoulMP
            }

            if (stat.getKey() == BuffStat.FullSoulMP) {
                pw.writeInt(stat.getValue().get(1)); // xFullSoulMP
            }
        }

        pw.writeShort(0); // the size of the following structure.

        pw.write(0); // nDefenseAtt
        pw.write(0); // nDefenseState
        pw.write(0); // nPVPDamage

        for (Map.Entry<BuffStat, List<Integer>> stat : buffStats.entrySet()) {
            if (stat.getKey() == BuffStat.Dice) {
                for(Integer j : stat.getValue()) {
                    pw.writeInt(j);
                }
            }
            if (stat.getKey() == BuffStat.KillingPoint) {
                pw.write(stat.getValue().get(1)); // nKillingPoint
            }

            if (stat.getKey() == BuffStat.PinkbeanRollingGrade) {
                pw.write(0); // nPinkbeanRollingGrade
            }

            if (stat.getKey() == BuffStat.Judgement) {
                pw.writeInt(1); // xJudgement
            }

            if (stat.getKey() == BuffStat.StackBuff) {
                pw.write(stat.getValue().get(1)); // mStackBuff
            }

            if (stat.getKey() == BuffStat.Trinity) {
                pw.write(0); // mTrinity
            }

            if (stat.getKey() == BuffStat.ElementalCharge) {
                pw.write(stat.getValue().get(1)); // mElementalCharge
                pw.writeShort(stat.getValue().get(2)); // wElementalCharge
                pw.write(stat.getValue().get(3)); // uElementalCharge
                pw.write(stat.getValue().get(4)); // zElementalCharge
            }

            if (stat.getKey() == BuffStat.LifeTidal) {
                pw.writeInt(stat.getValue().get(1)); // mLifeTidal
            }

            if (stat.getKey() == BuffStat.AntiMagicShell) {
                pw.write(stat.getValue().get(1)); // bAntiMagicShell
            }

            if (stat.getKey() == BuffStat.Larkness) {

                for(int i = 0; i < 2; i++) {

                    // LarknessInfo::Decode
                    pw.writeInt(0);
                    pw.writeInt(0);

                }

                pw.writeInt(0); // dgLarkness
                pw.writeInt(0); // lgLarkness
            }

            if (stat.getKey() == BuffStat.IgnoreTargetDEF) {
                pw.writeInt(0); // mIgnoreTargetDEF
            }

            if (stat.getKey() == BuffStat.StopForceAtomInfo) {

                // StopForceAtom::Decode
                pw.writeInt(0); // nIdx
                pw.writeInt(0); // nCount
                pw.writeInt(0); // nWeaponID

                pw.writeInt(0); // the size of the structure below.
                // pw.writeInt(0);

            }

            if (stat.getKey() == BuffStat.SmashStack) {
                pw.writeInt(0); // xSmashStack
            }

            if (stat.getKey() == BuffStat.MobZoneState) {
                pw.writeInt(0); // ?
            }

            if (stat.getKey() == BuffStat.Slow) {
                pw.write(0); // bSlowIgnoreMoveSkill
            }

            if (stat.getKey() == BuffStat.IceAura) {
                pw.write(1); // bIceAura
            }

            if (stat.getKey() == BuffStat.KnightsAura) {
                pw.write(1); // bKnightsAura
            }

            if (stat.getKey() == BuffStat.IgnoreMobpdpR) {
                pw.write(0); // bIgnoreMobpdpR
            }

            if (stat.getKey() == BuffStat.BDR) {
                pw.write(0); // bBdR
            }

            if (stat.getKey() == BuffStat.DropRIncrease) {
                pw.writeInt(0); // xDropRIncrease
                pw.write(0); // bDropRIncrease
            }

            if (stat.getKey() == BuffStat.PoseType) {
                pw.write(0); // bPoseType
            }

            if (stat.getKey() == BuffStat.Beholder) {
                pw.writeInt(1); // sBeholder
                pw.writeInt(1); // ssBeholder
            }

            if (stat.getKey() == BuffStat.CrossOverChain) {
                pw.writeInt(stat.getValue().get(1)); // xCrossOverChain
            }

            if (stat.getKey() == BuffStat.Reincarnation) {
                pw.writeInt(stat.getValue().get(1)); // xReincarnation
            }

            if (stat.getKey() == BuffStat.ExtremeArchery) {
                pw.writeInt(stat.getValue().get(1)); // bExtremeArchery
                pw.writeInt(stat.getValue().get(1)); // xExtremeArchery
            }

            if (stat.getKey() == BuffStat.QuiverCatridge) {
                pw.writeInt(stat.getValue().get(1)); // xQuiverCatridge
            }

            if (stat.getKey() == BuffStat.ImmuneBarrier) {
                pw.writeInt(0); // xImmuneBarrier
            }

            if (stat.getKey() == BuffStat.ZeroAuraStr) {
                pw.write(0); // bZeroAuraStr
            }

            if (stat.getKey() == BuffStat.ZeroAuraSpd) {
                pw.write(0); // bZeroAuraSpd
            }

            if (stat.getKey() == BuffStat.ArmorPiercing) {
                pw.writeInt(0); // bArmorPiercing
            }

            if (stat.getKey() == BuffStat.SharpEyes) {
                pw.writeInt(5); // mSharpEyes
            }

            if (stat.getKey() == BuffStat.AdvancedBless) {
                pw.writeInt(stat.getValue().get(1)); // xAdvancedBless
            }

            if (stat.getKey() == BuffStat.DotHealHPPerSecond) {
                pw.writeInt(0); // xDotHealHPPerSecond
            }

            if (stat.getKey() == BuffStat.SpiritGuard) {
                pw.writeInt(stat.getValue().get(1)); // nSpiritGuard
            }

            if (stat.getKey() == BuffStat.KnockBack) {
                pw.writeInt(0); // nKnockBack
                pw.writeInt(0); // bKnockBack
            }

            if (stat.getKey() == BuffStat.ShieldAttack) {
                pw.writeInt(0); // xShieldAttack
            }

            if (stat.getKey() == BuffStat.SSFShootingAttack) {
                pw.writeInt(0); // xSSFShootingAttack
            }

            if (stat.getKey() == BuffStat.BMageAura) {
                pw.writeInt(0); // xBMageAura
                pw.write(0); // bBMageAura
            }

            if (stat.getKey() == BuffStat.BattlePvP_Helena_Mark) {
                pw.writeInt(0); // cBattlePvP_Helena_Mark
            }

            if (stat.getKey() == BuffStat.PinkbeanAttackBuff) {
                pw.writeInt(0); // bPinkbeanAttackBuff
            }

            if (stat.getKey() == BuffStat.RoyalGuardState) {
                pw.writeInt(0); // bRoyalGuardState
                pw.writeInt(0); // xRoyalGuardState
            }

            if (stat.getKey() == BuffStat.MichaelSoulLink) {
                pw.writeInt(0); // xMichaelSoulLink
                pw.writeInt(0); // bMichaelSoulLink
                pw.writeInt(0); // cMichaelSoulLink
                pw.writeInt(0); // yMichaelSoulLink
            }

            if (stat.getKey() == BuffStat.AdrenalinBoost) {
                pw.write(0); // cAdrenalinBoost
            }

            if (stat.getKey() == BuffStat.RWCylinder) {
                pw.write(0); // bRWCylinder
                pw.writeShort(0); // cRWCylinder
            }

            if (stat.getKey() == BuffStat.RWMagnumBlow) {
                pw.writeShort(0); // bRWMagnumBlow
                pw.write(0); // xRWMagnumBlow
            }
            if(stat.getKey() == BuffStat.NewFlying) {
                pw.writeInt(0);
                pw.writeInt(0);
            }
        }

//        if(chrTo.registeredBuffs.get(Buccaneer.EnergyCharge) != null) {
//            pw.writeInt(chrTo.registeredBuffs.get(Buccaneer.EnergyCharge).getSecondaryStatValue());
//        }
//        else {
            pw.writeInt(0);
        //}

        for (Map.Entry<BuffStat, List<Integer>> stat : buffStats.entrySet()) {

            if (stat.getKey() == BuffStat.BladeStance) {
                pw.writeInt(0); // xBladeStance
            }

            if (stat.getKey() == BuffStat.DarkSight) {
                pw.writeInt(0); // cDarkSight
            }

            if (stat.getKey() == BuffStat.Stigma) {
                pw.writeInt(0); // bStigma
            }
        }

        for(int i = 0; i < 8; i++) {
            BuffStat stat = BuffStat.getCTSFromTSIndex(i);
            if (buffStats.containsKey(stat)) {
                pw.writeInt(buffStats.get(stat).get(1));
                pw.writeInt(sourceId);
                pw.write(0);
                pw.writeInt(buffDuration);
                if(stat == BuffStat.GuidedBullet) {
                    pw.writeInt(buffStats.get(stat).get(2));
                    pw.writeInt(chrTo.getId());
                }
                else if(stat == BuffStat.PartyBooster) {
                    pw.write(0);
                    pw.writeInt(0);
                    pw.writeShort(buffDuration / 1000);
                }
            }
        }

        for (Map.Entry<BuffStat, List<Integer>> stat : buffStats.entrySet()) {
            if(stat.getKey().isIndie()) {
                LinkedList<BuffSkillHandler> skillList = new LinkedList<>();
                for(BuffSkillHandler bsh : chrTo.registeredBuffs.values()) {
                    if(bsh.getBuffStats().containsKey(stat.getKey())) {
                        skillList.add(bsh);
                    }
                }
                pw.writeInt(skillList.size()); // the size of the array.
                for(BuffSkillHandler bsh : skillList) {
                    pw.writeInt(bsh.getSkillId()); // nReason
                    pw.writeInt(bsh.getBuffStats().get(stat.getKey()).get(0)); // nValue
                    pw.writeInt(150000); // nKey
                    pw.writeInt(6); // tCur - nDuration
                    pw.writeInt(bsh.getBuffDuration()); // tTerm
                    pw.writeInt(0); // size
                    // pw.writeInt(0); // nMValueKey
                    // pw.writeInt(0); // nMValue
                }
            }
        }

        if(buffStats.containsKey(BuffStat.UsingScouter))
            pw.writeInt(0); // nUsingScouter

        pw.writeShort(0);
        pw.write(0);
        pw.write(0); // bJustBuffCheck
        pw.write(0); // bFirstSet

        pw.writeInt(0); // 174.1

        boolean isMovementAffectingStat = buffStats.entrySet().stream()
                .anyMatch(stat -> stat.getKey().isMovementAffectingStat());

        if (isMovementAffectingStat)
            pw.write(0);

        Logging.log(String.format("SkillID: (%s)  Packet: %s%n", sourceId, pw.toString()));
        return pw.getPacket();
    }

    public static byte[] cancelBuff(Set<BuffStat> buffStats, MapleCharacter chrTo) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        writeBuffMask(pw, buffStats);
        for(BuffStat buffStat : buffStats) {
            if(buffStat.isIndie()) {
                LinkedList<BuffSkillHandler> skillList = new LinkedList<>();
                for(BuffSkillHandler bsh : chrTo.registeredBuffs.values()) {
                    if(bsh.getBuffStats().containsKey(buffStat)) {
                        skillList.add(bsh);
                    }
                }
                pw.writeInt(skillList.size()); // the size of the array.
                for(BuffSkillHandler bsh : skillList) {
                    pw.writeInt(bsh.getSkillId()); // nReason
                    pw.writeInt(bsh.getBuffStats().get(buffStat).get(0)); // nValue
                    pw.writeInt(150000); // nKey
                    pw.writeInt(6); // tCur - nDuration
                    pw.writeInt(bsh.getBuffDuration()); // tTerm
                    pw.writeInt(0); // size
                    // pw.writeInt(0); // nMValueKey
                    // pw.writeInt(0); // nMValue
                }
            }
        }
        pw.writeShort(0);
        pw.write(0);
        pw.write(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] cancelForeignBuff(int cid, Map<BuffStat, List<Integer>> buffStats) {
        Set<BuffStat> buffStatsSet = buffStats.keySet();
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        pw.writeInt(cid);
        writeBuffMask(pw, buffStatsSet);
        pw.write(3);
        pw.write(1);
        return pw.getPacket();
    }

    public static void writeBuffMask(PacketWriter pw, Set<BuffStat> stats) {
        int[] mask = new int[17];
        for (BuffStat stat : stats) {
            mask[(stat.getPosition() - 1)] |= stat.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            pw.writeInt(mask[(i - 1)]);
        }
    }

    public static byte[] createParty(int partyId) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(16);
        pw.writeInt(partyId);
        pw.writeInt(999999999);
        pw.writeInt(999999999);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.write(0);
        pw.write(1);
        pw.writeMapleAsciiString("Best party ever!");
        return pw.getPacket();
    }

    public static byte[] partyInvite(MapleCharacter from) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(4);
        pw.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
        pw.writeMapleAsciiString(from.getName());
        pw.writeInt(from.getLevel());
        pw.writeInt(from.getJobId());
        pw.writeInt(0);
        pw.write(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] partyJoinRequest(MapleCharacter from) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(8);
        pw.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
        pw.writeMapleAsciiString(from.getName());
        pw.writeInt(from.getLevel());
        pw.writeInt(from.getJobId());
        pw.writeInt(0);
        return pw.getPacket();
    }

    public static byte[] partyRequestInvite(MapleCharacter from) { // does not seems to exist anywhere
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(7);
        pw.writeInt(from.getId());
        pw.writeMapleAsciiString(from.getName());
        pw.writeInt(from.getLevel());
        pw.writeInt(from.getJobId());

        return pw.getPacket();
    }

    public static byte[] partyStatusMessage(int message, String charname) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(message);
        pw.writeMapleAsciiString(charname);
        return pw.getPacket();
    }

    public static void addPartyStatus(int forchannel, Party party, PacketWriter lew, boolean leaving) {
        addPartyStatus(forchannel, party, lew, leaving, false);
    }

    public static void addPartyStatus(int forchannel, Party party, PacketWriter lew, boolean leaving, boolean exped) {
        List<PartyCharacter> partymembers;
        if (party == null) {
            partymembers = new ArrayList<>();
        } else {
            partymembers = new ArrayList<>(party.getMembers());
        }
        while (partymembers.size() < 6) {
            partymembers.add(new PartyCharacter());
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeAsciiString(partychar.getName(), 13);
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(1); //SubJob
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(0); //account shut down...
        }

        lew.writeInt(party == null ? 0 : party.getLeader().getId());
        if (exped) {
            return;
        }
        for (PartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getChannel() == forchannel ? partychar.getMapId() : 999999999);
        }
            /*lew.writeInt(220000000);
            lew.writeInt(220010500);
            lew.writeInt(999999999);
            lew.writeInt(999999999);
            lew.writeInt(999999999);
            lew.writeInt(999999999);*/
        for (PartyCharacter partychar : partymembers) {
            if ((partychar.getChannel() == forchannel) && (!leaving)) {
                //lew.writeInt(partychar.getDoorTown());
                //lew.writeInt(partychar.getDoorTarget());
                //lew.writeInt(partychar.getDoorSkill());
                //lew.writeInt(partychar.getDoorPosition().x);
                //lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(leaving ? 999999999 : 0);
                lew.writeLong(leaving ? 999999999L : 0L);
                lew.writeLong(leaving ? -1L : 0L);
            }
        }
        lew.write(0);
        lew.writeMapleAsciiString("Best Party Ever!");
    }

    public static byte[] updateParty(int forChannel, Party party, PartyOperation op, PartyCharacter target) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                pw.write(21); // 18
                pw.writeInt(party.getId());
                pw.writeInt(target.getId());
                pw.write(op == PartyOperation.DISBAND ? 0 : 1);
                if (op == PartyOperation.DISBAND) {
                    break;
                }
                pw.write(op == PartyOperation.EXPEL ? 1 : 0);
                pw.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, pw, op == PartyOperation.LEAVE);
                break;
            case JOIN:
                pw.write(24); // 21
                pw.writeInt(party.getId());
                pw.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, pw, false);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                pw.write(15); // 13
                pw.writeInt(party.getId());
                addPartyStatus(forChannel, party, pw, op == PartyOperation.LOG_ONOFF);
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                pw.write(48); // 45
                pw.writeInt(target.getId());
                pw.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
        }
        return pw.getPacket();
    }

    public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        pw.write(82);
        pw.write(animation ? 0 : 1);
        pw.writeInt(townId);
        pw.writeInt(targetId);
        pw.writeInt(skillId);
        pw.writePos(position);

        return pw.getPacket();
    }
}
