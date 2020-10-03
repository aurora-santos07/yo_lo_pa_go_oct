package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Merchant {
	@PrimaryKey
	private Long _id;
	private Long idContract;
	private Long idProduct;
	private String name;
	private String street;
	private String external;
	private String internal;
	private String phone;
	private String mail;
	private String url;
	private String taxid;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public Long getIdContract() {
		return idContract;
	}

	public void setIdContract(Long idContract) {
		this.idContract = idContract;
	}

	public Long getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Long idProduct) {
		this.idProduct = idProduct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		if(street==null){
			return "";
		}
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getExternal() {
		if(external==null){
			return "";
		}
		return external;
	}

	public void setExternal(String external) {
		this.external = external;
	}

	public String getInternal() {
		if(internal==null){
			return "";
		}
		return internal;
	}

	public void setInternal(String internal) {
		this.internal = internal;
	}

	public String getPhone() {
		if(phone==null){
			return "";
		}
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}
}
