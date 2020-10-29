package hu.kits.opfr.domain.email;

import java.util.List;

import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.user.UserData;

public class EmailCreator {

    public static Email createReservationConfirmationEmail(Reservation reservation) {
        
        UserData user = reservation.user();
        
        String content = String.format("""
                Kedves %s,
                
                Tenisz pálya foglalása megerősítve.
                Foglalás azonosító: %s
                Pálya: %s
                Időpont: %s
                Megjegyzés: %s
                
                Üdvözlettel,
                
                KVTK  
                """, user.name(), reservation.id(), reservation.courtId(), reservation.dailyTimeRange().format(), reservation.comment());
        
        return new Email(user.email(), "Teniszpálya forglalás megerősítés", List.of(), content);
    }
    
}
