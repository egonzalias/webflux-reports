package co.com.crediya.api;

import co.com.crediya.api.exception.ErrorResponse;
import co.com.crediya.model.report.Report;
import co.com.crediya.usecase.report.ReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Obtiene el reporte de préstamos aprobados",
            description = "Este endpoint retorna el reporte con el número total de préstamos aprobados y su monto total.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reporte de préstamos aprobados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Report.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o error en la consulta",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acceso prohibido"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado o rol invalido para realizar esta accion."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public Mono<ServerResponse> getTotalApprovedRequests(ServerRequest serverRequest) {
        return reportUseCase.getApprovedLoansCountByReportId("approved-loans")
                .flatMap(report ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(report)
                );
    }

}
