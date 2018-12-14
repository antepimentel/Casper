package Radio;

public class Track {
    public String title;
    public String start_time;
    public String artwork_url;
    public String artwork_url_large;

    public String getArtwork_url() {
        return artwork_url;
    }

    public String getArtwork_url_large() {
        return artwork_url_large;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getTitle() {
        return title;
    }

    public void setArtwork_url(String artwork_url) {
        this.artwork_url = artwork_url;
    }

    public void setArtwork_url_large(String artwork_url_large) {
        this.artwork_url_large = artwork_url_large;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
