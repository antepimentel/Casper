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

public class UseAsAutoChannel extends AbstractCommand {

    private static String command = "useasautochannel";
    private static String desc = "temp";
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
    public int getCategory() {
        return CommandCategory.GENERAL;
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