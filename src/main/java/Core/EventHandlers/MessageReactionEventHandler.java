package Core.EventHandlers;

import Core.Bot;
import Core.PermissionHandler;
import Exceptions.*;
import JDBC.AutoAssignmentSQL;
import JDBC.EventBoardSQL;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;


public class MessageReactionEventHandler implements net.dv8tion.jda.core.hooks.EventListener {

    public static String MONITORED_REACTION = "\uD83E\uDD86";//Bot.props.getProperty(PropertyKeys.MONITORED_REACTION_KEY); //Unicode, this may not work after the props file has been re-saved
    private static String JOIN_REACTION = "\uD83D\uDE00"; // Smiley
    private static String LEAVE_REACTION = "\uD83D\uDE02"; // Tears of joy
    private static String ROLLCALL_REACTION = "\uD83D\uDE0E"; // Glasses
    private static String DELETE_REACTION = "\uD83D\uDE0D"; // Hearts

    @Override
    public void onEvent(Event e){
        try {
            // Ignore reactions from self
            String id = "";
            if(e instanceof GenericMessageReactionEvent){
                id = ((GenericMessageReactionEvent) e).getUser().getId();
            }

            if(id.equals(Bot.SELF_USER_ID)){
                // Do nothing
            } else if(e instanceof MessageReactionAddEvent){
                onMessageReactionAddEvent((MessageReactionAddEvent) e);
            } else if(e instanceof MessageReactionRemoveEvent){
                onMessageReactionRemoveEvent((MessageReactionRemoveEvent) e);
            }
        } catch (Exception err){
            System.out.println(err.getMessage());
        }
    }

    public void onMessageReactionAddEvent(MessageReactionAddEvent e){
        handleAutoRoleAddEvent(e);

        try {
            handleEventBoardAddEvent(e);
        } catch (GroupNotFoundException e1) {
            e1.printStackTrace();
        } catch (NoAvailableSpotsException e1) {
            e1.printStackTrace();
        } catch (MemberNotFoundException e1) {
            e1.printStackTrace();
        } catch (InvalidPermissionsException e1) {
            e1.printStackTrace();
        }
    }

    public void onMessageReactionRemoveEvent(MessageReactionRemoveEvent e){
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
    private void handleEventBoardAddEvent(MessageReactionAddEvent e) throws GroupNotFoundException, NoAvailableSpotsException, MemberNotFoundException, InvalidPermissionsException {
        Group g = GroupSQL.queryGroupFromMsgID(e.getGuild().getId(), e.getMessageId());
        if(g != null){
            String reaction = e.getReactionEmote().getName();
           if(reaction.equals(JOIN_REACTION)){
               LFGHandler.join(g, e.getMember());
               refreshMessage(e.getTextChannel(), e.getMessageId(), g);

           } else if(reaction.equals(LEAVE_REACTION)){
               LFGHandler.leave(g, e.getMember());
               refreshMessage(e.getTextChannel(), e.getMessageId(), g);

           } else if(reaction.equals(ROLLCALL_REACTION)){
                LFGHandler.pingPlayers(g);

           } else if(reaction.equals(DELETE_REACTION)){
                // Check permissions
               PermissionHandler.isLeaderOrMod(e.getMember(), g);
               GroupSQL.delete(g);
               e.getTextChannel().deleteMessageById(e.getMessageId()).complete();
           }
        }
    }

    private void handleEventBoardRemoveEvent(MessageReactionRemoveEvent e){
        Group g = GroupSQL.queryGroupFromMsgID(e.getGuild().getId(), e.getMessageId());
        if(g != null){
            // Don't really need this but I'll leave it as a place holder

        }
    }

    private static void refreshMessage(TextChannel tc, String msgID, Group group){
        tc.getMessageById(msgID).complete().editMessage(group.toStringFull()).queue();
    }

    public static String postEventGroup(Group g) throws NoBoardForPlatformException {
        TextChannel tc = EventBoardSQL.getEventBoard(g.getServerID(), g.getType());
        Message msg = tc.sendMessage(g.toStringFull()).complete();

        msg.addReaction(JOIN_REACTION).queue();
        msg.addReaction(LEAVE_REACTION).queue();
        msg.addReaction(ROLLCALL_REACTION).queue();
        msg.addReaction(DELETE_REACTION).queue();

        return msg.getId();
    }

}