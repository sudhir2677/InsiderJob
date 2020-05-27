package kumar.sudhir.insiderJob.model;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class HNData {

    public List<Story> stories;
    public Map<Long, List<Comment>> Story_to_commentMap;
    public Set<Story> preStories;

    public List<Story> updatedStrories;
    public Map<Long, List<Comment>> UpdatedStory_to_commentMap;


    public HNData(){
        preStories = new HashSet<>();
    }

    public List<Story> getStories() {
        return stories;
    }

    public Map<Long, List<Comment>> getStory_to_commentMap() {
        return Story_to_commentMap;
    }

    public Set<Story> getPreStories() {
        return preStories;
    }
}
