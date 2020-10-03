package com.wizarpos.emvsample.activity;

import java.io.Serializable;

public class Product implements Serializable {
    public String producto, cantidad;
    Double precio, total;
    public Double sum;

    public Product() {

    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getSum() {
        return sum;
    }


    public Product(String producto, String cantidad, Double precio, Double total, Double sum){
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
        this.sum = sum;
    }
}
