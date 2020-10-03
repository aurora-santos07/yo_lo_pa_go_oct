package net.yolopago.pago.ws.dto.contract;

public class TerminalDto {
    private Long id;
    private ContractDto contractDto;
    private MerchantDto merchantDto;
    private String terminalStatus;
    private String terminalType;
    private String serialNumber;
    private String syncVersion;
    //private Date lastSynch;
    private String version;
    //private SyncStatus syncStatus;
    //private Date syncDate;
    private Long id_user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContractDto getContractDto() {
        return contractDto;
    }

    public void setContractDto(ContractDto contractDto) {
        this.contractDto = contractDto;
    }

    public MerchantDto getMerchantDto() {
        return merchantDto;
    }

    public void setMerchantDto(MerchantDto merchantDto) {
        this.merchantDto = merchantDto;
    }

    public String getTerminalStatus() {
        return terminalStatus;
    }

    public void setTerminalStatus(String terminalStatus) {
        this.terminalStatus = terminalStatus;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

}
