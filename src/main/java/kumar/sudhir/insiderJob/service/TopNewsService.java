package kumar.sudhir.insiderJob.service;

import kumar.sudhir.insiderJob.model.Comment;
import kumar.sudhir.insiderJob.model.HNData;
import kumar.sudhir.insiderJob.model.Story;
import kumar.sudhir.insiderJob.model.hackerNews.HackerNewsData;
import kumar.sudhir.insiderJob.model.hackerNews.HackerNewsStory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TopNewsService {

    @Autowired
    HackerNewsData hackerNewsData;

    @Autowired
    HNData hnData;

    public List<Story> getTopStories() {
        return hnData.updatedStrories;
        //return hackerNewsData.getHnData().updatedStrories;
    }

    public List<Comment> getTopComment(Long id) {
        return hnData.UpdatedStory_to_commentMap.get(id);
        //return hackerNewsData.getHnData().Story_to_commentMap.get(id);
    }

    public Set<Story> getPreStory(){
        return hnData.preStories;
        //return hackerNewsData.getHnData().preStories;
    }
}
