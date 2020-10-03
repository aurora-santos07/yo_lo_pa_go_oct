package com.wizarpos.emvsample.db;

import com.wizarpos.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class TransDetailTable {
	private Integer _id;
	private Integer trace;
	private String pan;
	private byte cardEntryMode;
	private byte pinEntryMode;
	private String expiry;
    private byte transType;
    private String transDate; // YYYYMMDD
    private String transTime;
    private String authCode;
    private String rrn;
    private String Etransfer;
    private String hourTransfer;
    private String reference;
    private String originalReference;
    private String merchantName;
    private String merchantPhone;
    private String failReason ="";
    private String codeA;
    private String countName;
    private String tipoVenta;
    private String oper;
    private Long transAmount;
    private Integer othersAmount;
    private Integer balance;
    private String cardTypeName;
    private String cardBrandName;

    // EMV Data
    private byte csn;
    private String unpredictableNumber;
    private String ac;
    private String tvr;
    private String aid;
    private String tsi;
    private String appLabel;
    private String appName;
    private String aip;
    private String iad;
    private String arqc;
    private Integer ecBalance;
    private String transaction;
    private String Tdigit;
    private String TID;
    private String nc;
    private String dir;
    private String Bname;
    private String iccData;
    private String productoTicket;
    private String scriptResult;
    private String EMVTags;
    private Map<Integer, String> banorteTags = new HashMap<Integer, String>();
    
    public TransDetailTable()
    {
    	init();
    }
    
    public void init()
    {
    	_id = -1;
    	trace = 0;
    	pan ="";
    	cardEntryMode = 0;
    	pinEntryMode = 0;
    	transaction = "";
    	merchantName = "";
        merchantPhone = "";
    	Tdigit = "";
    	Etransfer = "";
    	reference = "";
    	productoTicket = "";
    	codeA = "";
    	EMVTags = "";
    	arqc = "";
    	Bname = "";
        hourTransfer = "";
    	dir = "";
    	TID = "";
    	expiry ="";
        transType = -1;
        transDate ="";
        transTime ="";
        authCode ="";
        rrn ="";
        oper ="";
        transAmount = Long.valueOf(-1);
        othersAmount = 0;
        balance = 0;
        // EMV Data
        csn = 0;
        tipoVenta = "";
        unpredictableNumber = "";
        ac = "";
        tvr = "";
        aid = "";
        tsi = "";
        appLabel = "";
        appName = "";
        aip = "";
        iad = "";
        ecBalance = -1;
        scriptResult = "";
        iccData = "";
    }
    
    public Integer getId()
    {
    	return _id;
    }
    
    public void setId(Integer id)
    {
    	this._id = id;
    }
    
    public Integer getTrace()
    {
    	return trace;
    }
    
    public void setTrace(Integer trace)
    {
    	this.trace = trace;
    }
    // pan
    public String getPAN()
    {
        return pan;
    }
    
    public void setPAN(String pan)
    {
    	this.pan = pan;
    }

    //lista producto
    // pan
    public String getProductoTicket()
    {
        return productoTicket;
    }

    public void setProductoTicket(String productoTicket)
    {
        this.productoTicket = productoTicket;
    }

    //Hora de la transferencia

    public String getHour()
    {
        return hourTransfer;
    }

    public void setHour(String hourTransfer)
    {
        this.hourTransfer = hourTransfer;
    }

    //tipo de trnasferencia venta o cancelación
    public String getVenta()
    {
        return tipoVenta;
    }

    public void setVenta(String tipoVenta)
    {
        this.tipoVenta = tipoVenta;
    }

    //telefono del comercio;

    public String getMerchantPhone() {
        return merchantPhone;
    }

    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }
    //nombre del comercio;
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    //ARQC
    public String getDir()
    {
        return dir;
    }

    public void setDir(String dir)
    {
        this.dir = dir;
    }

    //Referencia

    public String getARQC()
    {
        return arqc;
    }

    public void setARQC(String arqc)
    {
        this.arqc = arqc;
    }

       public String getReference()
    {
        if(reference==null){
            return "";
        }
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }
    public String getOriginalReference()
    {
        return originalReference;
    }

    public void setOriginalReference(String originalReference)
    {
        this.originalReference = originalReference;
    }

    //tipo de transaccion
    public String getTransaction(){
        return transaction;
    }

    public void setTransaction(String transaction){
        this.transaction = transaction;
    }

    //terminal id
    public String getTerminalId(){
        return TID;
    }

    public void setTerminalId(String TID){
        this.TID = TID;
    }


    //numero de control

    public String getControlNumber(){
        return nc;
    }

    public void setControlNumber(String nc){
        this.nc = nc;
    }


    //código de autorización
    public String getCodeAut(){
        return codeA;
    }

    public void setCodeAut(String codeA){
        this.codeA = codeA;
    }
    //últimos 4 digitos de la tarjeta
    public String getTdigit()
    {
        return Tdigit;
    }

    public void setTDigit(String Tdigit)
    {
        this.Tdigit = Tdigit;
    }

    //Nombre del banco
    public String getBankName()
    {
        return Bname;
    }

    public void setBankName(String Bname)
    {
        this.Bname = Bname;
    }

    //estatus de la transferencia
    public String getestatusTransfer()
    {
        return Etransfer;
    }

    public void setEstatusTransfer(String Etransfer)
    {
        this.Etransfer = Etransfer;
    }

    public String getCardTypeName() {

        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getCardBrandName() {
        return cardBrandName;
    }

    public void setCardBrandName(String cardBrandName) {
        this.cardBrandName = cardBrandName;
    }

    //Nombre de cliente

    public String getCountName() {
        return countName;
    }

    public void setCountName(String countName) {
        this.countName = countName;
    }
    
    // cardEntryMode
    public byte getCardEntryMode()
    {
    	return cardEntryMode;
    }
    
    public void setCardEntryMode(byte entryMode)
    {
    	this.cardEntryMode = entryMode;
    }

    // pinEntryMode
    public byte getPinEntryMode()
    {
    	return pinEntryMode;
    }
    
    public void setPinEntryMode(byte entryMode)
    {
    	this.pinEntryMode = entryMode;
    }
    
    // expiry
    public String getExpiry()
    {
    	return expiry;
    }
    
    public void setExpiry(String expiry)
    {
    	this.expiry = expiry;
    }
    
    // transType
    public byte getTransType()
    {
    	return transType;
    }
    
    public void setTransType(byte transType)
    {
    	this.transType = transType;
    } 
    
    // transDate
    public String getTransDate()
    {
    	return transDate;
    }
    
    public void setTransDate(String transDate)
    {
    	this.transDate = transDate;
    }
    
    // transTime
    public String getTransTime()
    {
    	return transTime;
    }
    
    public void setTransTime(String transTime)
    {
    	this.transTime = transTime;
    }
    
    // authCode
    public String getAuthCode()
    {
    	return authCode;
    }
    
    public void setAuthCode(String authCode)
    {
    	this.authCode = authCode;
    }
    
    // rrn
    public String getRRN()
    {
    	return rrn;
    }
    
    public void setRRN(String rrn)
    {
    	this.rrn = rrn;
    }
    // oper
    public String getOper()
    {
    	return oper;
    }
    
    public void setOper(String oper)
    {
    	this.oper = oper;
    }

    // transAmount
    public long getTransAmount()
    {
    	return transAmount;
    }
    
    public void setTransAmount(Long transAmount)
    {
    	this.transAmount = transAmount;
    }
    
    // othersAmount
    public Integer getOthersAmount()
    {
    	return othersAmount;
    }
    
    public void setOthersAmount(Integer amount)
    {
    	this.othersAmount = amount;
    }
    
    // balance
    public Integer getBalance()
    {
    	return balance;
    }
    
    public void setBalance(Integer balance)
    {
    	this.balance = balance;
    }
    
    // EMV Data
    // csn
    public byte getCSN()
    {
    	return csn;
    }
    
    public void setCSN(byte csn)
    {
    	this.csn = csn;
    }
    
    // unpredictableNumber
    public String getUnpredictableNumber()
    {
    	return unpredictableNumber;
    }
    
    public void setUnpredictableNumber(String unpredictableNumber)
    {
    	this.unpredictableNumber = unpredictableNumber;
    }
    
    // ac
    public String getAC()
    {
    	return ac;
    }
    
    public void setAC(String ac)
    {
    	this.ac = ac;
    }
    
    // tvr
    public String getTVR()
    {
    	return tvr;
    }
    
    public void setTVR(String tvr)
    {
    	this.tvr = tvr;
    }
    
    // aid
    public String getAID()
    {
    	return aid;
    }
    
    public void setAID(String aid)
    {
    	this.aid = aid;
    }
    
    // tsi
    public String getTSI()
    {
    	return tsi;
    }
    
    public void setTSI(String tsi)
    {
    	this.tsi = tsi;
    }
    
    // appLabel
    public String getAppLabel()
    {
    	return appLabel;
    }
    
    public void setAppLabel(String appLabel)
    {
    	this.appLabel = appLabel;
    }
    
    // appName
    public String getAppName()
    {
    	return appName;
    }
    
    public void setAppName(String appName)
    {
    	this.appName = appName;
    }
    
    // aip
    public String getAIP()
    {
    	return aip;
    }
    
    public void setAIP(String aip)
    {
    	this.aip = aip;
    }
    
    // iad
    public String getIAD()
    {
    	return iad;
    }
    
    public void setIAD(String iad)
    {
    	this.iad = iad;
    }
    
    // availableOfflineAmount
    public Integer getECBalance()
    {
    	return ecBalance;
    }
    
    public void setECBalance(Integer ecBalance)
    {
    	this.ecBalance = ecBalance;
    }
    
    // scriptResult
    public String getScriptResult()
    {
    	return scriptResult;
    }
    
    public void setScriptResult(String data)
    {
    	this.scriptResult = data;
    }

    //EMVTags
    public String getEMVTags() {
        return EMVTags;
    }

    public void setEMVTags(String EMVTags) {
        this.EMVTags = EMVTags;
    }
    
    // iccData
    public String getICCData()
    {
    	return iccData;
    }
    
    public void setICCData(String data)
    {
    	this.iccData = data;
    }
    
    public void setICCData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		iccData = StringUtil.toHexString(data, offset, length, false);
    	}
    }



    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }


    public Map<Integer, String> getBanorteTags() {
		return banorteTags;
	}

	@Override
	public String toString(){
		return DatabaseOpenHelper.TABLE_TRANS_DETAIL + " [trace=" + trace + 
			   ", pan="              + pan       +
			   ", entryMode="        + cardEntryMode +
			   ", expiry="           + expiry    +
			   ", transType="        + transType +
			   ", transDate="        + transDate +
			   ", transTime="        + transTime +
			   ", authCode="         + authCode  +
			   ", rrn="              + rrn       +
			   ", oper="             + oper      +
			   ", transAmount="      + transAmount +
			   ", balance="          + balance + "]";
	}
}

