package hu.kits.opfr.common;

import java.util.UUID;

public class IdGenerator {

    private static Generator uuidGenerator = () -> UUID.randomUUID().toString();
    
    private static Generator generator = uuidGenerator;
    
    public static void useFakeGenerator() {
        generator = new FakeIdGenerator();
    }
    
    public static String generateId() {
        return generator.generateId();
    }
    
    private static interface Generator {
        String generateId();
    }
    
    private static class FakeIdGenerator implements Generator {
        
        private int currentId;
        
        public String generateId() {
            return String.format("23c69354-d9e8-4d07-9e84-%012X", ++currentId).toLowerCase();
        }
        
    }
    
}
