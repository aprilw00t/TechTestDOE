import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketServiceImplTest {
    @Test
    public void purchaseTicketsOnlyAllowsTwenty(){
        Long acctId = 12l;
        TicketTypeRequest req = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 7);
        TicketTypeRequest[] reaArray = {req};
        TicketServiceImpl ticketService = new TicketServiceImpl();
        ticketService.purchaseTickets(acctId, req);
    }
}
