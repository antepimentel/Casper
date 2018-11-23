package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Core.Utility;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import Exceptions.NoBoardForPlatformException;
import JDBC.EventBoardSQL;
import JDBC.GroupSQL;
import JDBC.MainSQLHandler;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReloadBoards extends AbstractCommand {

    private static String command = "reloadboards";
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

    public void run(Message msg) throws InvalidPermissionsException, NoBoardForPlatformException {
        String response = "";
        PermissionHandler.checkModPermissions(msg.getMember());

        HashMap<String, TextChannel> channels = EventBoardSQL.getAllEventBoards(msg.getGuild().getId());
        ArrayList<Group> groups = GroupSQL.getGroupsByServer(msg.getGuild().getId());

        // Clear all the channels
        for(Map.Entry<String, TextChannel> entry: channels.entrySet()){
            Utility.clearChannel(entry.getValue());
        }

        // Post all groups into the proper channels
        for(Group g: groups){
            LFGHandler.repostAndUpdateMsgID(g);
        }
    }
}