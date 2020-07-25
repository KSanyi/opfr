package hu.kits.tennis.common;

public record Pair<S, T>(S first, T second) {

    public S getFirst() {
        return first;
    }
    
    public T getSecond() {
        return second;
    }
    
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
    
}
