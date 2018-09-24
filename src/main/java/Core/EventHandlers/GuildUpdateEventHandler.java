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

    public void onGuildMemberJoinEvent(GuildMemberJoinEvent e){
        String message = "Welcome " + e.getMember().getAsMention() + "! Tell us about yourself.";
        TextChannel gen = getGenChannel(e.getGuild());
        gen.sendMessage(message).queue();
    }

    public void onGuildMemberRoleAddEvent(GuildMemberRoleAddEvent e){
        List<Role> roles = e.getRoles();
        Role seraph = e.getGuild().getRolesByName("seraph", true).get(0);
        TextChannel gen = getGenChannel(e.getGuild());

        if(roles.contains(seraph)){
            gen.sendMessage("Congratulations " + e.getMember().getAsMention() + "! You've got your clan tag!").queue();
        }
    }

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

    public void onGuildLeaveEvent(GuildLeaveEvent e){
        String guildID = e.getGuild().getId();

        MainSQLHandler.deleteAllServerData(guildID);
    }

    public TextChannel getGenChannel(Guild g){
        return g.getTextChannelsByName("general", true).get(0);
    }
}
