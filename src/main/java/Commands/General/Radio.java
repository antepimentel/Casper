package Commands.General;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;

import Radio.StationManager;
import Exceptions.RadioAPIException;
import Radio.RadioStatus;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.io.IOException;


public class Radio extends AbstractCommand {

    private static String command = "radio";
    private static String desc = "get the status of the Sera Radio Network. ";
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
    public void run(Message msg) throws CustomAbstractException, IOException {
        try {
            RadioStatus status =  StationManager.getStatus();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("[Tune in](https://www.seraradionetwork.net/)");
            embedBuilder.setThumbnail(status.current_track.artwork_url);
            embedBuilder.setAuthor("Sera Radio Network", "https://www.seraradionetwork.net/", status.logo_url);
            embedBuilder.addField("Now Playing", status.current_track.getTitle(), false);

            String historyString = "";
            for(int i = 1; i < ((status.history.length >= 6) ? 6 : status.history.length); i++) {
                historyString += i + ". " + status.history[i].title + "\n";
            }

            historyString += "...";
            embedBuilder.addField("History", historyString, false);
            embedBuilder.setFooter("Host: Not Yet Available :(", null);
            embedBuilder.setColor(0x5f446f);
            MessageEmbed embed = embedBuilder.build();
            msg.getChannel().sendMessage(embed).queue();
        }
        catch (RadioAPIException | IOException ex) {
            throw ex;
        }
    }
}
