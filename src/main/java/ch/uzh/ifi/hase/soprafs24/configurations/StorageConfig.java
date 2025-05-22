package ch.uzh.ifi.hase.soprafs24.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class StorageConfig {
    @Bean
    public Storage storage() {
        
        return StorageOptions.getDefaultInstance().getService();
    }
}