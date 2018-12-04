package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.AutoAssignmentSQL;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class RemoveAutoRole extends AbstractCommand {

    private static String command = "removeautorole";
    private static String desc = "Removes the role from the auto assignment channel";
    private static String[] inputs = {"role"};

    @Override
    public String[] getInputs() {
        return inputs;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public int getCategory() {
        return CommandCategory.ADMIN;
    }

    public void run(Message msg) throws InvalidPermissionsException, NoArgumentsGivenException {
        PermissionHandler.checkModPermissions(msg.getMember());
        String[] args = getInputArgs(msg);

        String serverID = msg.getGuild().getId();
        String role = "";

        // Build into one argument
        for(int i = 0; i < args.length; i++){
            role = role + args[i] + " ";
        }
        role = role.trim();

        // Does role exist?
        List<Role> result = msg.getGuild().getRolesByName(role, true);

        if(result.size() < 1){
            // Role not found
        } else {
            //AutoAssignmentSQL.addAutoChannelForServer(msg.getGuild().getId(), msg.getTextChannel());
            AutoAssignmentSQL.dropAutoRoleForServer(serverID, result.get(0));
            msg.getChannel().sendMessage("Success").queue();
        }
    }
}