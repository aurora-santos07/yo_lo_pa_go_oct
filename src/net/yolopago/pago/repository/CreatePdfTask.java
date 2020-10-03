package net.yolopago.pago.repository;

import android.os.AsyncTask;
import android.util.Log;

import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.ws.TicketSignature;
import net.yolopago.pago.ws.dto.ticket.PdfDto;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatePdfTask extends AsyncTask<Void, Void, Response<PdfDto>> {
    private SessionDao sessionDao;
    private Retrofit retrofitTicket;
    private PdfDto pdf;

    public CreatePdfTask(SessionDao sessionDao, Retrofit retrofitTicket, PdfDto pdf){
        this.sessionDao = sessionDao;
        this.retrofitTicket = retrofitTicket;
        this.pdf = pdf;
    }

    @Override
    protected Response<PdfDto> doInBackground(Void... params) {

        return null;
    }

    @Override
    protected void onPostExecute(Response<PdfDto> responseVerification) {
        try {
            if (responseVerification.isSuccessful()) {
                //Log.i("Resultado exitoso PDF", responseVerification.toString());
            } else {
                Log.e("Error en la petición", responseVerification.toString());
            }
        } catch (Exception e) {
            Log.e("Error inesperado", "pdf", e);
        }
    }
}
