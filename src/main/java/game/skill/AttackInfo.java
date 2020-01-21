package game.skill;

import constants.Skill;
import game.character.CharacterStats;
import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import game.random.CRand32;
import javafx.util.Pair;
import utils.data.LittleEndianAccessor;

import java.awt.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class AttackInfo {
    public int skillId;
    public byte slv;
    public byte targets;
    public byte hits;
    public byte speed;
    public byte csstar;
    public byte slot;
    public int charge;
    public int pGrenade;
    public int lastAttackTickCount;
    public byte index;
    public List<Integer> keys; //kinesis
    public Point position;
    public List<MonsterDamage> monsters = new LinkedList<>();

    public AttackInfo(LittleEndianAccessor lea, MapleCharacter chr) {
        lea.skip(1);
        byte nMobCount = lea.readByte();
        targets = (byte) (nMobCount >>> 4 & 0xf);
        hits = (byte) (nMobCount & 0xf);
        skillId = lea.readInt();
        slv = lea.readByte();
        lea.skip(1);
        lea.skip(4);
        if(constants.Skill.isKeyDownSkill(skillId) || constants.Skill.isSuperNovaSkill(skillId)) {
            charge = lea.readInt();
        }
        if(Skill.isRushBombSkill(skillId)) {
            pGrenade = lea.readInt();
        }
        if(chr.isZero()) {
            lea.skip(1);
        }
        lea.skip(1); //!energy
        lea.skip(1);
        lea.readShort();
        lea.skip(4);
        lea.skip(1);
        speed = lea.readByte();
        lastAttackTickCount = lea.readInt(); //!energy
        lea.skip(4);
        int finalAttack = lea.readInt();
        if(skillId > 0 && finalAttack > 0) {
            lea.skip(1);
        }
//                if (skillid == 5111009) // spiral assault
//                    lea.skip(1);
//
//                if (Skill.isUseBulletMeleeAttack(skillid)) {
//                    lea.skip(2);
//
//                    if (Skill.isNonConsumingBulletMeleeAttack(skillid))
//                        lea.skip(4);
//                }
//
//                if (skillid == Shade.SpiritFrenzy) { // spirit frenzy
//                    lea.skip(4); // nSpiritCoreEnhance
//                }
        buildMonsters(lea);
    }

    private void buildMonsters(LittleEndianAccessor lea) {
        for(int mob = 0 ; mob < targets; mob++) {
            MonsterDamage md = new MonsterDamage();
            md.oid = lea.readInt();
            lea.skip(1);
            lea.skip(1);
            lea.skip(1);
            lea.skip(1);
            lea.skip(1);
            lea.skip(4);
            md.index = lea.readByte();
            lea.skip(2);
            lea.skip(2);
            lea.skip(2);
            lea.skip(2);
            lea.skip(2);
            for(int hit = 0; hit < hits; hit++) {
                int damage = lea.readInt();
                md.damage.add(new SimpleEntry<>(damage, false));
            }
            lea.skip(4); // GetMobUpDownYRange
            lea.skip(4); // mob crc
            // PACKETMAKER::MakeAttackInfoPacket
            byte bSkeleton = lea.readByte();
            if(bSkeleton == 1) {
                lea.readMapleAsciiString();
                lea.readInt();
            }
            else if(bSkeleton == 2) {
                lea.readMapleAsciiString();
                lea.readInt();
            }
            monsters.add(md);
        }
    }

    public int getExpectedDamage() {
        return SkillFactory.getSkill(skillId).getDamage(slv);
    }

//    public void printExpectedDamage(MapleCharacter chr, int skillDamage) {
//        int numRand = 11;
//        Iterator<CharacterStats> iter = chr.stats.descendingIterator();
//        long rand[] = new long[numRand];
//        for (int i = 0; i < numRand; i++) {
//            rand[i] = chr.rand.random();
//        }
//        while(iter.hasNext()) {
//            int index = 0;
//            CharacterStats stat = iter.next();
//            boolean success = true;
//            for(MonsterDamage md : monsters) {
//                //Monster monster = map.getMonster(md.oid);
//                //if(monster == null) {
//                //    return;
//                //}
//
//                index++;
//                boolean check[] = new boolean[11];
//                for (int i = 0; i < md.damage.size(); i++) {
//                    SimpleEntry<Integer, Boolean> line = md.damage.get(i);
//                    boolean reused = false;
//                    if (check[index % 11]) {
//                        reused = true;
//                    } else {
//                        check[index % 11] = true;
//                    }
//                    index += 2;
//                    double damage = CRand32.getRand(rand[index++ % 11], (int) stat.maxBaseDmg, (int) stat.minBaseDmg) * .9 * 1.1;
//                    damage = skillDamage / 100.0 * damage;
//                    if (reused) {
//                        index += (int) CRand32.getRand(rand[(index + i) % 11], 9, 0);
//                    }
//                    if (CRand32.getRand(rand[index++ % 11], 100, 0) < stat.getCritRate()) {
//                        int critDamage = (int) CRand32.getRand(rand[index++ % 11], stat.getMaxCrit(), stat.getMinCrit());
//                        damage = critDamage / 100.0 * damage + damage;
//                        line.setValue(true);
//                    }
//                    index++;
//                    if (line.getKey() != (int) damage) {
//                        if (line.getValue()) {
//                            System.out.print("CRITICAL HIT ");
//                        }
//                        System.out.println("EXPECTED: " + line.getKey() + " ACTUAL: " + (int) damage);
//                        success = false;
//                    }
//                }
//            }
//            if(success) {
//                break;
//            }
//        }
//        while(iter.hasNext()) {
//            iter.next();
//            iter.remove();
//        }
//    }

    public void printExpectedDamage(MapleCharacter chr, int skillDamage, ArrayList<MonsterDamageMods> mods) {


        long rand[] = new long[11];
        int index = 0;
        CharacterStats stat = chr.stats.getLast();
        int iter = 0;
        for(MonsterDamage md : monsters) {
            for (int i = 0; i < 11; i++) {
                rand[i] = chr.rand.random();
            }
            int bonusTD = 0;
            if(mods != null && mods.size() > 0) {
                bonusTD += mods.get(iter).bonusTD;
            }
            index++;
            boolean check[] = new boolean[11];
            for (int i = 0; i < md.damage.size(); i++) {
                SimpleEntry<Integer, Boolean> line = md.damage.get(i);
                boolean reused = false;
                if (check[index % 11]) {
                    reused = true;
                }
                else {
                    check[index % 11] = true;
                }
                index += 2;
                double damage = CRand32.getRand(rand[index++ % 11], (int) stat.maxBaseDmg, (int) stat.minBaseDmg) * .9 * 1.1;
                damage = skillDamage / 100.0 * damage;
                damage = damage * (1.0 + bonusTD / 100.0);
                if (reused) {
                    index += (int) CRand32.getRand(rand[(index + i) % 11], 9, 0);
                }
                if (CRand32.getRand(rand[index++ % 11], 100, 0) < stat.getCritRate()) {
                    int critDamage = (int) CRand32.getRand(rand[index++ % 11], stat.getMaxCrit(), stat.getMinCrit());
                    damage = critDamage / 100.0 * damage + damage;
                    line.setValue(true);
                }
                index++;
                if (line.getKey() != (int) damage) {
                    if (line.getValue()) {
                        System.out.print("CRITICAL HIT ");
                    }
                    System.out.println("EXPECTED: " + line.getKey() + " ACTUAL: " + (int) damage);
                }
            }
            iter++;
            index = 0;
        }

    }

    public class MonsterDamage {
        public int oid;
        public int index;
        public List<SimpleEntry<Integer, Boolean>> damage = new LinkedList<>();
    }

    public static class MonsterDamageMods {
        int bonusTD;
        int bonusBD;
        double multiplier;

        public MonsterDamageMods(int bonusTD, int bonusBD, double multiplier) {
            this.bonusTD = bonusTD;
            this.bonusBD = bonusBD;
            this.multiplier = multiplier;
        }

        public MonsterDamageMods() {
           this.multiplier = 1.0;
        }
    }
}
