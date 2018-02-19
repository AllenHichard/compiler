/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author allen
 */
public class Classe {
    private String nome;
    private List<Metodo> metodos;
    private List<TratamentoVariavel> atributos;
    private String nomeHeranca;
    private int LinhaHeranca;
    
    
    public Classe(String nome){
        this.nome = nome;
        this.metodos = new LinkedList<>();
        this.atributos = new LinkedList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getLinhaHeranca() {
        return LinhaHeranca;
    }

    public void setLinhaHeranca(int LinhaHeranca) {
        this.LinhaHeranca = LinhaHeranca;
    }
    
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getNomeHeranca() {
        return nomeHeranca;
    }

    public void setNomeHeranca(String nomeHeranca) {
        this.nomeHeranca = nomeHeranca;
    }
    
    public void heranca(String nomeHeranca){
        this.nomeHeranca = nomeHeranca;
    }
    
    public void addMetodo(Metodo m){
        this.metodos.add(m);
    }
    
    public void addAtributo(TratamentoVariavel a){
        this.atributos.add(a);
    }
    
    public TratamentoVariavel verificarAtributo(String nomeAtributo){
           Iterator it = atributos.iterator();
           while(it.hasNext()){
               TratamentoVariavel a = (TratamentoVariavel) it.next();
               if(a.getNome().equals(nomeAtributo)){
                   return a;
               }
           }
           if(nomeHeranca!=null){
               Classe c = AnalisadorSemantico.verificarClasse(nomeHeranca);
               return c.verificarAtributo(nomeAtributo);
           }
           return null;
    }

    public List<TratamentoVariavel> getAtributos() {
        return atributos;
    }
    
    public Metodo verificarMetodo(String nomeMetodo){
           Iterator it = metodos.iterator();
           while(it.hasNext()){
               Metodo m = (Metodo) it.next();
               if(m.getNome().equals(nomeMetodo)){
                   return m;
               }
           }
           if(nomeHeranca!=null){
               Classe c = AnalisadorSemantico.verificarClasse(nomeHeranca);
               return c.verificarMetodo(nomeMetodo);
           }
           return null;
    }
    
    public void listarMetodos(){
        for(int i = 0; i < metodos.size(); i++ ){
            System.out.println(metodos.get(i).toString());
        }
    }
    
    @Override
    public String toString(){
        if(nomeHeranca != null){
            return "Nome da Classe " + nome + " Nome da herança " + nomeHeranca;
        } else {
            return "Nome da Classe " + nome;
        }
    }
}
