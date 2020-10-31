package hu.kits.opfr.domain.email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import hu.kits.opfr.common.Clock;
import hu.kits.opfr.domain.reservation.Reservation;

public class CalendarAttachment {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    
    private final Reservation reservation;

    public CalendarAttachment(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public String formatToIcal() {
        
        String timestamp = DATE_TIME_FORMAT.format(Clock.now());
        String start = DATE_TIME_FORMAT.format(matchStart());
        String end = DATE_TIME_FORMAT.format(matchEnd());
        
        return
            "BEGIN:VCALENDAR\n" + 
            "ORGANIZER:luzerfc\n" +
            "PRODID:luzerfc\n" + 
            "VERSION:2.0\n" + 
            "METHOD:PUBLISH\n" + 
            "BEGIN:VEVENT\n" + 
            "DTSTART;TZID=Europe/Budapest:" + start + "\n" +
            "DTEND;TZID=Europe/Budapest:" + end + "\n" + 
            "LOCATION:" + location() + "\n" +
            "SUMMARY: Teniszpálya foglalás\n"+
            "DTSTAMP:" + timestamp + "\n" +
            "UID:" + UUID.randomUUID() + "\n" +
            "CREATED:" + timestamp + "\n" + 
            "DESCRIPTION:" + //Main.URL + "/reservation/" + reservation.id() + "\n" + 
            "LAST-MODIFIED:" + timestamp + "\n" + 
            "SEQUENCE:0\n" + 
            "STATUS:CONFIRMED\n" + 
            "TRANSP:OPAQUE\n" + 
            "END:VEVENT\n" + 
            "END:VCALENDAR";
    }
    
    private LocalDateTime matchStart() {
        return reservation.dailyTimeRange().startDateTime();
    }
    
    private LocalDateTime matchEnd() {
        return reservation.dailyTimeRange().endDateTime();
    }
    
    private String location() {
        return "Kiskunfélegyházi Városi Tenisz Klub";
    }
    
}
