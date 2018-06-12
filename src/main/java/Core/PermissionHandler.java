package Core;

import Exceptions.InvalidPermissionsException;
import LFG.Group;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class PermissionHandler {

    private static Permission[] modPerms = {Permission.MANAGE_ROLES, Permission.MESSAGE_MANAGE};

    public static boolean checkModPermissions(Member m) throws InvalidPermissionsException {
        List<Permission> perms = m.getPermissions();
        boolean check = true;

        for(int i = 0; i < modPerms.length; i++){
            check = check && perms.contains(modPerms[i]);
        }

        if(!check)
            throw new InvalidPermissionsException(m);

        return check;
    }

    public static boolean isLeaderOrMod(Member m, Group g) throws InvalidPermissionsException {
        if(m.equals(g.getPlayers().get(0))){
            return true;
        } else if(checkModPermissions(m)){
            return true;
        } else {
            throw new InvalidPermissionsException(m);
        }
    }

    public static boolean isPublicChannel(Message msg){
        ChannelType type = msg.getChannel().getType();
        return type == ChannelType.TEXT;
    }

    public static boolean isPrivateChannel(Message msg){
        ChannelType type = msg.getChannel().getType();
        return type == ChannelType.PRIVATE;
    }

    public static boolean isAnnouncementsChannel(Message msg){
        return msg.getChannel().getName().toLowerCase().equals("announcements");
    }
}
