package net.yolopago.pago.ws.dto.contracttype;

import java.util.HashSet;
import java.util.Set;

public class ContractTypeDto {
	private Long id;
	private String name;
	private String paymentProcesorType;
	private Set<PreferenceDto> preferencesDto = new HashSet<PreferenceDto>();
	private Set<AIDDto> aidDtos = new HashSet<AIDDto>();
	private Set<CAPKDto> capkDtos = new HashSet<CAPKDto>();

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

	public String getPaymentProcesorType() {
		return paymentProcesorType;
	}

	public void setPaymentProcesorType(String paymentProcesorType) {
		this.paymentProcesorType = paymentProcesorType;
	}

	public Set<PreferenceDto> getPreferencesDto() {
		return preferencesDto;
	}

	public void setPreferencesDto(Set<PreferenceDto> preferencesDto) {
		this.preferencesDto = preferencesDto;
	}

	public Set<AIDDto> getAidDtos() {
		return aidDtos;
	}

	public void setAidDtos(Set<AIDDto> aidDtos) {
		this.aidDtos = aidDtos;
	}

	public Set<CAPKDto> getCapkDtos() {
		return capkDtos;
	}

	public void setCapkDtos(Set<CAPKDto> capkDtos) {
		this.capkDtos = capkDtos;
	}
}
