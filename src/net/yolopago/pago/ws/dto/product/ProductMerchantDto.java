package net.yolopago.pago.ws.dto.product;

public class ProductMerchantDto {
	private Long id;
	private ProductDto productDto;
	private MerchantDto merchantDto;
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
	public MerchantDto getMerchantDto() {
		return merchantDto;
	}
	public void setMerchantDto(MerchantDto merchantDto) {
		this.merchantDto = merchantDto;
	}
	public Boolean getGeneric() {
		return generic;
	}
	public void setGeneric(Boolean generic) {
		this.generic = generic;
	}
}