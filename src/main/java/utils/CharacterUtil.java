package utils;

import constants.ServerConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CharacterUtil {
    private static final Pattern namePattern = Pattern.compile("[a-zA-Z0-9]{4,13}");
    private static final Pattern petPattern = Pattern.compile("[a-zA-Z0-9]{4,13}");

    public static boolean canCreateChar(final String name) {
        return getIdByName(name) == -1 && isEligibleCharName(name);
    }

    public static boolean isEligibleCharName(final String name) {
        if (name.length() > 13) {
            return false;
        }
        if (name.length() < 3 || !namePattern.matcher(name).matches()) {
            return false;
        }
        for (String z : ServerConfig.RESERVED) {
            if (name.indexOf(z) != -1) {
                return false;
            }
        }
        return true;
    }

    public static int getIdByName(final String name) {
        Connection con = DatabaseConnection.getConnection();
        try {
            final int id;
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return -1;
                    }
                    id = rs.getInt("id");
                }
            }

            return id;
        } catch (SQLException e) {
            System.err.println("error 'getIdByName' " + e);
        }
        return -1;
    }
}
