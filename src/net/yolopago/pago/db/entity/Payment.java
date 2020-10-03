package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Payment {
	@PrimaryKey
	private Long _id;
	private String paymentStatus;
	private String paymentType;
	private Long capturedDate;
	private Long payedDate ;
	private Double amount;
	private Integer months;
	private String emvTags;
	private Integer retries;
	private Long idTicket;
	private Double ticketAmount;
	private String signature;
	private Long idMerchant;
	private Long idTerminal;
	private Long idIssuer;
	private Long idSeller;
	private Long idCustomer;



	private String issuingBank;
	private String reason;
	private String cardBrand="";
	private String cardType="";


	private String paymentProcesorType;
	private String reference;
	private String resultAcquirer;
	private String resultAuthorizer;
	private String codeAcquirer;
	private String codeAuthorizer="";
	private Long inputAcquirer;
	private Long inputProsa;
	private Long outputAcquirer;
	private Long outputProsa;

	private String datosEmv;

	public String getDatosEmv() {
		return datosEmv;
	}

	public void setDatosEmv(String datos_emv) {
		this.datosEmv = datos_emv;
	}

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Long getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(Long capturedDate) {
		this.capturedDate = capturedDate;
	}

	public Long getPayedDate() {
		return payedDate;
	}

	public void setPayedDate(Long payedDate) {
		this.payedDate = payedDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getMonths() {
		return months;
	}

	public void setMonths(Integer months) {
		this.months = months;
	}

	public String getEmvTags() {
		return emvTags;
	}

	public void setEmvTags(String emvTags) {
		this.emvTags = emvTags;
	}

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public Long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}

	public Double getTicketAmount() {
		return ticketAmount;
	}

	public void setTicketAmount(Double ticketAmount) {
		this.ticketAmount = ticketAmount;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Long getIdMerchant() {
		return idMerchant;
	}

	public void setIdMerchant(Long idMerchant) {
		this.idMerchant = idMerchant;
	}

	public Long getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(Long idTerminal) {
		this.idTerminal = idTerminal;
	}

	public Long getIdIssuer() {
		return idIssuer;
	}

	public void setIdIssuer(Long idIssuer) {
		this.idIssuer = idIssuer;
	}

	public Long getIdSeller() {
		return idSeller;
	}

	public void setIdSeller(Long idSeller) {
		this.idSeller = idSeller;
	}

	public Long getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(Long idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getPaymentProcesorType() {
		return paymentProcesorType;
	}

	public void setPaymentProcesorType(String paymentProcesorType) {
		this.paymentProcesorType = paymentProcesorType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getResultAcquirer() {
		return resultAcquirer;
	}

	public void setResultAcquirer(String resultAcquirer) {
		this.resultAcquirer = resultAcquirer;
	}

	public String getResultAuthorizer() {
		return resultAuthorizer;
	}

	public void setResultAuthorizer(String resultAuthorizer) {
		this.resultAuthorizer = resultAuthorizer;
	}

	public String getCodeAcquirer() {
		return codeAcquirer;
	}

	public void setCodeAcquirer(String codeAcquirer) {
		this.codeAcquirer = codeAcquirer;
	}

	public String getCodeAuthorizer() {
		return codeAuthorizer;
	}

	public void setCodeAuthorizer(String codeAuthorizer) {
		this.codeAuthorizer = codeAuthorizer;
	}

	public Long getInputAcquirer() {
		return inputAcquirer;
	}

	public void setInputAcquirer(Long inputAcquirer) {
		this.inputAcquirer = inputAcquirer;
	}

	public Long getInputProsa() {
		return inputProsa;
	}

	public void setInputProsa(Long inputProsa) {
		this.inputProsa = inputProsa;
	}

	public Long getOutputAcquirer() {
		return outputAcquirer;
	}

	public void setOutputAcquirer(Long outputAcquirer) {
		this.outputAcquirer = outputAcquirer;
	}

	public Long getOutputProsa() {
		return outputProsa;
	}

	public void setOutputProsa(Long outputProsa) {
		this.outputProsa = outputProsa;
	}

	public String getReason() {		return reason;	}

	public void setReason(String reason) {		this.reason = reason;	}

	public String getIssuingBank() {return issuingBank;}

	public void setIssuingBank(String issuingBank) {this.issuingBank = issuingBank;	}

	public String getCardBrand() {		return cardBrand;	}

	public void setCardBrand(String cardBrand) {		this.cardBrand = cardBrand;	}

	public String getCardType() {		return cardType;	}

	public void setCardType(String cardType) {		this.cardType = cardType;	}


}
