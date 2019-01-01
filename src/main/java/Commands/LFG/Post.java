package Commands.LFG;

import Commands.CommandCategory;
import Core.Bot;
import Core.PropertyKeys;
import Commands.AbstractCommand;
import Exceptions.NameTooLongException;
import Exceptions.NoArgumentsGivenException;
import Exceptions.NoBoardForPlatformException;
import JDBC.EventBoardSQL;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.text.ParseException;
import java.util.ArrayList;

public class Post extends AbstractCommand {

    private static String command = "post";
    private static String desc = "Post a group";
    private static String[] inputs = {"name", "platform", "date(MM/dd)", "time", "timezone", "year (optional)"};

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
        return CommandCategory.LFG;
    }

    @Override
    public String[] getInputArgs(Message msg){
        ArrayList<String> temp = new ArrayList<String>();

        // Removes delimiter and splits input
        String[] args1 = msg.getContentRaw().replaceFirst(Bot.props.getProperty(PropertyKeys.DELIMITER_KEY), "").split("\"");
        temp.add(args1[1]);

        String[] args2 = args1[2].trim().split(" ");

        for(int i = 0; i < args2.length; i++){
            temp.add(args2[i]);
        }

        String[] result = new String[temp.size()];
        temp.toArray(result);
        return result;
    }

    @Override
    public void run(Message msg) throws NoArgumentsGivenException, NoBoardForPlatformException {
        String response = "";
        try {
            String[] args;

            // Give input the best chance at passing
            if(msg.getContentRaw().contains("\"")){
                args = getInputArgs(msg);
            } else {
                args = super.getInputArgs(msg);
            }

            // Declare variables here from args in case we need to change ordering later
            String name = args[0];
            String platform = args[1].toLowerCase();
            String date = args[2];
            String time = args[3];
            String timezone = args[4];
            String year = null;

            if(args.length >= 6){
                year = args[5];
            }

            //Check platform
            int platformIndex = -1;
            for(int i = 0; i < Group.PLATFORMS.size(); i++){
                if(Group.PLATFORMS.get(i).getName().equals(platform)) {
                    platformIndex = i;
                    break;
                }
            }

            if(platformIndex != -1) {
                //Group g = LFGHandler.post(msg.getGuild().getId(), args[0], args[1], args[2], args[3], msg.getMember(), args[4]);
                Group g = LFGHandler.post(msg.getGuild().getId(), name, date, time, timezone, msg.getMember(), platform, year);
                response = msg.getMember().getAsMention() + ", your group: *" + name + "* has been created, you can view it in " + EventBoardSQL.getEventBoard(msg.getGuild().getId(), platform).getAsMention();
            } else {
                response = "No platform exists for: " + platform;
            }

        } catch (ParseException e){
            e.printStackTrace();
            response = "Unable to parse date/time. Required format:"
                    + "```MM/dd hh:mmaa zzz```"
                    + "M - Month\n"
                    + "d - Day\n"
                    + "h - Hour\n"
                    + "m - Minute\n"
                    + "a - AM/PM\n"
                    + "z - Timezone\n";
        } catch (NameTooLongException e) {
            response = e.getMessage();
        }
        msg.getChannel().sendMessage(response).queue();
    }
}
