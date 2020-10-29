package hu.kits.opfr.infrastructure.http;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.common.DateTimeRange;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserService;
import io.javalin.http.Context;

class ReservationHandler {

    private final ReservationService reservationService;
    private final UserService userService;

    ReservationHandler(UserService userService, ReservationService reservationService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }
    
    void getReservationRange(Context context) {
        //String userId = context.pathParam("userId");
        //UserData user = userService.findUser(userId);
        
        DateTimeRange reservationRange = reservationService.getAllowedReservationRange();
        context.json(reservationRange);
    }
    
    void listMyReservations(Context context) {
        String userId = context.pathParam("userId");
        UserData user = userService.findUser(userId);
        context.json(reservationService.listMyReservations(user, DateRange.of(Clock.today().getYear())));
    }
    
    void reserveCourt(Context context) {
        String userId = context.pathParam("userId");
        UserData user = userService.findUser(userId);
        
        ReservationRequest reservationRequest = RequestParser.parseReservationRequest(context.body());
        
        reservationService.reserveCourt(user, reservationRequest);
    }
    
    void createReservationsCalendar(Context context) {
        LocalDate fromDate = context.queryParam("from", LocalDate.class).get();
        LocalDate toDate = context.queryParam("to", LocalDate.class).get();
        DateRange dateRange = DateRange.of(fromDate, toDate);
        
        Map<LocalDate, Map<String, List<Reservation>>> courtAvailability = reservationService.listCourtAvailability(dateRange);
        
        context.json(courtAvailability);
    }
    
}
