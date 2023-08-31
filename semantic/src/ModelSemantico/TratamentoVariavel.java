/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;

import java.util.Objects;

/**
 *
 * @author allen
 */
public class TratamentoVariavel {
    
    private String tipo;
    private String nome;
    private boolean Final;
    private int linha;
    private boolean vetor;
    private int valor1;
    private boolean matriz;
    private int valor2;

   public TratamentoVariavel(String tipo, String nome, boolean Final, int linha) {
        this.tipo = tipo;
        this.nome = nome;
        this.Final = Final;
        this.linha = linha;
    }
   
    public void chamada1(int valor1){
        this.valor1 = valor1;
        vetor = true;
        matriz = false;
    }
    
    public void chamada2(int valor2){
        this.valor2 = valor2;
        vetor = false;
        matriz = true;
    }

    public boolean isVetor() {
        return vetor;
    }

    public void setVetor(boolean vetor) {
        this.vetor = vetor;
    }

    public int getValor1() {
        return valor1;
    }

    public void setValor1(int valor1) {
        this.valor1 = valor1;
    }

    public boolean isMatriz() {
        return matriz;
    }

    public void setMatriz(boolean matriz) {
        this.matriz = matriz;
    }

    public int getValor2() {
        return valor2;
    }

    public void setValor2(int valor2) {
        this.valor2 = valor2;
    }
    
    public int getLinha() {
        return linha;
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

    public boolean isFinal() {
        return Final;
    }

    public void setFinal(boolean Final) {
        this.Final = Final;
    }

    @Override
    public boolean equals(Object obj) {
        TratamentoVariavel t = (TratamentoVariavel) obj;
        return this.nome.equals(t.getNome());
    }
   
}

