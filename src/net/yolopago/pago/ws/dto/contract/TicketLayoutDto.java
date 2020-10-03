package net.yolopago.pago.ws.dto.contract;

import java.util.Set;

public class TicketLayoutDto {
	private Long id;
	private Set<TicketHeaderDto> ticketHeaderDtos;
	private Set<TicketFooterDto> ticketFooterDtos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<TicketHeaderDto> getTicketHeaderDtos() {
		return ticketHeaderDtos;
	}

	public void setTicketHeaderDtos(Set<TicketHeaderDto> ticketHeaderDtos) {
		this.ticketHeaderDtos = ticketHeaderDtos;
	}

	public Set<TicketFooterDto> getTicketFooterDtos() {
		return ticketFooterDtos;
	}

	public void setTicketFooterDtos(Set<TicketFooterDto> ticketFooterDtos) {
		this.ticketFooterDtos = ticketFooterDtos;
	}
}
