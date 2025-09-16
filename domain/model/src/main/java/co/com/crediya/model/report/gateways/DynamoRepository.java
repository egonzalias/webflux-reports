package co.com.crediya.model.report.gateways;


import co.com.crediya.model.report.Report;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


public interface DynamoRepository {
    Mono<Void> incrementApprovedCount(String tableName, String reportId , BigDecimal loanAmount);
    Mono<Report> getApprovedLoansCountByReportId(String reportId);
}
