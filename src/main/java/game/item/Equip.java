package game.item;

import game.equip.EquipStat;
import utils.data.PacketWriter;
import utils.parsing.MapleData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Equip extends Item {
    public int slots, level, vicioushammer, enhance, enhanctBuff, reqLevel, yggdrasilWisdom,
                 bossDamage, ignorePDR, totalDamage, allStat, karmaCount;
    public int str, dex, int_, luk, hp, mp, watk, matk, wdef, mdef, acc, eva, speed, jump, charmExp;

    public Equip(MapleData root) {
        super(Integer.parseInt(root.getName().substring(0, root.getName().length() - 4)));
        MapleData info = root.getChildByPath("info");
        str = MapleData.getInt(info.getChildByPath("incSTR"), 0);
        dex = MapleData.getInt(info.getChildByPath("incDEX"), 0);
        int_ = MapleData.getInt(info.getChildByPath("incINT"), 0);
        luk = MapleData.getInt(info.getChildByPath("incLUK"), 0);
        watk = MapleData.getInt(info.getChildByPath("incPAD"), 0);
        matk = MapleData.getInt(info.getChildByPath("incMAD"), 0);
        wdef = MapleData.getInt(info.getChildByPath("incPDD"), 0);
        mdef = MapleData.getInt(info.getChildByPath("incMDD"), 0);
        acc = MapleData.getInt(info.getChildByPath("incACC"), 0);
        eva = MapleData.getInt(info.getChildByPath("incEVA"), 0);
        speed = MapleData.getInt(info.getChildByPath("incSpeed"), 0);
        jump = MapleData.getInt(info.getChildByPath("incJump"), 0);
        slots = MapleData.getInt(info.getChildByPath("tuc"), 0);
    }

    public Equip(int itemId, int position) {
        super(itemId, position, 1);
    }

    public Equip copy() {
        Equip copy = new Equip(id, position);
        copy.str = str;
        copy.dex = dex;
        copy.int_ = int_;
        copy.luk = luk;
        copy.hp = hp;
        copy.mp = mp;
        copy.watk = watk;
        copy.matk = matk;
        copy.wdef = wdef;
        copy.mdef = mdef;
        copy.acc = acc;
        copy.eva = eva;
        copy.speed = speed;
        copy.jump = jump;
        copy.slots = slots;
        return copy;
    }

    public Equip(ResultSet rs) throws SQLException {
        super(rs.getInt("itemid"));
        str = rs.getInt("str");
        dex = rs.getInt("dex");
        int_ = rs.getInt("int");
        luk = rs.getInt("luk");
        hp = rs.getInt("hp");
        mp = rs.getInt("mp");
        watk = rs.getInt("watk");
        matk = rs.getInt("matk");
        wdef = rs.getInt("wdef");
        mdef = rs.getInt("mdef");
        acc = rs.getInt("acc");
        eva = rs.getInt("eva");
        speed = rs.getInt("speed");
        jump = rs.getInt("jump");
        slots = rs.getInt("slots");
    }

    private int getMask() {
        int mask = 0;
        if(str > 0) {
            mask |= EquipStat.STR.getValue();
        }
        if(dex > 0) {
            mask |= EquipStat.DEX.getValue();
        }
        if(int_ > 0) {
            mask |= EquipStat.INT.getValue();
        }
        if(luk > 0) {
            mask |= EquipStat.LUK.getValue();
        }
        if(hp > 0) {
            mask |= EquipStat.MHP.getValue();
        }
        if(mp > 0) {
            mask |= EquipStat.MMP.getValue();
        }
        if(watk > 0) {
            mask |= EquipStat.WATK.getValue();
        }
        if(matk > 0) {
            mask |= EquipStat.MATK.getValue();
        }
        if(wdef > 0) {
            mask |= EquipStat.WDEF.getValue();
        }
        if(mdef > 0) {
            mask |= EquipStat.MDEF.getValue();
        }
        if(acc > 0) {
            mask |= EquipStat.ACC.getValue();
        }
        if(eva > 0) {
            mask |= EquipStat.AVOID.getValue();
        }
        if(speed > 0) {
            mask |= EquipStat.SPEED.getValue();
        }
        if(jump > 0) {
            mask |= EquipStat.JUMP.getValue();
        }
        if(slots > 0) {
            mask |= EquipStat.SLOTS.getValue();
        }
        return mask;
    }

    public void writeEquip(PacketWriter pw) {
        pw.writeInt(getMask());
        if(slots > 0) {
            pw.write(slots);
        }
        if(str > 0) {
            pw.writeShort(str);
        }
        if(dex > 0) {
            pw.writeShort(dex);
        }
        if(int_ > 0) {
            pw.writeShort(int_);
        }
        if(luk > 0) {
            pw.writeShort(luk);
        }
        if(hp > 0) {
            pw.writeShort(hp);
        }
        if(mp > 0) {
            pw.writeShort(mp);
        }
        if(watk > 0) {
            pw.writeShort(watk);
        }
        if(matk > 0) {
            pw.writeShort(matk);
        }
        if(wdef > 0) {
            pw.writeShort(wdef);
        }
        if(mdef > 0) {
            pw.writeShort(mdef);
        }
        if(acc > 0) {
            pw.writeShort(acc);
        }
        if(eva > 0) {
            pw.writeShort(eva);
        }
        if(speed > 0) {
            pw.writeShort(speed);
        }
        if(jump > 0) {
            pw.writeShort(jump);
        }
    }

    public void save(PreparedStatement ps) throws SQLException {
        ps.setInt(2, str);
        ps.setInt(3, dex);
        ps.setInt(4, int_);
        ps.setInt(5, luk);
        ps.setInt(6, hp);
        ps.setInt(7, mp);
        ps.setInt(8, watk);
        ps.setInt(9, matk);
        ps.setInt(10, wdef);
        ps.setInt(11, mdef);
        ps.setInt(12, acc);
        ps.setInt(13, eva);
        ps.setInt(14, speed);
        ps.setInt(15, jump);
        ps.setInt(16, slots);
    }

}
