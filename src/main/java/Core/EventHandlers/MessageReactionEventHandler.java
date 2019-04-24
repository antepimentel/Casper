package Core.EventHandlers;

import Core.Bot;
import Core.PermissionHandler;
import Exceptions.*;
import JDBC.AutoAssignmentSQL;
import JDBC.EventBoardSQL;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

import java.sql.SQLException;


public class MessageReactionEventHandler implements net.dv8tion.jda.core.hooks.EventListener {

    public static Emote MONITORED_REACTION;//Bot.props.getProperty(PropertyKeys.MONITORED_REACTION_KEY); //Unicode, this may not work after the props file has been re-saved
    private static Emote JOIN_REACTION; // Smiley
    private static Emote LEAVE_REACTION; // Tears of joy
    private static Emote ROLLCALL_REACTION; // Glasses
    private static Emote DELETE_REACTION; // Hearts

    @Override
    public void onEvent(Event e){
        try {
            // Ignore reactions from self
            String id = "";
            if(e instanceof GenericMessageReactionEvent){
                id = ((GenericMessageReactionEvent) e).getUser().getId();
            }

            // Setup reactions
            if(e instanceof ReadyEvent) {
                JDA jda = e.getJDA();
                MONITORED_REACTION = jda.getEmotesByName("plus", false).get(0);
                JOIN_REACTION = jda.getEmotesByName("plus", false).get(0);
                LEAVE_REACTION = jda.getEmotesByName("minus", false).get(0);
                ROLLCALL_REACTION = jda.getEmotesByName("rollcall", false).get(0);
                DELETE_REACTION = jda.getEmotesByName("delete", false).get(0);
            }

            if(id.equals(Bot.SELF_USER_ID)){
                // Do nothing
            } else if(e instanceof MessageReactionAddEvent){
                onMessageReactionAddEvent((MessageReactionAddEvent) e);
            } else if(e instanceof MessageReactionRemoveEvent){
                onMessageReactionRemoveEvent((MessageReactionRemoveEvent) e);
            }
        } catch (Exception err){
            System.out.println(err.toString());
            err.printStackTrace();

        }
    }

    public void onMessageReactionAddEvent(MessageReactionAddEvent e){
        if(e.getReactionEmote().getName() != JOIN_REACTION.getName() &&
                e.getReactionEmote().getName() != LEAVE_REACTION.getName() &&
                e.getReactionEmote().getName() != LEAVE_REACTION.getName() &&
                e.getReactionEmote().getName() != ROLLCALL_REACTION.getName() &&
                e.getReactionEmote().getName() != DELETE_REACTION.getName())
        {
            return;
        }

        handleAutoRoleAddEvent(e);
        try {
            handleEventBoardAddEvent(e);
        } catch (GroupNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println(e1.getMessage());
        } catch (NoAvailableSpotsException e1) {
            //e1.printStackTrace();
            System.out.println(e1.getMessage());
        } catch (MemberNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println(e1.getMessage());
        } catch (InvalidPermissionsException e1) {
            //e1.printStackTrace();
            System.out.println(e1.getMessage());
        } catch (SQLException e1) {
            //e1.printStackTrace();
            System.out.println("Database error in Msg Reaction Event Handler");
        }
    }

    public void onMessageReactionRemoveEvent(MessageReactionRemoveEvent e) throws SQLException {
        handleAutoRoleRemoveEvent(e);
        handleEventBoardRemoveEvent(e);
    }

    //============================================
    // AUTO ROLE METHODS
    //============================================
    private void handleAutoRoleAddEvent(MessageReactionAddEvent e){
        String roleID = AutoAssignmentSQL.queryMessageID(e.getGuild().getId(), e.getMessageId());
        if(roleID != null){
            Role newRole = e.getGuild().getRoleById(roleID);
            e.getGuild().getController().addSingleRoleToMember(e.getMember(), newRole).complete();
        }
    }

    private void handleAutoRoleRemoveEvent(MessageReactionRemoveEvent e){
        String roleID = AutoAssignmentSQL.queryMessageID(e.getGuild().getId(), e.getMessageId());
        if(roleID != null){
            Role roleToRemove = e.getGuild().getRoleById(roleID);
            e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), roleToRemove).complete();
        }
    }

    //============================================
    // EVENT BOARD METHODS
    //============================================
    private void handleEventBoardAddEvent(MessageReactionAddEvent e) throws GroupNotFoundException, NoAvailableSpotsException, MemberNotFoundException, InvalidPermissionsException, SQLException {
        Group g = GroupSQL.queryGroupFromMsgID(e.getGuild().getId(), e.getMessageId());

        if(g != null){
            Emote reaction = e.getReactionEmote().getEmote();
            String reactionID = reaction.getId();

           if(reactionID.equals(JOIN_REACTION.getId())){
               LFGHandler.join(g, e.getMember());
               refreshMessage(e.getTextChannel(), e.getMessageId(), g);

           } else if(reactionID.equals(LEAVE_REACTION.getId())){
               LFGHandler.leave(g, e.getMember());
               refreshMessage(e.getTextChannel(), e.getMessageId(), g);

           } else if(reactionID.equals(ROLLCALL_REACTION.getId())) {
               if(g.getRollcallCount() < 3) {
                   PermissionHandler.isLeaderOrMod(e.getMember(), g);
                   LFGHandler.pingPlayers(g);
                   refreshMessage(e.getTextChannel(), e.getMessageId(), g);
               }
           } else if(reactionID.equals(DELETE_REACTION.getId())){
                // Check permissions
               PermissionHandler.isLeaderOrMod(e.getMember(), g);
               GroupSQL.delete(g);
               LFGHandler.removeIdFromPinged(g.getID());
               e.getTextChannel().deleteMessageById(e.getMessageId()).complete();
           } else {
               System.out.println("Unknown Emote: "+reaction.toString());
           }
        } else {
            throw new GroupNotFoundException(null);
        }
    }

    private void handleEventBoardRemoveEvent(MessageReactionRemoveEvent e) throws SQLException {
        Group g = GroupSQL.queryGroupFromMsgID(e.getGuild().getId(), e.getMessageId());
        if(g != null){
            // Don't really need this but I'll leave it as a place holder

        }
    }

    private static void refreshMessage(TextChannel tc, String msgID, Group group){
        tc.getMessageById(msgID).complete().editMessage(group.toEmbed()).queue();
    }

    public static String postEventGroup(Group g) throws NoBoardForPlatformException {
        TextChannel tc = EventBoardSQL.getEventBoard(g.getServerID(), g.getType());
        Message msg = tc.sendMessage(g.toEmbed()).complete();

        msg.addReaction(JOIN_REACTION).queue();
        msg.addReaction(LEAVE_REACTION).queue();
        msg.addReaction(ROLLCALL_REACTION).queue();
        msg.addReaction(DELETE_REACTION).queue();

        return msg.getId();
    }
}