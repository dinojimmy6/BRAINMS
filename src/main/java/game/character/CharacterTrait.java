package game.character;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import game.character.Trait.TraitType;

public class CharacterTrait {
    public EnumMap<TraitType, Trait> traits;

    public CharacterTrait(ResultSet rs) throws SQLException {
        traits = new EnumMap<>(TraitType.class);
        for (Trait.TraitType t : Trait.TraitType.values()) {
            Trait newTrait = new Trait(t);
            newTrait.setExp(rs.getInt(t.name()));
            traits.put(t, newTrait);
        }
    }

    public CharacterTrait() {
        traits = new EnumMap<>(TraitType.class);
        for (Trait.TraitType t : Trait.TraitType.values()) {
            Trait newTrait = new Trait(t);
            traits.put(t, newTrait);
        }
    }
}
