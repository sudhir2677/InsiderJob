package kumar.sudhir.insiderJob.model.hackerNews;

import kumar.sudhir.insiderJob.model.Comment;
import kumar.sudhir.insiderJob.model.HNData;
import kumar.sudhir.insiderJob.model.Story;
import kumar.sudhir.insiderJob.utility.AsyncApiCall;
import kumar.sudhir.insiderJob.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
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
    private Map<Long,List<HackerNewsComment>> storyMappedComment;
    private Map<Long, HackerNewsUser> commentToUser;

    static class Pair<F,S>{
        F first;
        S second;
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    public HackerNewsData() {
        storyMappedComment = new HashMap<>();
        commentToUser = new HashMap<>();
    }

    public HNData getHnData() {
        return hnData;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(topStoriesUrl+"\n "+userTemplate+"\n "+urlTemplate);
        getData();
    }


    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    @Scheduled(fixedRate = 300000, initialDelay = 180000)
    //@Scheduled(fixedRate = 600000, initialDelay = 420000)
    public void getData() throws Exception{
        System.out.println("---------------------------------------------------------------");
        System.out.println("*********************   Current Data    ***********************");
        System.out.println("---------------------------------------------------------------");
        System.out.println("    pre Stories :-> "+hnData.getPreStoriesId());
        System.out.println("    Stories --> List of comments");

        getData1();
    }

    void getData1() throws Exception{
        List<HackerNewsStory> storiesList = getTopStories();
        getSubtreeCount(storiesList);
        storyMappedComment.clear();
        commentToUser.clear();
    }

    @Scheduled(fixedRate = 300000, initialDelay = 300000)
    public void responseData(){
        flag = true;
        hnData.updatedStrories = new ArrayList<>(hnData.stories);
        for(Story story : hnData.updatedStrories){
            hnData.preStories.add(story);
        }
        hnData.UpdatedStory_to_commentMap = new HashMap<>(hnData.Story_to_commentMap);
        hnData.stories.clear();
        hnData.Story_to_commentMap.clear();
    }

    private Executor executor = Executors.newFixedThreadPool(10);
    public void getSubtreeCount(List<HackerNewsStory> storiesList) throws Exception{
        for(HackerNewsStory story: storiesList){

            CompletableFuture<List<HackerNewsComment>> list = CompletableFuture.supplyAsync(new Supplier<List<HackerNewsComment>>() {

                @Override
                public List<HackerNewsComment> get() {
                    PriorityQueue<Pair<HackerNewsComment, Integer>> pq = new PriorityQueue<>(new Comparator<Pair<HackerNewsComment, Integer>>() {
                        @Override
                        public int compare(Pair<HackerNewsComment, Integer> t1, Pair<HackerNewsComment, Integer> t2) {
                            return t2.second - t1.second;
                        }
                    });
                    List<HackerNewsComment> commentList = asyncApiCall.getData(story.getKids(),urlTemplate,HackerNewsComment.class);

                    for(HackerNewsComment comment : commentList){
                        int count = 0;
                        if(comment.getKids() != null){
                            List<HackerNewsComment> subChild = asyncApiCall.getData(comment.getKids(),urlTemplate,HackerNewsComment.class);
                            for(HackerNewsComment childComment : subChild){
                                if(childComment.getKids() != null){
                                    count += childComment.getKids().size();
                                }else{
                                    count++;
                                }
                            }
                        }else{
                            count++;
                        }
                        pq.add(new Pair<>(comment, count));
                    }
                    int i = 0;
                    List<HackerNewsComment> comments = new ArrayList<>();
                    while(!pq.isEmpty() && i < 10){
                        Pair<HackerNewsComment,Integer> p = pq.poll();
                        comments.add(p.first);
                        //System.out.println(p.first.getId()+" -> "+p.second);
                        i++;
                    }
                    return comments;
                }
            }, executor).handle((T,ex) -> {
                if(ex != null){
                    System.out.println("[NOT Executed] : "+story.getId());
                    return null;
                }
                return T;
            }).thenApplyAsync((T)->{
                storyMappedComment.put(story.getId(), T);
                System.out.print("\t"+story.getId()+" --> ");
                for(int i = 0; i < T.size(); i++){
                    System.out.print(T.get(i).getId()+",");
                }
                System.out.println();
                getComment_to_user(T);
                convert_HNComment_HNdata(story.getId(),T);
                return T;
            });
        }
    }

    public List<HackerNewsStory> getTopStories(){
        Long[] storiesId = asyncApiCall.getData(topStoriesUrl, Long[].class);
        //System.out.println(topStoriesUrl+"\n"+urlTemplate+"\n"+Arrays.toString(storiesId));
        List<HackerNewsStory> stories = asyncApiCall.getData(Arrays.asList(storiesId),urlTemplate,HackerNewsStory.class);
        Collections.sort(stories);
        List<HackerNewsStory> getFirstTen = stories.stream().limit(10).collect(Collectors.toList());
        convert_HNstory_HNdata(getFirstTen);
        return getFirstTen;
    }

    public void getComment_to_user(List<HackerNewsComment> comments){
        List<String> userIds = new ArrayList<>();
        for(HackerNewsComment comment : comments){
            userIds.add(comment.getBy());
        }
        List<HackerNewsUser> usersList = asyncApiCall.getData(userIds,userTemplate,HackerNewsUser.class);
        int commentIterator = 0, userIterator = 0;
        while(commentIterator < comments.size() && userIterator < usersList.size()){
            commentToUser.put(comments.get(commentIterator).getId(), usersList.get(userIterator));
            commentIterator++;
            userIterator++;
        }
    }

    private void convert_HNComment_HNdata(Long storyid, List<HackerNewsComment> commentList){
        List<Comment> comments = new ArrayList<>();
        for(HackerNewsComment comment: commentList){
            Comment comm = convert_HNComment_to_ResponseComment(comment);
            comments.add(comm);
        }
        hnData.Story_to_commentMap.put(storyid,comments);
        if(hnData.UpdatedStory_to_commentMap == null){
            hnData.UpdatedStory_to_commentMap.put(storyid,comments);
        }else if(!flag){
            hnData.UpdatedStory_to_commentMap.put(storyid,comments);
        }

    }

    private Comment convert_HNComment_to_ResponseComment(HackerNewsComment newsComments){
        Comment comment = new Comment();
        comment.setId(newsComments.getId());
        comment.setUserAge(Utility.convertUnixTimeToHumanReadTIme(commentToUser.get(newsComments.getId()).getCreated()));
        comment.setText(newsComments.getText());
        comment.setUser(newsComments.getBy());
        return comment;
    }

    private void convert_HNstory_HNdata(List<HackerNewsStory> storiesList){
        hnData.stories = new ArrayList<>();

        for(HackerNewsStory stories : storiesList){
            Story story = convert_HNStory_to_ResponseStory(stories);
            hnData.stories.add(story);
        }

        if(hnData.updatedStrories == null){
            hnData.updatedStrories = new ArrayList<>(hnData.stories);
            for(Story story : hnData.updatedStrories){
                hnData.preStories.add(story);
            }
        }
    }

    private Story convert_HNStory_to_ResponseStory(HackerNewsStory hackerNewsStories){
        Story story = new Story();
        story.setUser(hackerNewsStories.getBy());
        story.setUrl(hackerNewsStories.getUrl());
        story.setTimeOfSubmission(Utility.convertUnixTimeToHumanReadTIme(hackerNewsStories.getTime()));
        story.setTitle(hackerNewsStories.getTitle());
        story.setScore(hackerNewsStories.getScore());
        story.setId(hackerNewsStories.getId());
        return story;
    }
}
