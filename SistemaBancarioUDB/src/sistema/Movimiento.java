package sistema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un depósito, retiro o transferencia bancaria.
 * Para depósitos, idCuentaOrigen debe ser null
 * Para retiros, idCuentaDestino debe ser null.
 * Para transferencias, se registra una sola fila con ambas cuentas.
 */
public class Movimiento {

    public enum TipoMovimiento {
        Deposito,
        Retiro,
        Transferencia
    }

    private Integer idMovimiento;
    private TipoMovimiento tipoMovimiento;
    private Integer idCuentaOrigen;
    private Integer idCuentaDestino;
    private BigDecimal monto;
    private LocalDateTime fechaHora;
    private String descripcion;

    public Movimiento() {
    }

    public Movimiento(TipoMovimiento tipoMovimiento,
                      Integer idCuentaOrigen,
                      Integer idCuentaDestino,
                      BigDecimal monto,
                      String descripcion) {
        this.tipoMovimiento = tipoMovimiento;
        this.idCuentaOrigen = idCuentaOrigen;
        this.idCuentaDestino = idCuentaDestino;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public Movimiento(Integer idMovimiento,
                      TipoMovimiento tipoMovimiento,
                      Integer idCuentaOrigen,
                      Integer idCuentaDestino,
                      BigDecimal monto,
                      LocalDateTime fechaHora,
                      String descripcion) {
        this.idMovimiento = idMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.idCuentaOrigen = idCuentaOrigen;
        this.idCuentaDestino = idCuentaDestino;
        this.monto = monto;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getIdCuentaOrigen() {
        return idCuentaOrigen;
    }

    public void setIdCuentaOrigen(Integer idCuentaOrigen) {
        this.idCuentaOrigen = idCuentaOrigen;
    }

    public Integer getIdCuentaDestino() {
        return idCuentaDestino;
    }

    public void setIdCuentaDestino(Integer idCuentaDestino) {
        this.idCuentaDestino = idCuentaDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
