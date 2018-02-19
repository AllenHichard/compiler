/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author allen
 */
public class Metodo {
    
    private String tipo;
    private String nome;
    private List<Parametro> par;
    private List<TratamentoVariavel> variaveisLocais;

    public Metodo(){
        this.par = new LinkedList<>();
        variaveisLocais = new  LinkedList<>();
    }

    public List<Parametro> getPar() {
        return par;
    }

    public List<TratamentoVariavel> getVariaveisLocais() {
        return variaveisLocais;
    }

    
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public void addParametro(Parametro p){
        this.par.add(p);
    }
    
    @Override
    public String toString(){
        if(tipo != null){
            return "Tipo " + tipo + " Nome do método " + nome;
        } else {
            return "Sem tipo - Nome do método " + nome;
        }
    }
}
