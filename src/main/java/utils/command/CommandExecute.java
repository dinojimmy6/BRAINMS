package utils.command;

import javax.activation.CommandObject;
import game.character.MapleCharacter;
import game.map.MapleMap;

public abstract class CommandExecute {

    public abstract int execute(MapleCharacter chr, MapleMap map, String[] splitted);
    //1 = Success
    //0 = Something Went Wrong

    enum ReturnValue {

        DONT_LOG,
        LOG;
    }
}
