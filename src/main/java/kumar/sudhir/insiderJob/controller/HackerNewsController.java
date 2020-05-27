package kumar.sudhir.insiderJob.controller;

import kumar.sudhir.insiderJob.service.TopNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HackerNewsController {

    @Autowired
    private TopNewsService topNewsService;

    /*
     * returns the top 10 stories ranked by score in the last 10 minutes. Each story will
     * have the title, url, score, time of submission, and the user who submitted it.
     * */
    @GetMapping("/top-stories")
    public ResponseEntity<?> getTopStories(){
        return new ResponseEntity<>(topNewsService.getTopStories(), HttpStatus.OK);
        //return topNewsService.getTopStories();
    }

    /*
     *  returns the top 10 parent comments on a given story, sorted by the total number of
     * comments (including child comments) per thread. Each comment will have the comment's text,
     *  the user’s HN handle, and their HN age. The HN age of a user is basically how old their Hacker News profile is in years
     * */
    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getTopComment(@PathVariable("id")Long id){
        return new ResponseEntity<>(topNewsService.getTopComment(id), HttpStatus.OK);
    }

    /*
     * returns all the past top stories that were served previously
     * */
    @GetMapping("/past-stories")
    public ResponseEntity<?> getPastStories(){
        return new ResponseEntity<>(topNewsService.getPreStory(), HttpStatus.OK);
        //return "Past Stories";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "Page you are looking is not there";
    }
}
