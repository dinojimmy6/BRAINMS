package game.character;

import constants.Jobs;
import constants.Jobs.Job;
import constants.ServerConfig;
import constants.Flags.Stat;
import game.equip.EquipSlot;
import game.item.Equip;
import game.item.ItemFactory;
import game.map.MapObject;
import game.map.MapleMap;
import game.map.MapleMapFactory;
import game.monster.AbstractLife;
import game.monster.Monster;
import game.random.CRand32;
import game.skill.*;
import org.apache.mina.core.session.IoSession;
import packet.CStage;
import packet.CUserPool;
import packet.CWvsContext;
import packet.MobPacket;
import utils.CharacterUtil;
import utils.DatabaseConnection;
import utils.Logging;
import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;
import game.character.Trait.TraitType;
import world.Party;
import world.PartyCharacter;
import world.PartyHandler;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class MapleCharacter extends AbstractLife {
    private IoSession session;
    private final int id, accountId;
    private final String name;
    private volatile int level;
    private Jobs.Job job;
    private volatile int mapId;
    private MapleMap map;
    private volatile int channel;
    int fatigue; //TODO get this into professions class
    public CharacterSkills cSkills;
    public CharacterBody body;
    public LinkedList<CharacterStats> stats = new LinkedList<>();
    public CharacterTrait characterTraits;
    public CharacterEquips equips;
    public Map<Integer, BuffSkillHandler> registeredBuffs = new HashMap<>();

    public CRand32 rand = new CRand32();

    private final Point position = new Point(698, 237);
    private Set<Monster> controlled = new HashSet<>();

    public static MapleCharacter buildMapleCharacter(LittleEndianAccessor lea, int accountId) {
        try {
            return new MapleCharacter(lea, accountId);
        }
        catch(CharacterConstructionException e) {
            return null;
        }
    }

    public static MapleCharacter buildMapleCharacter(int id) {
        try {
            return new MapleCharacter(id);
        }
        catch(CharacterConstructionException e) {
            return null;
        }
    }

    public static MapleCharacter buildMapleCharacter(int id, IoSession session) {
        try {
            return new MapleCharacter(id, session);
        }
        catch(CharacterConstructionException e) {
            return null;
        }
    }

    public MapleCharacter(LittleEndianAccessor lea, int accountId) throws CharacterConstructionException {
        byte gender, skin;
        int face, hair, hairColor = -1, hat = -1, top, bottom = -1, shoes, weapon, cape = -1, faceMark = -1, shield = -1;
        this.accountId = accountId;
        level = 1;
        mapId = 100000000;
        name = lea.readMapleAsciiString();
        if(!CharacterUtil.canCreateChar(name)) {
            Logging.exceptionLog("char name hack: " + name);
            throw new CharacterConstructionException();
        }

        lea.skip(4); // key type setting
        lea.skip(4); // -1

        int job_type = lea.readInt();
        Jobs.LoginJob lj = Jobs.LoginJob.getByLoginId(job_type);
        job = Jobs.Job.getJobById(lj.id);


        if (lj == null) {
            Logging.exceptionLog("New job type found: " + job_type);
            throw new CharacterConstructionException();
        }

        lea.readShort();
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
                equips.equip((Equip) ItemFactory.getItem(itemId));
            }
        }
        stats.add(new CharacterStats());
        cSkills = new CharacterSkills();
        characterTraits = new CharacterTrait();
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps;
        ResultSet rs;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters (accountid, name, map, level, job, str, dex, luk, `int`, maxhp, maxmp, hp, mp, ap, exp, fame, sp, " +
                                      "gender, skincolor, face, hair, facemarking)" +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            int index = 0;
            ps.setInt(++index, accountId);
            ps.setString(++index, name);
            ps.setInt(++index, mapId);
            ps.setInt(++index, level);
            ps.setInt(++index, job.getJobId());
            index = stats.get(0).save(ps, index);
            index = cSkills.saveSp(ps, index);
            index = body.save(ps, index);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                Logging.exceptionLog("fatal error creating char");
                throw new CharacterConstructionException();
            }
            equips.save(con, id);
            con.commit();
            ps.close();
            rs.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
            throw new CharacterConstructionException();
        }
    }

    public MapleCharacter(int id) throws CharacterConstructionException {
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
            CharacterStats cs = new CharacterStats(rs);
            body = new CharacterBody(rs);
            characterTraits = new CharacterTrait(rs);
            fatigue = rs.getShort("fatigue");
            List<Integer> equipList = new LinkedList<>();
            ps.close();
            rs.close();
            equips = new CharacterEquips(con, id);
            ps.close();
            rs.close();
            cSkills.loadSkills(con, id);
            cs.handlePassiveSkills(this);
            cs.calculateStats(this);
            stats.add(cs);
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
            throw new CharacterConstructionException();
        }
    }

    public MapleCharacter(int id, IoSession session) throws CharacterConstructionException {
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
        try {
            ps = con.prepareStatement("UPDATE characters SET level=?, job=?, subcategory=?, str=?," +
                                      " dex=?, luk=?, `int`=?, maxhp=?, maxmp=?, hp=?, mp=?, ap=?, exp=?, fame=?, sp=?, " +
                                      "gender=?, skincolor=?, face=?, hair=?, facemarking=? WHERE id = ?");
            int index = 0;
            ps.setInt(++index, level);
            ps.setInt(++index, job.getJobId());
            ps.setInt(++index, 0);
            index = stats.getLast().save(ps, index);
            index = cSkills.saveSp(ps, index);
            index = body.save(ps, index);
            ps.setInt(++index, id);
            ps.executeUpdate();
            equips.save(con, id);
            cSkills.save(id);
            ps.close();
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
        stats.getLast().addCharStats(pw);
        cSkills.addCharSp(pw, job);
        stats.getLast().addMisc(pw);
        pw.writeInt(0); // Waru points
        pw.writeInt(0);
        pw.writeInt(mapId);
        pw.write(0); //spawnpoint
        pw.writeInt(0);
        pw.writeShort(0); //subcategory
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
        Equip weapon = equips.equips.get(EquipSlot.WEAPON);
        pw.writeInt(weapon != null ? weapon.id : 0);

        //boolean zero = GameConstants.isZero(chr.getJob());
        Equip shield = equips.equips.get(EquipSlot.SECONDARY);
        pw.writeInt(shield != null ? shield.id : 0);

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

    public synchronized void writePacket(byte[] packet) {
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
        chr.writePacket(CUserPool.spawnPlayer(this));
    }

    @Override
    public void sendDestroyData(MapleCharacter chr) {
        chr.writePacket(CUserPool.removePlayer(id));
    }

    public static boolean isZero() {
        return false;
    }

    public void changeJob(Job targetJob) {
        job = targetJob;
        for(int skillId : getSkillsForJob()) {
            cSkills.addSkill(skillId);
        }
        cSkills.giveSp(targetJob.getRank(), 3);
        SkillManager.loadStaticBuffs(this);
        Map<Stat, Long> update = new HashMap<>();
        update.put(Stat.JOB, (long) targetJob.getJobId());
        Map<Stat, Long> update1 = new HashMap<>();
        update1.put(Stat.AVAILABLESP, 5L);
        writePacket(CWvsContext.updateStats(update, this));
        writePacket(CWvsContext.updateSkills(this));
        writePacket(CWvsContext.updateStats(update1, this));
    }

    public List<Integer> getSkillsForJob() {
        List<Integer> skillList = new ArrayList<>();
        switch (job) {
            case Hero:
                skillList.addAll(Arrays.asList(Hero.job4));
                skillList.addAll(Arrays.asList(Hero.hypers));
            case Crusader:
                skillList.addAll(Arrays.asList(Hero.job3));
            case Fighter:
                skillList.addAll(Arrays.asList(Hero.job2));
            case Warrior:
                skillList.addAll(Arrays.asList(Hero.job1));
                break;
            case Paladin:
                skillList.addAll(Arrays.asList(Paladin.job4));
                skillList.addAll(Arrays.asList(Paladin.hypers));
            case WhiteKnight:
                skillList.addAll(Arrays.asList(Paladin.job3));
            case Page:
                skillList.addAll(Arrays.asList(Paladin.job2));
                skillList.addAll(Arrays.asList(Hero.job1));
                break;
        }
        return skillList;
    }

    public int getDuration(Skill sk, int slv) {
        return (int) (stats.getLast().buffDuration / 100.0 + 1) * sk.getTime(slv) * 1000;
    }

    public void registerBuff(BuffSkillHandler bsh) {
        if(registeredBuffs.get(bsh.getKey()) != null) {
            registeredBuffs.get(bsh.getKey()).cancelSchedule();
        }
        registeredBuffs.put(bsh.getKey(), bsh);
    }

    public void unregisterBuff(BuffSkillHandler bsh) {
        registeredBuffs.remove(bsh.getKey());
    }

    public boolean drainMp(int drain) {
        if(stats.getLast().drainMp(drain)) {
            Map<Stat, Long> update = new HashMap<>();
            update.put(Stat.MP, (long) stats.getLast().mp);
            writePacket(CWvsContext.updateStats(update, this));
            return true;
        }
        return false;
    }

    public void controlMonster(Monster monster, boolean aggro) {
        if(monster == null) {
            return;
        }
        monster.setController(this);
        controlled.add(monster);
        writePacket(MobPacket.controlMonster(monster, aggro));
        //monster.writeAllStatus(this);
    }

    public void stopControllingMonster(Monster monster) {
        if(monster == null) {
            return;
        }
        if(controlled.contains(monster)) {
            controlled.remove(monster);
            monster.setController(null);
        }
    }

    public void updateMonsterAggro(Monster monster) {
        if(monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.controllerHasAggro = true;
            monster.updateLastControl();
        } else if(System.currentTimeMillis() - monster.getLastControlTime() > 30000){
            //monster.removeAllStatus(this);
            monster.switchController(this, true);
        }
    }

    public void enterMap(MapleMap map) {
        this.map = map;
        this.channel = map.channel;
        SkillManager.loadStaticBuffs(this);
        writePacket(CStage.getWarpToMap(this, channel, mapId, 0, true));
        writePacket(CWvsContext.updateSkills(this));
        writePacket(CWvsContext.setEventNameTag(new int[]{-1, -1, -1, -1, -1}));
        this.map.onCharEnterMap(this);
        this.map.addChar(this);
    }

    public void leaveMap() {
        for(Monster m : controlled) {
            map.updateMonsterController(m);
        }
        controlled.clear();
    }

    public Party getParty() {
        return null;
    }

    public int getControlledSize() {
        return controlled.size();
    }

    public void writeSpawnPlayer(PacketWriter pw) {
        pw.writeInt(id); // dwCharacterID
        // CUserRemote::Init
        pw.write(level);
        pw.writeMapleAsciiString(name);
        //MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        //if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            //pw.writeMapleAsciiString(ultExplorer.getCustomData());
        //} else {
            pw.writeMapleAsciiString("");
        //}
        //if (chr.getGuildId() <= 0) {
            pw.write(new byte[8]);
        //} else {
        //    MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        //    if (gs != null) {
        //        pw.writeMapleAsciiString(gs.getName());
        //        pw.writeShort(gs.getLogoBG());
        //        pw.write(gs.getLogoBGColor());
        //        pw.writeShort(gs.getLogo());
        //        pw.write(gs.getLogoColor());
        //    } else {
        //        pw.write(new byte[8]);
        //    }
        //}
        pw.write(body.gender);
        pw.writeInt(stats.getLast().fame);
        pw.writeInt(1); // farmLevel
        pw.writeInt(0); // NameTagMark

        writeDecodeForRemote(pw);

        pw.writeShort(job.getJobId());
        pw.writeShort(0); //subcategory
        pw.writeInt(0); // nTotalCHUC (star force enchantment)

        writeAvatarLook(pw);
        /*if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(pw, chr, true, false);
        }*/

        pw.writeInt(0); // dwDriverID
        pw.writeInt(0); // dwPassengerID
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);

        // nChocoCount
        pw.writeInt(0);

        // chr.getItemEffect()
        pw.writeInt(0); // nActiveEffectItemID
        pw.writeInt(0); // nMonkeyEffectItemID

        // MapleQuestStatus status = chr.getQuestNoAdd(MapleQuest.getInstance(124000));
        // status != null && status.getCustomData() != null ? Integer.parseInt(status.getCustomData()) :
        pw.writeInt(0);

        pw.writeInt(0); // nDamageSkin
        pw.writeInt(0); // ptPos
        pw.writeInt(0); // nDemonWing
        pw.writeInt(0); // nKaiserWingID
        pw.writeInt(0); // nKaiserTailID
        pw.writeInt(0); // nCompletedSetItemID
        pw.writeShort(-1); // nFieldSeatID

        // nPortableChairID

        /*pw.writeInt(GameConstants.getInventoryType(chr.getChair()) ==
                    MapleInventoryType.SETUP ? chr.getChair() : 0);*/
        //chair
        pw.writeInt(0);

        pw.writeInt(0);
        pw.writeInt(0); // lTowerChairIDList
        pw.writeInt(0); // head title? chr.getHeadTitle()
        pw.writeInt(0);
        pw.writeShort(200);
        pw.writeShort(200);
        pw.write(getStance());
        pw.writeShort(fh);

        pw.write(0);
        pw.write(0);
        pw.write(0);
        pw.write(1);
        pw.write(0);

        /*pw.writeInt(chr.getMount().getLevel());
        pw.writeInt(chr.getMount().getExp());
        pw.writeInt(chr.getMount().getFatigue());*/
        //mount placeholder
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);

        //announcebox placeholder
        pw.write(0);

        /*pw.write((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0) ? 1 : 0);
        if ((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0)) {
            pw.writeMapleAsciiString(chr.getChalkboard());
        }*/
        //chalkboard placeholder
        pw.write(0);

        /*Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(pw, rings.getLeft());
        addRingInfo(pw, rings.getMid());
        addMRingInfo(pw, rings.getRight(), chr);
        */
        //rings placeholder
        pw.write(0);
        pw.write(0);
        pw.write(0);

        pw.write(0); // mask
        pw.writeInt(0); // nEvanDragonGlide_Riding

        /*if (GameConstants.isKaiser(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            pw.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            pw.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "primium");
            pw.write(x == null ? 0 : Integer.parseInt(x));
        }*/

        pw.writeInt(0); // nSkillID (CUser::SetMakingMeisterSkillEff(..., nSkillID)

        //Farm
        pw.writeMapleAsciiString("");
        pw.writeInt(0); // nFarmPoint
        pw.writeInt(0); // nFarmLevel
        pw.writeInt(0); // nFarmExp
        pw.writeInt(0); // nDecoPoint
        pw.writeInt(0); // nFarmCash
        pw.write(0); // nFarmGender
        pw.writeInt(0); // nFarmTheme
        pw.writeInt(0); // nSlotExtend
        pw.writeInt(1); // nLockerSlotCount

        for (int i = 0; i < 5; i++) {
            pw.write(-1);
        }

        pw.writeInt(0); // nItemID
        pw.write(0); // bSoulEffect
        pw.write(0); // ?

        // StarPlanetRank::Decode
        pw.write(0);

        // DecodeStarPlanetTrendShopLook
        pw.writeInt(0);

        // DecodeTextEquipInfo
        pw.writeInt(0);

        // DecodeFreezeHotEventInfo
        pw.write(0); // nAccountType
        pw.writeInt(0); // dwAccountID

        // DecodeEventBestFriendInfo
        pw.writeInt(0); // dwEventBestFriendAID

        pw.write(0); // bOnOff (OnKinesisPsychicEnergyShieldEffect)
        pw.write(1); // bBeastFormWingOnOff
        pw.writeInt(0); // nMeso

        pw.writeInt(1);
        pw.writeInt(0);
        pw.writeMapleAsciiString("");
        pw.writeInt(0);
        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
    }

    public void writeDecodeForRemote(PacketWriter pw) {
        Map<BuffStat, List<Integer>> buffStats = new EnumMap<BuffStat, List<Integer>>(BuffStat.class);
        for(BuffSkillHandler bsh : registeredBuffs.values()) {
            buffStats.putAll(bsh.getBuffStats());
        }
        writeDecodeForRemote(pw, buffStats);
    }

    public void writeDecodeForRemote(PacketWriter pw, Map<BuffStat, List<Integer>> buffStats) {
        Set<BuffStat> buffStatsSet = buffStats.keySet();
        CWvsContext.writeBuffMask(pw, buffStatsSet);
        if(buffStats.containsKey(BuffStat.Speed)) {
            pw.write(buffStats.get(BuffStat.Speed).get(0));
        }
        if(buffStats.containsKey(BuffStat.ComboCounter)) {
            pw.write(buffStats.get(BuffStat.ComboCounter).get(0));
        }
        if(buffStats.containsKey(BuffStat.WeaponCharge)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ElementalCharge)) {
            pw.writeShort(buffStats.get(BuffStat.ElementalCharge).get(0));
        }
        if(buffStats.containsKey(BuffStat.Stun)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Shock)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.Darkness)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Seal)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Weakness)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.WeaknessMdamage)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Curse)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Slow)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.IceKnight)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.TimeBomb)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Team)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.DisOrder)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Thread)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Poison)) {
            pw.writeShort(0);
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ShadowPartner)) {
            pw.writeShort(1);
            /*switch(chr.job) {
                case 411:
                case NightLord:
                    pw.writeInt(NightLord.ShadowPartner);
                    break;
                case 421:
                case 422:
                    pw.writeInt(Shadower.ShadowPartner);
                    break;
                case 433:
                case 434:
                    pw.writeInt(DualBlade.MirrorImage);
                    break;
                case 3112:
                    pw.writeInt(DemonSlayer.BlueBlood);
                    break;
                default:
                    pw.writeInt(Shadower.ShadowPartner);
            }*/
        }
        if(buffStats.containsKey(BuffStat.Morph)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Ghost)) {
            pw.writeShort(0);
        }
        if(buffStats.containsKey(BuffStat.Attract)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Magnet)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.MagnetArea)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.NoBulletConsume)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.BanMap)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Barrier)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DojangShield)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ReverseInput)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.RespectPImmune)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.RespectMImmune)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DefenseAtt)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DefenseState)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DojangBerserk)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.RepeatEffect)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.StopPortion)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.StopMotion)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Fear)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.MagicShield)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Frozen)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Frozen2)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Web)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DrawBack)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.FinalCut)) {
            pw.writeShort(1);
            //pw.writeInt(DualBlade.FinalCut);
        }
        if(buffStats.containsKey(BuffStat.Cyclone)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.OnCapsule)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.Mechanic)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Inflation)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Explosion)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DarkTornado)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AmplifyDamage)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.HideAttack)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DevilishPower)) {
            pw.writeShort(1);
            //pw.writeInt(DemonSlayer.DarkMetamorphosis);
        }
        if(buffStats.containsKey(BuffStat.SpiritLink)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Event)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Event2)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DeathMark)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }if(buffStats.containsKey(BuffStat.PainMark)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Lapidification)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.VampDeath)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.VampDeathSummon)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.VenomSnake)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.PyramidEffect)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.KillingPoint)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.IgnoreTargetDEF)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Invisible)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Judgement)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.KeyDownAreaMoving)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.StackBuff)) {
            pw.writeShort(1);
        }
        if(buffStats.containsKey(BuffStat.BlessOfDarkness)) {
            pw.writeInt(buffStats.get(BuffStat.BlessOfDarkness).get(0));
        }
        if(buffStats.containsKey(BuffStat.Larkness)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ReshuffleSwitch)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SpecialAction)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.StopForceAtomInfo)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SoulGazeCriDamR)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.PowerTransferGauge)) {
            pw.writeShort(1);
            //pw.writeInt(Marksman.AggressiveResistance);
        }
        if(buffStats.containsKey(BuffStat.AffinitySlug)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SoulExalt)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.HiddenPieceOn)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SmashStack)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.MobZoneState)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.GiveMeHeal)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.TouchMe)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Contagion)) {
            pw.writeShort(0);
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ComboUnlimited)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.IgnorePCounter)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.IgnoreAllCounter)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.IgnorePImmune)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.IgnoreAllImmune)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.FinalJudgement)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.KnightsAura)) {
            pw.writeShort(1);
            pw.writeInt(Paladin.ParashockGuard);
        }
        if(buffStats.containsKey(BuffStat.IceAura)) {
            pw.writeShort(1);
            //pw.writeInt(ILMage.AbsoluteZeroAura);
        }
        if(buffStats.containsKey(BuffStat.FireAura)) {
            pw.writeShort(1);
            //pw.writeInt(FpMage.InfernoAura);
        }
        if(buffStats.containsKey(BuffStat.HeavensDoor)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DamAbsorbShield)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AntiMagicShell)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.NotDamaged)) {
            pw.writeShort(1);
            pw.writeInt(Paladin.Sacrosanctity);
        }
        if(buffStats.containsKey(BuffStat.BleedingToxin)) {
            pw.writeShort(1);
            //pw.writeInt(NightLord.BleedDart);
        }
        if(buffStats.containsKey(BuffStat.WindBreakerFinal)) {
            pw.writeShort(1);
            //pw.writeInt(DualBlade.BladeClone);
        }
        if(buffStats.containsKey(BuffStat.IgnoreMobDamR)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Asura)) {
            pw.writeShort(1);
            //pw.writeInt(DualBlade.AsurasAnger);
        }
        if(buffStats.containsKey(BuffStat.UnityOfPower)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Stimulate)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ReturnTeleport)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.CapDebuff)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.OverloadCount)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.FireBomb)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SurplusSupply)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.NewFlying)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.NaviFlying)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AmaranthGenerator)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.CygnusElementSkill)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.StrikerHyperElectric)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.EventPointAbsorb)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.EventAssemble)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Albatross)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Translucence)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.PoseType)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.LightOfSpirit)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ElementSoul)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.GlimmeringTime)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Reincarnation)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Beholder)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.QuiverCatridge)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ArmorPiercing)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ZeroAuraStr)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ZeroAuraSpd)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ImmuneBarrier)) {
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.FullSoulMP)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AntiMagicShell)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.Dance)) {
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.SpiritGuard)) {
            pw.writeInt(buffStats.get(BuffStat.SpiritGuard).get(1));
            //pw.writeInt(Shade.SpiritWard);
        }
        if(buffStats.containsKey(BuffStat.ComboTempest)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.HalfstatByDebuff)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.ComplusionSlant)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.JaguarSummoned)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.BMageAura)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.DarkLighting)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AttackCountX)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.FireBarrier)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.KeyDownMoving)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.MichaelSoulLink)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.KinesisPsychicEnergeShield)) {
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.BladeStance)) {
            pw.writeShort(0);
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.BowMasterConcentration)) {
            pw.writeShort(1);
            //pw.writeInt(Bowmaster.Concentration);
        }
        if(buffStats.containsKey(BuffStat.Fever)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AdrenalinBoost)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.RWBarrier)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.RWMagnumBlow)) {
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.Stigma)) {
            pw.writeShort(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.PoseType)) {
            pw.write(0);
        }
        pw.write(0); //defenseatt
        pw.write(0); //defensestate
        pw.write(0); //pvpdamage
        if(buffStats.containsKey(BuffStat.ZeroAuraStr)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.ZeroAuraSpd)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.BMageAura)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.MichaelSoulLink)) {
            pw.writeInt(0);
            pw.write(0);
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(buffStats.containsKey(BuffStat.AdrenalinBoost)) {
            pw.write(0);
        }
        if(buffStats.containsKey(BuffStat.Stigma)) {
            pw.writeInt(0);
        }
        pw.writeInt(0); //stopforceatom id
        pw.writeInt(0); //count
        pw.writeInt(0); //weaponid
        pw.writeInt(0); //iterations
        //if(registeredBuffs.get(Buccaneer.EnergyCharge) != null) {
        //    pw.writeInt(chr.registeredBuffs.get(Buccaneer.EnergyCharge).getSecondaryStatValue());
        //}
        //else {
            pw.writeInt(0);
        //}
        //TwoState
        for(int i = 0; i < 8; i++) {
            BuffStat stat = BuffStat.getCTSFromTSIndex(i);
            if (buffStats.containsKey(stat)) {
                pw.writeInt(buffStats.get(stat).get(0));
                pw.writeInt(0);
                pw.write(0);
                pw.writeInt(0);
            }
        }
    }

    public void broadcastMessage(byte[] packet) {
        for(MapleCharacter chr : map.chars.values()) {
            if(chr.inRange(position) && chr != this) {
                chr.writePacket(packet);
            }
        }
    }

    public void updatePartyMemberHP() {
        Party party = PartyHandler.getPartyByChrId(id);
        if (party != null) {
            byte[] packet = CUserPool.updatePartyMemberHP(getId(), stats.getLast().getCurHp(), stats.getLast().getMaxHp());
            for(PartyCharacter pc : party.getMembers()) {
                MapleCharacter partyChr = map.getChrFromMap(pc.getId());
                if(partyChr != null) {
                    partyChr.writePacket(packet);
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        Party party = PartyHandler.getPartyByChrId(id);
        if (party != null) {
            for(PartyCharacter pc : party.getMembers()) {
                MapleCharacter partyChr = map.getChrFromMap(pc.getId());
                if(partyChr != null) {
                    byte[] packet = CUserPool.updatePartyMemberHP(getId(), partyChr.stats.getLast().getCurHp(), partyChr.stats.getLast().getMaxHp());
                    writePacket(packet);
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getMapId() {
        return mapId;
    }

    public MapleMap getMap() {
        return map;
    }

    public int getChannel() {
        return channel;
    }

    public Job getJob() {
        return job;
    }

    public int getJobId() {
        return job.getJobId();
    }


    private class CharacterConstructionException extends Exception {
    }
}
