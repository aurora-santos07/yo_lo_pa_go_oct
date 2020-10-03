package net.yolopago.pago.ws;

import net.yolopago.pago.ws.dto.security.PrincipalDto;
import net.yolopago.pago.ws.dto.ticket.PdfDto;
import net.yolopago.pago.ws.dto.ticket.TicketDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TicketSignature {

    @POST("/ticket/mobil/ticket")
    Call<TicketDto> crear(@Body TicketDto json);


    @FormUrlEncoded
    @POST("/ticket/mobil/ticket/addFileTicket")
    Call<String> addFileTicket( @Field("idTicket") Long idTicket,
                         @Field("data") String data);

    @FormUrlEncoded //VYLP
    @POST("/ticket/mobil/ticket/getTicketById")
    Call<TicketDto> getTicketById(@Field("idTicket") Long idTicket);

    @FormUrlEncoded //VYLP
    @POST("/ticket/mobil/ticket/getProductForTicket")
    Call<List<TicketLineDto>>getProductForTicket(  @Field("idTicket") Long idTicket);

    //VYLP
    @POST("/ticket/mobil/ticket/getTicket")
    Call<List<TicketDto>> getTicket();

    @FormUrlEncoded //VYLP
    @POST("/ticket/mobil/ticket/getTicketSesion")
    Call<PrincipalDto> getTicketSesion(@Header("Authorization") String credentials, @Field("terminal") String terminal);

    @POST("out")
    Call<String> tiketLogOut();

}
