package Commands.Admin;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import Core.PermissionHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;

public class AddCustomCommand extends AbstractCommand {

    private static String command = "addcustomcommand";
    private static String desc = "Adds a custom command";
    private static String[] inputs = {"name", "command"};

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
    public void run(Message msg) throws NoArgumentsGivenException, InvalidPermissionsException, SQLException {
        String[] args = getInputArgs(msg);
        String response = "";

        String cName = args[0];
        String cCommand = args[1];
        String serverResponse = MainSQLHandler.checkCustomCommand(msg.getGuild().getId(), cName);

        if(serverResponse == null){
            MainSQLHandler.addCustomCommand(msg.getGuild().getId(), cName, cCommand);
            response = "Command added";
        } else {
            response = "That command already exists";
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
