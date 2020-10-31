package hu.kits.opfr.domain.court;

import java.util.List;

public interface TennisCourtRepository {

    List<TennisCourt> listAll();

    TennisCourt loadCourt(String courtId);
    
}
