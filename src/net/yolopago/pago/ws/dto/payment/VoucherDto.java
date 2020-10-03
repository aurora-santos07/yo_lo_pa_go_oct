package net.yolopago.pago.ws.dto.payment;

public class VoucherDto {
	private Long id;
	private String voucherStatus;
	private PaymentDto paymentDto;
	private String emvTags;
	private boolean okResponse = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(String voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public PaymentDto getPaymentDto() {
		return paymentDto;
	}

	public void setPaymentDto(PaymentDto paymentDto) {
		this.paymentDto = paymentDto;
	}

	public String getEmvTags() {
		return emvTags;
	}

	public void setEmvTags(String emvTags) {
		this.emvTags = emvTags;
	}

	public boolean isOkResponse() {
		return okResponse;
	}

	public void setOkResponse(boolean okResponse) {
		this.okResponse = okResponse;
	}
}

