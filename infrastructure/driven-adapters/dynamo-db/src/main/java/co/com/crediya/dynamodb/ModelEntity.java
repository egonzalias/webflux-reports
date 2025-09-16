package co.com.crediya.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class ModelEntity {

    private String reportId;
    private String total;

    public ModelEntity() {
    }

    public ModelEntity(String reportId, String total) {
        this.reportId = reportId;
        this.total = total;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("reportId")
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    @DynamoDbAttribute("total")
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
