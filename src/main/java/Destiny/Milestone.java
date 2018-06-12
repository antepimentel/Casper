package Destiny;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class Milestone {

    public static int TYPE_NF = 3;
    public static int TYPE_CTA = 5;

    private String name;
    private String desc;
    private String icon = "";
    private ArrayList<Activity> quests;

    public Milestone(JsonObject input, String hashKey) throws IOException {
        JsonObject milestoneJSON = DestinyAPIWrapper.getDestinyEntityDefinition(DestinyProperties.MILESTONE_DEF, hashKey);
        JsonObject data = milestoneJSON.getAsJsonObject("displayProperties");

        quests = new ArrayList<Activity>();

        if(data != null){
            name = data.getAsJsonPrimitive("name").toString().replace("\"", "");
            desc = data.getAsJsonPrimitive("description").toString().replace("\"", "");

            if(data.getAsJsonPrimitive("hasIcon").getAsBoolean()){
                icon = data.getAsJsonPrimitive("icon").toString().replace("\"", "");
            }
        }

        // Check for sub activities/quests
        JsonArray questsJson = input.getAsJsonObject(hashKey).getAsJsonArray("availableQuests");

        try{
            for(int i = 0; i < questsJson.size(); i++){
                String questHashKey = questsJson.get(i).getAsJsonObject().getAsJsonObject("activity").getAsJsonPrimitive("activityHash").toString();
                JsonObject questJson = DestinyAPIWrapper.getDestinyEntityDefinition(DestinyProperties.ACTIVITY_DEF, questHashKey);

                this.quests.add(new Activity(questJson));
            }

        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public String print(){
        String temp = name + "\n"
                + desc + "\n";

        return temp;
    }

    public ArrayList<Activity> getQuests(){
        return quests;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getIcon() {
        return icon;
    }

    public boolean hasIcon(){
        return !icon.equals("");
    }
}
