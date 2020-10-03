package net.yolopago.pago.ws.dto.contracttype;

public class ExceptionPANDto {
	private Integer _id;
	private String pan;
	private byte panSequence; //PAN Sequence Number

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public byte getPanSequence() {
		return panSequence;
	}

	public void setPanSequence(byte panSequence) {
		this.panSequence = panSequence;
	}
}
