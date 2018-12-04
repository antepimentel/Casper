package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

public class Enable extends AbstractCommand {

    private static String command = "enable";
    private static String desc = "Emable a command";
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
        return CommandCategory.ADMIN;
    }

    public void run(Message msg) throws NoArgumentsGivenException, InvalidPermissionsException {
        String[] args = getInputArgs(msg);
        String response = "";

        PermissionHandler.checkModPermissions(msg.getMember());
        AbstractCommand c = CommandHandler.getCommands().get(args[0]);

        MainSQLHandler.dropDisabledCommand(msg.getGuild().getId(), c.getCommand());
        //c.setEnabled(true);
        response = "Enabled " + c.getCommand();

        msg.getChannel().sendMessage(response).queue();
    }
}
