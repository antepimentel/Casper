package Commands.Admin;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.InvalidPermissionsException;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class Clear extends AbstractCommand {

    private static String command = "clear";
    private static String desc = "Clear a channel 50 messages at a time.";
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
        List<Message> messages = msg.getChannel().getHistory().retrievePast(50).complete();

        for(int i = 0; i < messages.size(); i++){
            messages.get(i).delete().queue();
        }
    }
}
