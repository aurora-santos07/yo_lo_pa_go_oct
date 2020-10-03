package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RevokedCAPK {
	@PrimaryKey
	private Integer _id;
	private String rid;
	private String capki;
	private String certSerial;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
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

	public String getCertSerial() {
		return certSerial;
	}

	public void setCertSerial(String certSerial) {
		this.certSerial = certSerial;
	}
}
