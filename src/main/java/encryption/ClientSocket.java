package encryption;

import constants.ServerConfig;
import game.LoginClient;
import org.apache.mina.core.session.IoSession;

public class ClientSocket<T> {
    public static final String KEY = ClientSocket.class.getName() + ".STATE";
    public MapleAESOFB recvCrypto;
    public MapleAESOFB sendCrypto;
    public T client;
    public int pLength;

    public ClientSocket(byte[] ivRecv, byte[] ivSend, T client) {
        recvCrypto = new MapleAESOFB(ivRecv, ServerConfig.MAPLE_VERSION);
        sendCrypto = new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConfig.MAPLE_VERSION));
        this.client = client;
        pLength = -1;
    }
}
