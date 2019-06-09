package game.character;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import constants.Jobs;
import constants.ServerConfig;
import game.equip.EquipSlot;
import game.map.MapObject;
import game.random.CRand32;
import org.apache.mina.core.session.IoSession;
import utils.CharacterUtil;
import utils.DatabaseConnection;
import utils.Logging;
import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;
import game.character.Trait.TraitType;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MapleCharacter extends MapObject {
    private IoSession session;
    public int id, accountId;
    String name;
    int level, subcategory;
    Jobs.Job job;
    public int mapId;
    int fatigue; //TODO get this into professions class
    public CharacterSkills cSkills;
    public CharacterBody body;
    public CharacterStats stats;
    public CharacterTrait characterTraits;
    public CharacterEquips equips;

    public CRand32 rand = new CRand32();

    private final Point position = new Point(698, 237);

    public MapleCharacter(LittleEndianAccessor lea, int accountId) {
        byte gender, skin;
        int face, hair, hairColor = -1, hat = -1, top, bottom = -1, shoes, weapon, cape = -1, faceMark = -1, shield = -1;
        this.accountId = accountId;
        level = 1;
        mapId = 100000000;
        name = lea.readMapleAsciiString();
        if(!CharacterUtil.canCreateChar(name)) {
            Logging.exceptionLog("char name hack: " + name);
            return;
        }

        lea.skip(4); // key type setting
        lea.skip(4); // -1

        int job_type = lea.readInt();
        Jobs.LoginJob lj = Jobs.LoginJob.getByLoginId(job_type);
        job = Jobs.Job.getJobById(lj.id);


        if (lj == null) {
            Logging.exceptionLog("New job type found: " + job_type);
            return;
        }

        subcategory = lea.readShort();
        gender = lea.readByte();
        skin = lea.readByte();

        lea.skip(1); // the amount of items a new character will receive.

        face = lea.readInt();
        hair = lea.readInt();

        if (lj.hairColor) {
            hairColor = lea.readInt();
        }

        if (lj.skinColor) {
            lea.readInt();
        }

        if (lj.faceMark) {
            faceMark = lea.readInt();
        }
        body = new CharacterBody(gender, skin, face, hair, faceMark);

        if (lj.hat) {
            hat = lea.readInt();
        }

        top = lea.readInt();

        if (lj.bottom) {
            bottom = lea.readInt();
        }

        if (lj.cape) {
            cape = lea.readInt();
        }

        shoes = lea.readInt();
        weapon = lea.readInt();

        if (lea.available() >= 4) {
            shield = lea.readInt();
        }

        equips = new CharacterEquips();
        int[] equipList = new int[] {hat, top, bottom, cape, shoes, weapon, shield};
        for (int itemId : equipList) {
            if (itemId > 0) {
                equips.equip(itemId);
            }
        }
        stats = new CharacterStats();
        cSkills = new CharacterSkills();
        characterTraits = new CharacterTrait();
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps;
        ResultSet rs;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters (accountid, name, map, level, job, subcategory, str, dex, luk, `int`, maxhp, maxmp, hp, mp, ap, exp, fame, sp, " +
                                      "gender, skincolor, face, hair, facemarking)" +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            int index = 0;
            ps.setInt(++index, accountId);
            ps.setString(++index, name);
            ps.setInt(++index, mapId);
            ps.setInt(++index, level);
            ps.setInt(++index, job.getJobId());
            ps.setInt(++index, subcategory);
            index = stats.save(ps, index);
            index = cSkills.save(ps, index);
            index = body.save(ps, index);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                Logging.exceptionLog("fatal error creating char");
                return;
            }
            equips.save(con, id);
            con.commit();
            ps.close();
            rs.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public MapleCharacter(int id) {
        this.id = id;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }
            name = rs.getString("name");
            accountId = rs.getInt("accountid");
            mapId = rs.getInt("map");
            level = rs.getShort("level");
            job = Jobs.Job.getJobById(rs.getShort("job"));
            cSkills = new CharacterSkills(rs);
            stats = new CharacterStats(rs);
            body = new CharacterBody(rs);
            characterTraits = new CharacterTrait(rs);
            fatigue = rs.getShort("fatigue");
            List<Integer> equipList = new LinkedList<>();
            ps.close();
            rs.close();
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while(rs.next()) {
                if(rs.getInt("position") < 0) {
                    equipList.add(rs.getInt("itemid"));
                }
            }
            equips = new CharacterEquips(equipList);
            ps.close();
            rs.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public MapleCharacter(int id, IoSession session) {
        this(id);
        this.session = session;
    }

    public static void deleteCharacter(int charId) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            ps = con.prepareStatement("DELETE FROM characters WHERE id = ?");
            ps.setInt(1, charId);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("DELETE FROM inventoryitems WHERE characterid = ?");
            ps.setInt(1, charId);
            ps.execute();
            con.commit();
            ps.close();
        } catch (SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public void save() {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps;
        ResultSet rs;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters (accountid, name, level, job, subcategory, str, dex, luk, `int`, maxhp, maxmp, hp, mp, ap, exp, fame, sp, " +
                                      "gender, skincolor, face, hair, facemarking)" +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            int index = 0;
            ps.setInt(++index, accountId);
            ps.setString(++index, name);
            ps.setInt(++index, level);
            ps.setInt(++index, job.getJobId());
            ps.setInt(++index, subcategory);
            index = stats.save(ps, index);
            index = cSkills.save(ps, index);
            index = body.save(ps, index);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                Logging.exceptionLog("fatal error creating char");
                return;
            }
            equips.save(con, id);
            con.commit();
            ps.close();
            rs.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public void writeGWCharacterStats(PacketWriter pw) {
        pw.writeInt(id);
        pw.writeInt(id);
        pw.writeInt(ServerConfig.WORLD);
        pw.writeAsciiString(name, 13);
        body.addCharBody(pw);
        pw.write(level);
        pw.writeShort(job.getJobId());
        stats.addCharStats(pw);
        cSkills.addCharSp(pw, job);
        stats.addMisc(pw);
        pw.writeInt(0); // Waru points
        pw.writeInt(0);
        pw.writeInt(mapId);
        pw.write(0); //spawnpoint
        pw.writeInt(0);
        pw.writeShort(subcategory); //subcategory
        //facemarking
        pw.write(fatigue);
        pw.writeInt(0); //current date
        for (TraitType t : TraitType.values()) {
            pw.writeInt(characterTraits.traits.get(t).getTotalExp());
        }
        for (TraitType t : TraitType.values()) {
            pw.writeShort(0); //today's stats
        }
        pw.write(0);
        pw.writeLong(0); //some time

        pw.writeInt(0);
        pw.write(0);
        pw.writeInt(0);
        pw.write(5); // pvp mode level
        pw.write(6); // pvp mode type
        pw.writeInt(0); // event points
        //partime job
        pw.write(0);
        pw.writeReversedLong(0);
        pw.writeInt(0);
        pw.write(0);
        /*
         * Character Card
         *
         */
        for (int i = 0; i < 9; i++) {
            pw.writeInt(0);
            pw.write(0);
            pw.writeInt(0);
        }

        pw.writeReversedLong(0); // account last login

        // is this character burning
        pw.write(0);
    }

    public void writeAvatarLook(PacketWriter pw) {
        pw.write(body.gender);
        pw.write(body.skinColor);
        pw.writeInt(body.face);
        pw.writeInt(job.getJobId());
        pw.write(1);
        pw.writeInt(body.hair);
        equips.writeEquips(pw);
        pw.write(255);
        //masked
        pw.write(255);
        //totem
        pw.write(255);
        pw.writeInt(0); //cash weapon
        Integer weapon = equips.equips.get(EquipSlot.WEAPON);
        pw.writeInt(weapon != null ? weapon : 0);

        //boolean zero = GameConstants.isZero(chr.getJob());
        Integer shield = equips.equips.get(EquipSlot.SECONDARY);
        pw.writeInt(shield != null ? shield : 0);

        pw.write(0); // mercedes ear

        // all 3 pets unique id
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);

//        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob())) {
//            pw.writeInt(chr.getFaceMarking());
//        } else if (GameConstants.isZero(chr.getJob())) {
//            pw.write(1);
//        }

        /*
         if (JobConstants.isBeastTamer(chr.getJob())) { // tale and ears
            pw.write(1);
            pw.writeInt(5010116);
            pw.write(1);
            pw.writeInt(5010119);
         }
         */
        pw.write(0); // mixed hair color
        pw.write(0); // mixed hair percent
    }

    public void writeSeeds(PacketWriter pw) {
        rand.writeSeeds(pw);
    }

    public void writePacket(byte[] packet) {
        session.write(packet);
    }

    public Point getPosition() {
        return position;
    }

    public boolean inRange(Point dest) {
        if(dest.distanceSq(position) <= ServerConfig.maxViewRangeRangeSq) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void sendSpawnData(MapleCharacter chr) {

    }

    @Override
    public void sendDestroyData(MapleCharacter chr) {

    }
}
