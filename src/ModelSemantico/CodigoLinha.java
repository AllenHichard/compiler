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
public class CodigoLinha {

    private Classe c;
    private Metodo m;
    private List globais;
    private List<Token> analisar;
    private List listaErros;
    private Token token;
    private String lexema;
    private String nomeToken;
    private int linha;
    private String tipoParametro;
    private boolean consumoIf;

    public CodigoLinha(List analisar, List listaErros) {
        this.globais = new LinkedList();
        this.listaErros = listaErros;
        this.analisar = analisar;

    }
    
    public void setM(Metodo m){
        this.m = m;
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
                    || lexema.equals("bool") || nomeToken.equals("IDENTIFICADOR")) {
                analisar.remove(0);
                armazenarVariaveis(lexema, false);
            } else if (lexema.equals("final")) {
                analisar.remove(0);
                armazenarCosntantes(lexema, true);

            }
        }
        return listaErros;
    }

    public void consumirAtePontoVirgula() {
        while (!lexema.equals(";")) {
            analisar.remove(0);
            atualizar();
        }
    }

    public void consumirAteFechaParenteses() {
        while (!lexema.equals(")") && !analisar.get(1).getLexema().equals("{")) {
            analisar.remove(0);
            atualizar();
        }
    }
    
    public void inicializacaoFor(Classe classe, Metodo metodo) {
        c = classe;
        m = metodo;
        List globais = AnalisadorSemantico.getGlobal();
        atualizar();
        if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis globais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                
            } else {
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoVetorMatriz(t);
                tratamentoConstante(tipo);
                atualizar();
                
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis atributos";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                
            } else {

                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoVetorMatriz(t);
                tratamentoConstante(tipo);
                atualizar();
                
            }
        } else {
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis locais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                
            } else {
                
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoConstante(tipo);
                atualizar();
               
            }

        }
    }
    
    public String inicializacaoInstancia(Classe classe, Metodo metodo) {
        c = classe;
        m = metodo;
        List globais = AnalisadorSemantico.getGlobal();
        atualizar();
        if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis globais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                atualizar();
                return tipo;
                
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis atributos";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {

                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                atualizar();
                return tipo;
            }
        } else {

            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis locais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {

                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                atualizar();
                return tipo;
                
            }

        }
        return null;

    }
    
    private void tratamentoVetorMatriz(TratamentoVariavel T) {
        atualizar();
        
        if (T.isVetor()) {
            analisar.remove(0); // [
            atualizar();
            if(T.getValor1() <= Integer.parseInt(lexema)){
                String erro = "Valor do vetor excedido, valor de acesso " + lexema + " Valor maximo " + (T.getValor1() -1) + " na linha " + linha;
                listaErros.add(erro);
            }
            analisar.remove(0); // numero
            analisar.remove(0); // ]
        } else if(T.isMatriz()){
            analisar.remove(0); // [
            atualizar();
            if(T.getValor1() <= Integer.parseInt(lexema)){
                String erro = "Valor do vetor excedido, valor de acesso " + lexema + "Valor maximo " +  (T.getValor1() -1) + " na linha " + linha ;
                listaErros.add(erro);
            }
            analisar.remove(0); // numero
            analisar.remove(0); // ]
            analisar.remove(0); // [
            atualizar();
            if(T.getValor1() <= Integer.parseInt(lexema)){
                String erro = "Valor do vetor excedido, valor de acesso " + lexema + "Valor maximo " +  (T.getValor1() -1) + " na linha " + linha ;
                listaErros.add(erro);
            }
            analisar.remove(0); // numero
            analisar.remove(0); // ]
        
        }

    }

    public void inicializacaoLinha(Classe classe, Metodo metodo) {
        atualizar();
        
        c = classe;
        m = metodo;
        List globais = AnalisadorSemantico.getGlobal();
        atualizar();
        if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis globais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {
                if(t.isFinal()){
                    String erro = "a variavel " + lexema + " não pode ser alterada  - erro na linha" + linha;
                    listaErros.add(erro);
                }
                
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoVetorMatriz(t);
                tratamentoConstante(tipo);
                atualizar();
                analisar.remove(0); //;
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis atributos";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {
                if(t.isFinal()){
                    String erro = "a variavel " + lexema + " não pode ser alterada  - erro na linha" + linha;
                    listaErros.add(erro);
                }
                
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoVetorMatriz(t);
                tratamentoConstante(tipo);
                atualizar();
                analisar.remove(0); //;
            }
        } else {
            
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t == null) {
                String erro = "a variavel " + lexema + " não existe entre as variáveis locais";
                listaErros.add(erro);
                analisar.remove(0);//identificador
                atualizar();
                consumirAtePontoVirgula();
                atualizar();
                analisar.remove(0); //;
            } else {
                if(t.isFinal()){
                    String erro = "a variavel " + lexema + " não pode ser alterada  - erro na linha" + linha;
                    listaErros.add(erro);
                }
                
                String tipo = t.getTipo();
                analisar.remove(0);//identificador
                tratamentoVetorMatriz(t);
                tratamentoConstante(tipo);
                atualizar();
                analisar.remove(0); //;
            }

        }

    }

    public void setMetodoClasse(Classe classe, Metodo metodo, boolean consumoIf) {
        c = classe;
        m = metodo;
        this.consumoIf = consumoIf;
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

    public void tratamentoScan() {
        atualizar();

        while (!lexema.equals(")")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            }
            scanTipoVariavel();
        }
    }

    public void scanTipoVariavel() {
        if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            List globais = AnalisadorSemantico.getGlobal();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t != null) {
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Variável Global " + lexema + " na linha " + linha + " Não foi declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Variável atributo " + lexema + " na linha " + linha + " Não foi declarada";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
            } else {
                String erro = "Variável local " + lexema + " na linha " + linha + " Não foi declarada";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }

        }
    }
    
    public void consumirAteFechaParenteses2() {
        while (!lexema.equals(")") && !analisar.get(1).getLexema().equals(";")) {
            analisar.remove(0);
            atualizar();
        }
    }
    
    public void tratamentoParametro(String nomeM, int linhaM, List<Parametro> parametro) {
        
        atualizar();
        int i = 0;
        while (!lexema.equals(")")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            }
            if(i == parametro.size()){
                String erro = "Na chamada do método " + nomeM +" na linha " + linhaM + " possui parametros que nao existem";
                listaErros.add(erro);
                consumirAteFechaParenteses2();
                
                return;
            }
            
            if (booleanoOuAlgebrico()) {
                String tipo = parametro.get(i).getTipo();
                if (!lexema.equals(")")) {
                    inicializacaoDiretaPrint(tipo);
                    
                }
            } else {
                inicializacaoBooleana("bool");
            }
            i++;
            atualizar();
        }
        if(i < parametro.size()){
            String erro = "Na chamada do método " + nomeM +" na linha " + linhaM + " falta parametros para comparação";
            listaErros.add(erro);
        }
    }
    
    public String tipoParametro(int i, List<Parametro> parametro){
        return parametro.get(i).getTipo();
    }

    public void tratamentoPrint() {
        atualizar();
        while (!lexema.equals(")")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            }
            if (booleanoOuAlgebrico()) {

                String tipo = parametroPrint();
                atualizar();
                if (!lexema.equals(")")) {
                    inicializacaoDiretaPrint(tipo);
                }
            } else {
                inicializacaoBooleana("bool");
            }
            atualizar();
        }
    }

    public String parametroPrint() {

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
                        analisar.remove(0); // )
                        return m.getTipo();
                    }
                } else {
                    TratamentoVariavel a = c.verificarAtributo(lexema);
                    if (c.verificarAtributo(lexema) != null) {
                        analisar.remove(0); // identificador
                        atualizar();
                        return a.getTipo();

                    }
                }
            } else {
                String erro = "A classe" + lexema + "não existe";
                listaErros.add(erro);
                analisar.remove(0); // Identificador
                analisar.remove(0); // :
                analisar.remove(0); // :
                analisar.remove(0); // 
                atualizar();
                if (lexema.equals("(")) {
                    analisar.remove(0); // (
                    analisar.remove(0); // )
                    atualizar();
                }
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
            String erro = "Informar a qual classe pertence o método na linha " + linha;
            listaErros.add(erro);
            analisar.remove(0); // identificador
            analisar.remove(0); // (
            analisar.remove(0); // )
            atualizar();
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            List globais = AnalisadorSemantico.getGlobal();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                return t.getTipo();
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))

            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                return t.getTipo();
            }

        } else if (nomeToken.equals("IDENTIFICADOR")) {
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                return t.getTipo();
            }

        } else if (nomeToken.equals("NUMERO")) {
            if (atribuirNumeroInt(lexema)) {
                return "int";
            } else {
                return "float";
            }
        } else if (nomeToken.equals("STRING")) {
            return "string";
        } else if (lexema.equals("false") || lexema.equals("true")) {
            return "bool";

        }
        return null;
    }

    public void tratamentoRetorno(String tipoVariavel) {
        atualizar();

        while (!lexema.equals(";")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            }
            if (booleanoOuAlgebrico()) {
                inicializacaoDireta(tipoVariavel);
            } else {
                inicializacaoBooleana(tipoVariavel);
            }
        }
        atualizar();
        analisar.remove(0); // ;
        atualizar();
        if(!lexema.equals("}")){
            String erro = "Código invalido pós return da linha : " + linha;
            while(!lexema.equals("}")){
                analisar.remove(0); // ;
                atualizar();
            }
            erro += " ate a linha " + linha;
            listaErros.add(erro);
        }

    }
    
    public void buscarFechaPar(){
        for(int i = 0; i < analisar.size(); i++){
            if(analisar.get(i).getLexema().equals(")") && analisar.get(i+1).getLexema().equals("{")){
                analisar.add(i, new Token(lexema, ";", linha));
                return;
            }
        }
    }

    public void tratamentoConstante(String tipoVariavel) {
        analisar.remove(0); // igual
        atualizar();
        while (!lexema.equals(";")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            }
            if (booleanoOuAlgebrico()) {
                inicializacaoDireta(tipoVariavel);
            } else {
                inicializacaoBooleana(tipoVariavel);
            }
        }

    }

    public boolean booleanoOuAlgebrico() {
        atualizar();
        int i = 0;
        while (!analisar.get(i).getNome().equals("OP.RELACIONAL") && !analisar.get(i).getLexema().equals(";")) {
            i++;
        }
        if (analisar.get(i).getNome().equals("OP.RELACIONAL") && !analisar.get(i - 1).getLexema().equals("-")) {
            return false;
        } else {
            return true;
        }
    }

    public void inicializacaoBooleana(String tipoVariavel) {
        atualizar();
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
            if (tipo1 == null || tipo2 == null) {
                if (consumoIf) {
                    consumirAteFechaParenteses();
                }
                String erro = "Não é possível comparar variavel do tipo:  " + tipo1
                        + " com variavel do tipo: " + tipo2 + " na linha " + linha;
                listaErros.add(erro);
            } else if (!tipo1.equals(tipo2)) {
                String erro = "Não é possível comparar variavel do tipo:  " + tipo1
                        + " com variavel do tipo: " + tipo2 + " na linha " + linha;
                listaErros.add(erro);

            }
        }
    }

    public String inicializacaoBooleana1(String tipoVariavel) {
        //Classe c = AnalisadorSemantico.verificarClasse(lexema);
         
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
                        tratamentoParametro(m.getNome(), linha, m.getPar());
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
            Classe nova = AnalisadorSemantico.verificarClasse(c.getNome());
            Metodo metodo = nova.verificarMetodo(lexema);
          
            if (nova.verificarMetodo(lexema) != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                setM(metodo);
                tratamentoParametro(metodo.getNome(), linha, metodo.getPar());
                analisar.remove(0); // )
                atualizar();
            } else {
                String erro = "O método " + lexema + " não existe na classe " + nova.getNome();
                listaErros.add(erro);
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                consumirAteFechaParenteses();
                analisar.remove(0); // )
                atualizar();
            }
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            analisar.remove(0); // - 
            analisar.remove(0); // - 
            analisar.remove(0); // >
            atualizar();
            List globais = AnalisadorSemantico.getGlobal();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
                tratamentoVetorMatriz(t);
                return t.getTipo();
            } else {
                String erro = "Variável " + lexema + " Não declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            analisar.remove(0); // - 
            analisar.remove(0); // >
            atualizar();
            Classe verif = AnalisadorSemantico.verificarClasse(c.getNome());
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, verif.getAtributos());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
                tratamentoVetorMatriz(t);
                return t.getTipo();
            } else {
                String erro = "Variável " + lexema + " Não declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                analisar.remove(0);
                atualizar();
                tratamentoVetorMatriz(t);
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

    public void inicializacaoDiretaPrint(String tipoVariavel) {

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
                        analisar.remove(0); // identificador
                        analisar.remove(0); // (
                        analisar.remove(0); // )
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
                String erro = "A classe" + lexema + "não existe";
                listaErros.add(erro);
                analisar.remove(0); // Identificador
                analisar.remove(0); // :
                analisar.remove(0); // :
                analisar.remove(0); // 
                atualizar();
                if (lexema.equals("(")) {
                    analisar.remove(0); // (
                    analisar.remove(0); // )
                    atualizar();
                }
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
            String erro = "Informar a qual classe pertence o método na linha " + linha;
            listaErros.add(erro);
            analisar.remove(0); // identificador
            analisar.remove(0); // (
            analisar.remove(0); // )
            atualizar();
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            List globais = AnalisadorSemantico.getGlobal();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                if (t.getTipo().equals(tipoVariavel)) {
                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Variável " + lexema + " da linha " + linha + "Não foi declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))

            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);

                if (t.getTipo().equals(tipoVariavel)) {
                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Variável " + lexema + " da linha " + linha + " Não foi declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }

        } else if (nomeToken.equals("IDENTIFICADOR")) {
           
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);

                if (t.getTipo().equals(tipoVariavel)) {
                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Variável " + lexema + " da linha " + linha + " Não foi declarada ";
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
        }
        if (!lexema.equals(",") && !lexema.equals(")")) {
            analisar.remove(0);
            atualizar();
            inicializacaoDiretaPrint(tipoVariavel);
        }

    }
    
    
    

    public void inicializacaoDireta(String tipoVariavel) {
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
                        atualizar();
                        tratamentoParametro(m.getNome(), linha, m.getPar());
                        analisar.remove(0); // )
                        atualizar();
                        if (!m.getTipo().equals(tipoVariavel)) {
                            String erro = "Tentativa de relacionar metodo " + m.getNome() + " do tipo " + m.getTipo()
                                    + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                            listaErros.add(erro);
                        }
                        atualizar();
                    } else {
                        String erro = "O metodo " + lexema + " informado na Linha " + linha + " não existe na classe " + c.getNome();
                        listaErros.add(erro);
                        analisar.remove(0); // identificador
                        analisar.remove(0); // (
                        atualizar();
                        consumirAteFechaParenteses();
                        analisar.remove(0); // )
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
                String erro = "A classe" + lexema + "não existe";
                listaErros.add(erro);
                analisar.remove(0); // Identificador
                analisar.remove(0); // :
                analisar.remove(0); // :
                analisar.remove(0); // 
                atualizar();
                if (lexema.equals("(")) {
                    analisar.remove(0); // (
                    consumirAteFechaParenteses();
                    analisar.remove(0); // )
                    atualizar();
                }
            }
        } else if (nomeToken.equals("IDENTIFICADOR") && analisar.get(1).getLexema().equals("(")) {
  
            Classe nova = AnalisadorSemantico.verificarClasse(c.getNome());
            Metodo metodo = nova.verificarMetodo(lexema);
          
            if (nova.verificarMetodo(lexema) != null) {
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                setM(metodo);
                tratamentoParametro(metodo.getNome(), linha, metodo.getPar());
                analisar.remove(0); // )
                atualizar();
            } else {
                String erro = "O método " + lexema + " não existe na classe " + nova.getNome();
                listaErros.add(erro);
                analisar.remove(0); // identificador
                analisar.remove(0); // (
                consumirAteFechaParenteses();
                analisar.remove(0); // )
                atualizar();
            }
        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals("-")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))
            analisar.remove(0);//-
            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            List globais = AnalisadorSemantico.getGlobal();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, globais);
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);
                if (t.getTipo().equals(tipoVariavel)) {
                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Chamada Global - Variável " + lexema + " Não declarada";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }

        } else if (lexema.equals("-") && analisar.get(1).getLexema().equals(">")) {
            //globais.contains(new TratamentoVariavel(null, lexema, false, 0))

            analisar.remove(0);//-
            analisar.remove(0);//>
            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, c.getAtributos());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);

                if (t.getTipo().equals(tipoVariavel)) {

                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Chamada Atributo - Variável " + lexema + " Não declarada ";
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }

        } else if (nomeToken.equals("IDENTIFICADOR")) {

            atualizar();
            TratamentoVariavel t = AnalisadorSemantico.verificarGlobal(lexema, m.getVariaveisLocais());
            if (t != null) {
                //TratamentoVariavel t = retornarTipoVariavel(lexema);

                if (t.getTipo().equals(tipoVariavel)) {

                    analisar.remove(0);
                    atualizar();
                    tratamentoVetorMatriz(t);
                } else {
                    String erro = "Tentatica de relacionar variavel " + t.getNome() + " do tipo " + t.getTipo()
                            + " em variavel do tipo " + tipoVariavel + " na linha " + linha;
                    listaErros.add(erro);
                    analisar.remove(0);
                    atualizar();
                }
            } else {
                String erro = "Chamada Local - Variável " + lexema + " Não declarada ";
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
        } else if (!lexema.equals(";")) {
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

}
