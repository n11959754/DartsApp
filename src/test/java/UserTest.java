import com.darts.dartsapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static final String USER_NAME = "John";
    private static final String USER_NAME_TWO = "Jane";
    private static final String EMAIL = "dog@cat.com";
    private static final String EMAIL_TWO = "test@test.com";
    private static final String PHONE = "0000000000";
    private static final String PHONE_TWO = "0123456789";
    private static final String PASSWORD = "password123";
    private static final String PASSWORD_TWO = "65432134724732";

    private User user;
    private User userTwo;

    @BeforeEach
    public void setUp() {
        user = new User(USER_NAME, EMAIL, PHONE, PASSWORD);
        userTwo = new User(USER_NAME_TWO, EMAIL_TWO, PHONE_TWO, PASSWORD_TWO);
    }

    @Test
    public void testSetUserID() {
        user.setUserID(1);
        assertEquals(1, user.getUserID());
    }

    @Test
    public void testGetUserName() {
        assertEquals(USER_NAME, user.getUsername());
    }

    @Test
    public void testSetUserName() {
        user.setUsername(USER_NAME_TWO);
        assertEquals(USER_NAME_TWO, user.getUsername());
    }

    @Test
    public void testGetEmail() {
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void testSetEmail() {
        user.setEmail(EMAIL_TWO);
        assertEquals(EMAIL_TWO, user.getEmail());
    }

    @Test
    public void testGetPhoneNumber() {
        assertEquals(PHONE, user.getPhoneNumber());
    }

    @Test
    public void testSetPhoneNumber() {
        user.setPhoneNumber(PHONE_TWO);
        assertEquals(PHONE_TWO, user.getPhoneNumber());
    }

    @Test
    public void testGetPassword() {
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    public void testSetPassword() {
        user.setPassword(PASSWORD_TWO);
        assertEquals(PASSWORD_TWO, user.getPassword());
    }
}