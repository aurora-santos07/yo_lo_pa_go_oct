package net.yolopago.pago.ws.dto.ticket;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TicketLineDto{
    private String product;
    private Double price;
    private Integer items;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getItems() {
        return items;
    }

    public void setItems(Integer items) {
        this.items = items;
    }

    public TicketLineDto(String product, Double price, Integer items){
        this.product = product;
        this.price = price;
        this.items = items;
    }
}