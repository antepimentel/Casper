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
    private static String desc = "Set a channel to be the event board channel for a platform";
    private static String[] inputs = {"platform"};

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
        String platform = args[0].toLowerCase();

        PermissionHandler.checkModPermissions(msg.getMember());

        int platformIndex = -1;
        for(int i = 0; i < Group.PLATFORMS.size(); i++){
            if(Group.PLATFORMS.get(i).getName().equals(platform)) {
                platformIndex = i;
                break;
            }
        }

        if(platformIndex != -1) {
            EventBoardSQL.setEventBoard(msg.getGuild().getId(), msg.getChannel().getId(), platform);
            response = "Board set for " + platform;
        } else {
            response = "Invalid platform: " + platform;
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
