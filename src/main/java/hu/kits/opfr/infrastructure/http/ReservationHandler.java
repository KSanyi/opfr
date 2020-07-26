package hu.kits.opfr.infrastructure.http;

import org.json.JSONObject;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.user.User;
import hu.kits.opfr.domain.user.UserService;
import io.javalin.http.Context;

class ReservationHandler {

    private final ReservationService reservationService;
    private final UserService userService;

    ReservationHandler(UserService userService, ReservationService reservationService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }
    
    void handleListMyReservationsRequest(Context context) {
        
        String userId = context.pathParam("userId");
        User user = userService.findUser(userId);
        context.json(reservationService.listMyReservations(user, DateRange.of(2020)));
    }
    
    void reserveCourt(Context context) {
        
        String userId = context.pathParam("userId");
        User user = userService.findUser(userId);
        
        JSONObject jsonObject = new JSONObject(context.body());
        
        ReservationRequest reservationRequest = RequestParser.parseReservationRequest(jsonObject);
        
        reservationService.reserveCourt(user, reservationRequest);
    }
    
}
