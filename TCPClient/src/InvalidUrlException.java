
public class InvalidUrlException extends Exception {

    /**
     * Constructor calls Exception super class with message
     */
    public InvalidUrlException() {
        super("UrlCache exception");
    }

    /**
     * Constructor calls Exception super class with message
     * @param message The message of exception
     */
    public InvalidUrlException(String message) {
        super(message);
    }
}
