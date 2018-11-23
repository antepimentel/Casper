package Commands.Admin;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.PermissionHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.AutoAssignmentSQL;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class AddAutoRole extends AbstractCommand {

    private static String command = "addautorole";
    private static String desc = "temp";
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
        return CommandCategory.GENERAL;
    }

    public void run(Message msg) throws InvalidPermissionsException, NoArgumentsGivenException {
        PermissionHandler.checkModPermissions(msg.getMember());

        String[] args = getInputArgs(msg);
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
            AutoAssignmentSQL.addAutoRoleForServer(msg.getGuild().getId(), result.get(0));
            msg.getChannel().sendMessage("Success").queue();
        }
    }
}
