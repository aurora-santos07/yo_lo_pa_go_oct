package net.yolopago.pago.ws.dto.contracttype;

import java.util.Set;

public class VoucherLayoutDto {
	private Long id;
	private String paymentProcesorType;
	private Set<VoucherHeaderDto> voucherHeaderDtos;
	private Set<VoucherFooterDto> voucherFooterDtos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentProcesorType() {
		return paymentProcesorType;
	}

	public void setPaymentProcesorType(String paymentProcesorType) {
		this.paymentProcesorType = paymentProcesorType;
	}

	public Set<VoucherHeaderDto> getVoucherHeaderDtos() {
		return voucherHeaderDtos;
	}

	public void setVoucherHeaderDtos(Set<VoucherHeaderDto> voucherHeaderDtos) {
		this.voucherHeaderDtos = voucherHeaderDtos;
	}

	public Set<VoucherFooterDto> getVoucherFooterDtos() {
		return voucherFooterDtos;
	}

	public void setVoucherFooterDtos(Set<VoucherFooterDto> voucherFooterDtos) {
		this.voucherFooterDtos = voucherFooterDtos;
	}
}
