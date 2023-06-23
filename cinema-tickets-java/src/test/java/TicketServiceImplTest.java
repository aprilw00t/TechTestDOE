import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.ChildWithoutAdultException;
import uk.gov.dwp.uc.pairtest.exception.InvalidIdException;
import uk.gov.dwp.uc.pairtest.exception.TooManyTicketsException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    TicketPaymentServiceImpl mockTicketPaymentService = mock(TicketPaymentServiceImpl.class);
    SeatReservationService mockSeatReservationService = mock(SeatReservationServiceImpl.class);

    TicketPaymentServiceImpl ticketPaymentService;
    SeatReservationService seatReservationService;
    @Before
            public void setup(){
        ticketPaymentService = new TicketPaymentServiceImpl();
        seatReservationService = new SeatReservationServiceImpl();

    }
    @Test(expected = TooManyTicketsException.class)
    public void purchaseTickets_OnlyAllowsTwenty(){
        Long acctId = 12l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15);
        TicketTypeRequest req2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 6);
        TicketTypeRequest[] reqArray = {req, req2};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        ticketService.purchaseTickets(acctId, reqArray);
    }

    @Test(expected = InvalidIdException.class)
    public void purchaseTickets_OnlyAllowIdMoreThanOne(){
        Long acctId = 0l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 18);
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        ticketService.purchaseTickets(acctId, req);
    }

    @Test(expected = ChildWithoutAdultException.class)
    public void purchaseTickets_OnlyAllowTicketsWithAdult(){
        Long acctId = 1l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 18);
        TicketTypeRequest[] reaArray = {req};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        ticketService.purchaseTickets(acctId, reaArray);
    }

    @Test
    public void purchaseTickets_CallsTicketPaymentService_withCorrectParams(){
        Long acctId = 1987l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest req2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest req3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest[] reqArray = {req, req2, req3};
        TicketServiceImpl ticketService = new TicketServiceImpl(mockTicketPaymentService, seatReservationService);

        ticketService.purchaseTickets(acctId, reqArray);
        Mockito.verify(mockTicketPaymentService, times(1)).makePayment(1987l, 30);
    }

    @Test
    public void purchaseTickets_CallsSeatReservationService_withCorrectParams(){
        TicketPaymentServiceImpl ticketPaymentService = new TicketPaymentServiceImpl();
        TicketPaymentServiceImpl spiedTicketPaymentServiceImpl = spy(ticketPaymentService);

        Long acctId = 1987l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest req2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest req3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest[] reqArray = {req, req2, req3};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, mockSeatReservationService);

        ticketService.purchaseTickets(acctId, reqArray);
        Mockito.verify(mockSeatReservationService, times(1)).reserveSeat(1987l, 2);
    }
}
