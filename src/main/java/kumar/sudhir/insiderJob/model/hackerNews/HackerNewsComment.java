package kumar.sudhir.insiderJob.model.hackerNews;

import java.util.List;

public class HackerNewsComment implements Comparable<HackerNewsComment>{

    private String by;
    private Long id;
    private List<Long> kids;
    private Long parent;
    private String text;
    private Long time;
    private String type;

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getKids() {
        return kids;
    }

    public void setKids(List<Long> kids) {
        this.kids = kids;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return (int)(this.getId()%(1e9+7));
    }

    @Override
    public int compareTo(HackerNewsComment hackerNewsComment) {
        return hackerNewsComment.getKids().size() - this.getKids().size();
    }
}

