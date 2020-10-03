package net.yolopago.pago.ws.dto.product;

public class ProductTaxDto {
	private Long id;
	private ProductDto productDto;
	private TaxDto taxDto;
	private Integer rate;

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

	public TaxDto getTaxDto() {
		return taxDto;
	}

	public void setTaxDto(TaxDto taxDto) {
		this.taxDto = taxDto;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}
}
