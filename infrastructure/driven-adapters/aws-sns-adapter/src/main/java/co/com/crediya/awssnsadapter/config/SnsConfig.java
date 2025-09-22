package co.com.crediya.awssnsadapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Configuration
public class SnsConfig {

    @Bean
    public SnsAsyncClient snsAsyncClient(@Value("${aws.region}") String region) {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(50)
                .build();

        return SnsAsyncClient.builder()
                .region(Region.of(region))
                .httpClient(httpClient)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
