package Commands.Destiny;

import Commands.AbstractCommand;
import Destiny.Activity;
import Destiny.DestinyAPIWrapper;
import Destiny.DestinyProperties;
import Destiny.Milestone;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.io.IOException;
import java.util.ArrayList;

public class WeeklySummary extends AbstractCommand {

    private static String command = "weeklysummary";
    private static String desc = "temp";
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
        return 0;
    }

    @Override
    public void run(Message msg) throws IOException {
        ArrayList<Milestone> milestones = DestinyAPIWrapper.getPublicMilestones();

        Milestone nf = getMilestoneByName(milestones, "Nightfall");
        MessageEmbed nfEmbed = buildMilestoneEmbed(nf);
        msg.getChannel().sendMessage(nfEmbed).queue();

        Milestone levi = getMilestoneByName(milestones, "Leviathan Raid");
        MessageEmbed leviEmbed = buildMilestoneEmbed(levi);
        msg.getChannel().sendMessage(leviEmbed).queue();

        Milestone med = getMilestoneByName(milestones, "Meditations");
        MessageEmbed medEmbed = buildMilestoneEmbed(med);
        msg.getChannel().sendMessage(medEmbed).queue();

        Milestone trials = getMilestoneByName(milestones, "Trials of the Nine");
        MessageEmbed trialsEmbed = buildMilestoneEmbed(trials);
        msg.getChannel().sendMessage(trialsEmbed).queue();
    }
    
    private MessageEmbed buildMilestoneEmbed(Milestone m){
        EmbedBuilder embed = new EmbedBuilder();

        embed.addField(m.getName(), m.getDesc(), false);
        embed.setThumbnail(DestinyProperties.BASE_PATH + m.getIcon());

        for (Activity quest: m.getQuests()) {
            if(m.getIcon().equals("")){
                embed.setThumbnail(DestinyProperties.BASE_PATH+quest.getIcon());
            }

            embed.addField(quest.getName(), quest.getDesc(), false);
            //embed.addField("Normal", "Level: "+quest.getLevel() + "\nPower Level: "+quest.getLightLevel(), true);
            embed.setImage(DestinyProperties.BASE_PATH + quest.getImage());
        }


        return embed.build();
    }

    private Milestone getMilestoneByName(ArrayList<Milestone> milestones, String target){
        Milestone result = null;
        for(int i = 0; i < milestones.size(); i++){
            if(milestones.get(i).getName().equalsIgnoreCase(target)){
                result = milestones.get(i);
            }
        }
        return result;
    }
}
