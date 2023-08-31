/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSemantico;

import ModelLexico.Token;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author allen
 */
public class TratamentoClasse {

    public static String nomeClasseAtual;
    private String erro;
    private Metodo novo;
    private Classe nova;
    private List<Classe> Classes;
    private Token token;
    private String lexema;
    private String nomeToken;
    private int linha;
    private List<Token> tabelaSimbolos;
    private List listaErros;
    private VariavelAtributo atributo;

    public TratamentoClasse(List listaErros, List<Token> tabelaSimbolos) {
        this.tabelaSimbolos = tabelaSimbolos;
        this.listaErros = listaErros;
        this.Classes = new LinkedList<>();
    }

    public boolean atualizar() {
        if (!tabelaSimbolos.isEmpty()) {
            token = (Token) tabelaSimbolos.get(0);
            lexema = token.getLexema();
            nomeToken = token.getNome();
            linha = token.getLinha();
            return true;
        }
        return false;
    }

    public boolean atualizarDentroClasse() {
        if (!lexema.equals("}")) {
            token = (Token) tabelaSimbolos.get(0);
            lexema = token.getLexema();
            nomeToken = token.getNome();
            linha = token.getLinha();
            return true;
        }
        return false;
    }

    public boolean verificarClasse(String nomeClasse) {
        Iterator it = Classes.iterator();
        while (it.hasNext()) {
            Classe c = (Classe) it.next();
            if (c.getNome().equals(nomeClasse)) {
                return true;
            }
        }
        return false;
    }

    public void pularClassePorNomeIgual() {
        atualizar();

        while (!tabelaSimbolos.isEmpty() && !lexema.equals("class")) {
            tabelaSimbolos.remove(0);
            atualizar();
        }

    }

    public List analisarClasse() {

        String classe;
        while (atualizar()) {
            tabelaSimbolos.remove(0); //classe
            atualizar();
            if (!verificarClasse(lexema)) {
                nova = new Classe(lexema);
                classe = lexema;
                nomeClasseAtual = lexema;
                Classes.add(nova);
                tabelaSimbolos.remove(0); // remove nome d classe
                atualizar();
                if (lexema.equals("<")) {
                    tabelaSimbolos.remove(0); // remove <
                    tabelaSimbolos.remove(0); // remove -
                    tabelaSimbolos.remove(0); // remove >
                    atualizar();
                    Classe c = AnalisadorSemantico.verificarClasse(lexema);
                    if (c == null) {
                        erro = "A classe " + lexema + " na linha " + linha + " não existe";
                        listaErros.add(erro);
                    } else if (c.getNomeHeranca() != null) {
                        erro = "A classe " + c.getNome() + " na linha " + c.getLinhaHeranca() + " não pode ter herança";
                        listaErros.add(erro);
                    }
                    tabelaSimbolos.remove(0);
                }
                tabelaSimbolos.remove(0); //{
                atualizar();

                Classe verif = AnalisadorSemantico.verificarClasse(classe);
                this.atributo = new VariavelAtributo(tabelaSimbolos, verif.getAtributos());
                this.listaErros.addAll(this.atributo.analisarGlobal());
                atualizar();

                if (lexema.equals(":")) {
                    analisarMetodo();
                }
                tabelaSimbolos.remove(0); //}
                atualizar();

            } else {
                erro = "A classe " + lexema + " da linha " + linha + " já existe";
                listaErros.add(erro);
                pularClassePorNomeIgual();

            }

        }
        return listaErros;
    }

    private void analisarMetodo() {
        tabelaSimbolos.remove(0); // :
        tabelaSimbolos.remove(0); // :
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals("(")) {
            novo = new Metodo();
            novo.setTipo(null);
            novo.setNome(lexema);
            if (nova.verificarMetodo(lexema) == null) {
                nova.addMetodo(novo);
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                parametros();
                atualizar();
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // {
                codigoLinha();
                tabelaSimbolos.remove(0); // }
                atualizar();
                if (lexema.equals(":")) {
                    analisarMetodo();
                }
                atualizar();
            } else {
                erro = "O método " + lexema + "da linha" + linha + "já existe na classe" + nova.getNome();
                listaErros.add(erro);
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                //parametros();
                atualizar();
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // {
                codigoLinha();
                tabelaSimbolos.remove(0); // }
                atualizar();
                if (lexema.equals(":")) {
                    analisarMetodo();
                }
            }
        } else {

            novo = new Metodo();
            Classe c = AnalisadorSemantico.verificarClasse(lexema);
            atualizar();
            if (tipo2() && c == null) {
                String erro = "A classe " + lexema + " declarada na linha " + linha + " Não existe";
                listaErros.add(erro);
            }
            novo.setTipo(lexema);
            novo.setNome(tabelaSimbolos.get(1).getLexema());
            if (nova.verificarMetodo(novo.getNome()) == null) {
                nova.addMetodo(novo);
                tabelaSimbolos.remove(0); // tipo
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                parametros();
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // {
                codigoLinha();
                tabelaSimbolos.remove(0); // }
                atualizar();
                if (lexema.equals(":")) {
                    analisarMetodo();
                }
            } else {
                erro = "O método " + novo.getNome() + " da linha " + linha + " já existe na classe " + nova.getNome();
                listaErros.add(erro);
                tabelaSimbolos.remove(0); // tipo
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                parametros();
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // {
                codigoLinha();
                tabelaSimbolos.remove(0); // }
                if (lexema.equals(":")) {
                    analisarMetodo();
                }
            }
        }
    }

    public boolean tipo2() {
        if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                || lexema.equals("bool")) {
            return false;
        }
        return true;
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

    }

    private void codigoLinha() {
        //<for> |  
        //<Instancia> | <chamadaMetodospontovirgula> | |                   
        CodigoLinha CL = new CodigoLinha(tabelaSimbolos, listaErros);
        atualizar();
        
        if (lexema.equals("if")) {
            tabelaSimbolos.remove(0); // if
            tabelaSimbolos.remove(0); // (
            atualizar();
            CL.setMetodoClasse(nova, novo, true);
            CL.inicializacaoBooleana("bool");
            tabelaSimbolos.remove(0); // )
            tabelaSimbolos.remove(0); // {
            codigoLinha();
            tabelaSimbolos.remove(0); // }
            codigoLinha();
        } else if (lexema.equals("for")) {
            tabelaSimbolos.remove(0); // for
            tabelaSimbolos.remove(0); // (
            atualizar();
            CL.setMetodoClasse(nova, novo, true);
            CL.inicializacaoFor(nova, novo);
            tabelaSimbolos.remove(0); //;
            CL.inicializacaoBooleana("bool");
            tabelaSimbolos.remove(0); //;
            CL.buscarFechaPar();
            CL.inicializacaoFor(nova, novo);
            atualizar();
            tabelaSimbolos.remove(0); // ;
            tabelaSimbolos.remove(0); // )
            tabelaSimbolos.remove(0); // {
            codigoLinha();
            tabelaSimbolos.remove(0); // }
            atualizar();

            codigoLinha();

        } else if (lexema.equals("-")) {
            Classe verif = AnalisadorSemantico.verificarClasse(nova.getNome());
            this.atributo = new VariavelAtributo(tabelaSimbolos, verif.getAtributos());
            CL.inicializacaoLinha(verif, novo);
            codigoLinha();
        } else if (tipo()) {
            VariavelLocal VL = new VariavelLocal(tabelaSimbolos, novo.getVariaveisLocais(), listaErros);
            VL.analisarGlobal(lexema);
            atualizar();
            codigoLinha();
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getLexema().equals("=") && tabelaSimbolos.get(2).getLexema().equals(">")) {
            tratamendoInstancia(CL);
            codigoLinha();
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getLexema().equals(":")) {
            chamadaMetodo(CL);
            codigoLinha();
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getLexema().equals("(")) {
            chamadaMetodo(CL);
            codigoLinha();
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getLexema().equals("=")) {
            Classe verif = AnalisadorSemantico.verificarClasse(nova.getNome());
            this.atributo = new VariavelAtributo(tabelaSimbolos, verif.getAtributos());
            CL.inicializacaoLinha(verif, novo);
            atualizar();
            codigoLinha();
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getLexema().equals("[")) {
            Classe verif = AnalisadorSemantico.verificarClasse(nova.getNome());
            this.atributo = new VariavelAtributo(tabelaSimbolos, verif.getAtributos());
            CL.inicializacaoLinha(verif, novo);
            codigoLinha();
        }else if (lexema.equals("<")) {
            tabelaSimbolos.remove(0);
            tabelaSimbolos.remove(0);
            atualizar();
            CL.setMetodoClasse(nova, novo, false);
            if (novo.getTipo() != null) {
                CL.tratamentoRetorno(novo.getTipo());
            } else {

                String erro1 = "O metodo " + novo.getNome() + " da classe " + nova.getNome() + " Não pode ter retorno";
                listaErros.add(erro1);
                consumirAtePontoVirgula();
                tabelaSimbolos.remove(0); //;

            }
            codigoLinha();
        } else if (lexema.equals("print")) {
            tratamentoPrint(CL);
            codigoLinha();
        } else if (lexema.equals("scan")) {
            tratamentoScan(CL);
            codigoLinha();
        }
    }

    public void consumirAtePontoVirgula() {
        while (!lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            atualizar();
        }
    }

    public boolean tipo() {
        if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                || lexema.equals("bool")) {
            return true;
        } else if (nomeToken.equals("IDENTIFICADOR")
                && tabelaSimbolos.get(1).getNome().equals("IDENTIFICADOR")) {
            return true;
        }
        return false;
    }

    private void tratamentoPrint(CodigoLinha CL) {
        tabelaSimbolos.remove(0); // print
        tabelaSimbolos.remove(0); // (
        atualizar();
        CL.setMetodoClasse(nova, novo, false);
        CL.tratamentoPrint();
        tabelaSimbolos.remove(0); // )
        tabelaSimbolos.remove(0); // ;
        atualizar();
    }

    private void tratamentoScan(CodigoLinha CL) {
        tabelaSimbolos.remove(0); // print
        tabelaSimbolos.remove(0); // (
        atualizar();
        CL.setMetodoClasse(nova, novo, false);
        CL.tratamentoScan();

        tabelaSimbolos.remove(0); // )
        tabelaSimbolos.remove(0); // ;
        atualizar();
    }

    
    
    public void chamadaMetodo(CodigoLinha CL) {
         
        if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals(":")) {
            Classe c = AnalisadorSemantico.verificarClasse(lexema);

            if (c != null) {
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // :
                tabelaSimbolos.remove(0); // : 
                atualizar();

                if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals("(")) {
                    Metodo m = c.verificarMetodo(lexema);
                    if (c.verificarMetodo(lexema) != null) {
                        tabelaSimbolos.remove(0); // identificador
                        tabelaSimbolos.remove(0); // (
                        compararParametros(CL, m);
                        tabelaSimbolos.remove(0); // )
                        atualizar();
                    } else {
                        
                        String erro = "O metodo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        tabelaSimbolos.remove(0); // identificador
                        tabelaSimbolos.remove(0); // (
                        consumirAteFechaParenteses();
                        
                        tabelaSimbolos.remove(0); // )
                        atualizar();
                    }
                    tabelaSimbolos.remove(0); // ;
                } else {
                    TratamentoVariavel a = c.verificarAtributo(lexema);
                    if (a != null) {
                        tabelaSimbolos.remove(0); // identificador
                        atualizar();

                    } else {
                        String erro = "O atributo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        tabelaSimbolos.remove(0); // identificador
                        atualizar();
                    }
                    tabelaSimbolos.remove(0); // ;
                }
            } else {
                String erro = "A classe" + lexema + "não existe";
                listaErros.add(erro);
                tabelaSimbolos.remove(0); // Identificador
                tabelaSimbolos.remove(0); // :
                tabelaSimbolos.remove(0); // :
                tabelaSimbolos.remove(0); // 
                atualizar();
                if (lexema.equals("(")) {
                    tabelaSimbolos.remove(0); // (
                    consumirAteFechaParenteses();
                    tabelaSimbolos.remove(0); // )
                    atualizar();

                }
                tabelaSimbolos.remove(0); // ;
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getLexema().equals("(")) {
            Classe c = AnalisadorSemantico.verificarClasse(nova.getNome());
            Metodo m = c.verificarMetodo(lexema);
            if (c.verificarMetodo(lexema) != null) {
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                CL.setM(novo);
                compararParametros(CL,m);
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // ;
                atualizar();
            } else {
                erro = "O método " + lexema + " não existe na classe " + nova.getNome();
                listaErros.add(erro);
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                consumirAteFechaParenteses();
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // ;
            }
        }

    }
    
    public void compararParametros(CodigoLinha CL, Metodo m){
        
        atualizar();
        CL.tratamentoParametro(m.getNome(), linha, m.getPar());
    }

    private void tratamendoInstancia(CodigoLinha CL) {
        atualizar();
        String tipo = CL.inicializacaoInstancia(nova, novo);
        if (tipo != null) {
            tabelaSimbolos.remove(0); // =
            tabelaSimbolos.remove(0); // >
            atualizar();
            if (tipo.equals(lexema)) {
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // ;
            } else {
                erro = "Variavel do tipo " + tipo + "recebeu instancia incorreta do tipo " + lexema;
                listaErros.add(erro);
                tabelaSimbolos.remove(0); // identificador
                tabelaSimbolos.remove(0); // (
                tabelaSimbolos.remove(0); // )
                tabelaSimbolos.remove(0); // ;
            }
            atualizar();
        }
        atualizar();
    }

    public void consumirAteFechaParenteses() {
        while (!lexema.equals(")") && !tabelaSimbolos.get(1).getLexema().equals(";")) {
            tabelaSimbolos.remove(0);
            atualizar();
        }
    }
}
