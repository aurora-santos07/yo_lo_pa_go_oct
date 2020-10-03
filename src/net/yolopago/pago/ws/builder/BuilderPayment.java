package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.Payment;
import net.yolopago.pago.db.entity.Voucher;
import net.yolopago.pago.ws.dto.payment.PaymentDto;
import net.yolopago.pago.ws.dto.payment.VoucherDto;
import java.net.URLDecoder;

import java.io.UnsupportedEncodingException;
import java.util.UnknownFormatConversionException;

public class BuilderPayment {

	public Payment build(PaymentDto paymentDto) {
		Payment payment = new Payment();

		payment.set_id(paymentDto.getId());
		payment.setPaymentStatus(paymentDto.getPaymentStatus());
		payment.setPaymentType(paymentDto.getPaymentType());
		if(paymentDto.getCapturedDate()!=null){ payment.setCapturedDate(paymentDto.getCapturedDate().getTime());}
		if(paymentDto.getPayedDate()!=null){ payment.setPayedDate(paymentDto.getPayedDate().getTime());}
		payment.setAmount(paymentDto.getAmount());
		payment.setMonths(paymentDto.getMonths());
		payment.setEmvTags(paymentDto.getEmvTags());
		payment.setRetries(paymentDto.getRetries());
		payment.setIdTicket(paymentDto.getIdTicket());
		payment.setTicketAmount(paymentDto.getTicketAmount());
		payment.setSignature(paymentDto.getSignature());
		payment.setIdMerchant(paymentDto.getMerchantDto().getId());
		payment.setIdTerminal(paymentDto.getTerminalDto().getId());
		if (paymentDto.getIssuerDto() != null) {
			payment.setIdIssuer(paymentDto.getIssuerDto().getId());
		}
		payment.setIdSeller(paymentDto.getSellerDto().getId());
		if (paymentDto.getCustomerDto() != null) {
			payment.setIdCustomer(paymentDto.getCustomerDto().getId());
		}

		payment.setReference(paymentDto.getReference());
		payment.setResultAcquirer(paymentDto.getResultAcquirer());
		payment.setResultAuthorizer(paymentDto.getResultAuthorizer());
		payment.setCodeAcquirer(paymentDto.getCodeAcquirer());
		if(paymentDto.getCodeAuthorizer()!=null){
			payment.setCodeAuthorizer(paymentDto.getCodeAuthorizer());
		}


		if(paymentDto.getInputAcquirer()!=null) {payment.setInputAcquirer(paymentDto.getInputAcquirer().getTime());}
		if(paymentDto.getInputProsa()!=null) {
			payment.setInputProsa(paymentDto.getInputProsa().getTime());
		}
		if(paymentDto.getOutputAcquirer()!=null) {payment.setOutputAcquirer(paymentDto.getOutputAcquirer().getTime());}
		if(paymentDto.getOutputProsa()!=null) {payment.setOutputProsa(paymentDto.getOutputProsa().getTime());}

		if(paymentDto.getIssuingBank()!=null) {
			payment.setIssuingBank(paymentDto.getIssuingBank());
		}

		if(paymentDto.getReason()!=null){
			try {
				payment.setReason(URLDecoder.decode(paymentDto.getReason(), "UTF-8"));
			}catch(UnsupportedEncodingException e){}
		}
		if(paymentDto.getCardBrand()!=null){
			payment.setCardBrand(paymentDto.getCardBrand());
		}
		if(paymentDto.getCardType()!=null){
			payment.setCardType(paymentDto.getCardType());
		}

		payment.setDatosEmv(paymentDto.getDatosEmv());



		return payment;
	}

	public Voucher build(VoucherDto voucherDto) {
		Voucher payment = new Voucher();

		payment.set_id(voucherDto.getId());
		payment.setIdPayment(voucherDto.getPaymentDto().getId());
		payment.setVoucherStatus(voucherDto.getVoucherStatus());
		payment.setEmvTags(voucherDto.getEmvTags());

		return payment;
	}
}
