package JDBC;

import Core.Bot;
import Core.PropertyKeys;

import java.sql.*;

/**
 * This is the main SQL related class, it initializes all the others
 */

public class MainSQLHandler {

    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/" + Bot.props.getProperty(PropertyKeys.JDBC_DB_NAME_KEY) + "?useSSL=false";

    static final String JDBC_USER = Bot.props.getProperty(PropertyKeys.JDBC_USER_KEY);
    static final String JDBC_PASS = Bot.props.getProperty(PropertyKeys.JDBC_PASS_KEY);

    public static Connection connObj;


    public static void init(){
        try{
            //Class.forName(JDBC_DRIVER);
            connObj = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
            connObj.setAutoCommit(false);

            GroupSQL.init();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addServer(String serverID, String name){

        String query = "insert into " + SQLSchema.TABLE_SEVRER + " values (?,?)";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);

            stmtObj.setString(1, serverID);
            stmtObj.setString(2, name);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dropServer(String serverID){

        String query = "delete from " + SQLSchema.TABLE_SEVRER + " where " + SQLSchema.SERVER_COL_ID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

            executeUpdate(stmtObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static void executeUpdate(PreparedStatement stmtObj){
        try {
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}