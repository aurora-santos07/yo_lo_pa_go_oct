package net.yolopago.pago.ws.dto.payment;

public class TerminalDto {
	private Long id;
	private String terminalType;
	private String serialNumber;
	private String syncVersion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSyncVersion() {
		return syncVersion;
	}

	public void setSyncVersion(String syncVersion) {
		this.syncVersion = syncVersion;
	}
}
