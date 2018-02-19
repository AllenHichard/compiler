/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelLexico;

/**
 *
 * @author allen
 */
public class Token {
    
    private final String nome;
    private final String lexema;
    private int linha;
    
    public Token(String nome, String lexema, int linha){
        this.nome = nome;
        this.lexema = lexema;
        this.linha = linha;
    }
    
    public void setLinha(int linha){
        this.linha = linha;
        
    }

    public String getNome() {
        return nome;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }
    
    
    
    @Override
    public String toString(){
        return this.linha + " - "+this.nome + " - " + this.lexema;
    }
}
