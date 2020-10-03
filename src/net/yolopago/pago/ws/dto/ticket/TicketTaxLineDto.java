package net.yolopago.pago.ws.dto.ticket;

import net.yolopago.pago.ws.dto.product.TaxDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TicketTaxLineDto {
    private Long id;
    private TaxDto taxDto;
    private Double amount;
}
