package myWhatsServer;

/**
 * My custom exception class.
 */
class DirException extends Exception {
    public DirException(String message) {
        super(message);
    }
}