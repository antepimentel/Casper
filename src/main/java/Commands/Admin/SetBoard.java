package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.EventBoardSQL;
import JDBC.MainSQLHandler;
import LFG.Group;
import net.dv8tion.jda.core.entities.Message;

public class SetBoard extends AbstractCommand {

    private static String command = "setboard";
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
        String platform = args[0].toLowerCase();

        PermissionHandler.checkModPermissions(msg.getMember());

        if(Group.PLATFORMS.contains(platform)){
            EventBoardSQL.setEventBoard(msg.getGuild().getId(), msg.getChannel().getId(), platform);
            response = "Board set for " + platform;
        } else {
            response = "Invalid platform: " + platform;
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
