package co.com.crediya.usecase.user;

import co.com.crediya.model.report.Report;
import co.com.crediya.model.report.gateways.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ReportUseCase {

    private final DynamoRepository dynamoRepository;
    /*private final LoggerService logger;
    private final SqsService sqsService;*/

    public Mono<Void> updateCounter(String tableName, String reportId, BigDecimal loanAmount) {
        /*String statusCode = loanRequestUpdateStatus.getStatus();
        Long id = loanRequestUpdateStatus.getId();*/
        return dynamoRepository.incrementApprovedCount(tableName, reportId, loanAmount);
    }

    public Mono<Report> getApprovedLoansCountByReportId(String reportId) {
        return dynamoRepository.getApprovedLoansCountByReportId(reportId);
    }


}
