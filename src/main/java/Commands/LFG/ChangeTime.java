package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.text.ParseException;

public class ChangeTime extends AbstractCommand {

    private static String command = "changetime";
    private static String desc = "temp";
    private static String[] inputs = {"ID", "date", "time", "timezone"};

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
        return CommandCategory.LFG;
    }

    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        String response = "";
        int ID = Integer.parseInt(args[0]);
        String date = args[1];
        String time = args[2];
        String timezone = args[3];

        try {
            Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);

            if(PermissionHandler.isLeaderOrMod(msg.getMember(), g)){
                g.setDate(date, time, timezone);
                GroupSQL.updateTime(g);
                response = response + g.toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            response = "Unable to parse date/time. Required format:"
                    + "```MM/dd hh:mmaa zzz```"
                    + "M - Month\n"
                    + "d - Day\n"
                    + "h - Hour\n"
                    + "m - Minute\n"
                    + "a - AM/PM\n"
                    + "z - Timezone\n";
        }
        msg.getChannel().sendMessage(response).queue();
    }
}
