package server;

import constants.ServerConfig;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import game.item.ItemFactory;
import game.skill.SkillFactory;
import game.skill.SkillManager;
import utils.DatabaseConnection;
import utils.Logging;

public class InitServer {

    private static final long serialVersionUID = 5172629591649728634L;

    public static final InitServer instance = new InitServer();
    public static long startTime = System.currentTimeMillis();

    public void run() throws InterruptedException, IOException {
        long start = System.currentTimeMillis();

        // Load opcode properties
        System.setProperty("sendops", "sendops.properties");
        System.setProperty("recvops", "recvops.properties");


        String args = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false",
                                    "127.0.0.1", ServerConfig.SQL_PORT, ServerConfig.SQL_DATABASE);

        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Runtime Exception - Could not connect to MySql Server.");
        }

        LoginServer.run_startup_configurations();
        ChannelServer.run_startup_configurations();
        SkillFactory.loadAllSkills();
        SkillManager.load();
        ItemFactory.loadAllEquips();
        long now = System.currentTimeMillis() - start;
        long seconds = now / 1000;
        long ms = now % 1000;
        Logging.log("Total loading time: " + seconds + "s " + ms + "ms");
    }

    public static void main(final String args[]) throws InterruptedException, IOException {
        instance.run();
    }
}
