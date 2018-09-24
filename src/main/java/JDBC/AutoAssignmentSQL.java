package JDBC;

import Core.Bot;
import Core.EventHandlers.AutoAssignmentEventHandler;
import net.dv8tion.jda.core.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutoAssignmentSQL {

    private static Connection connObj = MainSQLHandler.connObj;

    public static void init(){
        // Need to print for every server
        // TODO
    }

    public static void printAllMessages(){
        // TODO
        // Print all auto messages for every server

    }

    public static void addAutoRoleForServer(String serverID, Role role){
        String query = "insert into " + SQLSchema.TABLE_AR + " values (?,?,?,?)";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.setString(2, role.getName()); // Group ID
            stmtObj.setString(3, "temp"); // Name
            stmtObj.setString(4, role.getId()); // Date
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropAutoRoleForServer(String serverID, Role role){
        String query = "delete from " + SQLSchema.TABLE_AR + " where " + SQLSchema.AR_COL_SERVERID + " = ? and " + SQLSchema.AR_COL_ROLEID + " = ?";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.setString(2, role.getId());
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAutoChannelForServer(String serverID){
        boolean result = false;

        String query = "select * from " + SQLSchema.TABLE_AC + " where " + SQLSchema.AC_COL_SERVERID + " = ?";
        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);

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

    public static void addAutoChannelForServer(String serverID, TextChannel channel){
        String query = "insert into " + SQLSchema.TABLE_AC + " values (?,?,?)";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.setString(2, channel.getId()); // Group ID
            stmtObj.setString(3, channel.getName()); // Name
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeAutoChannelName(String serverID, TextChannel channel){
        String query = "update " + SQLSchema.TABLE_AC + " set " + SQLSchema.AC_COL_CHANNELID + " = ?, " + SQLSchema.AC_COL_CHANNELNAME + " = ? where " + SQLSchema.AC_COL_SERVERID + " = ?";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, channel.getId()); // Server ID
            stmtObj.setString(2, channel.getName()); // Group ID
            stmtObj.setString(3, serverID); // Name
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printMessagesForServer(String serverID){

        Guild server = Bot.jda.getGuildById(serverID);
        TextChannel channel = getAutoChannelByServerID(serverID);

        try{
            String query = "select * from " + SQLSchema.TABLE_AR + " where " + SQLSchema.AR_COL_SERVERID + " = ?";

            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            ResultSet rs = stmtObj.executeQuery();

            clearChannel(channel);

            List<Role> roles = new ArrayList<Role>();

            // Print messages
            while(rs.next()){
                String roleID = rs.getString(SQLSchema.AR_COL_ROLEID);
                Role role = server.getRoleById(roleID);
                roles.add(role);

                String msg = "React to add role: " + role.getName();
                channel.sendMessage(msg).queue();
            }

            // React to messages
            if(roles.size() > 0){
                List<Message> messages = channel.getHistory().retrievePast(roles.size()).complete();
                for(int i = 0; i < messages.size(); i++){
                    messages.get(i).addReaction(AutoAssignmentEventHandler.MONITORED_REACTION).queue();
                }

                // Add message IDs to SQL server
                for(int i = 0; i < messages.size(); i++){
                    String updateQuery = "update " + SQLSchema.TABLE_AR + " set " + SQLSchema.AR_COL_MESSAGEID + " = ? where " + SQLSchema.AR_COL_SERVERID + " = ? and " + SQLSchema.AR_COL_ROLEID + " = ?";

                    PreparedStatement updateObj = connObj.prepareStatement(updateQuery);
                    updateObj.setString(1, messages.get(i).getId());
                    updateObj.setString(2, serverID);
                    updateObj.setString(3, roles.get(roles.size()-1-i).getId());
                    updateObj.executeUpdate();
                    connObj.commit();
                    connObj.rollback();
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static TextChannel getAutoChannelByServerID(String serverID){
        TextChannel result = null;

        String query = "select " + SQLSchema.AC_COL_CHANNELID + " from " + SQLSchema.TABLE_AC + " where " + SQLSchema.AC_COL_SERVERID + " = ?";

        try{
            PreparedStatement stmtObj = connObj.prepareStatement(query);
            stmtObj.setString(1, serverID);
            ResultSet rs = stmtObj.executeQuery();

            rs.next();
            String channelID = rs.getString(SQLSchema.AC_COL_CHANNELID);
            result = Bot.jda.getGuildById(serverID).getTextChannelById(channelID);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteAllServerData(String serverID){
        String dropRoles = "delete from " + SQLSchema.TABLE_AR + " where " + SQLSchema.AR_COL_SERVERID + " = ?";
        String dropChannel = "delete from " + SQLSchema.TABLE_AC + " where " + SQLSchema.AC_COL_SERVERID + " = ?";

        try {
            PreparedStatement stmtObj = connObj.prepareStatement(dropChannel);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();

            stmtObj = connObj.prepareStatement(dropRoles);
            stmtObj.setString(1, serverID); // Server ID
            stmtObj.executeUpdate();
            connObj.commit();
            connObj.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearChannel(TextChannel channel){
        List<Message> messages = channel.getHistory().retrievePast(50).complete();

        for(int i = 0; i < messages.size(); i++){
            messages.get(i).delete().queue();
        }
    }
}
