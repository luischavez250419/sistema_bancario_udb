/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

/**
 *
 * @author luis
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cuenta_bancaria {
    //Atributos
    private String numeroCuenta;
    private BigDecimal saldo;
    private String tipoCuenta;
    private LocalDateTime fechaHoraApertura;
    private String estado;
    private Cliente cliente;
    
    //Constructores
    public Cuenta_bancaria(){
    
    }
    
    public Cuenta_bancaria(String numeroCuenta, BigDecimal saldo, String tipoCuenta,
            LocalDateTime fechaHoraApertura, String estado, Cliente cliente){
        
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
        this.fechaHoraApertura = fechaHoraApertura;
        this.estado = estado;
        this.cliente = cliente;
    }
    
    //Getters & Setters

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public LocalDateTime getFechaHoraApertura() {
        return fechaHoraApertura;
    }

    public void setFechaHoraApertura(LocalDateTime fechaHoraApertura) {
        this.fechaHoraApertura = fechaHoraApertura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
}
