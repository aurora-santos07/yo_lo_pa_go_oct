package net.yolopago.pago.ws.dto.payment;

public class ContractDto {
	private Long id;
	private String name;
	private ContractTypeDto contractTypeDto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ContractTypeDto getContractTypeDto() {
		return contractTypeDto;
	}
	public void setContractTypeDto(ContractTypeDto contractTypeDto) {
		this.contractTypeDto = contractTypeDto;
	}
}
