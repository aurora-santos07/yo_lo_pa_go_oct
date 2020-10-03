package net.yolopago.pago.ws.dto.payment;

public class ContractTypeDto {
	private Long id;
	private String name;
	private String paymentProcesorType;
	
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
	public String getPaymentProcesorType() {
		return paymentProcesorType;
	}
	public void setPaymentProcesorType(String paymentProcesorType) {
		this.paymentProcesorType = paymentProcesorType;
	}
}
