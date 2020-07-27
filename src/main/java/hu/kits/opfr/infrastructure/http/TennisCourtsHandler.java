package hu.kits.opfr.infrastructure.http;

import java.time.LocalDate;
import java.util.List;

import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtService;
import hu.kits.opfr.domain.reservation.ReservationService;
import io.javalin.http.Context;

class TennisCourtsHandler {

    private final TennisCourtService tennisCourtService;
    private final ReservationService reservationService;

    TennisCourtsHandler(TennisCourtService tennisCourtService, ReservationService reservationService) {
        this.tennisCourtService = tennisCourtService;
        this.reservationService = reservationService;
    }
    
    void listCourts(Context context) {
        
        List<TennisCourt> courts = tennisCourtService.listCourts();
        context.json(courts);
    }
    
    void listAvailableCourts(Context context) {
        LocalDate date = context.queryParam("date", LocalDate.class).get();
        Integer startAt = context.queryParam("startAt", Integer.class).get();
        Integer hours = context.queryParam("hours", Integer.class).get();
        DailyTimeRange dailyTimeRange = new DailyTimeRange(date, new TimeRange(startAt, hours));
        context.json(reservationService.listAvailableCourts(dailyTimeRange));
    }
    
}
