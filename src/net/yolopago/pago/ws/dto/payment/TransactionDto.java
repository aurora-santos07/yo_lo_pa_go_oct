package net.yolopago.pago.ws.dto.payment;

import java.util.Date;

public class TransactionDto {
	private Long id;
	private Date capturedDate;
	private Double amount;
	private String track1;
	private String track2;
	private String emvTags;
	private String paymentType;
	private Long idMerchant;
	private Long idTicket;
	private Long idTerminal;
	private Long idSeller;
	private Double ticketAmount;

	private String maskedPAN;

	private String controlNumber;
	private String afiliacion;
	private String aid;
	private String tvr;
	private String tsi;
	private String apn;
	private String cardHolder;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(Date capturedDate) {
		this.capturedDate = capturedDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getTrack1() {
		return track1;
	}

	public void setTrack1(String track1) {
		this.track1 = track1;
	}

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	public String getEmvTags() {
		return emvTags;
	}

	public void setEmvTags(String emvTags) {
		this.emvTags = emvTags;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Long getIdMerchant() {
		return idMerchant;
	}

	public void setIdMerchant(Long idMerchant) {
		this.idMerchant = idMerchant;
	}

	public Long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}

	public Long getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(Long idTerminal) {
		this.idTerminal = idTerminal;
	}

	public Long getIdSeller() {
		return idSeller;
	}

	public void setIdSeller(Long idSeller) {
		this.idSeller = idSeller;
	}

	public Double getTicketAmount() {
		return ticketAmount;
	}

	public void setTicketAmount(Double ticketAmount) {
		this.ticketAmount = ticketAmount;
	}

	public String getMaskedPAN() {		return maskedPAN;	}

	public void setMaskedPAN(String maskedPAN) {	this.maskedPAN = maskedPAN;}

	public String getControlNumber() {		return controlNumber;
	}

	public void setControlNumber(String controlNumber) {		this.controlNumber = controlNumber;	}

	public String getAfiliacion() {		return afiliacion;	}

	public void setAfiliacion(String afiliacion) {	this.afiliacion = afiliacion;	}

	public String getAid() {	return aid;}

	public void setAid(String aid) {this.aid = aid;	}

	public String getTvr() {	return tvr;	}

	public void setTvr(String tvr) {	this.tvr = tvr;	}

	public String getTsi() { return tsi;	}

	public void setTsi(String tsi) {	this.tsi = tsi;	}

	public String getApn() {	return apn;	}

	public void setApn(String apn) {this.apn = apn;	}

	public String getCardHolder() {		return cardHolder;	}

	public void setCardHolder(String cardHolder) {		this.cardHolder = cardHolder;	}
}