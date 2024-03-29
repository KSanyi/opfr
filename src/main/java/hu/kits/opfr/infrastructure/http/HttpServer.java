package hu.kits.opfr.infrastructure.http;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.application.ResourceFactory;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.common.OPFRException.OPFRAuthorizationException;
import hu.kits.opfr.domain.common.OPFRException.OPFRConflictException;
import hu.kits.opfr.domain.common.OPFRException.OPFRResourceNotFoundException;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.core.validation.JavalinValidation;
import io.javalin.http.Context;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final Javalin app;
    
    private final int port;
    
    public HttpServer(int port, ResourceFactory resourceFactory) {
        
        TennisCourtsHandler tennisCourtsHandler = new TennisCourtsHandler(resourceFactory.getTennisCourtService(), resourceFactory.getReservationService());
        ReservationHandler reservationHandler = new ReservationHandler(resourceFactory.getUserService(), resourceFactory.getReservationService());
        UserHandler usersHandler = new UserHandler(resourceFactory.getUserService());
        ApiDocHandler apiDocHandler = new ApiDocHandler();
        
        app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("")); 
            config.defaultContentType = "application/json";
            //config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            config.requestLogger(this::log);
            config.jsonMapper(new OpfrJsonMapper());
        }).routes(() -> {
            path("api/docs", () -> {
                get(apiDocHandler::createTestCasesList);
                get("{testCase}", apiDocHandler::createTestCaseDoc);
            });
            path("api/users", () -> {
                get(usersHandler::listAllUsers);
                post(usersHandler::saveNewUser);
                post("/register", usersHandler::register);
                get("/new-registrations", usersHandler::listNewlyRegisteredUsers);
                post("/activate/{userId}", usersHandler::activate);
                put("{userId}", usersHandler::updateUser);
                delete("{userId}", usersHandler::deleteUser);
                post("/authenticate/{userId}", usersHandler::authenticateUser);
                post("/generate-new-password/{userId}", usersHandler::generateNewPassword);
                post("/change-password/{userId}", usersHandler::changePassword);
            });
            path("api/courts", () -> {
                get(tennisCourtsHandler::listCourts);
                get("/available", tennisCourtsHandler::listAvailableCourts);
            });
            path("api/reservations", () -> {
                get("/calendar", reservationHandler::showReservationsCalendar);
                get("/simple-calendar", reservationHandler::showSimpleReservationsCalendar);
                get("{reservationId}", reservationHandler::getReservation);
                get(reservationHandler::listPlayersReservations);
                post(reservationHandler::reserveCourt);
                delete("{reservationId}",reservationHandler::cancelReservation);
            });
            path("api/reservations-range", () -> {
                get(reservationHandler::getReservationRange);
            });
        }).exception(BadRequestException.class, this::handleException)
          .exception(OPFRException.class, this::handleException);
        
        this.port = port;
        
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
        logger.trace("{} {} {} Status: {} from {} ({}) headers: {} agent: {}", ctx.method(), ctx.path(), body, ctx.status(), ctx.ip(), ctx.host(), ctx.headerMap(), ctx.userAgent());
        logger.info("{} {} {} Status: {}", ctx.method(), ctx.path(), body, ctx.status());
    }
    
    /*
    private static OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info()
            .version("1.0")
            .description("My Application");
        return new OpenApiOptions(applicationInfo).path("/swagger-docs").swagger(new SwaggerOptions("/swagger")
                .title("My Swagger Documentation"));
    }
    */
    
    public static class BadRequestException extends RuntimeException {
        
        public BadRequestException(String message) {
            super(message);
        }
        
    }
    
    private void handleException(BadRequestException ex, Context context) {
        handleException(context, 400, ex.getMessage());
    }
    
    private void handleException(OPFRException ex, Context context) {
        if(ex instanceof OPFRResourceNotFoundException) {
            handleException(context, 404, ex.getMessage());    
        } else if(ex instanceof OPFRConflictException) {
            handleException(context, 409, ex.getMessage());    
        } else if(ex instanceof OPFRAuthorizationException) {
            handleException(context, 403, ex.getMessage());    
        }
    }
    
    private static void handleException(Context context, int status, String message) {
        context.status(status);
        if(message != null) {
            context.result(message);            
        }
        logger.error("Status: {}, message: {}", context.status(), message);
    }
    
}
