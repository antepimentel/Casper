package JDBC;

import Core.Bot;
import Exceptions.NoAvailableSpotsException;
import LFG.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class GroupSQL {

    private static Connection connObj = MainSQLHandler.connObj;

    public static void init(){
        // Do we need this?
    }

    public static void save(Group g){
        String players = g.getPlayerIDString();
        String subs = g.getSubIDString();

        String query = "insert into " + SQLSchema.TABLE_POST + " values (?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, g.getServerID()); // Server ID
            stmtObj.setString(2, Integer.toString(g.getID())); // Group ID
            stmtObj.setString(3, g.getName()); // Name
            stmtObj.setString(4, Group.df.format(g.getDate())); // Date
            stmtObj.setString(5, players); // Players
            stmtObj.setString(6, subs); // Subs
            stmtObj.setString(7, g.getType()); // Platform
            stmtObj.setString(8, g.getMsgID()); // Message ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Group g){

        String query = "delete from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try{
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, g.getServerID());
            stmtObj.setString(2, Integer.toString(g.getID()));
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void updateName(Group g){

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_NAME + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, g.getName()); // Name
            stmtObj.setString(2, g.getServerID()); // Server ID
            stmtObj.setString(3, Integer.toString(g.getID())); // Group ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTime(Group g){

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_GROUPDATE + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, Group.df.format(g.getDate())); // Date
            stmtObj.setString(2, g.getServerID()); // Server ID
            stmtObj.setString(3, Integer.toString(g.getID())); // Group ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayers(Group g){
        String players = g.getPlayerIDString();
        String subs = g.getSubIDString();

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_PLAYERS + " = ?, " + SQLSchema.POST_COL_SUBS + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, players); // Players
            stmtObj.setString(2, subs); // Subs
            stmtObj.setString(3, g.getServerID()); // Server ID
            stmtObj.setString(4, Integer.toString(g.getID())); // Group ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMessageID(Group g){
        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_MSG_ID + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, g.getMsgID()); // Name
            stmtObj.setString(2, g.getServerID()); // Server ID
            stmtObj.setString(3, Integer.toString(g.getID())); // Group ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Group> getGroupsByServer(String serverID){

        String query = "select * from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ?";
        try{
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            ResultSet rs = stmtObj.executeQuery();
            ArrayList<Group> groups = new ArrayList<Group>();

            while(rs.next()){
                groups.add(getGroupFromSQLResult(rs));
            }
            return groups;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Group queryGroupFromMsgID(String serverID, String msgID){
        String query = "select * from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ? and "
                + SQLSchema.POST_COL_MSG_ID + "=?";

        try{
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, msgID);
            ResultSet rs = stmtObj.executeQuery();

            if(rs.next()){
                return getGroupFromSQLResult(rs);
            } else {
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private static Group getGroupFromSQLResult(ResultSet rs) throws SQLException {
        try {
            String serverID = rs.getString(SQLSchema.POST_COL_SERVERID);
            int groupID = Integer.parseInt(rs.getString(SQLSchema.POST_COL_GROUPID));
            String name = rs.getString(SQLSchema.POST_COL_NAME);
            Date date = Group.df.parse(rs.getString(SQLSchema.POST_COL_GROUPDATE));
            String players = rs.getString(SQLSchema.POST_COL_PLAYERS);
            String subs = rs.getString(SQLSchema.POST_COL_SUBS);
            String platform = rs.getString(SQLSchema.POST_COL_TYPE);
            String msgID = rs.getString(SQLSchema.POST_COL_MSG_ID);

            Group g = new Group(serverID, groupID, name, date, platform, msgID);

            StringTokenizer st = new StringTokenizer(players, "#");
            while(st.hasMoreTokens()){
                g.join(Bot.jda.getGuildById(serverID).getMemberById(st.nextToken()));
            }

            st = new StringTokenizer(subs, "#");
            while(st.hasMoreTokens()){
                g.joinAsSub(Bot.jda.getGuildById(serverID).getMemberById(st.nextToken()));
            }

            return g;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NoAvailableSpotsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteAllGroupsForServer(String serverID){

        String query = "delete from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ?";
        try{
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
