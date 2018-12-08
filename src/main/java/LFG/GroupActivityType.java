package LFG;

import Destiny.DestinyAPIWrapper;
import Destiny.DestinyProperties;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import static Destiny.DestinyAPIWrapper.getDestinyEntityDefinition;

public class GroupActivityType {
    private String code;
    private String destinyActivityDefintionHash;
    private boolean destinyActivityType;
    public GroupActivityType(String code, String activityHash, boolean isDestinyActivityType) {
        this.code = code;
        this.destinyActivityDefintionHash = activityHash;
        this.destinyActivityType = isDestinyActivityType;
    }

    public String getCode() {
        return code;
    }

    public JsonObject getDestinyData() throws IOException{
        JsonObject activity = DestinyAPIWrapper.getDestinyEntityDefinition(DestinyProperties.ACTIVITY_DEF, destinyActivityDefintionHash);
        return activity;
    }

    EmbedBuilder editEmbed(EmbedBuilder embedBuilder) {
        try {
            JsonObject activity = getDestinyData();
            JsonObject displayProperties = activity.getAsJsonObject("displayProperties");
            String name = displayProperties.getAsJsonPrimitive("name").getAsString();
            String description = displayProperties.getAsJsonPrimitive("description").getAsString();
            String iconUrl = "https://www.bungie.net"+displayProperties.getAsJsonPrimitive("icon").getAsString();
            String ebDesc = "Activity: "+name + "\n*"+description+"*";
            embedBuilder.setThumbnail(iconUrl);
            embedBuilder.setDescription(ebDesc);

        } catch (IOException ex) {
            System.out.println(ex.getMessage() + "\n" + ex.getStackTrace());
        }
        return embedBuilder;
    }
}
