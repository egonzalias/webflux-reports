package co.com.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reportes",
                    beanClass = Handler.class,
                    beanMethod = "getTotalApprovedRequests"
            )
    })
    public RouterFunction<ServerResponse> reportsRoutes(Handler handler) {
        return route()
                .GET("/api/v1/reportes", handler::getTotalApprovedRequests)
                .build();
    }

}
