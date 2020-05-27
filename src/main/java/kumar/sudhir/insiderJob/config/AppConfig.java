package kumar.sudhir.insiderJob.config;

import kumar.sudhir.insiderJob.utility.AsyncApiCall;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    public AsyncApiCall getAsyncApiCall(){
        return new AsyncApiCall();
    }

    /*@Bean
    public HNData getHNData(){
        return new HNData();
    }*/
}
