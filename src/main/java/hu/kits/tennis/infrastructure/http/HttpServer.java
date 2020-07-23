package hu.kits.tennis.infrastructure.http;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.path;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.tennis.application.ResourceFactory;
import hu.kits.tennis.infrastructure.http.jsonmapper.JsonMapper;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final Javalin app;
    
    private final int port;
    
    public HttpServer(int port, ResourceFactory resourceFactory) {
        
        TennisCourtsHandler tennisCourtsHandler = new TennisCourtsHandler(resourceFactory.getTennisCourtService());
        ReservationHandler reservationHandler = new ReservationHandler(resourceFactory.getReservationService());
        
        app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("")); 
            config.defaultContentType = "application/json";
            //config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            config.requestLogger(this::log);
        }).routes(() -> {
            path("api/courts", () -> {
                get(tennisCourtsHandler::handleListCoursesRequest);
            });
            path("api/reservations", () -> {
                get(reservationHandler::handleListMyReservationsRequest);
                post(reservationHandler::handleReservationRequest);
            });
        }).exception(BadRequestException.class, this::handleException);
        
        this.port = port;
        
        JavalinJson.setToJsonMapper(JsonMapper::mapToJsonString);
    }
    
    public void start() {
        app.start(port);
        logger.info("Tennis court server started");
    }
    
    public void stop() {
        app.stop();
        logger.info("Tennis court server stopped");
    }
    
    private void handleException(BadRequestException ex, Context context) {
        context.status(400);
        context.result(ex.getMessage());
        logger.error(ex.getMessage());
    }
    
    private void log(Context ctx, @SuppressWarnings("unused") Float executionTimeMs) {
        String body = ctx.body().isBlank() ? "" : "body: " + ctx.body().replaceAll("\n", "").replaceAll("\\s+", " ");
        logger.info("{} {} {} Status: {} from {} ({}) headers: {} agent: {}", ctx.method(), ctx.path(), body, ctx.status(), ctx.ip(), ctx.host(), ctx.headerMap(), ctx.userAgent());
        logger.info(ctx.method() + " "  + ctx.path() + body + " Status: " + ctx.status() + " from: " + ctx.ip() + "(" + ctx.host() + ")" + " headers: " + ctx.headerMap() + " agent: " + ctx.userAgent());
    }
    
    public static class BadRequestException extends RuntimeException {
        
        public BadRequestException(String message) {
            super(message);
        }
        
    }
    
}
