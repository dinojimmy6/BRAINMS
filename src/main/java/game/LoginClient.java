package game;

import encryption.LoginCrypto;
import game.character.MapleCharacter;
import org.apache.mina.core.session.IoSession;
import packet.LoginPacket;
import packet.RecvPacketOpcode;
import packet.SendPacketOpcode;
import server.LoginServer;
import utils.CharacterUtil;
import utils.DatabaseConnection;
import utils.Logging;
import utils.data.LittleEndianAccessor;
import utils.data.PacketWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class LoginClient {
    enum LoginState {NOT_CONNECTED, CONNECTED, SELECT, LOGGED_IN}
    IoSession session;
    String user;
    LoginState state;
    int attempts;
    int accId;
    String pin;
    int channel;

    public LoginClient(IoSession session) {
        this.session = session;
        state = LoginState.CONNECTED;
    }

    public void disconnect() {
        if(state != LoginState.LOGGED_IN) {
            LoginServer.logins.remove(accId);
        }
    }

    public void sendHotfixResponse() {
        session.write(LoginPacket.hotfixResponse());
    }

    public void sendChannelSelect(LittleEndianAccessor lea) {
        short worldId = lea.readShort();
        session.write(LoginPacket.channelSelect(0));
    }

    public void sendAuthServer() {
        session.write(LoginPacket.authServer());
    }

    public void handleWorldInfoRequest() {
        state = LoginState.SELECT;
        session.write(LoginPacket.worldInfo());
        session.write(LoginPacket.worldInfoEnd());
    }

    public void sendAuthResponse(LittleEndianAccessor lea) {
        session.write(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getValue() ^ lea.readInt()));
    }

    public void  handleLogin(LittleEndianAccessor lea) {
        lea.readByte();
        String pw = lea.readMapleAsciiString();
        user = lea.readMapleAsciiString();
        int loginStatus;
        Integer gm;
        byte reason;

        int banned = 0;
        String hash = null;
        String salt = null;
        String oldSession = null;
        String verify = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                banned = rs.getInt("banned");
                hash = rs.getString("password");
                salt = rs.getString("salt");
                oldSession = rs.getString("SessionIP");
                verify = rs.getString("verification_key");
                accId = rs.getInt("id");
                pin = rs.getString("pin");
                gm = rs.getInt("gm");
                reason = rs.getByte("greason");
            }
            ps.close();
            rs.close();
        }
        catch(SQLException e) {
            Logging.exceptionLog("ERROR" + e);
            return;
        }
        if(verify == null || !verify.equals("micromoose")) {
            loginStatus = 6;
        }
        else if(banned > 0) {
            loginStatus = 3;
        }
        else if(LoginServer.logins.containsKey(accId)) {
            loginStatus = 7;
        }
        else if(LoginCrypto.checkSha1Hash(hash, pw)) {
            loginStatus = 0;
        }
        else {
            loginStatus = 4;
        }
        if(loginStatus != 0) {
            ++attempts;
            if(attempts > 3) {
                session.closeNow();
            }
            else {
                session.write(LoginPacket.loginFail(loginStatus));
            }
        }
        else {
            attempts = 0;
            LoginServer.logins.put(accId, session.getRemoteAddress().toString().split(":")[0]);
            state = LoginState.SELECT;
            session.write(LoginPacket.loginSuccess(user, accId));
        }
    }

    public void handleCharList(LittleEndianAccessor lea) {
        lea.readByte();
        lea.readByte();
        channel = lea.readByte() + 1;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ?");
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            List<MapleCharacter> chrList = new LinkedList<>();
            while(rs.next()) {
                chrList.add(new MapleCharacter(rs.getInt("id")));
            }
            ps.close();
            rs.close();
            session.write(LoginPacket.charList(chrList, pin == null ? 0 : 1));
        }
        catch(SQLException e) {
            Logging.exceptionLog(e);
        }
    }

    public void handleNewCharCreation(LittleEndianAccessor lea) {
        if(state == LoginState.SELECT) {
            MapleCharacter chr = new MapleCharacter(lea, accId);
            session.write(LoginPacket.writeNewCharEntry(chr));
        }
    }

    public void handleDeleteChar(LittleEndianAccessor lea) {
        int state = 0;
        String pinIn = lea.readMapleAsciiString();
        int charId = lea.readInt();
        if(pin != null && pin.equals(pinIn)) {
            MapleCharacter.deleteCharacter(charId);
        }
        else {
            state = 20;
        }
        session.write(LoginPacket.deleteChar(charId, state));
    }

    public void handleCheckName(LittleEndianAccessor lea) {
        String name = lea.readMapleAsciiString();
        if(CharacterUtil.canCreateChar(name)) {
            session.write(LoginPacket.checkNameResponse(name, false));
        }
        else {
            session.write(LoginPacket.checkNameResponse(name, true));
        }
    }

    public void handleCreatePin(LittleEndianAccessor lea) {
        lea.readByte();
        lea.readByte();
        int charId = lea.readInt();
        if(pin != null) {
            Logging.exceptionLog("Pin already exists");
        }
        lea.readMapleAsciiString();
        lea.readMapleAsciiString();
        pin = lea.readMapleAsciiString();
        if(pin.length() >= 6 && pin.length() <= 16) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `pin` = ? WHERE id = ?");
                ps.setString(1, pin);
                ps.setInt(2, accId);
                ps.executeUpdate();
                ps.close();

            } catch (SQLException e) {
                Logging.exceptionLog("error updating login state " + e);
            }
        }
        else {
            Logging.exceptionLog("Invalid pin requested");
        }
    }

    public void handleCheckPin(LittleEndianAccessor lea) {
        final String pinIn = lea.readMapleAsciiString();
        final int charId = lea.readInt();
        lea.readByte();
        if(!pinIn.equals(pin)) {
            session.write(LoginPacket.pinError());
        }
        else {
            state = LoginState.LOGGED_IN;
            session.write(LoginPacket.finishLogin(channel, charId));
            LoginServer.characters.put(LoginServer.logins.get(accId), charId);
        }
    }

    public void handlePacket(RecvPacketOpcode header, LittleEndianAccessor lea) {
        switch(header) {
            case CLIENT_CONNECT:
                break;
            case LOGIN:
                handleLogin(lea);
                break;
            case CHAR_LIST_REQUEST:
                handleCharList(lea);
                break;
            case CHECK_PIN:
                handleCheckPin(lea);
                break;
            case CHECK_NAME:
                handleCheckName(lea);
                break;
            case CREATE_CHAR_REQUEST:
                handleNewCharCreation(lea);
                break;
            case DELETE_CHAR_REQUEST:
                handleDeleteChar(lea);
                break;
            case AUTH_REQUEST:
                sendAuthResponse(lea);
                break;
            case CHANNEL_SELECT_REQUEST:
                sendChannelSelect(lea);
                break;
            case HOTFIX:
                sendHotfixResponse();
                break;
            case REDISPLAY_WORLD_INFO:
            case WORLD_INFO_REQUEST:
                handleWorldInfoRequest();
                break;
            case CREATE_PIN:
                handleCreatePin(lea);
                break;
            case AUTH_SERVER:
                sendAuthServer();
                break;
            default:
                break;
        }
    }
}
