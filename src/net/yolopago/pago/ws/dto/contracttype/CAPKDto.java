package net.yolopago.pago.ws.dto.contracttype;

public class CAPKDto {
	private Long id;
	private String rid;		    // Registered Application Provider Identifier
	private String capki;		    // Certificate Authority Public Key Index
	private String hashIndex;		// Hash Algorithm Indicator
	private String arithIndex;		// Certificate Authority Public Key Algorithm Indicator
	private String modul;	        // Certificate Authority Public Key Modulus
	private String exponent;	    // Certificate Authority Public Key Exponent
	private String checkSum;       // Certificate Authority Public Key Check Sum
	private String expiry;	        // Certificate Expiration Date

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getCapki() {
		return capki;
	}

	public void setCapki(String capki) {
		this.capki = capki;
	}

	public String getHashIndex() {
		return hashIndex;
	}

	public void setHashIndex(String hashIndex) {
		this.hashIndex = hashIndex;
	}

	public String getArithIndex() {
		return arithIndex;
	}

	public void setArithIndex(String arithIndex) {
		this.arithIndex = arithIndex;
	}

	public String getModul() {
		return modul;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public String getExponent() {
		return exponent;
	}

	public void setExponent(String exponent) {
		this.exponent = exponent;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
}
