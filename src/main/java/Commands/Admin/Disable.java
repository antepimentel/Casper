package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

public class Disable extends AbstractCommand {

    private static String command = "disable";
    private static String desc = "Disable a command";
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
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    public void run(Message msg) throws NoArgumentsGivenException, InvalidPermissionsException {
        String[] args = getInputArgs(msg);
        String response = "";

        PermissionHandler.checkModPermissions(msg.getMember());
        AbstractCommand c = CommandHandler.getCommands().get(args[0]);

        MainSQLHandler.addDisabledCommand(msg.getGuild().getId(), c.getCommand());
        //c.setEnabled(false);
        response = "Disabled " + c.getCommand();

        msg.getChannel().sendMessage(response).queue();
    }
}
