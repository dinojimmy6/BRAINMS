package game.character;

import game.equip.EquipSlot;
import game.equip.EquipStat;
import game.equip.EquipType;
import game.item.Equip;
import game.item.ItemFactory;
import utils.DatabaseConnection;
import utils.data.PacketWriter;

import java.sql.*;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class CharacterEquips {
    public EnumMap<EquipSlot, Equip> equips = new EnumMap<>(EquipSlot.class);

    public CharacterEquips() {
        equips = new EnumMap<>(EquipSlot.class);
    }

    public CharacterEquips(Connection con, int chrId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM inventoryitems LEFT JOIN equipstats USING (inventoryitemid) WHERE characterid = ?");
        ps.setInt(1, chrId);
        ResultSet rs = ps.executeQuery();
        List<Integer> items = new LinkedList<>();
        while(rs.next()) {
            if(rs.getInt("position") < 0) {
                Equip newEquip = new Equip(rs);
                equips.put(EquipType.getFromItemId(newEquip.id).getAllowed(), newEquip);
            }
        }
        ps.close();
        rs.close();
    }

    public void save(Connection con, int chrId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("DELETE FROM inventoryitems WHERE characterid = ? AND `position` < 0");
        ps.setInt(1, chrId);
        ps.executeUpdate();
        ps.close();

        ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, quantity, `position`)" +
                                                    "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement ps2 = con.prepareStatement("INSERT INTO equipstats (" +
                                                     "inventoryitemid, str, dex, `int`, luk, hp, mp, watk, matk," +
                                                     "wdef, mdef, acc, eva, speed, jump, slots)" +
                                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for(EquipSlot es : equips.keySet()) {
            Equip e = equips.get(es);
            ps.setInt(1, chrId);
            ps.setInt(2, equips.get(es).id);
            ps.setInt(3, 1);
            ps.setInt(4, es.slot);
            ps.executeUpdate();
            final long iid;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    rs.close();
                    continue;
                }
                iid = rs.getLong(1);
            }
            ps2.setLong(1, iid);
            e.save(ps2);
            ps2.executeUpdate();

        }
        ps.close();
        ps2.close();
    }

    public void equip(Equip equip) {
        equips.put(EquipType.getFromItemId(equip.id).getAllowed(), equip);
    }

    public void writeEquips(PacketWriter pw) {
        for(EquipSlot slot : equips.keySet()) {
            pw.write(slot.slot * -1);
            pw.writeInt(equips.get(slot).id);
        }
    }

    public int getWeaponId() {
        Equip weapon = equips.get(EquipSlot.WEAPON);
        if(weapon != null) {
            return weapon.id;
        }
        return 0;
    }

    public void addInfo(PacketWriter pw, MapleCharacter chr) {
        for(EquipSlot slot : equips.keySet()) {
            pw.writeShort(slot.slot * -1);
            pw.write(1);
            pw.writeInt(equips.get(slot).id);
            pw.write(0); //no uniqueid implementation
            pw.writeLong(0); //expiration
            pw.writeInt(equips.get(slot).id);
            equips.get(slot).writeEquip(pw);
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
        pw.writeMapleAsciiString(chr.getName());
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
