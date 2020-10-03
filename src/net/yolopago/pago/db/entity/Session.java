package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Session {
	@PrimaryKey
	private Long _id;
	private String accessToken;
	private String refreshToken;
	private Long idUser;
	private String contractVersion;
	private String contractTypeVersion;
	private String productVersion;
	private Long terminalTime;
	private Long paymentTime;
	private Long idTerminal;
	private Long idMerchant;



	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getContractVersion() {
		return contractVersion;
	}

	public void setContractVersion(String contractVersion) {
		this.contractVersion = contractVersion;
	}

	public String getContractTypeVersion() {
		return contractTypeVersion;
	}

	public void setContractTypeVersion(String contractTypeVersion) {
		this.contractTypeVersion = contractTypeVersion;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public Long getTerminalTime() {
		return terminalTime;
	}

	public void setTerminalTime(Long terminalTime) {
		this.terminalTime = terminalTime;
	}

	public Long getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Long paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Long getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(Long idTerminal) {
		this.idTerminal = idTerminal;
	}

	public Long getIdMerchant() {
		return idMerchant;
	}

	public void setIdMerchant(Long idMerchant) {
		this.idMerchant = idMerchant;
	}
}
