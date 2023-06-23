package uk.gov.dwp.uc.pairtest.exception;

public class TooManyTicketsException extends InvalidPurchaseException{
    public TooManyTicketsException(String message){
        super(message);
    }
}
