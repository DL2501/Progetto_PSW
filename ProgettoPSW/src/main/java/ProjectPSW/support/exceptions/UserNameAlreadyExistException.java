package ProjectPSW.support.exceptions;

public class UserNameAlreadyExistException extends Exception {

    public UserNameAlreadyExistException() {
        super();
    }

    public UserNameAlreadyExistException(String message) {
        super(message);
    }
}
