package net.yolopago.pago.ws.dto.product;

import java.util.List;

public class ProductDto {
	private Long id;
	private String name;
	private String description;
	private String sku;
	private ProductTypeDto productTypeDto;
	private List<byte[]> photos;
	private PriceDto priceDto;
	private List<ProductTaxDto> productTaxeDtos;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public ProductTypeDto getProductTypeDto() {
		return productTypeDto;
	}
	public void setProductTypeDto(ProductTypeDto productTypeDto) {
		this.productTypeDto = productTypeDto;
	}
	public List<byte[]> getPhotos() {
		return photos;
	}
	public void setPhotos(List<byte[]> photos) {
		this.photos = photos;
	}
	public PriceDto getPriceDto() {
		return priceDto;
	}
	public void setPriceDto(PriceDto priceDto) {
		this.priceDto = priceDto;
	}
	public List<ProductTaxDto> getProductTaxeDtos() {
		return productTaxeDtos;
	}
	public void setProductTaxeDtos(List<ProductTaxDto> productTaxeDtos) {
		this.productTaxeDtos = productTaxeDtos;
	}	
}