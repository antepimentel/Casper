package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.util.ArrayList;

public class ViewAllGroups extends AbstractCommand {

    private static String command = "groups";
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
        return CommandCategory.LFG;
    }

    public void run(Message msg){
        ArrayList<String> responses = new ArrayList<String>();
        int numGroupsPerMessage = 5;

        ArrayList<Group> groups = LFGHandler.getGroupsByServer(msg.getGuild().getId());
        int i = 0;
        while(i < groups.size()){
            String response = "";
            for(int j = 0; j < numGroupsPerMessage; j++){
                if(i == groups.size()){
                    break;
                }
                response = response + groups.get(i).toString() + "\n\n";
                i++;
            }
            responses.add(response);
        }

        PrivateChannel ch = msg.getAuthor().openPrivateChannel().complete();
        for(int k = 0; k < responses.size(); k++){
            ch.sendMessage(responses.get(k)).queue();
        }
    }
}
