package co.com.crediya.awssnsadapter.service;

import co.com.crediya.model.report.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
public class SnsService {

    private final LoggerService loggerService;
    private final SnsAsyncClient snsAsyncClient;

    public Mono<Void> publishMessage(String topicArn, String subject, String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message(message)
                .build();

        return Mono.fromFuture(snsAsyncClient.publish(request))
                .doOnSuccess(response -> {
                    loggerService.info("SNS Message sent: " , response.messageId());
                })
                .doOnError(error -> {
                    loggerService.error("Error sending SNS message: " , error);
                })
                .then();
    }
}
