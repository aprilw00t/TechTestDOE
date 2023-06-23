package uk.gov.dwp.uc.pairtest.exception;

public class InvalidIdException extends InvalidPurchaseException{
    public InvalidIdException(String message){
        super(message);
    }
}
