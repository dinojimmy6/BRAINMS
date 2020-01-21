package server;

import game.character.CharacterStats;
import game.character.MapleCharacter;
import game.map.MapleMap;
import game.map.MapleMapFactory;
import game.monster.Monster;
import game.movement.LifeMovementFragment;
import game.movement.MovementParse;
import game.random.CRand32;
import game.skill.*;
import packet.*;
import utils.command.CommandProcessor;
import utils.data.LittleEndianAccessor;
import constants.Flags.Stat;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChannelProcessor implements Runnable {
    int channel;
    public BlockingQueue<Object> queue;
    public Map<Integer, MapleMap> maps;

    public ChannelProcessor(int channel) {
        this.channel = channel;
        queue = new LinkedBlockingQueue<>();
        maps = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            Object o = null;
            try {
                o = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (o instanceof Runnable) {
                ((Runnable) o).run();
            } else if (o instanceof LittleEndianAccessor) {

            } else if (o instanceof MapleCharacter) {
                placeChar((MapleCharacter) o);
            }
        }
    }

    public MapleMap findMap(MapleCharacter chr) {
        return maps.get(chr.getMapId());
    }

    public void placeChar(MapleCharacter chr) {
        MapleMap map = maps.get(chr.getMapId());
        if (map == null) {
            map = MapleMapFactory.getMap(chr.getMapId());
            map.channel = channel;
            maps.put(chr.getMapId(), map);
        }
        chr.enterMap(map);
    }

    public void handleGeneralChat(MapleCharacter chr, String text) {
        if (text.length() < 1 || (text.length() >= 80)) {
            //            //            //autoban
            return;
        }
        MapleMap chrMap = findMap(chr);
        if (chrMap == null) {
            return;
        }
        if (CommandProcessor.processCommand(chr, chrMap, text)) {
            return;
        }
        //handle muted
        chrMap.broadcastMessage(chr, CUserPool.writeChat(chr.getId(), text));
    }

    public void handleMeleeAttack(MapleCharacter chr, AttackInfo ai) {
        int slv = chr.cSkills.getSkillLevel(ai.skillId);
        AttackSkillHandler ash;
        if (slv <= 0) {
            ai.printExpectedDamage(chr, 100, null);
        } else {
            ash = SkillManager.manageAttack(ai.skillId, slv, chr, chr);
            ash.getExpectedDamage(ai);
            if (ai.monsters.size() > 0) {
                ash.onAttackHit();
            }
            for (AttackInfo.MonsterDamage md : ai.monsters) {
                Monster monster = chr.getMap().getMonster(md.oid);
                ash.onAttackPerMob(monster);
                chr.updateMonsterAggro(monster);
                monster.damage(chr, 100, false,0,false);
            }
        }

        Map<Stat, Long> update = new HashMap<>();
        update.put(Stat.MP, 50L);
        chr.writePacket(CWvsContext.updateStats(update, chr));
    }

    public void handleDistributeSp(MapleCharacter chr, int skillId, int amount) {
        chr.cSkills.distributeSp(skillId, amount);
        CharacterStats updated = new CharacterStats(chr.stats.getLast());
        updated.handlePassiveSkills(chr);
        updated.calculateStats(chr);
        chr.stats.add(updated);
        chr.writePacket(CWvsContext.updateSkills(chr));
        Map<Stat, Long> update = new HashMap<>();
        update.put(Stat.AVAILABLESP, 0L);
        chr.writePacket(CWvsContext.updateStats(update, chr));
    }

    public void handleSpecialMove(LittleEndianAccessor lea, MapleCharacter chr, int skillId, int slv) {
        if (slv != chr.cSkills.getSkillLevel(skillId)) {
            return; //ban
        }
        Monster mob;
        switch (skillId) {
            case Paladin.Threaten: //threaten
                lea.skip(4);
                int number_of_mobs = lea.readByte();
                for (int i = 0; i < number_of_mobs; i++) {
                    int oid = lea.readInt();
                    mob = maps.get(chr.getMapId()).getMonster(oid);
                    if (mob == null) {
                        continue;
                    }
                    //mob.switchController(chr, mob.isControllerHasAggro());
                    //StatusProcessor.getInstance().processStatus(mob, skillid, chr.getJob(), chr);
                }

                //chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr, skillid, 1,
                //chr.getLevel(), skillLevel, slea.readByte()), chr.getTruePosition());
                //c.getSession().write(CWvsContext.enableActions());
                break;
            default:
                Point pos = null;
                if ((lea.available() == 5L) || (lea.available() == 7L)) {
                    pos = lea.readPos();
                }
                ////chilling step
                SkillManager.manageSpecialMove(skillId, slv, channel, chr, chr);
                BuffSkillHandler buff = chr.registeredBuffs.get(skillId);
                if (buff != null) {
                    buff.showBuffCast();
                    buff.consumeResource();
                }
        }
    }

    public void handleMoveLife(LittleEndianAccessor lea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        int oid = lea.readInt();
        Monster monster = map.getMonster(oid);
        if (monster == null) {
            return;
        }
        if (monster.getController() != chr) {
            chr.stopControllingMonster(monster);
            return;
        }

        lea.skip(1);
        short moveId = lea.readShort();
        boolean useSkill = lea.readByte() > 0;
        byte skill = lea.readByte();
        int attackId = (skill >> 1) - 13;
        int skillId = lea.readByte() & 0xff;//skill_1
        int slv = lea.readByte() & 0xff;//skill_2
        short option = lea.readShort();//skill_3,skill_4
        //useSkill = Math.random() * 100 < monster.getStats().getAttackChance();
        lea.readByte();
        // for: short, short

        int numPts = lea.readByte();
        for (int i = 0; i < numPts; i++) {
            lea.readShort();
        }

        // ?
        lea.readByte();

        // ?
        lea.readInt();

        // ?
        lea.readInt();
        lea.readInt();

        // ?
        lea.skip(5);

        lea.readInt(); // tEncodedGatherDuration
        short x = lea.readShort();
        short y = lea.readShort();
        short vx = lea.readShort();
        short vy = lea.readShort();

        Point startPos = monster.position;

        List<LifeMovementFragment> res = MovementParse.parseMovement(lea, 2);

        if ((res != null) && (res.size() > 0)) {
            if (attackId >= 0 && attackId < 16) {
                /*MobAttackInfo ai = monster.getStats().getMobAttack(attackId);
                int attackAfter = ai.afterAttack;
                if(attackAfter != 0) {
                    c.getSession().write(MobPacket.mobSetAfterAttack(monster.getObjectId(), monster.getStats().getMobAttack(attackId).afterAttack, attackId, monster.isFacingLeft()));
                }
                ai.callSkills(c.getPlayer(), monster);*/
            }

            chr.writePacket(MobPacket.moveMonsterResponse(monster.oid, moveId, useSkill, skillId, slv));
            if (monster.controllerHasAggro) {
                //c.getSession().write(MobPacket.getMonsterSkill(monster.getObjectId()));
            }

            MovementParse.updatePosition(res, monster, -1);
            Point endPos = monster.position;
            map.moveMonster(monster, endPos);
            chr.broadcastMessage(MobPacket.moveMonster(useSkill, skill, skillId, slv, option, monster.oid, endPos, res));
        }
    }

    public static void handleMovePlayer(LittleEndianAccessor lea, MapleCharacter chr ) {
        MapleMap map = chr.getMap();
        if(map == null) {
            return;
        }
        lea.skip(1); // the type.
        lea.skip(13);
        short x = lea.readShort();
        short y = lea.readShort();
        lea.skip(4);
        final Point originalPos = chr.getPosition();
        List<LifeMovementFragment> res = MovementParse.parseMovement(lea, 1, chr);
        if (res != null) {
            if ((lea.available() < 10L) || (lea.available() > 26L)) {
                return;
            }
            chr.broadcastMessage(CUserPool.movePlayer(chr.getId(), res, originalPos));
            MovementParse.updatePosition(res, chr, 0);
        }
    }
}
