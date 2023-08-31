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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author allen
 */
public class VariavelAtributo {

    private List globais;
    private List<Token> analisar;
    private List listaErros;
    private Token token;
    private String lexema;
    private String nomeToken;
    private int linha;
    private static List<TratamentoVariavel> variavel;

    public VariavelAtributo(List analisar, List<TratamentoVariavel> VariavelAtributo) {
        this.globais = new LinkedList();
        this.listaErros = new LinkedList();
        this.analisar = analisar;
        this.variavel = VariavelAtributo;
    }

    public boolean atualizar() {
        token = (Token) analisar.get(0);
        lexema = token.getLexema();
        nomeToken = token.getNome();
        linha = token.getLinha();
        if (lexema.equals("}") || lexema.equals(":")) {
            return false;
        }
        return true;
    }

    public List analisarGlobal() {
        
        while (atualizar()) {
            if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                    || lexema.equals("bool")) {
                analisar.remove(0);
                armazenarVariaveis(lexema, false);
            } else if (lexema.equals("final")) {
                analisar.remove(0);
                armazenarCosntantes(lexema, true);
                
            } else if(nomeToken.equals("IDENTIFICADOR")){
                Classe c = AnalisadorSemantico.verificarClasse(lexema);
                if(c==null){
                    String erro = "A classe " + lexema+" declarada na linha " + linha  + " Não existe";
                    listaErros.add(erro);
                    analisar.remove(0);
                    armazenarVariaveis(lexema, false);
                }
            }
        }
        return listaErros;
    }

    public void armazenarCosntantes(String tipo, boolean Final) {
        atualizar();
        String tipoVariavel = lexema;
        analisar.remove(0);
        atualizar();
        String nome = lexema;
        while (!lexema.equals(";")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            } else if (globais.contains(new TratamentoVariavel(null, lexema, false, 0))) {
                String erro = "Variável " + lexema + " declarada na linha " + linha
                        + " já foi declarada na linha " + contem();
                analisar.remove(0); //erro
                listaErros.add(erro);
                tratamentoConstante(tipoVariavel);
            } else {
                analisar.remove(0);
                atualizar();
                tratamentoConstante(tipoVariavel);
                globais.add(new TratamentoVariavel(tipoVariavel, nome, Final, linha));
            }
        }
        analisar.remove(0);
        atualizar();
    }

    public void tratamentoConstante(String tipoVariavel) {
        analisar.remove(0); // igual
        atualizar();
        if (booleanoOuAlgebrico()) {
            inicializacaoDireta(tipoVariavel);
        } else {
            inicializacaoBooleana(tipoVariavel);
        }

    }

    public boolean booleanoOuAlgebrico() {
        atualizar();
        int i = 0;
        while (!analisar.get(i).getNome().equals("OP.RELACIONAL") && !analisar.get(i).getLexema().equals(";")
                && !analisar.get(i).getLexema().equals(",")) {
            i++;
        }
        if (analisar.get(i).getLexema().equals("=") && analisar.get(i + 1).getLexema().equals("=")) {
            return false;
        }
        if (analisar.get(i).getNome().equals("OP.RELACIONAL") && !analisar.get(i).getLexema().equals("=")) {
            return false;
        } else {
            return true;
        }
    }

    public void inicializacaoBooleana(String tipoVariavel) {
        if (lexema.equals("&&") || lexema.equals("||") || lexema.equals("(") || lexema.equals(")")
                || lexema.equals("!")) {
            analisar.remove(0);
            atualizar();
            inicializacaoBooleana(tipoVariavel);
        } else if (!lexema.equals(";")) {
            if (!tipoVariavel.equals("bool")) {
                String erro = "Variavel esperada do tipo bool, excedida pelo tipo " + tipoVariavel;
                listaErros.add(erro);
            }
            String tipo1 = inicializacaoBooleana1(tipoVariavel);
            removerSinalComparacao();
            String tipo2 = inicializacaoBooleana1(tipoVariavel);
            if (!tipo1.equals(tipo2)) {
                String erro = "Não é possível comparar variavel do tipo:  " + tipo1
                        + " com variavel do tipo: " + tipo2 + " na linha " + linha;
                listaErros.add(erro);

            }
        }
    }

    public String inicializacaoBooleana1(String tipoVariavel) {
        //Classe c = AnalisadorSemantico.verificarClasse(lexema);
        CodigoLinha cl = new CodigoLinha(analisar, listaErros);
        if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals(":")) {
            Classe c = AnalisadorSemantico.verificarClasse(lexema);
            if (c != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // :
                analisar.remove(0); // : 
                atualizar();
                if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
                    Metodo m = c.verificarMetodo(lexema);
                    if (c.verificarMetodo(lexema) != null) {
                        analisar.remove(0); // identificador
                        analisar.remove(0); // (
                         cl.tratamentoParametro(lexema, linha, m.getPar());
                        analisar.remove(0); // )
                        atualizar();
                        return m.getTipo();
                    } else {
                        String erro = "O metodo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
                    }
                } else {
                    TratamentoVariavel a = c.verificarAtributo(lexema);
                    if (c.verificarAtributo(lexema) != null) {
                        analisar.remove(0); // identificador
                        atualizar();
                        return a.getTipo();
                    } else {
                        String erro = "O atributo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        analisar.remove(0); // identificador
                        atualizar();
                    }
                }
            } else {
                String erro = "A classe" + lexema + "não existe";
                listaErros.add(erro);
                while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
            Classe nova = AnalisadorSemantico.verificarClasse(TratamentoClasse.nomeClasseAtual);
            Metodo metodo = nova.verificarMetodo(lexema);
            CodigoLinha clh = new CodigoLinha(analisar, listaErros);
            if (nova.verificarMetodo(lexema) != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                clh.setM(metodo);
                clh.tratamentoParametro(metodo.getNome(), linha, metodo.getPar());
                analisar.remove(0); // )
                atualizar();
            } else {
                String erro = "O método " + lexema + " não existe na classe " + nova.getNome();
                listaErros.add(erro);
                while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
                
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, variavel);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
                return t.getTipo();
            } else {
                String erro = "Variável " + lexema + " Não declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (nomeToken.equals("NUMERO")) {
            if (atribuirNumeroInt(lexema)) {
                analisar.remove(0);
                atualizar();
                return "int";
            } else {
                analisar.remove(0);
                atualizar();
                return "float";
            }
        } else if (nomeToken.equals("STRING")) {

            analisar.remove(0);
            atualizar();
            return "string";

        } else if (lexema.equals("false") || lexema.equals("true")) {

            analisar.remove(0);
            atualizar();
            return "bool";
        }
        return null;
    }

    public void removerSinalComparacao() {
        if (lexema.equals("=") && analisar.get(1).getLexema().equals("=")) {
            analisar.remove(0);
            analisar.remove(0);
        } else {
            analisar.remove(0);
        }
        atualizar();
    }

    public void inicializacaoDireta(String tipoVariavel) {
        CodigoLinha cl = new CodigoLinha(analisar, listaErros);
        if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals(":")) {
            Classe c = AnalisadorSemantico.verificarClasse(lexema);
            if (c != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // :
                analisar.remove(0); // : 
                atualizar();
                if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
                    Metodo m = c.verificarMetodo(lexema);
                    if (c.verificarMetodo(lexema) != null) {
                        analisar.remove(0); // identificador
                        analisar.remove(0); // (
                        cl.tratamentoParametro(lexema, linha, m.getPar());
                        analisar.remove(0); // )
                        if (!m.getTipo().equals(tipoVariavel)) {
                            String erro = "Tentativa de relacionar metodo " + m.getNome() + " do tipo " + m.getTipo()
                                    + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                            listaErros.add(erro);
                        }
                        atualizar();
                    } else {
                        String erro = "O metodo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
                    }
                } else {
                    TratamentoVariavel a = c.verificarAtributo(lexema);
                    if (c.verificarAtributo(lexema) != null) {
                        analisar.remove(0); // identificador
                        atualizar();
                        if (!a.getTipo().equals(tipoVariavel)) {
                            String erro = "Tentatica de relacionar variavel " + a.getNome() + " do tipo " + a.getTipo()
                                    + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                            listaErros.add(erro);
                        }
                        atualizar();

                    } else {
                        String erro = "O atributo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        analisar.remove(0); // identificador
                        atualizar();
                    }
                }
            } else {
                String erro = "A classe " + lexema + " não existe";
                listaErros.add(erro);
                while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
            Classe nova = AnalisadorSemantico.verificarClasse(TratamentoClasse.nomeClasseAtual);
            Metodo metodo = nova.verificarMetodo(lexema);
            CodigoLinha clh = new CodigoLinha(analisar, listaErros);
            if (nova.verificarMetodo(lexema) != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                clh.setM(metodo);
                clh.tratamentoParametro(metodo.getNome(), linha, metodo.getPar());
                analisar.remove(0); // )
                atualizar();
            } else {
                String erro = "O método " + lexema + " não existe na classe " + nova.getNome();
                listaErros.add(erro);
                while(!lexema.equals(";")){
                            analisar.remove(0);
                            atualizar();
                        }
                        atualizar();
                
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, variavel);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                if (t.getTipo().equals(tipoVariavel)) {
                    analisar.remove(0);
                    atualizar();
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Variável " + lexema + " Não declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (nomeToken.equals("NUMERO")) {
            if (tipoVariavel.equals("int") && atribuirNumeroInt(lexema)) {
                analisar.remove(0);
                atualizar();
            } else if (tipoVariavel.equals("float") && !atribuirNumeroInt(lexema)) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Tentatica de relacionar variavel " + lexema + " do tipo " + nomeToken
                        + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (nomeToken.equals("STRING")) {
            if (tipoVariavel.equals("string")) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Tentatica de relacionar variavel " + lexema + " do tipo " + nomeToken
                        + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (lexema.equals("false") || lexema.equals("true")) {
            if (tipoVariavel.equals("bool")) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Tentatica de relacionar variavel " + lexema + " do tipo " + "booleano"
                        + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (!lexema.equals(";") && !lexema.equals(",")) {
            analisar.remove(0);
            atualizar();
            inicializacaoDireta(tipoVariavel);
        }

    }

    public TratamentoVariavel retornarTipoVariavel(String lex) {
        Iterator it = globais.iterator();
        while (it.hasNext()) {
            TratamentoVariavel t = (TratamentoVariavel) it.next();
            if (t.getNome().equals(lex)) {
                return t;
            }
        }
        return null;

    }

    /*
     public void InicializacaoAritmetica() {
     while (!lexema.equals(";") && !lexema.equals(",")) {
     if (nomeToken.equals("IDENTIFICADOR")) {
     if (globais.contains(new TratamentoVariavel(null, lexema, false, 0))) {
     analisar.remove(0);
     atualizar();
     } else if (!analisar.get(1).getLexema().equals("(") && !analisar.get(1).getLexema().equals(":")) {
     String erro = "Variável " + lexema + " Não declarada ";
     listaErros.add(erro);
     analisar.remove(0);
     atualizar();
     }
     }
     }
     }*/
    public void consumirAteVirgulaOuFinal() {
        while (!lexema.equals(";") && !lexema.equals(",")) {
            analisar.remove(0);
            atualizar();
        }
    }

    public void armazenarVariaveis(String tipo, boolean Final) {
        atualizar();
        while (!lexema.equals(";")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            } else if (globais.contains(new TratamentoVariavel(null, lexema, false, 0))) {
                String erro = "Variável " + lexema + " declarada na linha " + linha
                        + " já foi declarada na linha " + contem();
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            } else {
                globais.add(new TratamentoVariavel(tipo, lexema, Final, linha));
                analisar.remove(0);
                atualizar();
                tratamentoVetor();
            }
        }
        analisar.remove(0);
        atualizar();
    }

    public int contem() {
        Iterator it = globais.iterator();
        while (it.hasNext()) {
            TratamentoVariavel t = (TratamentoVariavel) it.next();
            if (t.getNome().equals(lexema)) {
                return t.getLinha();
            }
        }
        return 0;
    }

    private void tratamentoVetor() {
        atualizar();
        if (lexema.equals("[")) {
            analisar.remove(0);
            atualizar();
            if (isNumeroRegexp(lexema)) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Esperado um número inteiro maior que zero - Entrada lida " + lexema + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
            analisar.remove(0);
            atualizar();
            tratamentoMatriz();
        }

    }

    private void tratamentoMatriz() {
        if (lexema.equals("[")) {
            analisar.remove(0);
            atualizar();
            if (isNumeroRegexp(lexema)) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Esperado um número inteiro maior que zero - Entrada lida " + lexema + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
            analisar.remove(0);
            atualizar();
        }
    }

    public boolean isNumeroRegexp(String texto) {
        Pattern pat = Pattern.compile("[1-9][0-9]*");
        Matcher mat = pat.matcher(texto);
        return mat.matches();
    }

    public boolean atribuirNumeroInt(String texto) {
        Pattern pat = Pattern.compile("-?[0-9]+");
        Matcher mat = pat.matcher(texto);
        return mat.matches();
    }

    /*
     public static boolean isNumeroTry(String texto) {
     try {
     Integer.parseInt(texto);
     return true;
     } catch (NumberFormatException nfex) {
     return false;
     }
     }
     */
}
