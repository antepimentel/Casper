package Core;

public class Linker {
    private String discordId;
    private String destinyMembershipId;
    private int platform;

    public Linker(String discordId, String destinyMembershipId, int platform) {
        this.discordId = discordId;
        this.destinyMembershipId = destinyMembershipId;
        this.platform = platform;
    }

    public String getDiscordId() {
        return discordId;
    }

    public int getPlatform() {
        return platform;
    }

    public String getDestinyMembershipId() {
        return destinyMembershipId;
    }

    public void setDestinyMembershipId(String destinyMembershipId) {
        this.destinyMembershipId = destinyMembershipId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}
