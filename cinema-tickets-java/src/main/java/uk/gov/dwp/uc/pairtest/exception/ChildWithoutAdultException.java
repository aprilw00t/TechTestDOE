package uk.gov.dwp.uc.pairtest.exception;

public class ChildWithoutAdultException extends InvalidPurchaseException{
    public ChildWithoutAdultException(String message){
        super(message);
    }
}
