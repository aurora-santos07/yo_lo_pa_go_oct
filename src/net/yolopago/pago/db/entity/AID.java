package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AID {
	@PrimaryKey
	private int _id;
	private String aid;   					    // Application Identifier
	private String appLabel;				    // Application Label
	private String appPreferredName;		    // Application Preferred Name
	private byte    appPriority;				// Application Priority
	private int termFloorLimit;			    // Terminal Floor Limit
	private String tacDefault;		// Terminal Action Code - Default
	private String tacDenial;		// Terminal Action Code - Denial
	private String tacOnline;		// Terminal Action Code - Online
	private byte    targetPercentage;			// Target Percentage
	private int thresholdValue;			    // threshold Value
	private byte    maxTargetPercentage;		// Maximum Target Percentage
	private String acquirerId;				    // Acquirer Identifier
	private String mcc;		                // Merchant Category Code
	private String mid;				        // Merchant Identifier
	private String appVersionNumber;			// Application Version Number
	private byte    posEntryMode;				// Point-of-Service(POS) Entry Mode
	private String transReferCurrencyCode;	    // TransactionChip Reference Currency Code
	private byte    transReferCurrencyExponent; // TransactionChip Reference Currency Exponent
	private String defaultDDOL;				// Default Dynamic Data Authentication Data Object List(DDOL)
	private String defaultTDOL;				// Default TransactionChip Certificate Data Object List(TDOL)
	// supportOnlinePin[0] = 0 means the Application unsupported online PIN,
	// any other value means the Application supported online PIN
	private byte    supportOnlinePin;			// DF18
	private byte    needCompleteMatching;		// DF01

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

	public String getAppPreferredName() {
		return appPreferredName;
	}

	public void setAppPreferredName(String appPreferredName) {
		this.appPreferredName = appPreferredName;
	}

	public byte getAppPriority() {
		return appPriority;
	}

	public void setAppPriority(byte appPriority) {
		this.appPriority = appPriority;
	}

	public int getTermFloorLimit() {
		return termFloorLimit;
	}

	public void setTermFloorLimit(int termFloorLimit) {
		this.termFloorLimit = termFloorLimit;
	}

	public String getTacDefault() {
		return tacDefault;
	}

	public void setTacDefault(String tacDefault) {
		this.tacDefault = tacDefault;
	}

	public String getTacDenial() {
		return tacDenial;
	}

	public void setTacDenial(String tacDenial) {
		this.tacDenial = tacDenial;
	}

	public String getTacOnline() {
		return tacOnline;
	}

	public void setTacOnline(String tacOnline) {
		this.tacOnline = tacOnline;
	}

	public byte getTargetPercentage() {
		return targetPercentage;
	}

	public void setTargetPercentage(byte targetPercentage) {
		this.targetPercentage = targetPercentage;
	}

	public int getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public byte getMaxTargetPercentage() {
		return maxTargetPercentage;
	}

	public void setMaxTargetPercentage(byte maxTargetPercentage) {
		this.maxTargetPercentage = maxTargetPercentage;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getAppVersionNumber() {
		return appVersionNumber;
	}

	public void setAppVersionNumber(String appVersionNumber) {
		this.appVersionNumber = appVersionNumber;
	}

	public byte getPosEntryMode() {
		return posEntryMode;
	}

	public void setPosEntryMode(byte posEntryMode) {
		this.posEntryMode = posEntryMode;
	}

	public String getTransReferCurrencyCode() {
		return transReferCurrencyCode;
	}

	public void setTransReferCurrencyCode(String transReferCurrencyCode) {
		this.transReferCurrencyCode = transReferCurrencyCode;
	}

	public byte getTransReferCurrencyExponent() {
		return transReferCurrencyExponent;
	}

	public void setTransReferCurrencyExponent(byte transReferCurrencyExponent) {
		this.transReferCurrencyExponent = transReferCurrencyExponent;
	}

	public String getDefaultDDOL() {
		return defaultDDOL;
	}

	public void setDefaultDDOL(String defaultDDOL) {
		this.defaultDDOL = defaultDDOL;
	}

	public String getDefaultTDOL() {
		return defaultTDOL;
	}

	public void setDefaultTDOL(String defaultTDOL) {
		this.defaultTDOL = defaultTDOL;
	}

	public byte getSupportOnlinePin() {
		return supportOnlinePin;
	}

	public void setSupportOnlinePin(byte supportOnlinePin) {
		this.supportOnlinePin = supportOnlinePin;
	}

	public byte getNeedCompleteMatching() {
		return needCompleteMatching;
	}

	public void setNeedCompleteMatching(byte needCompleteMatching) {
		this.needCompleteMatching = needCompleteMatching;
	}
}
