package hu.kits.tennis.domain;

import hu.kits.tennis.domain.user.Role;
import hu.kits.tennis.domain.user.User;

public class TestUtil {

    public static final User TEST_MEMBER_1 = new User("T1", "Test user 1", Role.MEMBER);
    public static final User TEST_MEMBER_2 = new User("T2", "Test user 2", Role.MEMBER);
    
}