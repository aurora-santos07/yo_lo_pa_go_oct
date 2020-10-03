package net.yolopago.pago.ws.dto.ticket;

import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.ws.dto.payment.UserDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TicketDto implements Serializable {
    private Long id;
    private Long idMerchant;
    private String customer;
    private Long idUser;
    private TicketStatus ticketStatus;
    private List<TicketLineDto> ticketLines;

    private String ticketFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(Long idMerchant) {
        this.idMerchant = idMerchant;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public List<TicketLineDto> getTicketLines() {
        return ticketLines;
    }

    public void setTicketLines(List<TicketLineDto> ticketLines) {
        this.ticketLines = ticketLines;
    }

    public String getTicketFile() {       return ticketFile;    }

    public void setTicketFile(String ticket_file) {        this.ticketFile = ticketFile;    }
}
