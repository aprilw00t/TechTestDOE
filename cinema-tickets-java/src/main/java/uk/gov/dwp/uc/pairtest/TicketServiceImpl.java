package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.ChildWithoutAdultException;
import uk.gov.dwp.uc.pairtest.exception.InvalidIdException;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.TooManyTicketsException;

public class TicketServiceImpl implements TicketService {
    private final TicketPaymentServiceImpl ticketPaymentService;
    private final SeatReservationService seatReservationService;

    private static int ADULT_PRICE = 20;
    private static int CHILD_PRICE = 10;

    public TicketServiceImpl(TicketPaymentServiceImpl ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        boolean adultIncluded = false;
        int numberOfTickets = 0;
        int numberOfSeats = 0;
        int cost = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            numberOfTickets += ticketTypeRequest.getNoOfTickets();

            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT) {
                adultIncluded = true;
                cost += ADULT_PRICE * ticketTypeRequest.getNoOfTickets();
                numberOfSeats += 1;
            }

            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.CHILD) {
                cost += CHILD_PRICE * ticketTypeRequest.getNoOfTickets();
                numberOfSeats += 1;
            }

        }

        validatePurchase(accountId, adultIncluded, numberOfTickets);

        ticketPaymentService.makePayment(accountId, cost);
        seatReservationService.reserveSeat(accountId, numberOfSeats);

    }

    private void validatePurchase(Long accountId, Boolean adultIncluded, int numberOfTickets) {

        if (accountId <= 0) {
            throw new InvalidIdException("Id should be above 0");
        }

        if (!adultIncluded) {
            throw new ChildWithoutAdultException("Child and infant tickets should be purchased with an adult tickets");
        } else if (numberOfTickets > 20) {
            throw new TooManyTicketsException("Ticket number should not exceed 20");
        }
    }
}
