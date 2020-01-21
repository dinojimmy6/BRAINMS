package utils.command;

import constants.Flags;
import constants.Jobs;
import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import game.monster.MonsterFactory;
import packet.CWvsContext;
import packet.MobPacket;

import java.util.HashMap;
import java.util.Map;

public class GMCommand {
    public static class Spawn extends CommandExecute {
        @Override
        public int execute(MapleCharacter chr, MapleMap map, String[] splitted) {
            final int mid = Integer.parseInt(splitted[1]);
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            Integer level = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lvl");
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

            Monster mob;
            try {
                mob = MonsterFactory.getMonster(mid);
            } catch (RuntimeException e) {
                chr.writePacket(CWvsContext.sendMessage(5, "Error: " + e.getMessage()));
                return 0;
            }
            if (mob == null) {
                chr.writePacket(CWvsContext.sendMessage(5, "Mob does not exist"));
                return 0;
            }
            map.spawnMonsterOnPoint(mob, chr.getPosition());
            chr.writePacket(MobPacket.controlMonster(mob, false));
            mob.setController(chr);
            return 1;
        }
    }

    public static class Job extends CommandExecute {
        @Override
        public int execute(MapleCharacter chr, MapleMap map, String[] splitted) {
            final int jobId = Integer.parseInt(splitted[1]);
            chr.changeJob(Jobs.Job.getJobById(jobId));
            return 1;
        }
    }

    public static class GetSp extends CommandExecute {
        @Override
        public int execute(MapleCharacter chr, MapleMap map, String[] splitted) {
            int job = Integer.parseInt(splitted[1]);
            int sp = Integer.parseInt(splitted[2]);
            chr.cSkills.giveSp(job, sp);
            Map<Flags.Stat, Long> update = new HashMap<>();
            update.put(Flags.Stat.AVAILABLESP, 1L);
            chr.writePacket(CWvsContext.updateStats(update, chr));
            return 1;
        }
    }
}
