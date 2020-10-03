package net.yolopago.pago.repository;

import android.os.AsyncTask;
import android.util.Log;

import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.ws.TicketSignature;
import net.yolopago.pago.ws.dto.ticket.TicketDto;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

class CreateTicketTask extends AsyncTask<Void, Void, Response<TicketDto>> {

    //Prevent leak
    private SessionDao sessionDao;
    private Retrofit retrofitTicket;
    private TicketDto ticket;

    public CreateTicketTask(SessionDao sessionDao, Retrofit retrofitTicket, TicketDto ticket) {
        this.sessionDao = sessionDao;
        this.retrofitTicket = retrofitTicket;
        this.ticket = ticket;
    }

    @Override
    protected Response<TicketDto> doInBackground(Void... params) {
        try {
           // Session session = sessionDao.getFirst();
            TicketSignature ticketSignature = retrofitTicket.create(TicketSignature.class);
            Call<TicketDto> cString = ticketSignature.crear(ticket);//TODO add session idMerchat , idUser
            return  cString.execute();
        } catch (Exception e) {
            Log.e("Error inesperado", "fallo background", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<TicketDto> responseVerification) {
        try {
            if (responseVerification.isSuccessful()) {
                //Log.i("Resultado exitoso", responseVerification.toString());
            } else {
                Log.e("Error en la petición", responseVerification.toString());
            }
        } catch (Exception e) {
            Log.e("Error inesperado", "fallo ", e);
        }
    }
}