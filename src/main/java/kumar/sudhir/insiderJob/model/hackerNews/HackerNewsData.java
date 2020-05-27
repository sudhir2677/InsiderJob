package kumar.sudhir.insiderJob.model.hackerNews;

import kumar.sudhir.insiderJob.model.Comment;
import kumar.sudhir.insiderJob.model.HNData;
import kumar.sudhir.insiderJob.model.Story;
import kumar.sudhir.insiderJob.utility.AsyncApiCall;
import kumar.sudhir.insiderJob.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class HackerNewsData implements CommandLineRunner {

    @Value("${hackerNews.topStories}")
    private String topStoriesUrl;

    @Value("${hackerNews.urlForData}")
    private String urlTemplate;


    @Value("${hackerNews.userUrl}")
    private String userTemplate;

    @Autowired
    private AsyncApiCall asyncApiCall;

    @Autowired
    private HNData hnData;

    boolean flag;
    public List<Story> HN_to_stories;
    public Map<Long, List<Comment>> HN_Story_to_commentMap;

    public HackerNewsData() {
    }


    public HNData getHnData() {
        return hnData;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("HNData : "+hnData);
        System.out.println("AsyncApiCall : "+asyncApiCall);
        System.out.println(topStoriesUrl+" "+userTemplate+" "+urlTemplate);
        getData();
    }

    @Scheduled(fixedRate = 600000, initialDelay = 480000)
    public void getData(){
        List<HackerNewsStory> storiesList = getTopStories();
        Map<Long, List<HackerNewsComment>> commentList = getStory_to_Comment(storiesList);
        Map<Long, HackerNewsUser> commentToUser = getComment_to_user(commentList);
        convert_HNData_to_ResponseData(storiesList, commentList, commentToUser);
    }

    @Scheduled(fixedRate = 600000, initialDelay = 599999)
    public void responseData(){
        hnData.updatedStrories = hnData.stories;
        for(Story story : hnData.getStories()){
            hnData.preStories.add(story);
        }
        hnData.Story_to_commentMap = hnData.UpdatedStory_to_commentMap;
//        HN_to_stories = null;
//        HN_Story_to_commentMap = null;
        hnData.stories = null;
        hnData.Story_to_commentMap = null;
    }

    public List<HackerNewsStory> getTopStories(){
        Long[] storiesId = asyncApiCall.getData(topStoriesUrl, Long[].class);
        System.out.println(topStoriesUrl+"\n"+urlTemplate+"\n"+Arrays.toString(storiesId));
        List<HackerNewsStory> stories = asyncApiCall.getData(Arrays.asList(storiesId),urlTemplate,HackerNewsStory.class);
        Collections.sort(stories);
        List<HackerNewsStory> getFirstTen = stories.stream().limit(10).collect(Collectors.toList());
        //Map<Long,List<HackerNewsComment>> storiesMappedToComment = getStoryComment(getFirstTen);
        return getFirstTen;
    }

    public Map<Long, List<HackerNewsComment>> getStory_to_Comment(List<HackerNewsStory> stories){
        Map<Long, List<HackerNewsComment>> comments = new HashMap<>();
        for(HackerNewsStory HackerNewsStory: stories){
            List<HackerNewsComment> comment = asyncApiCall.getData(HackerNewsStory.getKids(),urlTemplate,HackerNewsComment.class);
            comments.put(HackerNewsStory.getId(), comment.stream().limit(10).collect(Collectors.toList()));
        }
        return comments;
    }

    public Map<Long, HackerNewsUser> getComment_to_user(Map<Long, List<HackerNewsComment>> comments){
        Map<Long, HackerNewsUser> users = new HashMap<>();
        for(Long key : comments.keySet()){
            List<String> userIds = new ArrayList<>();
            for(HackerNewsComment comment : comments.get(key)){
                userIds.add(comment.getBy());
            }
            List<HackerNewsUser> usersList = asyncApiCall.getData(userIds,userTemplate,HackerNewsUser.class);
            for(int i = 0; i < comments.get(key).size(); i++){
                users.put(comments.get(key).get(i).getId(), usersList.get(i));
            }
        }
        return users;
    }

    void convert_HNData_to_ResponseData(List<HackerNewsStory> storiesList, Map<Long, List<HackerNewsComment>> commentList, Map<Long, HackerNewsUser> commentToUser){
        hnData.stories = new ArrayList<>();
        //HN_to_stories = new ArrayList<>();

        for(HackerNewsStory stories : storiesList){
            Story story = convert_HNStory_to_ResponseStory(stories);
            hnData.stories.add(story);
            hnData.preStories.add(story);
            //HN_to_stories.add(story);
        }
        //HN_Story_to_commentMap = new HashMap<>();
        hnData.Story_to_commentMap = new HashMap<>();
        for (Long HNstoriesid : commentList.keySet()){
            List<Comment> comments = new ArrayList<>();
            for(HackerNewsComment comment: commentList.get(HNstoriesid)){
                Comment comm = convert_HNComment_to_ResponseComment(comment, commentToUser);
                comments.add(comm);
            }
            //HN_Story_to_commentMap.put(HNstoriesid,comments);
            hnData.Story_to_commentMap.put(HNstoriesid,comments);
        }
        if(hnData.updatedStrories == null){
            hnData.updatedStrories = new ArrayList<>(hnData.stories);
        }
        if(hnData.Story_to_commentMap == null){
            hnData.UpdatedStory_to_commentMap = new HashMap<>(hnData.Story_to_commentMap);
        }
        /*if(!flag){
            hnData.stories = new ArrayList<>(HN_to_stories);
            for(Story story : hnData.getStories()){
                hnData.preStories.add(story);
            }
            hnData.Story_to_commentMap = new HashMap<>(HN_Story_to_commentMap);
            HN_to_stories = null;
            HN_Story_to_commentMap = null;
            flag = true;
        }*/
    }

    Story convert_HNStory_to_ResponseStory(HackerNewsStory hackerNewsStories){
        Story story = new Story();
        story.setUser(hackerNewsStories.getBy());
        story.setUrl(hackerNewsStories.getUrl());
        story.setTimeOfSubmission(Utility.convertUnixTimeToHumanReadTIme(hackerNewsStories.getTime()));
        story.setTitle(hackerNewsStories.getTitle());
        story.setScore(hackerNewsStories.getScore());
        story.setId(hackerNewsStories.getId());
        return story;
    }

    Comment convert_HNComment_to_ResponseComment(HackerNewsComment newsComments, Map<Long, HackerNewsUser> commentToUser){
        Comment comment = new Comment();
        comment.setUserAge(Utility.convertUnixTimeToHumanReadTIme(commentToUser.get(newsComments.getId()).getCreated()));
        comment.setText(newsComments.getText());
        comment.setUser(newsComments.getBy());
        return comment;
    }

}
