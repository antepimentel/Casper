package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import LFG.Group;
import LFG.GroupActivityType;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.io.IOException;

public class ActivityCodes extends AbstractCommand {
    private static String command = "activitycodes";
    private static String desc = "Get a list of activity codes for use in the setactivity command.";
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
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public void run(Message msg) {
        String response = "Available activity codes: ";
        try {
            for(GroupActivityType type : Group.GROUPTYPES) {
                JsonObject data = type.getDestinyData();
                response += "\n**Code**: "+type.getCode() + ", **Activity**: "+data.getAsJsonObject("displayProperties").getAsJsonPrimitive("name").getAsString();
            }
        } catch (IOException ex) {

        }

        PrivateChannel dmChannel = msg.getAuthor().openPrivateChannel().complete();
        dmChannel.sendMessage(response);
    }
}
