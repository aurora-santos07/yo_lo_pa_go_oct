package net.yolopago.pago.ws.dto.product;

public class MerchantDto {
	private Long id;
	private String name;
	private ContractDto contractDto;
	
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
	public ContractDto getContractDto() {
		return contractDto;
	}
	public void setContractDto(ContractDto contractDto) {
		this.contractDto = contractDto;
	}
	
}
