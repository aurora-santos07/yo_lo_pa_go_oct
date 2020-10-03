package net.yolopago.pago.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import net.yolopago.pago.repository.TicketRepository;
import net.yolopago.pago.ws.dto.security.PrincipalDto;
import net.yolopago.pago.ws.dto.ticket.PdfDto;
import net.yolopago.pago.ws.dto.ticket.TicketDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.util.List;

public class TicketViewModel extends AndroidViewModel {
    private MutableLiveData<String> msg;
    private MutableLiveData<String> errorMsg;
    private TicketRepository ticketRepository;
    private TicketDto ticketDto;
    private PdfDto pdfDto;

    public TicketViewModel(@NonNull Application application) {
        super(application);
        msg = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();

        ticketRepository = TicketRepository.getInstance(application);
        ticketRepository.setMsg(msg);
        ticketRepository.setErrorMsg(msg);
    }

    public LiveData<String> crearTicket(TicketDto ticketDto) {
        return ticketRepository.crearTicket(ticketDto);
    }

    public LiveData<String>guardarTicketPdf(PdfDto pdfDto){
        return ticketRepository.guardarTicketPdf(pdfDto);
    }

    public Long idTicket(Long iduser){
        return ticketRepository.getTicket(iduser);
    }

    public LiveData<List>verProductos(Long idTicket){
        return ticketRepository.getProductClient(idTicket);
    }

    public LiveData<String>addFileTicket(Long idTicket,String data){
        return ticketRepository.addFileTicket(idTicket, data);
    }

    public LiveData<TicketDto>getTicketById(Long idTicket){
        return ticketRepository.getTicketById(idTicket);
    }
    public LiveData<PrincipalDto>getTicketSession(String user, String pasword,String terminal){
        return ticketRepository.getTicketSession(user,  pasword, terminal);
    }
    public LiveData<String>logOut(){
        return ticketRepository.logOut();
    }
}
