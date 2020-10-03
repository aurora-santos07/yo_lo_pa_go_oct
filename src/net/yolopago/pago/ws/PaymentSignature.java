package net.yolopago.pago.ws;

import net.yolopago.pago.ws.dto.payment.PaymentDto;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.payment.VoucherDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentSignature {



	@FormUrlEncoded
	@POST("payment/mobil/payment/compraTx")
	Call<String> compraTx(@Field("transactionDto") String json);

    @FormUrlEncoded
    @POST("payment/mobil/payment/solicitaLLave")
    Call<String> solicitaLLave( @Field("passkey") String passkey,
                          @Field("numeroSerie") String numeroSerie,
                          @Field("crc32") String crc32);

	@FormUrlEncoded
	@POST("payment/mobil/payment/cancelacion")
	Call<PaymentDto> cancelacion(@Field("idPayment") Long idPayment);

	@FormUrlEncoded //VEREFY
	@POST("payment/mobil/payment/getTicketPayment")
	Call<List<PaymentDto>> getTicketPayment(@Field("idpayment") Long idpayment);

	@FormUrlEncoded
	@POST("payment/mobil/payment/getPaymentByIdTicket")
	Call<List<PaymentDto>> getPaymentByIdTicket(@Field("idTicket") Long idTicket);
	@FormUrlEncoded
	@POST("payment/mobil/payment/getReverseByIdTicket")
	Call<PaymentDto> getReverseByIdTicket(@Field("idTicket") Long idTicket);

	@FormUrlEncoded
	@POST("payment/mobil/tx/findPageByMerchant")
	Call<List<TxDto>> findPageByMerchant(@Field("firstDate") String firstDate, @Field("lastDate") String lastDate,
												   @Field("page") Long page, @Field("size") Integer size);

	@FormUrlEncoded
	@POST("payment/mobil/tx/findPageReturns")
	Call<List<TxDto>> findPageReturns(@Header("Authorization") String credentials,
									  @Field("idPayment") Long idPayment,
									  @Field("page") Long page, @Field("size") Integer size,
									  @Field("access_token") String accessToken);


	@FormUrlEncoded
	@POST("payment/mobil/payment/addFilePayment")
	Call<String> addFilePayment(@Field("idPayment") Long idPayment,
							   @Field("data") String data);
	@FormUrlEncoded //VLP
	@POST("payment/mobil/payment/getPaymentSesion")
	Call<PrincipalDto> getPaymentSesion(@Header("Authorization") String credentials, @Field("terminal") String terminal);

	@POST("out")
	Call<String> paymentLogOut();
}
