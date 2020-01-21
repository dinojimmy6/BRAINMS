package game.status;

import game.character.CharacterStats;
import game.character.MapleCharacter;
import game.monster.Monster;
import game.skill.Skill;
import game.skill.SkillFactory;
import packet.SendPacketOpcode;
import server.ChannelServer;
import utils.Logging;
import utils.data.PacketWriter;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Kevin on 7/19/2017.
 */
public class StackableDOT extends MobStatus {
    private int skillId;
    private int slv;
    private int duration;
    private int damage;
    private int stacks;
    private int superPos;
    private int multiStack = 1;
    private ScheduledFuture<?> poisonSchedule;

    public StackableDOT(Monster monster, int skillId, int slv, MapleCharacter charFrom) {
        super(monster, charFrom);
        this.skillId = skillId;
        this.slv = slv;
        /*switch(skillId) {
            case NightLord.ToxicVenom:
            case Shadower.ToxicVenom:
            case DualBlade.ToxicVenom:
            case Bowmaster.QuiverCartridge:
                this.multiStack = 3;
                break;
        }*/
        Skill skill = SkillFactory.getSkill(this.skillId);
        CharacterStats cs = charFrom.stats.getLast();
        double avgCritDam = ((cs.getMaxCrit() + cs.getMinCrit()) / 200.0) * cs.getCritRate() / 100.0 + 1;
        double dotDamage = skill.getDot(slv) * cs.maxBaseDmg * avgCritDam / 100.0;
        //if(monster.getStats().isBoss()) {
        //    dotDamage *= ps.getFinalDmg() * (ps.getDamR() + ps.getBossDamR() + 1.0);
        //}
        //else {
        //    dotDamage *= ps.getFinalDmg() * (ps.getDamR() + 1.0);
        //}
        this.damage = Math.min(Integer.MAX_VALUE, (int) dotDamage);
        this.duration = skill.getDotTime(slv);
        if(this.duration > 0) {
            removeStatus();
            addStatus();
        }
        this.schedule = ChannelServer.timers[charFrom.getChannel()].schedule(this, duration * 1000);
        this.poisonSchedule = ChannelServer.timers[charFrom.getChannel()].register(new Runnable() {
            @Override
            public void run() {
                try {
                    ChannelServer.processors[monster.map.channel].queue.put((Runnable) () -> {
                        if (monster.isAlive()) {
                            applyServerEffect(); } });
                }
                catch (InterruptedException e) {
                    Logging.log(e.getMessage());
                }
            }}, 1000);
    }

    @Override
    public void applyServerEffect() {
        if (damage > 0) {
            if (damage >= monster.hp) {
                damage = (int) (monster.hp - 1);
            }
            monster.damage(charFrom, damage, false, 0, true);
        }
    }

    @Override
    public void applyStatus(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();
        ArrayList<StackableDOT> statuses = monster.getAllDots();
        stacks = statuses.size();
        pw.writeShort(SendPacketOpcode.APPLY_MOB_STATUS.getValue());
        pw.writeInt(oid);
        MobStatusFlag.DOT.writeSingleMask(pw);
        int totalStacks = 0;
        for(StackableDOT dot : statuses) {
            totalStacks += dot.getMultiStack();
        }
        pw.write((byte) totalStacks);
        int index = 0;
        for(int i = 0; i < stacks; i++) {
            StackableDOT s = statuses.get(i);
            for (int j = 0; j < s.getMultiStack(); j++) {
                pw.writeInt(charFrom.oid);
                pw.writeInt(s.getSkillId());
                pw.writeInt(s.getDamage());
                pw.writeInt(1000);
                pw.writeInt(8000);
                pw.writeInt(s.getDuration() * 1000);//duration of animations (in ms for some reason)
                pw.writeInt(s.getDuration());
                s.setSuperPos(index);
                pw.writeInt(index);
                pw.writeInt(0);
                pw.writeInt(0);
                pw.writeInt(0);
                pw.writeInt(0);
                pw.writeInt(0);
                ++index;
            }
        }
        pw.writeShort(0);
        pw.write(0);
        writeStatus(chr, pw);
    }

    @Override
    public void cancelStatus() {
        removeStatus();
        int totalStacks = 0;
        for(StackableDOT dot : monster.getDots(charFrom)) {
            totalStacks += dot.getMultiStack();
        }
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CANCEL_MOB_STATUS.getValue());
        pw.writeInt(oid);
        MobStatusFlag.DOT.writeSingleMask(pw);
        pw.writeInt(totalStacks);
        pw.writeInt(multiStack);
        for (int j = 1; j <= multiStack; j++) {
            pw.writeInt(charFrom.getId());
            pw.writeInt(superPos - (multiStack - j));
            pw.write(4);// was1
            pw.write(2);
        }
        if(controller != null) {
            //map.broadcastMessage(controller, pw.getPacket(), monster.getTruePosition());
            controller.writePacket(pw.getPacket());
        }
    }

    @Override
    public void addStatus() {
        monster.addDot(this, skillId);
    }

    @Override
    public void removeStatus() {
        monster.removeDot(this.getCharFrom(), skillId);
    }

    public int getDuration() {
        return duration;
    }

    public int getDamage() {
        return damage;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getMultiStack() {
        return multiStack;
    }

    public int getSuperPos() {
        return superPos;
    }

    public void setSuperPos(int superPos) {
        this.superPos = superPos;
    }

    @Override
    public void cancelSchedule() {
        if(schedule != null) {
            if(schedule.cancel(false)) {
                schedule = null;
            }
        }
        if(poisonSchedule != null) {
            poisonSchedule.cancel(true);
            poisonSchedule = null;
        }
    }
}
