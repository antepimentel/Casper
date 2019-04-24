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
import java.util.TimeZone;

public class GroupSQL {

    private static Connection connObj = MainSQLHandler.connObj;

    public static void init(){
        // Do we need this?
    }

    public static void save(Group g) throws SQLException {
        System.out.println(g.getTimezone());
        String players = g.getPlayerIDString();
        String subs = g.getSubIDString();

        String query = "insert into " + SQLSchema.TABLE_POST + " values (?,?,?,?,?,?,?,?,?,?,?,?)";

        //noinspection MagicConstant

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, g.getServerID()); // Server ID
        stmtObj.setString(2, Integer.toString(g.getID())); // Group ID
        stmtObj.setString(3, g.getName()); // Name
        stmtObj.setString(4, Group.df_na.format(g.getDate())); // Date
        stmtObj.setString(5, players); // Players
        stmtObj.setString(6, subs); // Subs
        stmtObj.setString(7, g.getType()); // Platform
        stmtObj.setString(8, g.getMsgID()); // Message ID
        stmtObj.setString(9, g.getOwnerID()); // Group Owner
        stmtObj.setString(10, g.getGroupActivityType() == null ? "" : g.getGroupActivityType().getCode());
        stmtObj.setInt(11, g.getRollcallCount());
        stmtObj.setString(12, g.getTimezone());

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();

        stmtObj.close();
    }

    public static void delete(Group g) throws SQLException {
        String query = "delete from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, g.getServerID());
        stmtObj.setString(2, Integer.toString(g.getID()));

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();

    }

    public static void updateName(Group g) throws SQLException {

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_NAME + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, g.getName()); // Name
        stmtObj.setString(2, g.getServerID()); // Server ID
        stmtObj.setString(3, Integer.toString(g.getID())); // Group ID

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();
        stmtObj.close();
    }

    public static void updateTypeCode(Group g) throws SQLException {
        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_TYPE_CODE+ " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, g.getGroupActivityType() == null ? "" : g.getGroupActivityType().getCode()); // Name
        stmtObj.setString(2, g.getServerID()); // Server ID
        stmtObj.setString(3, Integer.toString(g.getID())); // Group ID

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();
        stmtObj.close();
    }
    public static void updateTime(Group g) throws SQLException {

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_GROUPDATE + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, Group.df_na.format(g.getDate())); // Date
        stmtObj.setString(2, g.getServerID()); // Server ID
        stmtObj.setString(3, Integer.toString(g.getID())); // Group ID

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();
        stmtObj.close();
    }

    public static void updateRollcallCount(Group g) throws SQLException {

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_ROLLCALL_COUNT + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setInt(1, g.getRollcallCount()); // Date
        stmtObj.setString(2, g.getServerID()); // Server ID
        stmtObj.setString(3, Integer.toString(g.getID())); // Group ID

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();
        stmtObj.close();
    }

    public static void updatePlayers(Group g) throws SQLException {
        String players = g.getPlayerIDString();
        String subs = g.getSubIDString();

        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_PLAYERS + " = ?, " + SQLSchema.POST_COL_SUBS + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, players); // Players
        stmtObj.setString(2, subs); // Subs
        stmtObj.setString(3, g.getServerID()); // Server ID
        stmtObj.setString(4, Integer.toString(g.getID())); // Group ID

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();

        connObj.commit();
        connObj.rollback();
        stmtObj.close();
    }

    public static void updateMessageID(Group g){
        String query = "update " + SQLSchema.TABLE_POST + " set " + SQLSchema.POST_COL_MSG_ID + " = ? where " + SQLSchema.POST_COL_SERVERID + " = ? and " + SQLSchema.POST_COL_GROUPID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, g.getMsgID()); // Name
            stmtObj.setString(2, g.getServerID()); // Server ID
            stmtObj.setString(3, Integer.toString(g.getID())); // Group ID

            System.out.println("SQL: " + stmtObj.toString());
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
            stmtObj.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Group> getGroupsByServer(String serverID) throws SQLException {

        String query = "select * from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        ResultSet rs = stmtObj.executeQuery();
        ArrayList<Group> groups = new ArrayList<Group>();

        while(rs.next()){
            groups.add(getGroupFromSQLResult(rs));
        }

        stmtObj.close();
        rs.close();
        return groups;
    }

    public static Group queryGroupFromMsgID(String serverID, String msgID) throws SQLException {
        String query = "select * from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ? and "
                + SQLSchema.POST_COL_MSG_ID + "=?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, msgID);

        System.out.println("SQL: " + stmtObj.toString());
        ResultSet rs = stmtObj.executeQuery();
        Group group = null;

        if(rs.next()){
            group = getGroupFromSQLResult(rs);
        }

        stmtObj.close();
        rs.close();
        return group;
    }

    private static Group getGroupFromSQLResult(ResultSet rs) throws SQLException {
        try {
            String serverID = rs.getString(SQLSchema.POST_COL_SERVERID);
            int groupID = Integer.parseInt(rs.getString(SQLSchema.POST_COL_GROUPID));
            String name = rs.getString(SQLSchema.POST_COL_NAME);
            Date date = Group.df_na.parse(rs.getString(SQLSchema.POST_COL_GROUPDATE));
            String players = rs.getString(SQLSchema.POST_COL_PLAYERS);
            String subs = rs.getString(SQLSchema.POST_COL_SUBS);
            String platform = rs.getString(SQLSchema.POST_COL_TYPE);
            String msgID = rs.getString(SQLSchema.POST_COL_MSG_ID);
            String ownerID = rs.getString(SQLSchema.POST_COL_OWNER_ID);
            String typeCode = rs.getString(SQLSchema.POST_COL_TYPE_CODE);
            int rollcallCount = rs.getInt(SQLSchema.POST_COL_ROLLCALL_COUNT);
            String timezone = rs.getString(SQLSchema.POST_COL_TIMEZONE);


            net.dv8tion.jda.core.entities.Member owner = Bot.jda.getGuildById(serverID).getMemberById(ownerID);

            Group g = new Group(serverID, groupID, name, date, timezone, platform, owner, msgID, rollcallCount);

            StringTokenizer st = new StringTokenizer(players, "#");
            while(st.hasMoreTokens()){
                g.join(Bot.jda.getGuildById(serverID).getMemberById(st.nextToken()));
            }

            st = new StringTokenizer(subs, "#");
            while(st.hasMoreTokens()){
                g.joinAsSub(Bot.jda.getGuildById(serverID).getMemberById(st.nextToken()));
            }

            if(g.getPlayers().size() == 0) g.setEmpty(true);

            g.setGroupActivityType(Group.getGroupTypeByCode(typeCode));

            return g;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NoAvailableSpotsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteAllGroupsForServer(String serverID) throws SQLException {

        String query = "delete from " + SQLSchema.TABLE_POST + " where " + SQLSchema.POST_COL_SERVERID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        stmtObj.executeUpdate();
        connObj.commit();
        connObj.rollback();

        stmtObj.close();
    }
}
