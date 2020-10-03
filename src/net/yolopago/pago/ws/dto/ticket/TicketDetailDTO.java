package net.yolopago.pago.ws.dto.ticket;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TicketDetailDTO {
    private String descProduct;
    private Integer noItems;
    private Double price;


    public String getDescProduct(){
        return descProduct;
    }
    public void setDescProduct(String descProduct){
        this.descProduct = descProduct;
    }
    public Integer getNoItems(){
        return noItems;
    }
    public void setNoItems(Integer noItems){
        this.noItems = noItems;
    }
    public Double getPriceItem(){
        return price;
    }
    public void setPriceItem(Double price){
        this.price = price;
    }
}
