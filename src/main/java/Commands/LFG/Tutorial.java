package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.util.regex.Pattern;

public class Tutorial extends AbstractCommand {
    private static String command = "tutorial";
    private static String desc = "Send a tutorial on how to use the bot's LFG features.";
    private static String[] inputs = {};

    private static String tutorialTextFullPart1 =
            "**[LFG Tutorial]**\n" +
            "\n" +
            "**Creating Groups:**\n" +
            "1. Post a group using the r!post command:\n" +
            "\tUsage: r!post Name Date Time Timezone Platform\n" +
            "\tExample: \\`r!post \"Example Group\" 11/9 1:00PM NSZT pc\\`\n" +
            "\n" +
            "\tIf your group's name is more than one word, surround it with quotes. Dates and times must follow the mm/dd hh:mmAM/PM format.\n" +
            "\t\t\t\t\t\n" +
            "2. This will create a new post in the events channel which corresponds to the platform you entered: Find your group in that channel to get your group's ID. You will automatically be added to any group that you create. \n" +
            "\n" +
            "**Editing Groups:**\n" +
            "- If you want to assign a Destiny 2 activity to your group you can do so using the r!setgroupactivity command:\n" +
            "\tUsage: r!setgroupactivity ID ActivityCode\n" +
            "\tExample: r!setgroupactivity 0 levi\n" +
            "\n" +
            "- You can edit your group's name using the r!changename command:\n" +
            "\tUsage: r!changename ID Name\n" +
            "\tExample r!changename 0 Flawless Leviathan Run\n" +
            "\n" +
            "\tThere is no need to use quotes with this command\n" +
            "\n";

    private static String tutorialTextFullPart2 =
            "- You can edit your groups time and date using the r!changetime command:\n" +
            "\tUsage: r!changetime ID date time timezone\n" +
            "\tExample: r!changetime 0 11/6 1:00pm CST\n" +
            "\n" +
            "**Joining and Leaving Groups**\n" +
            "Seraphim Elite has two group board channels for the PC and PS4 platforms, #events-pc and #events-ps4. In there you will find all the available groups. Each post will have four reactions underneath it: \n" +
            "\t**+** : Add yourself to the group.\n" +
            "\t**-** : Remove yourself from the group.\n" +
            "\t**!** : Rollcall the group (Creator only).\n" +
            "\t**X** : Delete the group (Creator ony).\n" +
            "Press the reaction to do the actions described above, please allow for about a second for the action to be processed and avoid spamming the reactions if possible. \n" +
            "\n" +
            "**Rollcall**\n" +
            "- You can rollcall your group by using the **!** reaction underneath any group that you created. This will ping every member of the group so to prevent spam you are limited to 3 rollcalls per group. \n" +
            "\n" +
            "**Integration with Destiny 2**\n" +
            "- Rasputin can also display your Destiny information in any group that you've joined using the r!link command:\n" +
            "\tUsage: r!link username platform\n" +
            "\tExample: r!link NullRoz007#1650 pc\n" +
            "\n" +
            "- Available platforms are ps4 and pc, this will display your light levels for each of your characters alongside your name and warn you if you are underleveled for an activity if the group has been assigned one. \n";


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

    public void run(Message msg) throws CustomAbstractException {
        PrivateChannel dmChannel = msg.getAuthor().openPrivateChannel().complete();
        dmChannel.sendMessage(tutorialTextFullPart1).complete();
        dmChannel.sendMessage(tutorialTextFullPart2).queue();
    }
}
