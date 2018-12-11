package Commands.Admin;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import Core.PermissionHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

public class RemoveCustomCommand extends AbstractCommand {

    private static String command = "removecustomcommand";
    private static String desc = "Removes a custom command";
    private static String[] inputs = {"name"};

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

    @Override
    public void run(Message msg) throws InvalidPermissionsException, NoArgumentsGivenException {
        String[] args = getInputArgs(msg);
        String response = "";

        String cName = args[0];

        String serverResponse = MainSQLHandler.checkCustomCommand(msg.getGuild().getId(), cName);

        if(serverResponse == null){
            response = "That command does not exist";
        } else {
            MainSQLHandler.dropCustomCommand(msg.getGuild().getId(), cName);
            response = "Command removed";
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
