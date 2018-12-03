package Commands;

import Core.Bot;
import Core.PropertyKeys;
import Exceptions.CustomAbstractException;
import Exceptions.NoArgumentsGivenException;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractCommand {

    private boolean isEnabled = true;

    public AbstractCommand(){

    }

    public String getUsage(String name, String[] inputs){
        String result = "`" + Bot.props.getProperty(PropertyKeys.DELIMITER_KEY)+name + " ";
        for(int i = 0; i < inputs.length; i++){
            result = result + "<" + inputs[i] + "> ";
        }
        result = result + "`";
        return result;
    }

    public abstract String getCommand();

    public abstract String getDescription();

    public abstract String[] getInputs();

    public abstract int getCategory();

    public boolean isEnabled(){
        return isEnabled;
    }

    public void setEnabled(boolean enabled){
        this.isEnabled = enabled;
    }

    public abstract void run(Message msg) throws CustomAbstractException, IOException;

    /**
     * Gets the input arguments from a Message object
     * @param m
     * @return
     * @throws NoArgumentsGivenException
     */
    public String[] getInputArgs(Message m) throws NoArgumentsGivenException {

        String[] result;

        // Removes delimiter and splits input
        String[] args = m.getContentRaw().replaceFirst(Bot.props.getProperty(PropertyKeys.DELIMITER_KEY), "").split(" ");

        if(args.length > 1){
            // Removes first argument, the command word
            result = Arrays.copyOfRange(args, 1, args.length);
            //System.out.println(result.toString());
            return result;
        } else {
            throw new NoArgumentsGivenException(CommandHandler.getCommands().get(args[0]));
        }
    }

}
