package pl.edu.pjwstk.bankingcommon.CustomException;

public class CurrencyException extends RuntimeException {
    public CurrencyException(String message) {
        super(message);
    }
}
