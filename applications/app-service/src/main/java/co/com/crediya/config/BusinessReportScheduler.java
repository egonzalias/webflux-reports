package co.com.crediya.config;

import co.com.crediya.awssnsadapter.service.SnsService;
import co.com.crediya.model.report.Report;
import co.com.crediya.model.report.gateways.LoggerService;
import co.com.crediya.usecase.report.ReportUseCase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BusinessReportScheduler {

    private final ReportUseCase reportUseCase;
    private final SnsService snsService;
    private final LoggerService loggerService;

    @Value("${aws.region}")
    private String arnTopicDailyLoanReports;

    @PostConstruct
    public void scheduleDailyReport() {
        long initialDelay = computeInitialDelayInSeconds();
        long period = 24 * 60 * 60; // 24h

        Flux.interval(Duration.ofSeconds(initialDelay), Duration.ofSeconds(period))
                .flatMap(tick -> generateAndSendDailyReport())
                .doOnSubscribe(sub -> loggerService.info("⏰ Scheduler started, waiting until 2:00 AM..."))
                .subscribe();
    }

    private long computeInitialDelayInSeconds() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(2).withMinute(0).withSecond(0).withNano(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).getSeconds();
    }

    public Mono<Void> generateAndSendDailyReport() {
        return reportUseCase.getApprovedLoansCountByReportId("approved-loans")
                .map(this::buildSummaryMessage)
                .flatMap(summary -> snsService.publishMessage(
                        arnTopicDailyLoanReports,
                        "📈 Reporte Diario de Rendimiento",
                        summary
                ));
    }

    private String buildSummaryMessage(Report report) {
        return String.format("""
            📊 *Reporte Diario de Rendimiento*

            ✅ Préstamos Aprobados: %d
            💰 Monto Total Prestado: $%,.2f

            Fecha: %s
            """, report.getTotal(), report.getTotalAmount(), LocalDate.now());


    }
}
