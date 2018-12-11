package JDBC;

import Core.Bot;
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
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
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
    // GENERIC SERVER COMMAND
    //===========================================

    public static void addServer(String serverID, String name){

        String query = "insert into " + SQLSchema.TABLE_SERVER + " values (?,?)";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);

            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllServerData(String serverID){

        AutoAssignmentSQL.deleteAllServerData(serverID);
        GroupSQL.deleteAllGroupsForServer(serverID);
        deleteCustomCommandsForServer(serverID);
        deleteDisabledCommandsForServer(serverID);
        dropServer(serverID);
    }

    public static void dropServer(String serverID){
        String query = "delete from " + SQLSchema.TABLE_SERVER + " where " + SQLSchema.SERVER_COL_ID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //===========================================
    // DISABLED COMMAND
    //===========================================

    public static boolean checkDisabledCommand(String serverID, String name){
        boolean result = false;

        String query = "select * from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ? and " + SQLSchema.DC_COL_NAME + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            ResultSet rs = stmtObj.executeQuery();

            // rs.next returns false on empty result set
            if(rs.next()){
                result = true;
            } else {
                result = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void addDisabledCommand(String serverID, String name){

        PreparedStatement stmtObj = null;

        String query = "insert into " + SQLSchema.TABLE_DISABLEDCOMMAND + " values (?,?)";
        try {
            stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            executeUpdate(stmtObj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropDisabledCommand(String serverID, String name){

        PreparedStatement stmtObj = null;

        String query = "delete from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ? and " + SQLSchema.DC_COL_NAME + " = ?";
        try {
            stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            executeUpdate(stmtObj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDisabledCommandsForServer(String serverID){
        String query = "delete from " + SQLSchema.TABLE_DISABLEDCOMMAND + " where " + SQLSchema.DC_COL_SERVERID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeUpdate(PreparedStatement stmtObj){
        try {
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //===========================================
    // CUSTOM COMMAND
    //===========================================

    public static void deleteCustomCommandsForServer(String serverID){
        String query = "delete from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addCustomCommand(String serverID, String name, String command){
        String query = "insert into " + SQLSchema.TABLE_CUSTOMCOMMAND + " values (?, ?, ?)";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);
            stmtObj.setString(3, command);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dropCustomCommand(String serverID, String name){
        String query = "delete from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ? and " + SQLSchema.CC_COL_NAME + " =?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String checkCustomCommand(String serverID, String name){
        String query = "select * from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ? and " + SQLSchema.CC_COL_NAME + " =?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            ResultSet rs = stmtObj.executeQuery();

            if(rs.next()){
               return rs.getString(SQLSchema.CC_COL_COMMAND);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getAllCustomCommandsForServer(String serverID){
        String query = "select * from " + SQLSchema.TABLE_CUSTOMCOMMAND + " where " + SQLSchema.CC_COL_SERVERID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

            ResultSet rs = stmtObj.executeQuery();

            HashMap<String, String> result = new HashMap<String, String>();

            while(rs.next()){
                result.put(rs.getString(SQLSchema.CC_COL_NAME), rs.getString(SQLSchema.CC_COL_COMMAND));
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
