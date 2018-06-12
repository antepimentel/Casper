package Commands.LFG;

import Commands.AbstractCommand;
import Exceptions.CustomAbstractException;
import Exceptions.NoAvailableSpotsException;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class JoinGroup extends AbstractCommand {

    private static String command = "joingroup";
    private static String desc = "temp";
    private static String[] inputs = {"ID"};

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
    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        String response = "";

        try {
            LFGHandler.join(Integer.parseInt(args[0]), msg.getMember());
            response = msg.getMember().getAsMention()+" added to group: " + args[0];

        } catch (NoAvailableSpotsException e) {
            e.printStackTrace();

            try {
                LFGHandler.joinAsSub(Integer.parseInt(args[0]), msg.getMember());
                response = msg.getMember().getAsMention()+" Group: " + args[0] + " is full. Adding as a substitute";

            } catch (NoAvailableSpotsException e1) {
                e1.printStackTrace();
                response = e.getMessage();
            }

        }
        msg.getChannel().sendMessage(response).queue();
    }
}
