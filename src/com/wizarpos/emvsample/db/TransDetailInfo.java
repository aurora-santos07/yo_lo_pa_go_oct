package com.wizarpos.emvsample.db;

import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.util.StringUtil;

public class TransDetailInfo extends TransDetailTable implements Constant {
    // unstored data
    private byte[] responseCode = null;
    private boolean macFlag = false;
    private byte[] mac = new byte[8];
	private String track1Data;
	private String track2Data;
    private String track3Data;
    private byte[] pinBlock = null;
    private byte cardType = -1;
    private String serviceCode = "";
    private Integer balance = -1;
    private String responseMsg="";
    // EMV
    private boolean appSelected = false;
    private boolean emvOnline = false;
    private byte emvOnlineResult = ONLINE_FAIL;
	private byte emvRetCode = 0;
	private byte emvStatus  = 0;
    private boolean emvCardError = false;
    private boolean panViaMSR = false;

    private byte emvKernelType = CONTACT_EMV_KERNAL;
    private byte[] issuerAuthData = null;
    
    private int needSignature = 1;

    private Long ticketId=null;
    private Long paymentId=null;
    private String rfc="";
    private String operador="";

	public TransDetailInfo()
    {
    	init();
    }
    
    public void init()
    {
    	super.init();
    	responseCode = null;	
        mac = new byte[8];
        macFlag = false;
        track2Data = null;
        track3Data = null;
        pinBlock = null;
        cardType = -1;
        serviceCode = "";
        
        emvOnline = false;
        emvOnlineResult = ONLINE_FAIL;
        emvRetCode = 0;
        emvStatus = 0;
        balance = -1;
        emvCardError = false;
        panViaMSR = false;
        emvKernelType = CONTACT_EMV_KERNAL;
        issuerAuthData = null;
        appSelected = false;
        
        needSignature = 1;
    }

	// responseCode
    public byte[] getResponseCode()
    {
    	return responseCode;
    }
    
    public void setResponseCode(byte[] responseCode)
    {
    	if(responseCode.length == 2)
    	{
    		this.responseCode = new byte[2];
    		System.arraycopy(responseCode, 0, this.responseCode, 0, 2);
    	}
    }
    
    // macFlag
    public boolean getMacFlag()
    {
    	return macFlag;
    }
    
    public void setMacFlag(boolean macFlag)
    {
    	this.macFlag = macFlag;
    }
    
    // mac
    public byte[] getMac()
    {
    	return mac;
    }
    
    public void setMac(byte[] mac)
    {
    	if(mac.length == 8)
    	{
    		System.arraycopy(mac, 0, this.mac, 0, mac.length);
    	}
    }

	public String getTrack1Data() {
		return track1Data;
	}

	public void setTrack1Data(String track1Data) {
		this.track1Data = track1Data;
	}

	// track2Data
    public String getTrack2Data()
    {
    	return track2Data;
    }
    
    public void setTrack2Data(String track2Data)
    {
    	if(track2Data != null && track2Data.length() > 0)
    	{
    		this.track2Data = track2Data;
    	}
    }
    
    public void setTrack2Data(byte[] track2Data, int offset, int length)
    {
    	if(   track2Data != null
    	   && track2Data.length > 0 
    	   && (offset + length) < track2Data.length
    	  )
    	{
    		byte[] tmpData = new byte[length];
    		System.arraycopy(track2Data, offset, tmpData, 0, length);
    		this.track2Data = StringUtil.toString(tmpData);
    	}
    }
    
    // track3Data
    public String getTrack3Data()
    {
    	return track3Data;
    }
    
    public void setTrack3Data(String track3Data)
    {
    	if(track3Data != null && track3Data.length() > 0)
    	{
    		this.track3Data = track3Data;
    	}
    }
    
    public void setTrack3Data(byte[] track3Data, int offset, int length)
    {
    	if(   track3Data != null
    	   && track3Data.length > 0 
    	   && (offset + length) < track3Data.length
    	  )
    	{
    		byte[] tmpData = new byte[length];
    		System.arraycopy(track3Data, offset, tmpData, 0, length);
    		this.track3Data = StringUtil.toString(tmpData);
    	}
    }
    
    // pinBlock
    public byte[] getPinBlock()
    {
    	return pinBlock;
    }
    
    public void setPinBlock(byte[] pinBlock)
    {
    	if(pinBlock != null && pinBlock.length == 8)
    	{
    		this.pinBlock = new byte[8];
    		System.arraycopy(pinBlock, 0, this.pinBlock, 0, 8);
    	}
    }
    
    // cardType
    public byte getCardType()
    {
    	return cardType;
    }
    
    public void setCardType(byte cardType)
    {
    	this.cardType = cardType;
    }
    
    // serviceCode
    public String getServiceCode()
    {
    	return serviceCode;
    }
    
    public void setServiceCode(String serviceCode)
    {
    	this.serviceCode = serviceCode;
    }
    
    // emvStatus
    public byte getEMVStatus()
    {
    	return emvStatus;
    }
    
    public void setEMVStatus(byte emvStatus)
    {
    	this.emvStatus = emvStatus;
    }
    
    // emvOnline
    public boolean getEMVOnlineFlag()
    {
    	return emvOnline;
    }
    
    public void setEMVOnlineFlag(boolean flag)
    {
    	this.emvOnline = flag;
    }
    
    // emvOnlineResult
    public byte getEMVOnlineResult()
    {
    	return emvOnlineResult;
    }
    
    public void setEMVOnlineResult(byte result)
    {
    	this.emvOnlineResult = result;
    }
    
    // emvretCode
    public byte getEMVRetCode()
    {
    	return emvRetCode;
    }
    
    public void setEMVRetCode(byte emvRetCode)
    {
    	this.emvRetCode = emvRetCode;
    }
    
    // emvCardError
    public boolean getEmvCardError()
    {
    	return emvCardError;
    }
    
    public void setEmvCardError(boolean errorFlag)
    {
    	this.emvCardError = errorFlag;
    }

    // panViaMSR
    public boolean getPanViaMSR()
    {
        return panViaMSR;
    }

    public void setPanViaMSR(boolean flag)
    {
        this.panViaMSR = flag;
    }

    // emvkernelType
    public byte getEMVKernelType()
    {
    	return emvKernelType;
    }
    
    public void setEMVKernelType(byte kernelType)
    {
    	this.emvKernelType = kernelType;
    }
    
    // issuerAuthData
    public byte[] getIssuerAuthData()
    {
    	return issuerAuthData;
    }
    
    public void setIssuerAuthData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		issuerAuthData = new byte[length];
    		System.arraycopy(data, offset, issuerAuthData, 0, length);
    	}
    }
    
    public boolean getAppSelected()
    {
    	return appSelected;
    }
    
    public void setAppSelected(boolean flag)
    {
    	appSelected = flag;
    }
    
    public int getNeedSignature() {
		return needSignature;
	}

	public void setNeedSignature(int needSignature) {
		this.needSignature = needSignature;
	}

    public String getResponseMsg() {   return responseMsg;   }

    public void setResponseMsg(String responseMsg) {    this.responseMsg = responseMsg;  }

    public Long getTicketId() {       return ticketId;   }

    public void setTicketId(Long ticketId) {       this.ticketId = ticketId;    }

    public String getRfc() {       return rfc;    }

    public void setRfc(String rfc) {       this.rfc = rfc;    }

    public String getOperador() {       return operador;    }

    public void setOperador(String operador) {       this.operador = operador;    }

    public Long getPaymentId() {   return paymentId;   }

    public void setPaymentId(Long paymentId) {    this.paymentId = paymentId;
    }
}
