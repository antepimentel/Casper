package Destiny;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Activity {

    private String name;
    private String desc;
    private String icon;
    private String image;
    private String level;
    private String lightLevel;

    public Activity(JsonObject input){
        JsonObject data = input.getAsJsonObject("displayProperties");
        if(data != null){
            name = data.getAsJsonPrimitive("name").toString().replace("\"", "");
            desc = data.getAsJsonPrimitive("description").toString().replace("\"", "");
            icon = data.getAsJsonPrimitive("icon").toString().replace("\"", "");
        }

        this.image = input.getAsJsonPrimitive("pgcrImage").toString().replace("\"", "");
        this.level = input.getAsJsonPrimitive("activityLevel").toString().replace("\"", "");
        this.lightLevel = input.getAsJsonPrimitive("activityLightLevel").toString().replace("\"", "");
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

    public String getLevel() {
        return level;
    }

    public String getLightLevel() {
        return lightLevel;
    }

    public String getImage(){
        return image;
    }
}
