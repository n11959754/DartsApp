import com.darts.dartsapp.controller.SignUpController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SignUpTest {

    private final SignUpController controller = new SignUpController();
  // TEST for valid input
    @Test
    public void testValidInputReturnsNull() {
        String result = controller.ValidateSignUp("JohnDoe", "testemail@test.com", "0123456789", "password123");
        assertNull(result);
    }
    // TEST for something left empty
    @Test
    public void testEmptyFieldsReturnError() {
        String result = controller.ValidateSignUp("", "testemail@test.com", "0123456789", "password123");
        assertEquals("A field is not filled out.", result);
    }
    // TEST for incorrect password
    @Test
    public void testShortPasswordReturnsError() {
        String result = controller.ValidateSignUp("JohnDoe", "testemail@test.com", "0123456789", "catdog");
        assertEquals("Passwords must be greater than 10 characters.", result);
    }
    // TEST for incorrect email
    @Test
    public void testInvalidEmailReturnsError() {
        String result = controller.ValidateSignUp("JohnDoe", "testemailtest.com", "0123456789", "password123");
        assertEquals("Entered email is invalid.", result);
    }
    // TEST for incorrect phone number
    @Test
    public void testInvalidPhoneReturnsError() {
        String result = controller.ValidateSignUp("JohnDoe", "testemail@test.com", "12345", "password123");
        assertEquals("Entered phone number is invalid.", result);
    }
}
