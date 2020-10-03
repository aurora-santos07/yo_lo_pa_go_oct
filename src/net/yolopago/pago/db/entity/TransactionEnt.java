package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import net.yolopago.pago.ws.dto.payment.TransactionDto;


@Entity
public class TransactionEnt {

    @PrimaryKey
    private Long _id;
    private Double amount;
    private String paymentType;
    private Long idTicket;
    private String controlNumber;

    public TransactionEnt(){ }

    public TransactionEnt(TransactionDto dto){
        this.idTicket=dto.getIdTicket();
        this.amount=dto.getAmount();
        this.paymentType=dto.getPaymentType();
        this.controlNumber=dto.getControlNumber();
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }

    public String getControlNumber() {		return controlNumber;
    }

    public void setControlNumber(String controlNumber) {		this.controlNumber = controlNumber;	}

}
