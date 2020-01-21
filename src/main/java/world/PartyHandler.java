package world;

import game.character.MapleCharacter;
import packet.CWvsContext;
import server.ChannelServer;
import utils.DatabaseConnection;
import utils.data.LittleEndianAccessor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PartyHandler {

    private static final ConcurrentMap<Integer, Party> parties = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Party> playerPartyMap = new ConcurrentHashMap<>();
    private static final AtomicInteger runningPartyId = new AtomicInteger(1);

    static {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET party = -1, fatigue = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
        }
    }

/*    public static synchronized void handlePartyOperation(LittleEndianAccessor lea, MapleCharacter chr) {
        int operation = lea.readByte();
        Party party = playerPartyMap.get(chr.getId());

        switch (operation) {
            case 1:
                if (party == null) {
                    party = createParty(chr);
                    chr.writePacket(CWvsContext.createParty(party.getId()));
                } else {
                    chr.writePacket(CWvsContext.sendMessage(5, "You can't create a party as you are already in one"));
                }
                break;
            case 2:
                if (party == null) {
                    break;
                }
                PartyCharacter leader = party.getLeader();
                PartyCharacter pc = party.getMemberById(chr.getId());

                if(pc.equals(leader)) {
                    for(PartyCharacter member : party.getMembers()) {
                        member.sendPacket(CWvsContext.updateParty(leader.getChannel(), party, PartyOperation.DISBAND, pc));
                    }
                    for(PartyCharacter member : party.getMembers()) {
                        playerPartyMap.remove(member.getId());
                    }
                    parties.remove(party.getId());
                } else {
                    for(PartyCharacter member : party.getMembers()) {
                        member.sendPacket(CWvsContext.updateParty(leader.getChannel(), party, PartyOperation.LEAVE, pc));
                    }
                    party.removeMember(pc);
                    playerPartyMap.remove(pc.getId());
                }
                break;
            case 3:
                int partyId = lea.readInt();
                if(party == null) {
                    party = parties.get(partyId);
                    if(party != null) {
                        if((party.getMembers().size() < 6)) {
                            playerPartyMap.put(chr.getId(), party);
                            try {
                                ChannelServer.processors[chr.getChannel()].queue.put((Runnable) () -> {
                                    chr.updatePartyMemberHP();
                                    chr.receivePartyMemberHP();
                                });
                            }
                            catch(InterruptedException e) {};
                        }
                        else {
                            chr.writePacket(CWvsContext.partyStatusMessage(22, null));
                        }
                    }
                    else {
                        chr.writePacket(CWvsContext.sendMessage(5, "The party you are trying to join does not exist"));
                    }
                }
                else {
                    chr.writePacket(CWvsContext.sendMessage(5, "You can't join the party as you are already in one"));
                }
                break;
            case 4:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsCoartntext.PartyPacket.pyCreated(party.getId()));
                }

                String theName = lea.readMapleAsciiString();
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 8) {
                            //c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(33, invited.getName()));
                            c.getPlayer().dropMessage(5, "You have invited " + invited.getName() + " to the party.");
                            invited.getClient().getSession().write(CWvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "The person you are trying to invite is already in a party.");
                    }
                } else {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(17, null));
                }
                break;
            case 6://was5
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(lea.readInt());
                if (expelled != null) {
                    if ((MapConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        MapleDojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case 7://was 6
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(lea.readInt());
                if ((newleader != null) && (partyplayer.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                }
                break;
            case 66://was 7
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (MapConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = lea.readInt();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 8)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().getSession().write(CWvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                int partyId = lea.readInt();
                MapleParty partyToJoin = World.Party.getParty(partyId);
                MapleCharacter leader = c.getPlayer().getMap().getCharacterById(partyToJoin.getLeader().getId());
                if(leader != null) {
                    leader.getClient().getSession().write(CWvsContext.PartyPacket.partyJoinRequest(c.getPlayer()));
                    c.getPlayer().dropMessage(5, "You have applied to the party.");
                    partyToJoin.setPending(new MaplePartyCharacter(c.getPlayer()));
                }
                else {
                    c.getPlayer().dropMessage(5, "The leader of the party must be in the same map.");
                }
                break;
            case 999:
                if (lea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122900));
                }
                break;
            default:
                Logging.log("Unhandled Party function." + operation);
        }
    }*/
/*
    public static void partyChat(int partyid, String chattext, String namefrom) {
        partyChat(partyid, chattext, namefrom, 1);
    }*/



/*    public static void partyPacket(int partyid, byte[] packet, MaplePartyCharacter exception) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            return;
        }

        for (MaplePartyCharacter partychar : party.getMembers()) {
            int ch = Find.findChannel(partychar.getName());
            if (ch > 0 && (exception == null || partychar.getId() != exception.getId())) {
                MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) { //Extra check just in case
                    chr.getClient().getSession().write(packet);
                }
            }
        }
    }

    public static void partyChat(int partyid, String chattext, String namefrom, int mode) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            return;
        }

        for (MaplePartyCharacter partychar : party.getMembers()) {
            int ch = Find.findChannel(partychar.getName());
            if (ch > 0) {
                MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null && !chr.getName().equalsIgnoreCase(namefrom)) { //Extra check just in case
                    chr.getClient().getSession().write(CField.multiChat(namefrom, chattext, mode));
                    if (chr.getClient().isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, "[GM Message] " + namefrom + " said to " + chr.getName() + " (Party): " + chattext));
                    }
                }
            }
        }
    }*/

/*    public static void partyMessage(int partyid, String chattext) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            return;
        }

        for (MaplePartyCharacter partychar : party.getMembers()) {
            int ch = Find.findChannel(partychar.getName());
            if (ch > 0) {
                MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) { //Extra check just in case
                    chr.dropMessage(5, chattext);
                }
            }
        }
    }*/

/*    public static void expedMessage(int expedId, String chattext) {
        MapleExpedition party = getExped(expedId);
        if (party == null) {
            return;
        }
        for (int i : party.getParties()) {
            partyMessage(i, chattext);
        }
    }


    public static Party createParty(MapleCharacter chr) {
        PartyCharacter pc = new PartyCharacter(chr);
        Party party = new Party(runningPartyId.getAndIncrement(), pc);
        parties.put(party.getId(), party);
        playerPartyMap.put(chr.getId(), party);
        return party;
    }

    public static MapleParty createPartyAndAdd(MaplePartyCharacter chrfor, int expedId) {
        MapleExpedition ex = getExped(expedId);
        if (ex == null) {
            return null;
        }
        MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor, expedId);
        parties.put(party.getId(), party);
        ex.getParties().add(party.getId());
        return party;
    }

    public static MapleParty getParty(int partyid) {
        return parties.get(partyid);
    }

    public static MapleExpedition getExped(int partyid) {
        return expeds.get(partyid);
    }

    public static MapleExpedition disbandExped(int partyid) {
        PartySearch toRemove = getSearchByExped(partyid);
        if (toRemove != null) {
            removeSearch(toRemove, "The Party Listing was removed because the party disbanded.");
        }
        final MapleExpedition ret = expeds.remove(partyid);
        if (ret != null) {
            for (int p : ret.getParties()) {
                MapleParty pp = getParty(p);
                if (pp != null) {
                    updateParty(p, PartyOperation.DISBAND, pp.getLeader());
                }
            }
        }
        return ret;
    }


    public static boolean partyListed(MapleParty party) {
        return getSearchByParty(party.getId()) != null;
    }*/

    public static Party getPartyByChrId(int chrId) {
        return playerPartyMap.get(chrId);
    }
}