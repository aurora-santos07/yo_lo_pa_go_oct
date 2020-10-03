package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.AID;
import net.yolopago.pago.db.entity.CAPK;
import net.yolopago.pago.db.entity.ExceptionPAN;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.RevokedCAPK;
import net.yolopago.pago.db.entity.VoucherFooter;
import net.yolopago.pago.db.entity.VoucherHeader;
import net.yolopago.pago.db.entity.VoucherLayout;
import net.yolopago.pago.ws.dto.contract.PreferenceDto;
import net.yolopago.pago.ws.dto.contracttype.AIDDto;
import net.yolopago.pago.ws.dto.contracttype.CAPKDto;
import net.yolopago.pago.ws.dto.contracttype.ExceptionPANDto;
import net.yolopago.pago.ws.dto.contracttype.RevokedCAPKDto;
import net.yolopago.pago.ws.dto.contracttype.VoucherFooterDto;
import net.yolopago.pago.ws.dto.contracttype.VoucherHeaderDto;
import net.yolopago.pago.ws.dto.contracttype.VoucherLayoutDto;

public class BuilderContractType {

	public Preference build(PreferenceDto preferenceDto) {
		Preference preference = new Preference();

		preference.set_id(preferenceDto.getId());
		preference.setName(preferenceDto.getName());
		preference.setValue(preferenceDto.getValue());

		return preference;
	}

	public AID build(AIDDto aidDto) {
		AID aid = new AID();

		aid.set_id(aidDto.getId().intValue());
		aid.setAid(aidDto.getIdAID());
		aid.setAppLabel(aidDto.getLabel());
		aid.setAppPreferredName(aidDto.getPreferredName());
		aid.setAppPriority(Byte.parseByte(aidDto.getPriority()));
		aid.setTermFloorLimit(aidDto.getTermFloorLimit());
		aid.setTacDefault(aidDto.getTacDefault());
		aid.setTacDenial(aidDto.getTacDenial());
		aid.setTacOnline(aidDto.getTacOnline());
		aid.setTargetPercentage(Byte.parseByte(aidDto.getTargetPercentage()));
		aid.setThresholdValue(aidDto.getThresholdValue());
		aid.setMaxTargetPercentage(Byte.parseByte(aidDto.getMaxTargetPercentage()));
		aid.setAcquirerId(aidDto.getAcquirerId());
		aid.setMcc(aidDto.getMcc());
		aid.setMid(aidDto.getMid());
		aid.setAppVersionNumber(aidDto.getAppVersionNumber());
		aid.setPosEntryMode(Byte.parseByte(aidDto.getPosEntryMode()));
		aid.setTransReferCurrencyCode(aidDto.getTransReferCurrencyCode());
		aid.setTransReferCurrencyExponent(Byte.parseByte(aidDto.getTransReferCurrencyExponent()));
		aid.setDefaultDDOL(aidDto.getDefaultDDOL());
		aid.setDefaultTDOL(aidDto.getDefaultTDOL());
		aid.setSupportOnlinePin(Byte.parseByte(aidDto.getSupportOnlinePin()));
		aid.setNeedCompleteMatching(Byte.parseByte(aidDto.getNeedCompleteMatching()));

		return aid;
	}

	public CAPK build(CAPKDto capkDto) {
		CAPK capkTable = new CAPK();

		capkTable.set_id(capkDto.getId().intValue());
		capkTable.setRid(capkDto.getRid());
		capkTable.setCapki(capkDto.getCapki());
		capkTable.setHashIndex(Byte.parseByte(capkDto.getHashIndex()));
		capkTable.setArithIndex(Byte.parseByte(capkDto.getArithIndex()));
		capkTable.setModul(capkDto.getModul());
		capkTable.setExponent(capkDto.getExponent());
		capkTable.setCheckSum(capkDto.getCheckSum());
		capkTable.setExpiry(capkDto.getExpiry());

		return capkTable;
	}

	public RevokedCAPK build(RevokedCAPKDto revokedCAPKDto) {
		RevokedCAPK revokedCAPK = new RevokedCAPK();

		revokedCAPK.set_id(revokedCAPKDto.get_id());
		revokedCAPK.setRid(revokedCAPKDto.getRid());
		revokedCAPK.setCapki(revokedCAPKDto.getCapki());
		revokedCAPK.setCertSerial(revokedCAPKDto.getCertSerial());

		return revokedCAPK;
	}

	public ExceptionPAN build(ExceptionPANDto exceptionPANDto) {
		ExceptionPAN exceptionPAN = new ExceptionPAN();

		exceptionPAN.set_id(exceptionPANDto.get_id());
		exceptionPAN.setPan(exceptionPANDto.getPan());
		exceptionPAN.setPanSequence(exceptionPANDto.getPanSequence());

		return exceptionPAN;
	}

	public VoucherHeader build(Long idVoucherLayout, VoucherHeaderDto voucherHeaderDto) {
		VoucherHeader voucherHeader = new VoucherHeader();

		voucherHeader.set_id(voucherHeader.get_id());
		voucherHeader.setLineType(voucherHeader.getLineType());
		voucherHeader.setContent(voucherHeader.getContent());
		voucherHeader.setBold(voucherHeader.getBold());
		voucherHeader.setFont(voucherHeader.getFont());
		voucherHeader.setFontSize(voucherHeader.getFontSize());
		voucherHeader.setIdVoucherLayout(idVoucherLayout);

		return voucherHeader;
	}

	public VoucherFooter build(Long idVoucherLayout, VoucherFooterDto voucherFooterDto) {
		VoucherFooter voucherFooter = new VoucherFooter();

		voucherFooter.set_id(voucherFooter.get_id());
		voucherFooter.setLineType(voucherFooter.getLineType());
		voucherFooter.setContent(voucherFooter.getContent());
		voucherFooter.setBold(voucherFooter.getBold());
		voucherFooter.setFont(voucherFooter.getFont());
		voucherFooter.setFontSize(voucherFooter.getFontSize());
		voucherFooter.setIdVoucherLayout(idVoucherLayout);

		return voucherFooter;
	}

	public VoucherLayout build(VoucherLayoutDto voucherLayoutDto) {
		VoucherLayout voucherLayout = new VoucherLayout();

		voucherLayout.set_id(voucherLayoutDto.getId());

		return voucherLayout;
	}
}
