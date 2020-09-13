package hu.kits.opfr.domain.reservation;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.common.IdGenerator;
import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtRepository;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.user.UserData;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TennisCourtRepository tennisCourtRepository;

    public ReservationService(ReservationRepository reservationRepository, TennisCourtRepository tennisCourtRepository) {
        this.reservationRepository = reservationRepository;
        this.tennisCourtRepository = tennisCourtRepository;
    }
    
    public List<TennisCourt> listAvailableCourts(DailyTimeRange dailyTimeRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        
        return tennisCourts.stream()
                .filter(court -> isCourtAvailableAt(court.id(), dailyTimeRange))
                .collect(toList());
    }
    
    public void reserveCourt(UserData user, ReservationRequest reservationRequest) throws OPFRException {
        
        String courtId = reservationRequest.courtId();
        DailyTimeRange dailyTimeRange = reservationRequest.dailyTimeRange();
        String comment = reservationRequest.comment();
        
        boolean courtAvailable = isCourtAvailableAt(courtId, dailyTimeRange);
        if(courtAvailable) {
            Reservation reservation = new Reservation(IdGenerator.generateId(), user, courtId, dailyTimeRange, Clock.now(), comment);
            reservationRepository.save(reservation);
        } else {
            throw new OPFRException("Court is not available");
        }
    }
    
    public List<Reservation> listMyReservations(UserData user, DateRange dateRange) {
        
        return reservationRepository.load(dateRange, user);
    }
    
    public Map<TennisCourt, Map<LocalDate, List<Reservation>>> listCourtAvailability(DateRange dateRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        
        List<Reservation> reservations = reservationRepository.load(dateRange);
        
        return tennisCourts.stream().collect(toMap(
                court -> court, 
                court -> createCourtAvailability(dateRange, court, reservations)));
    }
    
    private boolean isCourtAvailableAt(String courtId, DailyTimeRange dailyTimeRange) {
        List<Reservation> reservations = reservationRepository.load(dailyTimeRange.date(), courtId);
        
        return !reservations.stream()
                .map(Reservation::dailyTimeRange)
                .anyMatch(dailyTimeRange::intersectWith);
    }
    
    private static Map<LocalDate, List<Reservation>> createCourtAvailability(DateRange dateRange, TennisCourt court, List<Reservation> reservations) {
        
        var reservationsForThisCourt = reservations.stream()
                .filter(res -> res.courtId().equals(court.id()))
                .collect(toList());
        
        return dateRange.days().collect(toMap(
                day -> day, 
                day -> reservationsForDay(day, reservationsForThisCourt),
                (a, b) -> a,
                TreeMap::new));
    }
    
    private static List<Reservation> reservationsForDay(LocalDate day, List<Reservation> reservations) {
        
        return reservations.stream()
                .filter(res -> res.dailyTimeRange().date().equals(day))
                .collect(toList());
    }
}
