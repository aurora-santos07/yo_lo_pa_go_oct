package net.yolopago.pago.ws.dto.payment;

public class IssuerDto {
	private Long id;
	private String name;
	private String fiid;
	private String network;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFiid() {
		return fiid;
	}
	public void setFiid(String fiid) {
		this.fiid = fiid;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
}
