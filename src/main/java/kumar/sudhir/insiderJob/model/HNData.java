package kumar.sudhir.insiderJob.model;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HNData {

    public List<Story> stories;
    public Map<Long, List<Comment>> Story_to_commentMap;
    public Set<Story> preStories;

    public List<Story> updatedStrories;
    public Map<Long, List<Comment>> UpdatedStory_to_commentMap;


    public HNData(){
        preStories = new HashSet<>();
        Story_to_commentMap = new HashMap<>();
        UpdatedStory_to_commentMap = new HashMap<>();
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

    public List<Long> getPreStoriesId(){
        List<Long> preStoriesId = new ArrayList<>();
        for(Story story: preStories){
            preStoriesId.add(story.getId());
        }
        return preStoriesId;
    }
}
