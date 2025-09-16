package co.com.crediya.dynamodb;

import co.com.crediya.dynamodb.helper.TemplateAdapterOperations;
import co.com.crediya.model.report.Report;
import co.com.crediya.model.report.gateways.DynamoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<ModelEntity, String, ModelEntity /*adapter model*/> implements DynamoRepository {

    private final DynamoDbAsyncClient dynamoDbClient;

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, DynamoDbAsyncClient dynamoDbClient, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, ModelEntity.class /*domain model*/), "approved-loans-reports", new String[] {} /*index is optional*/);
        this.dynamoDbClient = dynamoDbClient;
    }

    public Mono<List<ModelEntity /*domain model*/>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<ModelEntity /*domain model*/>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "secondary_index" /*index is optional if you define in constructor*/);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<Void> incrementApprovedCount(String tableName, String reportId, BigDecimal loanAmount) {
        Map<String, AttributeValue> key = Map.of("reportId", AttributeValue.builder().s(reportId).build());

        String updateExpression = "ADD #total :incr, #totalAmount :amountIncr";

        Map<String, AttributeValue> expressionAttributeValues = Map.of(
                ":incr", AttributeValue.builder().n("1").build(),
                ":amountIncr", AttributeValue.builder().n(String.valueOf(loanAmount)).build()
        );

        Map<String, String> expressionAttributeNames = Map.of(
                "#total", "total",
                "#totalAmount", "totalAmount"
        );

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .updateExpression(updateExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        return Mono.fromFuture(dynamoDbClient.updateItem(request))
                .flatMap(updateResponse -> {
                    Map<String, AttributeValue> attributes = updateResponse.attributes();
                    if (attributes != null && attributes.containsKey("total")) {
                        int total = Integer.parseInt(attributes.get("total").n());
                        return Mono.empty();
                    } else {
                        // Si no está, devuelve 0 o null
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<Report> getApprovedLoansCountByReportId(String reportId) {
        Map<String, AttributeValue> key = Map.of("reportId", AttributeValue.builder().s(reportId).build());

        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName("approved-loans-reports")
                .key(key)
                .build();

        return Mono.fromFuture(dynamoDbClient.getItem(getRequest))
                .map(response -> {
                    Map<String, AttributeValue> item = response.item();
                    if (item == null || item.isEmpty()) {
                        return new Report(reportId, 0, new BigDecimal(0));
                    }
                    int total = Integer.parseInt(item.getOrDefault("total", AttributeValue.builder().n("0").build()).n());
                    String totalAmount = item.getOrDefault("totalAmount", AttributeValue.builder().n("0").build()).n();
                    return new Report(reportId, total , new BigDecimal(totalAmount));
                });
    }
}
