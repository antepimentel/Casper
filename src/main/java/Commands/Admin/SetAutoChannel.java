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

public class SetAutoChannel extends AbstractCommand {

    private static String command = "setautochannel";
    private static String desc = "Call this from the channel you want to use for auto role assignment";
    private static String[] inputs = {};

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
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    public void run(Message msg) throws InvalidPermissionsException, NoArgumentsGivenException {
        PermissionHandler.checkModPermissions(msg.getMember());

        String serverID = msg.getGuild().getId();

        if(AutoAssignmentSQL.checkAutoChannelForServer(serverID)){
            AutoAssignmentSQL.changeAutoChannelName(serverID, msg.getTextChannel());
        } else {
            AutoAssignmentSQL.addAutoChannelForServer(serverID, msg.getTextChannel());
        }
        AutoAssignmentSQL.printMessagesForServer(serverID);
    }
}