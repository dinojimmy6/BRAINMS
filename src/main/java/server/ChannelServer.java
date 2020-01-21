package server;

import encryption.CodecFactory;
import game.Timer;
import game.character.MapleCharacter;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import utils.Logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ChannelServer {
    private static InetSocketAddress InetSocketadd;
    private static SocketAcceptor acceptor;
    public static Map<Integer, MapleCharacter> characters = new HashMap<>();
    public static ChannelProcessor[] processors;
    public static Timer[] timers;

    public static final void run_startup_configurations() {
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.setCloseOnDeactivation(true);
        acceptor.setHandler(new ChannelServerHandler());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
        try {
            InetSocketadd = new InetSocketAddress(8585);
            acceptor.bind(InetSocketadd);
            Logging.log("Channel 1 server is listening on port 8585.");
        } catch (IOException e) {
            Logging.exceptionLog("Could not bind to port 8585: " + e);
        }
        processors = new ChannelProcessor[1];
        timers = new Timer[1];
        processors[0] = new ChannelProcessor(0);
        timers[0] = new Timer();
        timers[0].start();
        Thread t = new Thread(processors[0]);
        t.start();
    }
}
