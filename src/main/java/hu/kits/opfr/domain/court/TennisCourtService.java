package hu.kits.opfr.domain.court;

import java.util.List;

public class TennisCourtService {

    private final TennisCourtRepository tennisCourtRepository;

    public TennisCourtService(TennisCourtRepository tennisCourtRepository) {
        this.tennisCourtRepository = tennisCourtRepository;
    }

    public List<TennisCourt> listCourts() {
        return tennisCourtRepository.listAll();
    }
    
    
    
}
