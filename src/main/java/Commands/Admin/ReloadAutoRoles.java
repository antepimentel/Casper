package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.InvalidPermissionsException;
import JDBC.AutoAssignmentSQL;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class ReloadAutoRoles extends AbstractCommand {

    private static String command = "reloadautoroles";
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

    public void run(Message msg) throws InvalidPermissionsException {
        PermissionHandler.checkModPermissions(msg.getMember());
        String serverID = msg.getGuild().getId();
        AutoAssignmentSQL.printMessagesForServer(serverID);
    }
}