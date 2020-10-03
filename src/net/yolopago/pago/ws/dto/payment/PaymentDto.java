package net.yolopago.pago.ws.dto.payment;

import java.util.Date;
import java.util.Map;

public class PaymentDto {
	private Long id;
	private String paymentStatus;
	private String paymentType;
	private String reverseType;

	private Date capturedDate;
	private Date payedDate;
	private Double amount;
	private Integer months;
	private String track1;
	private String track2;
	private String emvTags;
	private transient Map<String, String> tlvTags;
	private Integer retries;
	private Long idTicket;
	private Double ticketAmount;
	private String signature;
	private String reason;

	private String cardType="";
	private String cardBrand="";

	private String maskedPAN="";
	private String issuingBank="";
	private String controlNumber="";

	private MerchantDto merchantDto;
	private TerminalDto terminalDto;
	private IssuerDto issuerDto;
	private SellerDto sellerDto;
	private CustomerDto customerDto;

	private String scripts;
	private String scriptsResults;

	private String paymentProcesorType;
	private String reference;
	private String originalReference;
	private String resultAcquirer;
	private String resultAuthorizer;
	private String codeAcquirer;
	private String codeAuthorizer;
	private Date inputAcquirer;
	private Date inputProsa;
	private Date outputAcquirer;
	private Date outputProsa;
	private String afiliacion;
	private String idAfiliacion;
	private String aid;
	private String tvr;
	private String tsi;
	private String apn;
	private String card_name;
	private String voucher_file;
    private String datosEmv;

    public String getDatosEmv() {
        return datosEmv;
    }

    public void setDatosEmv(String datosEmv) {
        this.datosEmv = datosEmv;
    }




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getReverseType() {
		return reverseType;
	}

	public void setReverseType(String reverseType) {
		this.reverseType = reverseType;
	}

	public Date getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(Date capturedDate) {
		this.capturedDate = capturedDate;
	}

	public Date getPayedDate() {
		return payedDate;
	}

	public void setPayedDate(Date payedDate) {
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

	public Map<String, String> getTlvTags() {
		return tlvTags;
	}

	public void setTlvTags(Map<String, String> tlvTags) {
		this.tlvTags = tlvTags;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public MerchantDto getMerchantDto() {
		return merchantDto;
	}

	public void setMerchantDto(MerchantDto merchantDto) {
		this.merchantDto = merchantDto;
	}

	public TerminalDto getTerminalDto() {
		return terminalDto;
	}

	public void setTerminalDto(TerminalDto terminalDto) {
		this.terminalDto = terminalDto;
	}

	public IssuerDto getIssuerDto() {
		return issuerDto;
	}

	public void setIssuerDto(IssuerDto issuerDto) {
		this.issuerDto = issuerDto;
	}

	public SellerDto getSellerDto() {
		return sellerDto;
	}

	public void setSellerDto(SellerDto sellerDto) {
		this.sellerDto = sellerDto;
	}

	public CustomerDto getCustomerDto() {
		return customerDto;
	}

	public void setCustomerDto(CustomerDto customerDto) {
		this.customerDto = customerDto;
	}

	public String getScripts() {
		return scripts;
	}

	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

	public String getScriptsResults() {
		return scriptsResults;
	}

	public void setScriptsResults(String scriptsResults) {
		this.scriptsResults = scriptsResults;
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

	public String getOriginalReference() {
		return originalReference;
	}

	public void setOriginalReference(String originalReference) {
		this.originalReference = originalReference;
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

	public Date getInputAcquirer() {
		return inputAcquirer;
	}

	public void setInputAcquirer(Date inputAcquirer) {
		this.inputAcquirer = inputAcquirer;
	}

	public Date getInputProsa() {
		return inputProsa;
	}

	public void setInputProsa(Date inputProsa) {
		this.inputProsa = inputProsa;
	}

	public Date getOutputAcquirer() {
		return outputAcquirer;
	}

	public void setOutputAcquirer(Date outputAcquirer) {
		this.outputAcquirer = outputAcquirer;
	}

	public Date getOutputProsa() {
		return outputProsa;
	}

	public void setOutputProsa(Date outputProsa) {
		this.outputProsa = outputProsa;
	}

	public String getIssuingBank() {		return issuingBank;	}

	public void setIssuingBank(String issuingBank) {		this.issuingBank = issuingBank;	}

	public String getCardType() {		return cardType;	}

	public void setCardType(String cardType) {		this.cardType = cardType;	}

	public String getCardBrand() {		return cardBrand;	}

	public void setCardBrand(String cardBrand) {		this.cardBrand = cardBrand;	}

	public String getMaskedPAN() {		return maskedPAN;	}

	public void setMaskedPAN(String maskedPAN) {		this.maskedPAN = maskedPAN;	}

	public String getControlNumber() {	return controlNumber;}
	public void setControlNumber(String controlNumber) {	this.controlNumber = controlNumber;}

	public String getAfiliacion() {		return afiliacion;	}

	public void setAfiliacion(String afiliacion) {		this.afiliacion = afiliacion;	}

	public String getIdAfiliacion() {
		return idAfiliacion;
	}

	public void setIdAfiliacion(String idAfiliacion) {
		this.idAfiliacion = idAfiliacion;
	}

	public String getAid() {	return aid;	}
	public void setAid(String aid) {	this.aid = aid;	}

	public String getTvr() {	return tvr;	}
	public void setTvr(String tvr) {	this.tvr = tvr;	}

	public String getTsi() {	return tsi;	}
	public void setTsi(String tsi) {	this.tsi = tsi;	}

	public String getApn() {	return apn;	}
	public void setApn(String apn) {		this.apn = apn;	}

	public String getCardName() {	return card_name;
	}

	public void setCardName(String cardName) {	this.card_name = cardName;
	}

	public String getVoucherFile() {
		return voucher_file;
	}

	public void setVoucherFile(String voucherFile) {
		this.voucher_file = voucherFile;
	}
}