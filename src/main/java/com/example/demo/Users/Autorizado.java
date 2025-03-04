package com.example.demo.Users;

import com.example.demo.Pedidos.Pedido;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Autorizado {

    private Long idAutorizado;
    private String nombreCompleto;
    private String identificacion;
    private String telefono;
    private String ethereumAddress;

    @JsonIgnore // Evita que el objeto completo del pedido se serialice directamente
    private Pedido pedido;

    // Otros getters y setters

    public Long getIdAutorizado() {
        return idAutorizado;
    }

    public void setIdAutorizado(Long idAutorizado) {
        this.idAutorizado = idAutorizado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEthereumAddress() {
        return ethereumAddress;
    }

    public void setEthereumAddress(String ethereumAddress) {
        this.ethereumAddress = ethereumAddress;
    }

    @JsonProperty("pedido")
    public void setPedidoId(Long pedidoId) {
        if (pedidoId != null) {
            // Aquí puedes buscar el pedido por su ID y asignarlo al objeto 'pedido'
            Pedido pedido = new Pedido();  // Deberías buscar el pedido en la base de datos
            pedido.setId(pedidoId);
            this.pedido = pedido;
        }
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    @JsonGetter("pedido")
    public Long getPedidoId() {
        return (pedido != null) ? pedido.getId() : null;
    }
}
