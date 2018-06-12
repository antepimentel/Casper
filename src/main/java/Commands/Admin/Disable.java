package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import net.dv8tion.jda.core.entities.Message;

public class Disable extends AbstractCommand {

    private static String command = "disable";
    private static String desc = "temp";
    private static String[] inputs = {"command"};

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

    public void run(Message msg) throws NoArgumentsGivenException, InvalidPermissionsException {
        String[] args = getInputArgs(msg);
        String response = "";

        PermissionHandler.checkModPermissions(msg.getMember());
        AbstractCommand c = CommandHandler.getCommands().get(args[0]);
        c.setEnabled(false);
        response = "Disabled " + c.getCommand();

        msg.getChannel().sendMessage(response).queue();
    }
}
