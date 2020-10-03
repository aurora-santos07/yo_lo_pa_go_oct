package net.yolopago.pago.ws.dto.contract;

import net.yolopago.pago.ws.dto.product.ProductDto;

import java.util.Set;

public class MerchantDto {
	private Long id;
	private MerchantDto parentMerchantDto;
	private Set<MerchantDto> merchantDtos;
	private ProductDto productDto;
	private ContractDto contractDto;
	private String name;
	private String street;
	private String external;
	private String internal;
	private String phone;
	private String mail;
	private String url;
	private String taxid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MerchantDto getParentMerchantDto() {
		return parentMerchantDto;
	}

	public void setParentMerchantDto(MerchantDto parentMerchantDto) {
		this.parentMerchantDto = parentMerchantDto;
	}

	public Set<MerchantDto> getMerchantDtos() {
		return merchantDtos;
	}

	public void setMerchantDtos(Set<MerchantDto> merchantDtos) {
		this.merchantDtos = merchantDtos;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getExternal() {
		if(external==null){
			return "";
		}
		return external;
	}

	public void setExternal(String external) {
		this.external = external;
	}

	public String getInternal() {
		if(internal==null){return "";}return internal;
	}

	public void setInternal(String internal) {
		this.internal = internal;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}
}
