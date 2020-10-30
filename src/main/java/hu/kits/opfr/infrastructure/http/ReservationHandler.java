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
import hu.kits.opfr.domain.user.UserService;
import io.javalin.http.Context;

class ReservationHandler {

    private final ReservationService reservationService;
    private final UserService userService;

    ReservationHandler(UserService userService, ReservationService reservationService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }
    
    void getReservation(Context context) {
        String reservationId = context.pathParam("reservationId");
        reservationService.findReservation(reservationId).ifPresentOrElse(context::json, () -> context.status(404));
    }
    
    void getReservationRange(Context context) {
        //String userId = context.pathParam("userId");
        //UserData user = userService.findUser(userId);
        
        DateTimeRange reservationRange = reservationService.getAllowedReservationRange();
        context.json(reservationRange);
    }
    
    void listPlayersReservations(Context context) {
        String userId = context.queryParam("player");
        context.json(reservationService.listPlayersReservations(userId, DateRange.of(Clock.today().getYear())));
    }
    
    void reserveCourt(Context context) {
        ReservationRequest reservationRequest = RequestParser.parseReservationRequest(context.body());
        
        Reservation reservation = reservationService.reserveCourt(reservationRequest);
        context.json(reservation);
    }
    
    void cancelReservation(Context context) {
        String reservationId = context.pathParam("reservationId");
        reservationService.cancelReservation(reservationId);
    }
    
    void showReservationsCalendar(Context context) {
        LocalDate fromDate = context.queryParam("from", LocalDate.class).get();
        LocalDate toDate = context.queryParam("to", LocalDate.class).get();
        DateRange dateRange = DateRange.of(fromDate, toDate);
        
        Map<LocalDate, Map<String, List<Reservation>>> courtAvailability = reservationService.listCourtAvailability(dateRange);
        
        context.json(courtAvailability);
    }
    
}
