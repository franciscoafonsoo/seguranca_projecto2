package myWhatsServer;

public class BadPwdException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BadPwdException() {
        super();
    }

    public BadPwdException(String message) {
        super(message);
    }

    public BadPwdException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPwdException(Throwable cause) {
        super(cause);
    }


}
