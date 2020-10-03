package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.Contract;
import net.yolopago.pago.db.entity.ContractType;
import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.TicketFooter;
import net.yolopago.pago.db.entity.TicketHeader;
import net.yolopago.pago.db.entity.TicketLayout;
import net.yolopago.pago.ws.dto.contract.ContractDto;
import net.yolopago.pago.ws.dto.contract.ContractTypeDto;
import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.contract.PreferenceDto;
import net.yolopago.pago.ws.dto.contract.TicketFooterDto;
import net.yolopago.pago.ws.dto.contract.TicketHeaderDto;
import net.yolopago.pago.ws.dto.contract.TicketLayoutDto;

public class BuilderContract {

	public Merchant build(MerchantDto merchantDto) {
		Merchant merchant = new Merchant();

		merchant.set_id(merchantDto.getId());

		merchant.setName(merchantDto.getName());
		merchant.setStreet(merchantDto.getStreet());
		merchant.setExternal(merchantDto.getExternal());
		merchant.setInternal(merchantDto.getInternal());
		merchant.setPhone(merchantDto.getPhone());
		merchant.setMail(merchantDto.getMail());
		merchant.setUrl(merchantDto.getUrl());
		merchant.setTaxid(merchantDto.getTaxid());

		return merchant;
	}

	public Contract build(ContractDto contractDto) {
		Contract contract = new Contract();

		contract.set_id(contractDto.getId());
		contract.setIdContractType(contractDto.getContractTypeDto().getId());
		contract.setName(contractDto.getName());

		return contract;
	}

	public ContractType build(ContractTypeDto contractTypeDto) {
		ContractType contractType = new ContractType();

		contractType.set_id(contractTypeDto.getId());
		contractType.setName(contractTypeDto.getName());

		return contractType;
	}

	public TicketHeader build(Long idTicketLayout, TicketHeaderDto ticketHeaderDto) {
		TicketHeader ticketHeader = new TicketHeader();

		ticketHeader.set_id(ticketHeader.get_id());
		ticketHeader.setLineType(ticketHeader.getLineType());
		ticketHeader.setContent(ticketHeader.getContent());
		ticketHeader.setBold(ticketHeader.getBold());
		ticketHeader.setFont(ticketHeader.getFont());
		ticketHeader.setFontSize(ticketHeader.getFontSize());
		ticketHeader.setIdTicketLayout(idTicketLayout);

		return ticketHeader;
	}

	public TicketFooter build(Long idTicketLayout, TicketFooterDto ticketFooterDto) {
		TicketFooter ticketFooter = new TicketFooter();

		ticketFooter.set_id(ticketFooter.get_id());
		ticketFooter.setLineType(ticketFooter.getLineType());
		ticketFooter.setContent(ticketFooter.getContent());
		ticketFooter.setBold(ticketFooter.getBold());
		ticketFooter.setFont(ticketFooter.getFont());
		ticketFooter.setFontSize(ticketFooter.getFontSize());
		ticketFooter.setIdTicketLayout(idTicketLayout);

		return ticketFooter;
	}

	public TicketLayout build(TicketLayoutDto ticketLayoutDto) {
		TicketLayout ticketLayout = new TicketLayout();

		ticketLayout.set_id(ticketLayoutDto.getId());

		return ticketLayout;
	}

	public Preference build(PreferenceDto preferenceDto) {
		Preference preference = new Preference();

		preference.set_id(preferenceDto.getId());
		preference.setName(preferenceDto.getName());
		preference.setValue(preferenceDto.getValue());

		return preference;
	}
}
