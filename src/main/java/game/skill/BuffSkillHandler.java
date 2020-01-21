package game.skill;

import game.character.CharacterStats;
import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import packet.CWvsContext;
import server.ChannelServer;
import utils.Logging;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BuffSkillHandler implements Runnable {
    protected Skill skill;
    protected int key;
    protected int channel;
    protected int buffDuration;
    protected MapleCharacter chrTo;
    protected MapleCharacter chrFrom;

    private int statValue;
    private int secondaryStatValue;
    private int tertiaryStatValue;

    protected ScheduledFuture<?> schedule;
    protected EnumMap<BuffStat, List<Integer>> buffStats;

    public BuffSkillHandler(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
        this.skill = SkillFactory.getSkill(skillId);
        buffDuration = chrFrom.getDuration(skill, slv);
        this.buffDuration = chrFrom.getDuration(skill, slv);
        this.key = skillId;
        this.chrTo = chrTo;
        this.chrFrom = chrFrom;
        buffStats = new EnumMap<BuffStat, List<Integer>>(BuffStat.class);
    }

    @Override
    public void run() {
        cancelBuff();
    }

    public void updateCharacterStats() {
        CharacterStats updated = new CharacterStats(chrTo.stats.getLast());
        updated.handleBuffStats(chrTo);
        updated.calculateStats(chrTo);
        chrTo.stats.add(updated);
    }

    public void registerEffect() {
        chrTo.registerBuff(this);
        if(buffDuration >= 1000 ) {
            schedule = ChannelServer.timers[channel].schedule(this, buffDuration);
        }
        updateCharacterStats();
    }

    public boolean cancelSchedule() {
        if(schedule != null) {
            return schedule.cancel(false);
        }
        return false;
    }

    public void cancelBuff() {
        try {
            ChannelServer.processors[channel].queue.put((Runnable) () -> {
                chrTo.unregisterBuff(this);
                chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), chrTo));
                chrTo.broadcastMessage(CWvsContext.cancelForeignBuff(chrTo.getId(), buffStats));
                updateCharacterStats();
            });
        } catch (InterruptedException e) {
            Logging.log(e.getMessage());
        }
    }

    public void resetCancelBuff() {
        chrTo.unregisterBuff(this);
        chrTo.writePacket(CWvsContext.cancelBuff(buffStats.keySet(), chrTo));
        chrTo.broadcastMessage(CWvsContext.cancelForeignBuff(chrTo.getId(), buffStats));
    }

    public void giveBuff() {
        chrTo.writePacket(CWvsContext.giveBuff(skill.id, buffDuration, buffStats, chrTo));
    }

    public void showBuffAll() {
        //chrTo.getMap().broadcastMessage(chrTo, CWvsContext.giveForeignBuff(chrTo.getId(), buffStats, chrTo), false);
    }

    public void showBuffCast() {
        //chrFrom.getMap().broadcastMessage(chrFrom, CField.EffectPacket.showBuffeffect(chrFrom, skillId, 1, chrFrom.getLevel(), mse.getLevel(), (byte)0), false);
    }

//    protected List<MapleCharacter> getChrsToPartyBuff() {
//        final Rectangle bounds = mse.calculateBoundingBox(chrFrom.getTruePosition(), chrFrom.isFacingLeft());
//        final List<MapleMapObject> affecteds = chrFrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
//        ArrayList<MapleCharacter> giveBuffList = new ArrayList<>(10);
//        for (final MapleMapObject affectedObj : affecteds) {
//            final MapleCharacter affected = (MapleCharacter) affectedObj;
//            if (affected.isAlive() && affected != chrFrom && (chrFrom.getParty() != null && affected.getParty() != null && chrFrom.getParty().getId() == affected.getParty().getId())) {
//                giveBuffList.add(affected);
//            }
//        }
//        return giveBuffList;
//    }

    public boolean hasVisibleEffect() {
        return false;
    }

    public void consumeResource() {
        int slv = chrFrom.cSkills.getSkillLevel(skill.id);
        chrFrom.drainMp(skill.getMpCon(slv));
    }

    public int getSkillId() {
        return skill.id;
    }

    public int getKey() {
        return key;
    }

    public int getBuffDuration() {
        return buffDuration;
    }

    public MapleCharacter getChrTo() {
        return chrTo;
    }

    public EnumMap<BuffStat, List<Integer>> getBuffStats() {
        return buffStats;
    }

    public List<Integer> getBuffStat(BuffStat buffStat) {
        return buffStats.get(buffStat);
    }

    public void setBuffStat(BuffStat buffStat, int value, int index) {
        if(buffStats.get(buffStat) != null && buffStats.get(buffStat).size() > index)
            buffStats.get(buffStat).set(index, value);
    }

    public int getStatValue() {
        return statValue;
    }

    public void setStatValue(int statValue) {
        this.statValue = statValue;
    }

    public int getSecondaryStatValue() {
        return secondaryStatValue;
    }

    public void setSecondaryStatValue(int secondaryStatValue) {
        this.secondaryStatValue = secondaryStatValue;
    }

    public int getTertiaryStatValue() {
        return tertiaryStatValue;
    }

    public void setTertiaryStatValue(int tertiaryStatValue) {
        this.tertiaryStatValue = tertiaryStatValue;
    }

    public void giveOnAttackCast() {}

    public void giveOnAttackCast(AttackInfo attack) {}

    public void giveOnMobDamage(AttackInfo attack) {}

    public void giveOnSkillDamage(int skillId) {}

    public void giveOnTakeDamage(int damage, int monsterId) {}

    public void giveOnTakeFatalDamage() {
    }

    public void givePerMobAttack(Monster monster, AttackInfo attack) {}

    public void onAttackMobResponse(AttackInfo attack) {}

    //public boolean onStatusReceive(MapleDisease d) {
        //return false;
    //}

    public void handleMobList(List<Integer> mobList) {}

    public void onChangeMap() {}

    public void onMonsterKill(Monster monster) {}
}