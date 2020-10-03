package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CAPK {
	@PrimaryKey
	private int _id;
	private String rid;            // Registered Application Provider Identifier
	private String capki;            // Certificate Authority Public Key Index
	private byte hashIndex;        // Hash Algorithm Indicator
	private byte arithIndex;        // Certificate Authority Public Key Algorithm Indicator
	private String modul;            // Certificate Authority Public Key Modulus
	private String exponent;        // Certificate Authority Public Key Exponent
	private String checkSum;       // Certificate Authority Public Key Check Sum
	private String expiry;            // Certificate Expiration Date

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
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

	public byte getHashIndex() {
		return hashIndex;
	}

	public void setHashIndex(byte hashIndex) {
		this.hashIndex = hashIndex;
	}

	public byte getArithIndex() {
		return arithIndex;
	}

	public void setArithIndex(byte arithIndex) {
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
