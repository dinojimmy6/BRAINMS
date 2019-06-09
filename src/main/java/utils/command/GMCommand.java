package utils.command;

import game.character.MapleCharacter;
import game.map.MapleMap;
import game.monster.Monster;
import game.monster.MonsterFactory;
import packet.CWvsContext;

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
            return 1;
        }
    }
}
