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
    public void setup() {
        ticketPaymentService = new TicketPaymentServiceImpl();
        seatReservationService = new SeatReservationServiceImpl();
    }

    @Test(expected = TooManyTicketsException.class)
    public void purchaseTickets_OnlyAllowsTwenty() {
        Long acctId = 12L;
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 6);
        TicketTypeRequest[] requestArray = {request, request2};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);

        ticketService.purchaseTickets(acctId, requestArray);
    }

    @Test(expected = InvalidIdException.class)
    public void purchaseTickets_OnlyAllowIdMoreThanOne() {
        Long acctId = 0L;
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 18);
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);

        ticketService.purchaseTickets(acctId, request);
    }

    @Test(expected = ChildWithoutAdultException.class)
    public void purchaseTickets_OnlyAllowTicketsWithAdult() {
        Long acctId = 1L;
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 18);
        TicketTypeRequest[] requestArray = {request};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);

        ticketService.purchaseTickets(acctId, requestArray);
    }

    @Test
    public void purchaseTickets_CallsTicketPaymentService_withCorrectParams() {
        Long acctId = 1987L;
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest[] requestArray = {request, request2, request3};
        TicketServiceImpl ticketService = new TicketServiceImpl(mockTicketPaymentService, seatReservationService);

        ticketService.purchaseTickets(acctId, requestArray);

        Mockito.verify(mockTicketPaymentService, times(1)).makePayment(1987l, 30);
    }

    @Test
    public void purchaseTickets_CallsSeatReservationService_withCorrectParams() {
        TicketPaymentServiceImpl ticketPaymentService = new TicketPaymentServiceImpl();
        Long acctId = 1987L;
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest[] requestArray = {request, request2, request3};
        TicketServiceImpl ticketService = new TicketServiceImpl(ticketPaymentService, mockSeatReservationService);

        ticketService.purchaseTickets(acctId, requestArray);

        Mockito.verify(mockSeatReservationService, times(1)).reserveSeat(1987l, 2);
    }
}
