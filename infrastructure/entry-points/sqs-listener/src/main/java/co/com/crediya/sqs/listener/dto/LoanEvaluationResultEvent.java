package co.com.crediya.sqs.listener.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanEvaluationResultEvent {
    private String idLoanRequest;
    private String status;
    private String email;
    private String fullName;
    private BigDecimal loanAmount;
}
