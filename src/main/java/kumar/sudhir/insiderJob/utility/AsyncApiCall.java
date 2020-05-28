package kumar.sudhir.insiderJob.utility;

import kumar.sudhir.insiderJob.model.hackerNews.HackerNewsComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AsyncApiCall {

    @Autowired
    RestTemplate restTemplate;

    private Executor executor = Executors.newFixedThreadPool(30);

    public <T> T getData(String url, Class<T> type){
        //System.out.println("restTemplate : "+restTemplate+" \n url :"+url);
        ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, type);
        T storiesId = responseEntity.getBody();
        return storiesId;
    }

    public <K,T,U> List<T> getData(List<K> ids, U url, Class<T> type){
        List<CompletableFuture<T>> futures = ids.stream().map(id -> getToDoAsync(id, url, type ))
                .collect(Collectors.toList());
        List<T> result = futures.stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
        return result;
    }

    public <K,T,U> CompletableFuture<T> getToDoAsync(K id, U url, Class<T> type){
        String urls = url.toString()+id.toString()+".json";
        //System.out.println("url : "+urls);
        CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                ResponseEntity<T> responseEntity = restTemplate.getForEntity(urls, type);
                T story = responseEntity.getBody();
                return story;
            }
        },executor).handle((T,ex) -> {
            if(ex != null){
                System.out.println("[NOT Executed] : "+urls);
                return null;
            }
            return T;
        });
        return future;
    }


}
