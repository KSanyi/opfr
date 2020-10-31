package hu.kits.opfr.infrastructure.database;

import java.util.ArrayList;
import java.util.List;

import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtRepository;

public class TennisCourtFakeRepository implements TennisCourtRepository {

    private final List<TennisCourt> tennisCourts = new ArrayList<>();
    
    public TennisCourtFakeRepository() {
        
        tennisCourts.add(new TennisCourt("1", "1-es pálya", new TimeRange(0, 24)));
        tennisCourts.add(new TennisCourt("2", "2-es pálya", new TimeRange(0, 24)));
        tennisCourts.add(new TennisCourt("3", "3-es pálya", new TimeRange(0, 24)));
        tennisCourts.add(new TennisCourt("4", "4-es pálya (hátsó)", new TimeRange(6, 16)));
        tennisCourts.add(new TennisCourt("5", "5-ös pálya (hátsó)", new TimeRange(6, 16)));
    }
    
    @Override
    public List<TennisCourt> listAll() {
        return List.copyOf(tennisCourts);
    }

    @Override
    public TennisCourt loadCourt(String courtId) {
        return tennisCourts.stream()
                .filter(court -> court.id().equals(courtId))
                .findAny()
                .orElseThrow(() -> new OPFRException.OPFRResourceNotFoundException("Can not find court with id " + courtId));
    }

}
