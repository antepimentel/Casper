package JDBC;

import Core.Bot;
import Core.Linker;
import Core.PropertyKeys;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This is the main SQL related class, it initializes all the others
 */

public class MainSQLHandler {

    // JDBC Driver Name & Database URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/" + Bot.props.getProperty(PropertyKeys.JDBC_DB_NAME_KEY) + "?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true";

    static final String JDBC_USER = Bot.props.getProperty(PropertyKeys.JDBC_USER_KEY);
    static final String JDBC_PASS = Bot.props.getProperty(PropertyKeys.JDBC_PASS_KEY);

    public static Connection connObj;


    public static void init(){
        try{
            //Class.forName(JDBC_DRIVER);
            connObj = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
            connObj.setAutoCommit(false);

            GroupSQL.init();
            AutoAssignmentSQL.init();
            EventBoardSQL.init();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //===========================================
    // DISCORD <-> DESTINY LINK
    //===========================================

    public static void addLinker(String discordId, String destinyMembershipId, int platform){
        String query = "insert into " + SQLSchema.TABLE_LINK + " values (?, ?, ?)";

        PreparedStatement stmtObj = null;
        try {
            stmtObj = connObj.prepareStatement(query);

        stmtObj.setString(1, discordId);
        stmtObj.setString(2, destinyMembershipId);
        stmtObj.setInt(3, platform);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropLinker(String discordId) {
        String query = "delete from " + SQLSchema.TABLE_LINK + " where " + SQLSchema.LINK_COL_DISCORDID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, discordId);

            System.out.println("SQL: " + stmtObj.toString());
            executeUpdate(stmtObj);
            stmtObj.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Linker queryLinker(String discordId) {
        System.out.println(discordId);
        String query = "select * from " + SQLSchema.TABLE_LINK + " where "+SQLSchema.LINK_COL_DISCORDID + " = ?";


        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, discordId);


        System.out.println("SQL: " + stmtObj.toString());
        ResultSet resultSet = stmtObj.executeQuery();
        if(resultSet.next()) {
            stmtObj.close();
            return getLinkerFromSQLResult(resultSet);
        } else {
            stmtObj.close();
            return null;
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Linker getLinkerFromSQLResult(ResultSet resultSet) throws SQLException{
        String discordId = resultSet.getString(SQLSchema.LINK_COL_DISCORDID);
        String destinyId = resultSet.getString(SQLSchema.LINK_COL_DESTINYID);
        int platform = resultSet.getInt(SQLSchema.LINK_COL_PLATFORM);

        Linker linker = new Linker(discordId, destinyId, platform);
        return linker;
    }
    //===========================================
    // GENERIC SERVER COMMAND
    //===========================================

    public static void addServer(String serverID, String name) {

        String query = "insert into " + SQLSchema.TABLE_SERVER + " values (?,?)";

        PreparedStatement stmtObj = null;
        try {
            stmtObj = connObj.prepareStatement(query);

        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllServerData(String serverID) throws SQLException {

        AutoAssignmentSQL.deleteAllServerData(serverID);
        GroupSQL.deleteAllGroupsForServer(serverID);
        deleteCustomCommandsForServer(serverID);
        deleteDisabledCommandsForServer(serverID);
        dropServer(serverID);
    }

    public static void dropServer(String serverID) throws SQLException {
        String query = "delete from " + SQLSchema.TABLE_SERVER + " where " + SQLSchema.SERVER_COL_ID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

    }


    //===========================================
    // DISABLED COMMAND
    //===========================================

    public static boolean checkDisabledCommand(String serverID, String name) throws SQLException{
        boolean result = false;

        String query = "select * from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ? and " + SQLSchema.DC_COL_NAME + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        ResultSet rs = stmtObj.executeQuery();

        // rs.next returns false on empty result set
        if(rs.next()){
            result = true;
        } else {
            result = false;
        }

        stmtObj.close();
        rs.close();

        return result;
    }

    public static void addDisabledCommand(String serverID, String name) throws SQLException{

        PreparedStatement stmtObj = null;

        String query = "insert into " + SQLSchema.TABLE_DISABLEDCOMMAND + " values (?,?)";

        stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

    }

    public static void dropDisabledCommand(String serverID, String name) throws SQLException{

        PreparedStatement stmtObj = null;

        String query = "delete from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ? and " + SQLSchema.DC_COL_NAME + " = ?";

        stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

    }

    public static void deleteDisabledCommandsForServer(String serverID) throws SQLException{
        String query = "delete from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();

    }

    private static void executeUpdate(PreparedStatement stmtObj) throws SQLException{
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
    }



    //===========================================
    // CUSTOM COMMAND
    //===========================================

    public static void deleteCustomCommandsForServer(String serverID) throws SQLException{
        String query = "delete from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();
    }

    public static void addCustomCommand(String serverID, String name, String command) throws SQLException{
        String query = "insert into " + SQLSchema.TABLE_CUSTOMCOMMAND + " values (?, ?, ?)";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);
        stmtObj.setString(3, command);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();
    }

    public static void dropCustomCommand(String serverID, String name) throws SQLException{
        String query = "delete from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ? and " + SQLSchema.CC_COL_NAME + " =?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        executeUpdate(stmtObj);
        stmtObj.close();
    }

    public static String checkCustomCommand(String serverID, String name){
        String query = "select * from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ? and " + SQLSchema.CC_COL_NAME + " =?";

        PreparedStatement stmtObj = null;
        try {
            stmtObj = connObj.prepareStatement(query);

        stmtObj.setString(1, serverID);
        stmtObj.setString(2, name);

        System.out.println("SQL: " + stmtObj.toString());
        ResultSet rs = stmtObj.executeQuery();

        if(rs.next()){
           return rs.getString(SQLSchema.CC_COL_COMMAND);
        } else {
            return null;
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getAllCustomCommandsForServer(String serverID) throws SQLException{
        String query = "select * from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ?";

        PreparedStatement stmtObj = connObj.prepareStatement(query);
        stmtObj.setString(1, serverID);

        System.out.println("SQL: " + stmtObj.toString());
        ResultSet rs = stmtObj.executeQuery();

        HashMap<String, String> result = new HashMap<String, String>();

        while(rs.next()){
            result.put(rs.getString(SQLSchema.CC_COL_NAME), rs.getString(SQLSchema.CC_COL_COMMAND));
        }

        stmtObj.close();
        rs.close();
        return result;

    }
}
