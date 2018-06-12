package Core.EventHandlers;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.List;

public class GuildUpdateEventHandler implements EventListener {

    @Override
    public void onEvent(Event e){
        if(e instanceof GuildMemberJoinEvent){
            onGuildMemberJoinEvent((GuildMemberJoinEvent) e);
        } else if(e instanceof GuildMemberRoleAddEvent){
            onGuildMemberRoleAddEvent((GuildMemberRoleAddEvent) e);
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

    public TextChannel getGenChannel(Guild g){
        return g.getTextChannelsByName("general", true).get(0);
    }
}
