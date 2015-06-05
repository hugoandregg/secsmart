package com.example.hugo.secsmart.dominio;

import java.io.Serializable;

/**
 * Created by hugo on 05/06/15.
 */
public class Porta implements Serializable{
    private String numero;

    private String estado;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
