package hu.kits.tennis.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<>(setA);
        result.addAll(setB);
        return Set.copyOf(result);
    }
    
    @SafeVarargs
    public static <T> List<T> union(List<T> ... lists) {
        List<T> result = new ArrayList<>();
        for(var list : lists) {
            result.addAll(list);
        }
        return List.copyOf(result);
    }
    
}
