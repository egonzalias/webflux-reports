package co.com.crediya.model.report;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    private String reportId;
    private int total;
    private BigDecimal totalAmount;
}
