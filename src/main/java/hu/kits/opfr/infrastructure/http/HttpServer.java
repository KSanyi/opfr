package hu.kits.opfr.infrastructure.http;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.put;
import static io.javalin.apibuilder.ApiBuilder.delete;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.application.ResourceFactory;
import hu.kits.opfr.domain.common.OPFRException;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.core.validation.JavalinValidation;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final Javalin app;
    
    private final int port;
    
    public HttpServer(int port, ResourceFactory resourceFactory) {
        
        TennisCourtsHandler tennisCourtsHandler = new TennisCourtsHandler(resourceFactory.getTennisCourtService(), resourceFactory.getReservationService());
        ReservationHandler reservationHandler = new ReservationHandler(resourceFactory.getUserService(), resourceFactory.getReservationService());
        UserHandler usersHandler = new UserHandler(resourceFactory.getUserService());
        
        app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("")); 
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
            config.defaultContentType = "application/json";
            //config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            config.requestLogger(this::log);
        }).routes(() -> {
            path("api/users", () -> {
                get(usersHandler::listAllUsers);
                post(usersHandler::saveNewUser);
                put(":userId", usersHandler::updateUser);
                delete(":userId", usersHandler::deleteUser);
                post("/authenticate/:userId", usersHandler::authenticateUser);
                post("/generate-new-password/:userId", usersHandler::generateNewPassword);
                post("/change-password/:userId", usersHandler::changePassword);
            });
            path("api/courts", () -> {
                get(tennisCourtsHandler::listCourts);
                get("/available", tennisCourtsHandler::listAvailableCourts);
            });
            path("api/reservations", () -> {
                get("/calendar", reservationHandler::createReservationsCalendar);
                get(":userId", reservationHandler::listMyReservations);
                post(":userId", reservationHandler::reserveCourt);
            });
        }).exception(BadRequestException.class, this::handleException)
          .exception(OPFRException.class, this::handleException);
        
        this.port = port;
        
        JavalinJson.setToJsonMapper(JsonMapper::mapToJsonString);
        
        JavalinValidation.register(LocalDate.class, LocalDate::parse);
    }
    
    public void start() {
        app.start(port);
        logger.info("OPFR server started");
    }
    
    public void stop() {
        app.stop();
        logger.info("OPFR server stopped");
    }
    
    private void log(Context ctx, @SuppressWarnings("unused") Float executionTimeMs) {
        String body = ctx.body().isBlank() ? "" : "body: " + ctx.body().replaceAll("\n", "").replaceAll("\\s+", " ");
        logger.info("{} {} {} Status: {} from {} ({}) headers: {} agent: {}", ctx.method(), ctx.path(), body, ctx.status(), ctx.ip(), ctx.host(), ctx.headerMap(), ctx.userAgent());
    }
    
    private static OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info()
            .version("1.0")
            .description("My Application");
        return new OpenApiOptions(applicationInfo).path("/swagger-docs").swagger(new SwaggerOptions("/swagger")
                .title("My Swagger Documentation"));
    }
    
    public static class BadRequestException extends RuntimeException {
        
        public BadRequestException(String message) {
            super(message);
        }
        
    }
    
    private void handleException(BadRequestException ex, Context context) {
        context.status(400);
        context.result(ex.getMessage());
        logger.error(ex.getMessage());
    }
    
    private void handleException(OPFRException ex, Context context) {
        context.status(409);
        context.result(ex.getMessage());
        logger.error(ex.getMessage());
    }
    
}
