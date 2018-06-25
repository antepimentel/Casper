package JDBC;

/**
 * This class holds the SQL Schema information for consistent SQL queries
 */

public class SQLSchema {

    // Server
    public static String TABLE_SEVRER = "server";
    public static String SERVER_COL_ID = "id";
    public static String SERVER_COL_NAME = "name";

    // Custom Command
    public static String TABLE_CUSTOMCOMMAND = "customcommand";
    public static String CC_COL_SERVERID = "serverid";
    public static String CC_COL_NAME = "name";
    public static String CC_COL_COMMAND = "command";

    // Disabled Command
    public static String TABLE_DISABLEDCOMMAND = "disabledcommand";
    public static String DC_COL_SERVERID = "serverid";
    public static String DC_COL_NAME = "name";

    // Post
    public static String TABLE_POST = "post";
    public static String POST_COL_SERVERID = "serverid";
    public static String POST_COL_GROUPID = "groupid";
    public static String POST_COL_NAME = "name";
    public static String POST_COL_GROUPDATE = "groupdate";
    public static String POST_COL_PLAYERS = "players";
    public static String POST_COL_SUBS = "subs";

    // AutoAssignment
    //TODO
}
