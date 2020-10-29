package hu.kits.opfr.domain.reservation;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.common.DateTimeRange;
import hu.kits.opfr.common.IdGenerator;
import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtRepository;
import hu.kits.opfr.domain.email.EmailCreator;
import hu.kits.opfr.domain.email.EmailSender;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.user.UserData;

public class ReservationService {

    private final ReservationSettingsRepository reservationSettingsRepository;
    private final ReservationRepository reservationRepository;
    private final TennisCourtRepository tennisCourtRepository;
    
    private final EmailSender emailSender;

    public ReservationService(ReservationSettingsRepository reservationSettingsRepository, ReservationRepository reservationRepository, TennisCourtRepository tennisCourtRepository,
            EmailSender emailSender) {
        this.reservationSettingsRepository = reservationSettingsRepository;
        this.reservationRepository = reservationRepository;
        this.tennisCourtRepository = tennisCourtRepository;
        this.emailSender = emailSender;
    }
    
    public List<TennisCourt> listAvailableCourts(DailyTimeRange dailyTimeRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        
        return tennisCourts.stream()
                .filter(court -> isCourtAvailableAt(court.id(), dailyTimeRange))
                .collect(toList());
    }
    
    public DateTimeRange getAllowedReservationRange() {
        LocalDateTime now = Clock.now();
        LocalDateTime from = now.withMinute(0).withSecond(0).plusHours(1);
        LocalDateTime to = now.plusDays(6).withMinute(0).withSecond(0).withHour(23);
        
        Optional<LocalTime> todaysReservationOpenTime = reservationSettingsRepository.getReservationOpenTimeFor(now.toLocalDate());
        if(todaysReservationOpenTime.isPresent() && now.toLocalTime().isAfter(todaysReservationOpenTime.get())) {
            to = to.plusDays(1);
        }
        
        return DateTimeRange.of(from, to);
    }
    
    public void reserveCourt(UserData user, ReservationRequest reservationRequest) throws OPFRException {
        
        String courtId = reservationRequest.courtId();
        DailyTimeRange dailyTimeRange = reservationRequest.dailyTimeRange();
        
        if(!isReservationAllowedFor(dailyTimeRange)) {
            throw new OPFRException("Reservation is not allowed for " + dailyTimeRange.date());
        }
        
        String comment = reservationRequest.comment();
        
        boolean courtAvailable = isCourtAvailableAt(courtId, dailyTimeRange);
        if(courtAvailable) {
            Reservation reservation = new Reservation(IdGenerator.generateId(), user, courtId, dailyTimeRange, Clock.now(), comment);
            reservationRepository.save(reservation);
            emailSender.sendEmail(EmailCreator.createReservationConfirmationEmail(reservation));
        } else {
            throw new OPFRException("Court is not available");
        }
    }
    
    private boolean isReservationAllowedFor(DailyTimeRange dailyTimeRange) {
        var allowedReservationRange = getAllowedReservationRange();
        return allowedReservationRange.contains(dailyTimeRange);
    }

    public List<Reservation> listMyReservations(UserData user, DateRange dateRange) {
        
        return reservationRepository.load(dateRange, user);
    }
    
    public Map<LocalDate, Map<String, List<Reservation>>> listCourtAvailability(DateRange dateRange) {
        
        List<Reservation> reservations = reservationRepository.load(dateRange);
        
        return dateRange.days().collect(toMap(
                date -> date,
                date -> createReservationsByCourt(date, reservations),
                (t1, t2) -> t2,
                TreeMap::new));
    }
    
    private boolean isCourtAvailableAt(String courtId, DailyTimeRange dailyTimeRange) {
        List<Reservation> reservations = reservationRepository.load(dailyTimeRange.date(), courtId);
        
        return !reservations.stream()
                .map(Reservation::dailyTimeRange)
                .anyMatch(dailyTimeRange::intersectWith);
    }
    
    private static Map<String, List<Reservation>> createReservationsByCourt(LocalDate date, List<Reservation> reservations) {
        
        return reservations.stream()
                .filter(res -> res.dailyTimeRange().date().equals(date))
                .collect(Collectors.groupingBy(Reservation::courtId, TreeMap::new, toList()));
    }

    public Optional<LocalTime> drawTodaysReservationOpeningTime() {
        LocalDate today = Clock.today();
        Optional<LocalTime> todaysReservationOpenTime = reservationSettingsRepository.getReservationOpenTimeFor(today);
        if(todaysReservationOpenTime.isEmpty()) {
            LocalTime randomTime = LocalTime.of(20, new Random().nextInt(60));
            reservationSettingsRepository.saveReservationOpenTime(today, randomTime);
            return Optional.of(randomTime);
        } else {
            return Optional.empty();
        }
    }
    
}
