package Core;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class Utility {

    public static void clearChannel(TextChannel tc){
        List<Message> messages = tc.getHistory().retrievePast(50).complete();

        for(int i = 0; i < messages.size(); i++){
            messages.get(i).delete().complete();
        }
    }
}
