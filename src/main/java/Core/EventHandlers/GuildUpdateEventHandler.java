package Core.EventHandlers;

import JDBC.AutoAssignmentSQL;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import sun.applet.Main;

import java.util.List;

public class GuildUpdateEventHandler implements EventListener {

    /**
     * This catches a generic event and checks the type.
     * Filters to the appropriate method.
     * @param e
     */
    @Override
    public void onEvent(Event e){
        if(e instanceof GuildMemberJoinEvent){
            onGuildMemberJoinEvent((GuildMemberJoinEvent) e);

        } else if(e instanceof GuildMemberRoleAddEvent){
            onGuildMemberRoleAddEvent((GuildMemberRoleAddEvent) e);

        } else if(e instanceof GuildJoinEvent){
            onGuildJoinEvent((GuildJoinEvent) e);

        } else if(e instanceof GuildLeaveEvent){
            onGuildLeaveEvent((GuildLeaveEvent) e);
        }
    }

    /**
     * Welcome message, new member
     *
     * @param e
     */
    public void onGuildMemberJoinEvent(GuildMemberJoinEvent e){
        String message = "Welcome " + e.getMember().getAsMention() + "! Tell us about yourself.";
        TextChannel gen = getGenChannel(e.getGuild());
        gen.sendMessage(message).queue();

        //TODO: Make this customizable, Also send DM message
    }

    /**
     * Role update event
     *
     * @param e
     */
    public void onGuildMemberRoleAddEvent(GuildMemberRoleAddEvent e){
        List<Role> roles = e.getRoles();
        Role seraph = e.getGuild().getRolesByName("seraph", true).get(0);
        TextChannel gen = getGenChannel(e.getGuild());

        if(roles.contains(seraph)){
            gen.sendMessage("Congratulations " + e.getMember().getAsMention() + "! You've got your clan tag!").queue();
        }

        //TODO: Make this more general, add Initiate role on join. Deal with Former Seraph role being added.
    }

    /**
     * This runs when the bot first joins a server.
     * Adds server to db, checks for an auto-assignment channel
     *
     * @param e
     */
    public void onGuildJoinEvent(GuildJoinEvent e){
        String guildID = e.getGuild().getId();
        String guildName = e.getGuild().getName();

        MainSQLHandler.addServer(guildID, guildName);

        // Check for auto-assignment channel
        List<TextChannel> channels = e.getGuild().getTextChannelsByName("role-assignment", false);
        if(channels.size() == 1){
            AutoAssignmentSQL.addAutoChannelForServer(e.getGuild().getId(), channels.get(0));
        }
    }

    /**
     * This runs when the bot leaves a server.
     * Scrubs the db of data related to that server.
     *
     * @param e
     */
    public void onGuildLeaveEvent(GuildLeaveEvent e){
        String guildID = e.getGuild().getId();

        MainSQLHandler.deleteAllServerData(guildID);
    }

    /**
     * Returns a channel named "general"
     *
     * @param g
     * @return
     */
    public TextChannel getGenChannel(Guild g){
        return g.getTextChannelsByName("general", true).get(0);
    }
}
