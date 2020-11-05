package hu.kits.opfr.domain.reservation;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.common.DateTimeRange;
import hu.kits.opfr.common.DateTimeUtils;
import hu.kits.opfr.common.IdGenerator;
import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.common.OPFRException.OPFRAuthorizationException;
import hu.kits.opfr.domain.common.OPFRException.OPFRConflictException;
import hu.kits.opfr.domain.common.OPFRException.OPFRResourceNotFoundException;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtRepository;
import hu.kits.opfr.domain.email.EmailCreator;
import hu.kits.opfr.domain.email.EmailSender;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserRepository;

public class ReservationService {

    private final static int RESERVATION_CANCEL_MINS = 60;
    private final static int REMINDER_EMAIL_SENT_BEFORE_HOURS = 2;
    
    private final ReservationSettingsRepository reservationSettingsRepository;
    private final ReservationRepository reservationRepository;
    private final TennisCourtRepository tennisCourtRepository;
    private final UserRepository userRepository;
    
    private final EmailSender emailSender;

    public ReservationService(ReservationSettingsRepository reservationSettingsRepository, ReservationRepository reservationRepository, TennisCourtRepository tennisCourtRepository,
            UserRepository userRepository, EmailSender emailSender) {
        this.reservationSettingsRepository = reservationSettingsRepository;
        this.reservationRepository = reservationRepository;
        this.tennisCourtRepository = tennisCourtRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }
    
    public Optional<Reservation> findReservation(String reservationId) {
        return reservationRepository.findReservation(reservationId);
    }
    
    public List<TennisCourt> listAvailableCourts(DailyTimeRange dailyTimeRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        
        return tennisCourts.stream()
                .filter(court -> isCourtAvailableAt(court, dailyTimeRange))
                .collect(toList());
    }
    
    public DateTimeRange getAllowedReservationRange() {
        LocalDateTime now = Clock.now();
        LocalDateTime from = now.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        LocalDateTime to = now.plusDays(6).withMinute(0).withSecond(0).withNano(0).withHour(23);
        
        Optional<LocalTime> todaysReservationOpenTime = reservationSettingsRepository.getReservationOpenTimeFor(now.toLocalDate());
        if(todaysReservationOpenTime.isPresent() && now.toLocalTime().isAfter(todaysReservationOpenTime.get())) {
            to = to.plusDays(1);
        }
        
        return DateTimeRange.of(from, to);
    }
    
    public Reservation reserveCourt(ReservationRequest reservationRequest) throws OPFRException {
        
        UserData user = userRepository.loadUser(reservationRequest.userId());
        String courtId = reservationRequest.courtId();
        DailyTimeRange dailyTimeRange = reservationRequest.dailyTimeRange();
        
        if(!isReservationAllowedFor(dailyTimeRange)) {
            throw new OPFRAuthorizationException("Reservation is not allowed for " + dailyTimeRange.date());
        }
        
        String comment = reservationRequest.comment();
        
        TennisCourt court = tennisCourtRepository.loadCourt(courtId);
        
        boolean courtAvailable = isCourtAvailableAt(court, dailyTimeRange);
        if(courtAvailable) {
            Reservation reservation = new Reservation(IdGenerator.generateId(), user, courtId, dailyTimeRange, Clock.now(), comment);
            reservationRepository.save(reservation);
            emailSender.sendEmail(EmailCreator.createReservationConfirmationEmail(reservation));
            return reservation;
        } else {
            throw new OPFRConflictException("Court is not available");
        }
    }
    
    private boolean isReservationAllowedFor(DailyTimeRange dailyTimeRange) {
        var allowedReservationRange = getAllowedReservationRange();
        return allowedReservationRange.contains(dailyTimeRange);
    }

    public List<Reservation> listPlayersReservations(String playerId, DateRange dateRange) {
        
        return reservationRepository.load(dateRange, playerId);
    }
    
    public void cancelReservation(String reservationId) {
        
        Reservation reservation = reservationRepository.findReservation(reservationId).orElseThrow(() -> new OPFRResourceNotFoundException());
        
        if(Clock.now().plusMinutes(RESERVATION_CANCEL_MINS).isBefore(reservation.dailyTimeRange().startDateTime())) {
            reservationRepository.delete(reservationId);            
        } else {
            throw new OPFRAuthorizationException("Too late to cancel reservation");
        }
    }
    
    public Map<LocalDate, Map<String, List<Reservation>>> listDailyReservationsPerCourt(DateRange dateRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        List<Reservation> reservations = reservationRepository.load(dateRange);
        
        return dateRange.days().collect(toMap(
                date -> date,
                date -> createReservationsByCourt(date, reservations, tennisCourts),
                (t1, t2) -> t2,
                TreeMap::new));
    }
    
    public Map<LocalDate, List<TimeRange>> listDailyFreeSlots(DateRange dateRange) {
        
        List<TennisCourt> tennisCourts = tennisCourtRepository.listAll();
        
        Map<LocalDate, Map<String, List<Reservation>>> dailyReservationsPerCourt = listDailyReservationsPerCourt(dateRange);
        return dailyReservationsPerCourt.entrySet().stream().collect(
                toMap(
                    e -> e.getKey(), 
                    e -> calculateFreeSlots(e.getValue(), tennisCourts)));
    }
    
    private static List<TimeRange> calculateFreeSlots(Map<String, List<Reservation>> reservationsByCourt, List<TennisCourt> tennisCourts) {
        
        List<TimeRange> freeSlots = new ArrayList<>();
        for(var court : tennisCourts) {
            List<TimeRange> reservedRanges = reservationsByCourt.get(court.id()).stream()
                    .map(res -> res.dailyTimeRange().timeRange())
                    .collect(toList());
            List<TimeRange> freeSlotsForCourt = court.courtAvailibility().minus(reservedRanges);
            freeSlots.addAll(freeSlotsForCourt);
        }
        
        return TimeRange.union(TimeRange.union(freeSlots));
    }

    private boolean isCourtAvailableAt(TennisCourt court, DailyTimeRange dailyTimeRange) {
        
        if(!court.courtAvailibility().contains(dailyTimeRange.timeRange())) {
            return false;
        }
        
        List<Reservation> reservations = reservationRepository.load(dailyTimeRange.date(), court.id());
        
        return !reservations.stream()
                .map(Reservation::dailyTimeRange)
                .anyMatch(dailyTimeRange::intersectWith);
    }
    
    private static Map<String, List<Reservation>> createReservationsByCourt(LocalDate date, List<Reservation> reservations, List<TennisCourt> tennisCourts) {
        
        Map<String, List<Reservation>> reservationsByCourt = reservations.stream()
                .filter(res -> res.dailyTimeRange().date().equals(date))
                .collect(groupingBy(Reservation::courtId, TreeMap::new, toList()));
        
        return tennisCourts.stream().collect(toMap(court -> court.id(), court -> reservationsByCourt.getOrDefault(court.id(), List.of())));
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

    public int sendReminderEmailsForUpcomingReservations() {
        
        LocalDateTime closestRoundHour = DateTimeUtils.closestRoundHour(Clock.now());
        int startHourRemindersToSend = closestRoundHour.getHour() + REMINDER_EMAIL_SENT_BEFORE_HOURS;
        List<Reservation> todaysReservations = reservationRepository.load(DateRange.singleDay(Clock.today()));
        List<Reservation> upcomingReservations = todaysReservations.stream()
                .filter(reservation -> reservation.dailyTimeRange().timeRange().startAt() == startHourRemindersToSend)
                .collect(toList());
        
        upcomingReservations.forEach(reservation -> emailSender.sendEmail(EmailCreator.createReservationReminderEmail(reservation)));
        
        return upcomingReservations.size();
    }
    
}
