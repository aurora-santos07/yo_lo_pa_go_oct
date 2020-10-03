package net.yolopago.pago.ws.dto.payment;

public class SellerDto {
	private Long id;
	private MerchantDto merchantDto;
	private UserDto userDto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MerchantDto getMerchantDto() {
		return merchantDto;
	}
	public void setMerchantDto(MerchantDto merchantDto) {
		this.merchantDto = merchantDto;
	}
	public UserDto getUserDto() {
		return userDto;
	}
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}
}
