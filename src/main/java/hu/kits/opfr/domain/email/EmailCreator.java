package hu.kits.opfr.domain.email;

import java.util.List;

import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.user.UserData;

public class EmailCreator {

    public static Email createReservationConfirmationEmail(Reservation reservation) {
        
        UserData user = reservation.user();
        
        String content = String.format("""
                Kedves %s,<br/>
                <br/>
                Tenisz pálya foglalása megerősítve.<br/>
                Foglalás azonosító: %s<br/>
                Pálya: %s<br/>
                Időpont: %s<br/>
                Megjegyzés: %s<br/>
                <br/>
                Üdvözlettel,<br/>
                <br/>
                KVTK  
                """, user.name(), reservation.id(), reservation.courtId(), reservation.dailyTimeRange().format(), reservation.comment());
        
        return new Email(user.email(), "Teniszpálya foglalás megerősítés", List.of(), content, new CalendarAttachment(reservation));
    }

    public static Email createReservationReminderEmail(Reservation reservation) {
        UserData user = reservation.user();
        
        String content = String.format("""
                Kedves %s,<br/>
                <br/>
                Tenisz pálya foglalása hamarosan kezdődik.<br/>
                Foglalás azonosító: %s<br/>
                Pálya: %s<br/>
                Időpont: %s<br/>
                Megjegyzés: %s<br/>
                <br/>
                Üdvözlettel,<br/>
                <br/>
                KVTK  
                """, user.name(), reservation.id(), reservation.courtId(), reservation.dailyTimeRange().format(), reservation.comment());
        
        return new Email(user.email(), "Teniszpálya foglalás emlékeztető", List.of(), content);
    }

    public static Email createRegistrationNotificationEmail(List<String> adminEmails, UserData user) {
        String content = String.format("""
                Sziasztok,<br/>
                <br/>
                Új felhasználó regisztrált<br/>
                Azonosító: %s<br/>
                Név: %s<br/>
                Email: %s<br/>
                Telefonszám: %s<br/>
                <br/>
                Üdv,<br/>
                <br/>
                KVTK  
                """, user.userId(), user.name(), user.email(), user.phone());
        
        return new Email(adminEmails.get(0), "Új OPFR regisztráció értesítés emlékeztető", adminEmails, content);
    }

    public static Email createActivationNotificationEmail(UserData userData) {
        String content = String.format("""
                Kedves %s,<br/>
                <br/>
                %s felhasználói névvel registrélt OPFR tagsága mostantól aktív!<br/>
                <br/>
                Üdvözlettel,<br/>
                <br/>
                KVTK  
                """, userData.userId(), userData.name());
        
        return new Email(userData.email(), "OPFR regisztráció", List.of(), content);
    }
    
}
