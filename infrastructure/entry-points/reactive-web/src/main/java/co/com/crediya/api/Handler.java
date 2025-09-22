package co.com.crediya.api;

import co.com.crediya.usecase.report.ReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class Handler {

    private final ReportUseCase reportUseCase;
    private final Validator validator;

    public Mono<ServerResponse> getTotalApprovedRequests(ServerRequest serverRequest) {
        return reportUseCase.getApprovedLoansCountByReportId("approved-loans")
                .flatMap(report ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(report)
                );
    }

}
