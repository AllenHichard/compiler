/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;

import ModelLexico.AnalisadorLexico;
import ModelLexico.Token;
import ModelSintatico.AnalisadorSintatico;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author allen
 */
public class AnalisadorSemantico {
    
    private static List<TratamentoVariavel> VariavelGlobal;
    private static List<Classe> classes;
    private ArquivoSemantico arquivo;
    private List<Token> tabelaSimbolos;
    private List<Token> tabelaSimbolos2;
    private VariavelGlobal global;
    private List listaErros;
    private TratamentoClasse tClasse;
    
    public AnalisadorSemantico() throws FileNotFoundException, IOException{
        this.arquivo = new ArquivoSemantico();
        this.listaErros = new LinkedList();
        while (arquivo.proxArquivo()) {
            tabelaSimbolos = arquivo.carregarProxTabela();
            tabelaSimbolos2 = new LinkedList<>();
            tabelaSimbolos2.addAll(tabelaSimbolos);
            CarregarTabela Ct = new CarregarTabela(tabelaSimbolos2);
            classes = Ct.getClasses();
            VariavelGlobal = Ct.getGlobal();
            this.global = new VariavelGlobal(tabelaSimbolos, VariavelGlobal);
            this.listaErros = this.global.analisarGlobal();
            this.tClasse = new TratamentoClasse(listaErros, tabelaSimbolos);
            this.listaErros = tClasse.analisarClasse();
            listaErros.addAll(Ct.getErros());
            arquivo.SalvarArquivo(listaErros, listaErros.isEmpty());
        }
    }
    
    public static List getGlobal(){
        return VariavelGlobal;
    }
    
    public static TratamentoVariavel verificarGlobal(String nomeV, List verificacao){
           Iterator it = verificacao.iterator();
           while(it.hasNext()){
               TratamentoVariavel c = (TratamentoVariavel) it.next();  
               if(c.getNome().equals(nomeV)){
                   return c;
               }
           }
           return null;
    }
    
    public static Classe verificarClasse(String nomeClasse){
           Iterator it = classes.iterator();
           while(it.hasNext()){
               Classe c = (Classe) it.next();
               if(c.getNome().equals(nomeClasse)){
                   return c;
               }
           }
           return null;
    }
    
    
    
    public void visualizarMetodosClasse(){
        for(int i = 0; i < classes.size(); i++){
            Classe c = classes.get(i);
            System.out.println(c.toString());
            c.listarMetodos();
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new AnalisadorLexico().analisador();
        AnalisadorSemantico semantico = new AnalisadorSemantico();
        //semantico.visualizarMetodosClasse();
    }
    
    
    
}

