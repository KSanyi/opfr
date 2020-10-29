package hu.kits.opfr.domain.scheduler;

import java.util.function.Supplier;

public interface Task {

    public String name();
    
    public String run();
    
    public static Task unnamed(Supplier<String> action) {
        return new Task() {
            
            @Override
            public String run() {
                return action.get();
            }
            
            @Override
            public String name() {
                return "";
            }
        };
    }
}
