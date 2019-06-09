package utils.command;

import java.util.ArrayList;
import java.util.HashMap;
import game.character.MapleCharacter;
import game.map.MapleMap;
import packet.CWvsContext;

public class CommandProcessor {

    private final static HashMap<String, CommandExecute> commands = new HashMap<>();
    private final static HashMap<Integer, ArrayList<String>> commandList = new HashMap<>();

    static {
        commands.put("@spawn", new GMCommand.Spawn());
    }

    public static boolean processCommand(MapleCharacter chr, MapleMap map, String line) {
        if(line.charAt(0) != '@') {
            return false;
        }
        String[] splitted = line.split(" ");
        splitted[0] = splitted[0].toLowerCase();
        CommandExecute co = commands.get(splitted[0]);
        if (co == null) {
            chr.writePacket(CWvsContext.sendMessage(5, "That player command does not exist."));
        }
        try {
            co.execute(chr, map, splitted);
        } catch (Exception e) {
            chr.writePacket(CWvsContext.sendMessage(5, "There was an error with the command."));
        }
        return true;
    }
}