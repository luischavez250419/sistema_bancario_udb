/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

/**
 *
 * @author luis
 */
public class Cliente extends Persona{
    //Atributos
    private String codigoCliente;
    private String dui;
    private String estado;
    
    public Cliente(){
        super();
    }
    
    //Constructores
    public Cliente(String codigoCliente, String dui, String estado, 
            String nombreCompleto, String telefono, String email, String direccion){
        
        super(nombreCompleto, telefono, email, direccion);
        
        this.codigoCliente = codigoCliente;
        this.dui = dui;
        this.estado = estado;
    }
    
    //Getters & Setters

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
