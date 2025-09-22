package co.com.crediya.usecase.report;

import co.com.crediya.model.report.Report;
import co.com.crediya.model.report.gateways.DynamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportUseCaseTest {

    @Mock
    private DynamoRepository dynamoRepository;

    private ReportUseCase reportUseCase;


    @BeforeEach
    void setUp() {
        reportUseCase = new ReportUseCase(dynamoRepository);
    }

    @Test
    void shouldUpdateCounterSuccessfully() {
        String tableName = "loanReports";
        String reportId = "report-2025-09-20";
        BigDecimal loanAmount = new BigDecimal("5000");

        when(dynamoRepository.incrementApprovedCount(tableName, reportId, loanAmount))
                .thenReturn(Mono.empty());

        StepVerifier.create(reportUseCase.updateCounter(tableName, reportId, loanAmount))
                .verifyComplete();

        verify(dynamoRepository).incrementApprovedCount(tableName, reportId, loanAmount);
    }

    @Test
    void shouldReturnReportDataSuccessfully() {
        String reportId = "report-2025-09-20";
        Report expectedReport = new Report(reportId, 10, new BigDecimal("100000"));

        when(dynamoRepository.getApprovedLoansCountByReportId(reportId))
                .thenReturn(Mono.just(expectedReport));

        StepVerifier.create(reportUseCase.getApprovedLoansCountByReportId(reportId))
                .expectNext(expectedReport)
                .verifyComplete();

        verify(dynamoRepository).getApprovedLoansCountByReportId(reportId);
    }
}