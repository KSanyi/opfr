package hu.kits.opfr.application;

import javax.sql.DataSource;

import hu.kits.opfr.domain.court.TennisCourtRepository;
import hu.kits.opfr.domain.court.TennisCourtService;
import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.user.UserRepository;
import hu.kits.opfr.domain.user.UserService;
import hu.kits.opfr.domain.user.password.DummyPasswordHasher;
import hu.kits.opfr.infrastructure.database.ReservationJdbcRepository;
import hu.kits.opfr.infrastructure.database.ReservationSettingsJdbcRepository;
import hu.kits.opfr.infrastructure.database.TennisCourtFakeRepository;
import hu.kits.opfr.infrastructure.database.UserJdbcRepository;

public class ResourceFactory {

    private final UserService userService;
    private final ReservationService reservationService;
    private final TennisCourtService tennisCourtService;
    
    public ResourceFactory(DataSource dataSource) {
        UserRepository userRepository = new UserJdbcRepository(dataSource);
        userService = new UserService(userRepository, new DummyPasswordHasher());
        TennisCourtRepository tennisCourtRepository = new TennisCourtFakeRepository();
        reservationService = new ReservationService(
                new ReservationSettingsJdbcRepository(dataSource), 
                new ReservationJdbcRepository(dataSource, userRepository), 
                tennisCourtRepository);
        tennisCourtService = new TennisCourtService(tennisCourtRepository);
    }

    public TennisCourtService getTennisCourtService() {
        return tennisCourtService;
    }
    
    public ReservationService getReservationService() {
        return reservationService;
    }

    public UserService getUserService() {
        return userService;
    }
    
}
