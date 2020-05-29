package kumar.sudhir.insiderJob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Retryable
public class InsiderJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsiderJobApplication.class, args);
	}

}
