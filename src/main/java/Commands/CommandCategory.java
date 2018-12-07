package Commands;

import Core.PermissionHandler;
import Exceptions.InvalidPermissionsException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class CommandCategory {
    public static CommandCategory ADMIN = new CommandCategory("Admin commands.", new String[0], true);
    public static CommandCategory GENERAL = new CommandCategory("General use commands.", new String[0], false);
    public static CommandCategory LFG = new CommandCategory("Commands for creating and editing groups.", new String[] { "rasputin-commands"}, false);

    private String[] approvedChannels;
    private boolean mod;
    private String description;

    public CommandCategory(String description, String[] approvedChannels, boolean mod) {
        this.approvedChannels = approvedChannels;
        this.mod = mod;
        this.description = description;
    }

    public boolean canRun(Member member, Message message) {
        boolean result = true;
        try {
            PermissionHandler.checkModPermissions(member);

            System.out.println("MOD!");
        } catch (InvalidPermissionsException ex) { //member is not a mod so check if command can be run
            if(mod) { //cat requires mod
                result = false;
            } else if(approvedChannels.length != 0) { //channel restrictions
                result =  Arrays.asList(approvedChannels).indexOf(message.getChannel().getName()) != -1;
            }
        }

        return result;
    }

    public String[] getApprovedChannels (){
        return approvedChannels;
    }

    public boolean isMod() {
        return mod;
    }

    public String getDescription() {
        return description;
    }

    public void setApprovedChannels(String[] channels) {
        approvedChannels = channels;
    }

    public void setMod(boolean m) {
        mod = m;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
