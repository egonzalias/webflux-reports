package co.com.crediya.sqs.listener;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.report.enums.LoanStatusEnum;
import co.com.crediya.model.report.gateways.LoggerService;
import co.com.crediya.sqs.listener.dto.LoanEvaluationResultEvent;
import co.com.crediya.usecase.report.ReportUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final LoggerService logger;
    private final ReportUseCase reportUseCase;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${aws.table-approved-loans-reports}")
    private String awsDynamoTableApproveLoans;
    @Value("${aws.report-id-approved-loans}")
    private String awsValuePrimaryKey;

    @Override
    public Mono<Void> apply(Message message) {

        logger.info("Listen queue... ",message.body());
        //Testing porpouse
        //String jsonBody = "{\"idLoanRequest\":\"34\",\"status\":\"APROB\",\"email\":\"egonzalias@gmail.com\",\"fullName\":\"John Guzman\",\"paymentPlan\":null,\"loanAmount\":1000.0}";
        try{
            LoanEvaluationResultEvent dto = mapper.readValue(message.body(), LoanEvaluationResultEvent.class);
            if(LoanStatusEnum.APROB.name().equals(dto.getStatus())){
                logger.info("Loan approved, updating report counter for loan ID: {}", dto.getIdLoanRequest());
                return reportUseCase.updateCounter(awsDynamoTableApproveLoans, awsValuePrimaryKey, dto.getLoanAmount());
            }
            logger.info("Loan not approved, skipping report update for loan ID: {}", dto.getIdLoanRequest());
            return Mono.empty();
        }catch (Exception e){
            logger.error("Error processing message queue... ", e);
            return Mono.error(new ValidationException(List.of(e.getMessage())));
        }
    }
}
