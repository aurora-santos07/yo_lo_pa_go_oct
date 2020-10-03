package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.ticketLines;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

public class BuilderTicket {

    public ticketLines build(TicketLineDto ticketLineDto) {
        ticketLines line = new ticketLines();
        line.setProduct(ticketLineDto.getProduct());
        line.setItems(ticketLineDto.getItems());
        line.setPrice(ticketLineDto.getPrice());
        return line;
    }
}
