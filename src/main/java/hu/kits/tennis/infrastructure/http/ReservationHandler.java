package hu.kits.tennis.infrastructure.http;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.tennis.common.DateRange;
import hu.kits.tennis.domain.common.DailyTimeRange;
import hu.kits.tennis.domain.court.TennisCourt;
import hu.kits.tennis.domain.court.TennisCourtService;
import hu.kits.tennis.domain.reservation.ReservationService;
import hu.kits.tennis.domain.user.Role;
import hu.kits.tennis.domain.user.User;
import io.javalin.http.Context;

public class ReservationHandler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final ReservationService reservationService;

    public ReservationHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    private User user = new User("T1", "TTT", Role.ADMIN);
    
    public void handleListMyReservationsRequest(Context context) {
        
        try {
            context.json(reservationService.listMyReservations(user, DateRange.of(2020)));
        } catch(Exception ex) {
            logger.error("Bad request: ", ex);
            throw new HttpServer.BadRequestException(ex.getMessage()); 
        }
        
    }
    
    public void handleReservationRequest(Context context) {
        
        try {
            reservationService.reserveCourt(user, "1", new DailyTimeRange("2020-08-01", 10, 2), "");
        } catch(Exception ex) {
            logger.error("Bad request: ", ex);
            throw new HttpServer.BadRequestException(ex.getMessage()); 
        }
        
    }
    
}
