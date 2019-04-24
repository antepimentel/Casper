package JDBC;

import Core.Bot;
import Core.Utility;
import Exceptions.NoBoardForPlatformException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the Event Board. Users post groups and they appear in the event channel.
 * Other users react to the event to join/leave
 */

public class EventBoardSQL {

    private static Connection connObj = MainSQLHandler.connObj;

    public static void init() throws SQLException {
        printAllMessages();
    }

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

    public static HashMap<String, TextChannel> getAllEventBoardsForServer(String serverID) {
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

    public static ArrayList<TextChannel> getAllEventBoards() {
        String query = "select * from " + SQLSchema.TABLE_EB;

        ArrayList<TextChannel> channels = new ArrayList<TextChannel>();
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);

            ResultSet rs = stmtObj.executeQuery();

            while(rs.next()){
                String channelID = rs.getString(SQLSchema.EB_COL_CHANNELID);
                TextChannel eb = Bot.jda.getGuildById(rs.getString(SQLSchema.EB_COL_SERVERID)).getTextChannelById(channelID);
                channels.add(eb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channels;
    }

    public static ArrayList<String> getServersWithEventBoards(){
        String query = "select distinct " + SQLSchema.SERVER_COL_ID + " from " + SQLSchema.TABLE_SERVER;

        ArrayList<String> result = new ArrayList<String>();

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            ResultSet rs = stmtObj.executeQuery();

            while(rs.next()){
                result.add(rs.getString(SQLSchema.SERVER_COL_ID));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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

    private static void printAllMessages() throws SQLException {

        ArrayList<String> servers = getServersWithEventBoards();
        ArrayList<TextChannel> channels = EventBoardSQL.getAllEventBoards();

        // Clear all the channels
        for(TextChannel tc: channels){
            Utility.clearChannel(tc);
        }

        for(String server: servers){
            ArrayList<Group> groups = GroupSQL.getGroupsByServer(server);

            // Post all groups into the proper channels
            for(Group g: groups){
                try {
                    LFGHandler.repostAndUpdateMsgID(g);
                } catch (NoBoardForPlatformException e) {
                    e.getMessage();
                }
            }
        }

    }
}
