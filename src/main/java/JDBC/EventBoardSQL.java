package JDBC;

import Core.Bot;
import Exceptions.NoBoardForPlatformException;
import LFG.Group;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles the Event Board. Users post groups and they appear in the event channel.
 * Other users react to the event to join/leave
 */

public class EventBoardSQL {

    private static Connection connObj = MainSQLHandler.connObj;

    public static TextChannel getEventBoard(String serverID, String type) throws NoBoardForPlatformException {
        String query = "select " + SQLSchema.EB_COL_CHANNELID + " from " + SQLSchema.TABLE_EB
                + " where " + SQLSchema.EB_COL_SERVERID + "=? and " + SQLSchema.EB_COL_TYPE + "=?";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.setString(2, type); // System Type

            ResultSet rs = stmtObj.executeQuery();

            if(rs.next()){
                String channelID = rs.getString(SQLSchema.EB_COL_CHANNELID);
                TextChannel eb = Bot.jda.getGuildById(serverID).getTextChannelById(channelID);
                return eb;
            } else {
                throw new NoBoardForPlatformException(type);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HashMap<String, TextChannel> getAllEventBoards(String serverID) {
        String query = "select * from " + SQLSchema.TABLE_EB
                + " where " + SQLSchema.EB_COL_SERVERID + "=?";

        HashMap<String, TextChannel> channels = new HashMap<String, TextChannel>();
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID

            ResultSet rs = stmtObj.executeQuery();

            while(rs.next()){
                String channelID = rs.getString(SQLSchema.EB_COL_CHANNELID);
                String platform = rs.getString(SQLSchema.EB_COL_TYPE);
                TextChannel eb = Bot.jda.getGuildById(serverID).getTextChannelById(channelID);
                channels.put(platform, eb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channels;
    }

    public static void setEventBoard(String serverID, String channelID, String type){

        // Check if this exists first, otherwise create a new entry
        String query = "select " + SQLSchema.EB_COL_CHANNELID + " from " + SQLSchema.TABLE_EB
                + " where " + SQLSchema.EB_COL_SERVERID + "=? and " + SQLSchema.EB_COL_TYPE + "=?";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.setString(2, type); // System Type

            ResultSet rs = stmtObj.executeQuery();

            if(rs.next()){
                // Entry exists, update it
                query = "update " + SQLSchema.TABLE_EB + " set " + SQLSchema.EB_COL_CHANNELID + "=?"
                        + " where " + SQLSchema.EB_COL_SERVERID + "=? and " + SQLSchema.EB_COL_TYPE + "=?";

                PreparedStatement stmtObj2 = connObj.prepareStatement(query);
                stmtObj2.setString(1, serverID); // Channel ID
                stmtObj2.setString(2, channelID); // Server ID
                stmtObj2.setString(3, type); // System Type

                stmtObj2.executeUpdate();
                connObj.commit();
                connObj.rollback();
            } else {
                // Entry does not exist, create a new one
                query = "insert into " + SQLSchema.TABLE_EB + " values(?,?,?)";


                PreparedStatement stmtObj2 = connObj.prepareStatement(query);
                stmtObj2.setString(1, serverID); // Channel ID
                stmtObj2.setString(2, channelID); // Server ID
                stmtObj2.setString(3, type); // System Type
                stmtObj2.executeUpdate();
                connObj.commit();
                connObj.rollback();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
