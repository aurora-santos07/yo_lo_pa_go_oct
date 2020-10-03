package net.yolopago.pago.ws.dto.payment;

public class MerchantDto {
	private Long id;
	private String name;
	private String taxid;
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
	public String getTaxid() {
		return taxid;
	}
	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}
	public ContractDto getContractDto() {
		return contractDto;
	}
	public void setContractDto(ContractDto contractDto) {
		this.contractDto = contractDto;
	}
	
}
