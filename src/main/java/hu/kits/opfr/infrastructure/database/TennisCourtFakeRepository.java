package hu.kits.opfr.infrastructure.database;

import java.util.ArrayList;
import java.util.List;

import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.court.TennisCourtRepository;

public class TennisCourtFakeRepository implements TennisCourtRepository {

    private final List<TennisCourt> tennisCourts = new ArrayList<>();
    
    public TennisCourtFakeRepository() {
        
        tennisCourts.add(new TennisCourt("1", "Fő pálya"));
        tennisCourts.add(new TennisCourt("2", "Hátsó bal oldali pálya"));
        tennisCourts.add(new TennisCourt("3", "Hátsó jobb oldali pálya"));
        tennisCourts.add(new TennisCourt("4", "Kis pálya"));
    }
    
    @Override
    public List<TennisCourt> listAll() {
        return List.copyOf(tennisCourts);
    }

}
