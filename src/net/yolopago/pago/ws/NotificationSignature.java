package net.yolopago.pago.ws;

import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.product.ProductDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NotificationSignature {


	@FormUrlEncoded
	@POST("contract/mobil/contract/canTransac")
	Call<String> canTransac(@Field("model") String model,
								 @Field("serialNumber") String serialNumber);

	@FormUrlEncoded
	@POST("product/mobil/product/getProduct")
	Call<ProductDto> getProduct(@Header("Authorization") String credentials,
								@Field("idProduct") Long idProduct,
								@Field("access_token") String accessToken);

	@POST("contract/mobil/contract/getMerchant")
	Call<MerchantDto> getMerchant();

	@FormUrlEncoded
	@POST("contract/mobil/contract/getContractSesion")
	Call<PrincipalDto> getContractSesion(@Header("Authorization") String credentials,
										 @Field("terminal") String terminal);

	@POST("out")
	Call<String> contractLogOut();
}
