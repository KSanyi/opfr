package hu.kits.tennis.infrastructure.http;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.tennis.domain.court.TennisCourt;
import hu.kits.tennis.domain.court.TennisCourtService;
import io.javalin.http.Context;

public class TennisCourtsHandler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final TennisCourtService tennisCourtService;

    public TennisCourtsHandler(TennisCourtService tennisCourtService) {
        this.tennisCourtService = tennisCourtService;
    }
    
    public void handleListCoursesRequest(Context context) {
        
        try {
            List<TennisCourt> courts = tennisCourtService.listCourts();
            context.json(courts);
        } catch(Exception ex) {
            logger.error("Bad request: ", ex);
            throw new HttpServer.BadRequestException(ex.getMessage()); 
        }
        
    }
    
}
