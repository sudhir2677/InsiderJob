package kumar.sudhir.insiderJob.model;

public class Story implements Comparable<Story> {

    private String title;
    private String url;
    private int score;
    private String timeOfSubmission;
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
}