package net.yolopago.pago.ws.dto.product;

public class ProductContractDto {
	private Long id;
	private ProductDto productDto;
	private ContractDto contractDto;
	private Boolean generic;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProductDto getProductDto() {
		return productDto;
	}
	public void setProductDto(ProductDto productDto) {
		this.productDto = productDto;
	}
	public ContractDto getContractDto() {
		return contractDto;
	}
	public void setContractDto(ContractDto contractDto) {
		this.contractDto = contractDto;
	}
	public Boolean getGeneric() {
		return generic;
	}
	public void setGeneric(Boolean generic) {
		this.generic = generic;
	}
}