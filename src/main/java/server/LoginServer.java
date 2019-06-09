package server;

import encryption.CodecFactory;
import game.character.MapleCharacter;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import utils.Logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginServer {
    private static InetSocketAddress InetSocketadd;
    private static SocketAcceptor acceptor;
    public static Map<Integer, String> logins = new ConcurrentHashMap<>();
    public static Map<String, Integer> characters = new ConcurrentHashMap<>();
    public static final void run_startup_configurations() {
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.setCloseOnDeactivation(true);
        acceptor.setHandler(new LoginServerHandler());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
        try {
            InetSocketadd = new InetSocketAddress(8484);
            acceptor.bind(InetSocketadd);
            Logging.log("Login Server is listening on port 8484.");
        } catch (IOException e) {
            Logging.exceptionLog("Could not bind to port 8484: " + e);
        }
    }
}
