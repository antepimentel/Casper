package LFG;

public class Platform {
    private String name;
    private int embedColor;
    private String embedIconUrl;

    public Platform(String name, int embedColor, String embedIconUrl) {
        this.name = name;
        this.embedColor = embedColor;
        this.embedIconUrl = embedIconUrl;
    }

    public String getName() { return name; }
    public int getEmbedColor() { return embedColor; }
    public String getEmbedIconUrl() { return embedIconUrl; }
}
