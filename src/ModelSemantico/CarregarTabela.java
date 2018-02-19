/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;

import ModelLexico.Token;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author allen
 */
public class CarregarTabela {

    private List<Token> tabelaSimbolos;
    private List<TratamentoVariavel> Globais;
    private List<Classe> Classes;
    private Classe nova;
    private Metodo novo;
    private Token token;
    private String lexema = "Inicio";
    private String nomeToken;
    private List erros;
    private int linha;
    private boolean main;

    public CarregarTabela(List<Token> tabelaSimbolos) {
        this.Classes = new LinkedList<>();
        this.Globais = new LinkedList<>();
        this.tabelaSimbolos = tabelaSimbolos;
        this.erros = new LinkedList();
        main = false;
        tabelaSimbolos.add(new Token("Fim", "Fim", linha));
        carregarGlobais();
        CarregarClasse();

    }

    public List getErros(){
        return erros;
    }
    
    public List getClasses() {
        return Classes;
    }

    public List getGlobal() {
        return Globais;
    }

    public boolean atualizar() {
        if (lexema.equals("Fim")) {
            return false;
        }
        token = (Token) tabelaSimbolos.get(0);
        lexema = token.getLexema();
        nomeToken = token.getNome();
        linha = token.getLinha();
        if (lexema.equals("class")) {
            return false;
        }
        return true;
    }

    private void carregarGlobais() {
        while (atualizar()) {
            if (lexema.equals("final")) {
                tabelaSimbolos.remove(0);
                atualizar();
                carregarConstante();
            } else {
                carregarVariavel();
            }
        }
    }

    private void carregarVariavel() {
        String tipo = lexema;
        tabelaSimbolos.remove(0);
        atualizar();
        while (!lexema.equals(";")) {
            Globais.add(new TratamentoVariavel(tipo, lexema, false, linha));
            //System.out.println(tipo + " " + lexema + " " + false + " " + linha); 
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                atualizar();
            }
        }
        tabelaSimbolos.remove(0);
        atualizar();
    }

    private void carregarConstante() {
        String tipo = lexema;
        tabelaSimbolos.remove(0);
        atualizar();
        while (!lexema.equals(";")) {
            Globais.add(new TratamentoVariavel(tipo, lexema, true, linha));
            //System.out.println(tipo + " " + lexema + " " + true + " " + linha); 
            while (!lexema.equals(";") && !lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                atualizar();
            }
            if (lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                atualizar();
            }
        }
        tabelaSimbolos.remove(0);
        atualizar();
    }

    public boolean prox() {
        if (lexema.equals("Fim")) {
            return false;
        }
        token = (Token) tabelaSimbolos.get(0);
        lexema = token.getLexema();
        nomeToken = token.getNome();
        linha = token.getLinha();
        if (lexema.equals(":") || lexema.equals("}")) {
            return false;
        }
        return true;
    }

    private void atualizarAtributo() {
        while (prox()) {
            if (lexema.equals("final")) {
                tabelaSimbolos.remove(0);
                prox();
                carregarConstanteAtributo();
            } else {
                carregarVariavelAtributo();
            }
        }
        if (lexema.equals("}")) {
            tabelaSimbolos.remove(0);
            atualizar();
            CarregarClasse();
        } else {
            atualizar();
            agregarMetodo();
        }
    }

    private void carregarVariavelAtributo() {
        String tipo = lexema;
        tabelaSimbolos.remove(0);
        prox();
        while (!lexema.equals(";")) {
            nova.addAtributo(new TratamentoVariavel(tipo, lexema, false, linha));
            //System.out.println(tipo + " " + lexema + " " + false + " " + linha); 
            tabelaSimbolos.remove(0);
            prox();
            if (lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                prox();
            }
        }
        tabelaSimbolos.remove(0);
        prox();
    }

    private void carregarConstanteAtributo() {
        String tipo = lexema;
        tabelaSimbolos.remove(0);
        prox();
        while (!lexema.equals(";")) {
            nova.addAtributo(new TratamentoVariavel(tipo, lexema, true, linha));
            //System.out.println(tipo + " " + lexema + " " + true + " " + linha); 
            while (!lexema.equals(";") && !lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                prox();
            }
            if (lexema.equals(",")) {
                tabelaSimbolos.remove(0);
                prox();
            }
        }
        tabelaSimbolos.remove(0);
        prox();
    }

    private void CarregarClasse() {
        prox();
        if (lexema.equals("class")) {
            tabelaSimbolos.remove(0);
            prox();
            nova = new Classe(lexema);
            Classes.add(nova);
            //System.out.println("Class " + lexema);
            tabelaSimbolos.remove(0);
            prox();
            if (lexema.equals("<")) {
                tabelaSimbolos.remove(0); //<
                tabelaSimbolos.remove(0); //-
                tabelaSimbolos.remove(0); //>
                prox();
                nova.setNomeHeranca(lexema);
                nova.setLinhaHeranca(linha);
                tabelaSimbolos.remove(0); // herança
                tabelaSimbolos.remove(0); // {
                prox();
                atualizarAtributo();
            } else {
                tabelaSimbolos.remove(0); // {
                prox();
                atualizarAtributo();
            }
        }
    }

    public boolean atualizarMetodo() {
        token = (Token) tabelaSimbolos.get(0);
        lexema = token.getLexema();
        nomeToken = token.getNome();
        linha = token.getLinha();
        if (lexema.equals("class") || lexema.equals(":")) {
            return false;
        }
        return true;
    }

    private void agregarMetodo() {
        tabelaSimbolos.remove(0); // :
        tabelaSimbolos.remove(0); // :
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals("(")) {
            
            novo = new Metodo();
            novo.setTipo(null);
            novo.setNome(lexema);
            nova.addMetodo(novo);
            //System.out.println("metodo " + lexema);
            tabelaSimbolos.remove(0); // identificador
            tabelaSimbolos.remove(0); // (
            parametros();
            atualizar();
        } else {
            if(lexema.equals("bool") && tabelaSimbolos.get(1).getLexema().equals("main")){
                if(main){
                    String erro = "erro ao declarar metodo main na linha " + linha + " já existe um metodo main"
                            + " e a linguagem só permite um";
                    erros.add(erro);
                }
                else if(main == false){
                    main = true;
                }
            }
            novo = new Metodo();
            novo.setTipo(lexema);
            novo.setNome(tabelaSimbolos.get(1).getLexema());
            nova.addMetodo(novo);
            tabelaSimbolos.remove(0); // tipo
            tabelaSimbolos.remove(0); // identificador
            tabelaSimbolos.remove(0); // (
            parametros();
            atualizar();
        }
    }

    private void parametros() {
        atualizar();
        String tipo;
        String nome;
        while (!lexema.equals(")")) {
            tipo = lexema;
            tabelaSimbolos.remove(0);
            atualizar();
            nome = lexema;
            tabelaSimbolos.remove(0);
            atualizar();
            novo.addParametro(new Parametro(tipo, nome));
        }
        tabelaSimbolos.remove(0);
        atualizar();
        while (!lexema.equals(":") && !lexema.equals("class") && !lexema.equals("Fim")) {
            conferir();
            tabelaSimbolos.remove(0);
            atualizar();
        }
        if (lexema.equals(":")) {
            agregarMetodo();
        } else if (lexema.equals("class")) {
            CarregarClasse();
        } else if (lexema.equals("Fim")) {
            //System.out.println("Fim");
            tabelaSimbolos.remove(0);
        }
    }
    

    public boolean conferir() {
        
        if(nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals(":")){
            tabelaSimbolos.remove(0);
            tabelaSimbolos.remove(0);
            return true;
        }
        return false;
    }
}
