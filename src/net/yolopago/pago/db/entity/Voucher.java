package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Voucher {
	@PrimaryKey
	private Long _id;
	private Long idPayment;
	private String voucherStatus;
	private String emvTags;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public Long getIdPayment() {
		return idPayment;
	}

	public void setIdPayment(Long idPayment) {
		this.idPayment = idPayment;
	}

	public String getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(String voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public String getEmvTags() {
		return emvTags;
	}

	public void setEmvTags(String emvTags) {
		this.emvTags = emvTags;
	}
}
