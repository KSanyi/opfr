package hu.kits.opfr.domain.court;

import hu.kits.opfr.domain.common.TimeRange;

public record TennisCourt(String id, String name, TimeRange courtAvailibility){
    
}
