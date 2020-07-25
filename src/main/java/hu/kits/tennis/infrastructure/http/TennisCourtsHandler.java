package hu.kits.tennis.infrastructure.http;

import java.util.List;

import hu.kits.tennis.domain.court.TennisCourt;
import hu.kits.tennis.domain.court.TennisCourtService;
import io.javalin.http.Context;

class TennisCourtsHandler {

    private final TennisCourtService tennisCourtService;

    TennisCourtsHandler(TennisCourtService tennisCourtService) {
        this.tennisCourtService = tennisCourtService;
    }
    
    void handleListCoursesRequest(Context context) {
        
        List<TennisCourt> courts = tennisCourtService.listCourts();
        context.json(courts);
    }
    
}
