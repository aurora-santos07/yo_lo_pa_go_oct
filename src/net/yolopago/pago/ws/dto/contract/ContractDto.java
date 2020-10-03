package net.yolopago.pago.ws.dto.contract;

import java.util.HashSet;
import java.util.Set;

public class ContractDto {
	private Long id;
	private ContractTypeDto contractTypeDto;
	private String name;
	private TicketLayoutDto ticketLayoutDto;
	private String terminal;
	private Set<PreferenceDto> preferenceDtos = new HashSet<PreferenceDto>();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContractTypeDto getContractTypeDto() {
		return contractTypeDto;
	}
	public void setContractTypeDto(ContractTypeDto contractTypeDto) {
		this.contractTypeDto = contractTypeDto;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TicketLayoutDto getTicketLayoutDto() {
		return ticketLayoutDto;
	}
	public void setTicketLayoutDto(TicketLayoutDto ticketLayoutDto) {
		this.ticketLayoutDto = ticketLayoutDto;
	}
	public Set<PreferenceDto> getPreferenceDtos() {
		return preferenceDtos;
	}
	public void setPreferenceDtos(Set<PreferenceDto> preferenceDtos) {
		this.preferenceDtos = preferenceDtos;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminalActiva) {
		this.terminal = terminalActiva;
	}
}
