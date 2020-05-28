package kumar.sudhir.insiderJob.model;

public class Story implements Comparable<Story> {

    private String title;
    private Long id;
    private String url;
    private int score;
    private String timeOfSubmission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String user;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimeOfSubmission() {
        return timeOfSubmission;
    }

    public void setTimeOfSubmission(String timeOfSubmission) {
        this.timeOfSubmission = timeOfSubmission;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int compareTo(Story stories) {
        return stories.score - this.score;
    }

    @Override
    public int hashCode() {
        return (int)(this.getId()%(1e9+7));
    }
}