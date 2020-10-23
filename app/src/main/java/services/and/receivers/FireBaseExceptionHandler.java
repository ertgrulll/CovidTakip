package services.and.receivers;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class FireBaseExceptionHandler {
    public static String handle(Exception exc) {
        String msg;
        try {
            throw Objects.requireNonNull(exc);
        }
        catch (FirebaseNetworkException e) {
            msg = Constants.FB_NETWORK_EXC_MSG;
        }
        catch (FirebaseAuthUserCollisionException e) {
            msg = Constants.FB_AUTH_USER_COLLISION_EXC_MSG;
        }
        catch (FirebaseAuthWeakPasswordException e) {
            msg = Constants.FB_WEAK_PASS_EXC_MSG;
        }
        catch (FirebaseApiNotAvailableException e) {
            msg = Constants.FB_API_NOT_AVAILABLE_EXC_MSG;
        }
        catch (FirebaseAuthEmailException e) {
            msg = Constants.FB_AUTH_EMAIL_EXC_MSG;
        }
        catch (FirebaseAuthInvalidCredentialsException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("formatted")) {
                msg = Constants.FB_AUTH_INVALID_CREDENTIALS_FORMAT_EXC_MSG;
            }
            else if(e.getMessage().contains("format of the phone number")) {
                msg = Constants.FB_AUTH_INVALID_PHONE_TYPE;
            }
            else if(e.getMessage().contains("phone auth credential is invalid")) {
                msg = Constants.FB_AUTH_INVALID_CODE;
            }
            else msg = Constants.FB_AUTH_INVALID_CREDENTIALS_PASS_EXC_MSG;
        }
        catch (FirebaseAuthInvalidUserException e) {
            msg = Constants.FB_AUTH_INVALID_USER_EXC_MSG;
        }
        catch (FirebaseTooManyRequestsException e) {
            msg = Constants.FB_TOO_MANY_REQUESTS_EXC_MSG;
        }
        catch (Exception e) {
            msg = Constants.FB_EXC_MSG;
        }
        return msg;
    }
}
