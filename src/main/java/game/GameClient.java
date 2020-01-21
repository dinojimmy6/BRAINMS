package game;

import constants.Skill;
import game.character.MapleCharacter;
import game.skill.AttackInfo;
import game.skill.Paladin;
import game.skill.SkillFactory;
import org.apache.mina.core.session.IoSession;
import packet.CWvsContext;
import packet.LoginPacket;
import packet.RecvPacketOpcode;
import packet.SendPacketOpcode;
import server.ChannelServer;
import server.LoginServer;
import utils.data.LittleEndianAccessor;
import constants.Jobs.Job;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class GameClient {
    IoSession session;
    MapleCharacter chr;
    int channel;
    SynchronousQueue<MapleCharacter> delivered;
    public GameClient(IoSession session) {
        this.session = session;
        this.channel = 0;
        delivered = new SynchronousQueue<>();
    }

    public void handleCharLogin(LittleEndianAccessor lea) {
        lea.readInt(); // this could be the world or account
        final int charId = lea.readInt();
        if(LoginServer.characters.get(session.getRemoteAddress().toString().split(":")[0]) != charId) {
            session.closeNow();
            return;
        }
        if(ChannelServer.characters.get(charId) != null) {
            //do change channel
        }
        else {
            chr = MapleCharacter.buildMapleCharacter(charId, session);
            if(chr == null) {
                session.closeNow();
                return;
            }
            try {
                ChannelServer.processors[channel].queue.put(chr);
            }
            catch(InterruptedException e) {}
        }
    }

    public void handlePacket(RecvPacketOpcode header, LittleEndianAccessor lea) throws InterruptedException {
        switch(header) {
            case CHAR_LOGIN:
                handleCharLogin(lea);
                break;
            case AUTH_REQUEST:
                session.write(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getValue() ^ lea.readInt()));
                break;
            case ON_MELEE_ATTACK: {
                AttackInfo ai = new AttackInfo(lea, chr);
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleMeleeAttack(chr, ai);
                });
                break;
            }
            case GENERAL_CHAT: {
                lea.skip(4);
                String text = lea.readMapleAsciiString();
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleGeneralChat(chr, text);
                });
                break;
            }
            case DISTRIBUTE_SP: {
                lea.skip(4); //update tick
                int skillId = lea.readInt();
                int amount = lea.readInt();
                if(amount <= 0) {
                    //ban
                    return;
                }
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleDistributeSp(chr, skillId, amount);
                });
                break;
            }
            case MOVE_PLAYER: {
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleMovePlayer(lea, chr);
                });
                break;
            }
            case SPECIAL_MOVE: {
                lea.skip(4);
                int skillId = lea.readInt();
                int slv = lea.readByte();
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleSpecialMove(lea, chr, skillId, slv);
                });
                break;
            }
            case MOVE_LIFE: {
                ChannelServer.processors[channel].queue.put((Runnable) () -> {
                    ChannelServer.processors[channel].handleMoveLife(lea, chr);
                });
            }
        }
    }

    public void disconnect() throws InterruptedException {
        LoginServer.logins.remove(chr.getAccountId());
        ChannelServer.processors[channel].queue.put((Runnable) () -> {
            chr.save();
            chr.getMap().onCharLeaveMap(chr);
        });
    }
}
