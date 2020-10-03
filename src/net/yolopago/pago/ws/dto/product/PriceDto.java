package net.yolopago.pago.ws.dto.product;

import java.util.Date;

public class PriceDto {
	private Long id;
	private ProductDto productDto;
	private String priceStatus;
	private Boolean fixedPrice;
	private Double maxPrice;
	private Double buyPrice;
	private Double sellPrice;
	private Date effectiveDate;
	
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
	public String getPriceStatus() {
		return priceStatus;
	}
	public void setPriceStatus(String priceStatus) {
		this.priceStatus = priceStatus;
	}
	public Boolean getFixedPrice() {
		return fixedPrice;
	}
	public void setFixedPrice(Boolean fixedPrice) {
		this.fixedPrice = fixedPrice;
	}
	public Double getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Double getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
