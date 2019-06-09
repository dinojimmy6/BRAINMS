package game.character;

import game.equip.EquipSlot;
import game.equip.EquipStat;
import game.equip.EquipType;
import utils.data.PacketWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;

public class CharacterEquips {
    public EnumMap<EquipSlot, Integer> equips;

    public CharacterEquips() {
        equips = new EnumMap<>(EquipSlot.class);
    }

    public CharacterEquips(List<Integer> items) {
        equips = new EnumMap<>(EquipSlot.class);
        for(Integer item : items) {
            equips.put(EquipType.getFromItemId(item).getAllowed(), item);
        }
    }

    public void save(Connection con, int chrId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, quantity, position)" +
                                                    "VALUES (?, ?, ?, ?)");
        for(EquipSlot es : equips.keySet()) {
            ps.setInt(1, chrId);
            ps.setInt(2, equips.get(es));
            ps.setInt(3, 1);
            ps.setInt(4, es.slot);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    public void equip(int equipId) {
        equips.put(EquipType.getFromItemId(equipId).getAllowed(), equipId);
    }

    public void writeEquips(PacketWriter pw) {
        for(EquipSlot slot : equips.keySet()) {
            pw.write(slot.slot * -1);
            pw.writeInt(equips.get(slot));
        }
    }

    public void addInfo(PacketWriter pw, MapleCharacter chr) {
        for(EquipSlot slot : equips.keySet()) {
            pw.writeShort(slot.slot * -1);
            pw.write(1);
            pw.writeInt(equips.get(slot));
            pw.write(0); //no uniqueid implementation
            pw.writeLong(0); //expiration
            pw.writeInt(equips.get(slot));
            int mask = EquipStat.WATK.getValue();
            pw.writeInt(mask); //equip stats mask
            pw.writeShort(17);
            pw.writeInt(0); //equip special stats
            addEquipBonusStats(pw, chr);
        }
        pw.writeShort(0);
        pw.writeShort(0); //cash equip
        pw.writeShort(0); //equip inv
        pw.writeShort(0); //evan
        pw.writeShort(0); //android equip
        pw.writeShort(0); // pet consume
        pw.writeShort(0); //android
        pw.writeShort(0); //ab
        pw.writeShort(0); //bits
        pw.writeShort(0); //zero
        pw.writeShort(0); //totem
        pw.writeShort(0); //zero beta cash
        pw.writeShort(0); //haku
    }

    public static void addEquipBonusStats(PacketWriter pw, MapleCharacter chr) {
        pw.writeMapleAsciiString(chr.name);
        pw.write(0); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
        pw.write(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0); // > 0 = mounted, 0 = empty, -1 = none.
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeLong(0); //no unique id
        pw.writeLong(0);
        pw.writeInt(-1); //?
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeShort(0); // new
        pw.writeShort(0); // new
        pw.writeShort(0); // new
    }
}
