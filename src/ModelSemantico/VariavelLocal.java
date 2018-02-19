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
public class VariavelLocal {

    private List locais;
    private List<Token> analisar;
    private List listaErros;
    private Token token;
    private String lexema;
    private String nomeToken;
    private int linha;

    public VariavelLocal(List analisar, List locais, List erros) {
        this.locais = locais;
        this.listaErros = erros;
        this.analisar = analisar;
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

    public List analisarGlobal(String tipo) {
        lexema = tipo;
        Classe c = AnalisadorSemantico.verificarClasse(lexema);
        atualizar();
        if (tipo() && c == null) {
            String erro = "A classe " + lexema + " declarada na linha " + linha + " Não existe";
            listaErros.add(erro);
        }

        analisar.remove(0);
        armazenarVariaveis(lexema, false);
        return listaErros;
    }

    public boolean tipo() {
        if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                || lexema.equals("bool")) {
            return false;
        } 
        return true;
    }
    public void armazenarVariaveis(String tipo, boolean Final) {
        atualizar();
        while (!lexema.equals(";")) {
            if (lexema.equals(",")) {
                analisar.remove(0);
                atualizar();
            } else if (locais.contains(new TratamentoVariavel(null, lexema, false, 0))) {
                String erro = "Variável " + lexema + " declarada na linha " + linha
                        + " já foi declarada na linha " + contem();
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            } else {
                TratamentoVariavel t = new TratamentoVariavel(tipo, lexema, Final, linha);
                locais.add(t);
                analisar.remove(0);
                atualizar();
                tratamentoVetor(t);
            }
        }
        analisar.remove(0);
        atualizar();
    }

    public int contem() {
        Iterator it = locais.iterator();
        while (it.hasNext()) {
            TratamentoVariavel t = (TratamentoVariavel) it.next();
            if (t.getNome().equals(lexema)) {
                return t.getLinha();
            }
        }
        return 0;
    }

    private void tratamentoVetor(TratamentoVariavel t) {
        atualizar();
        if (lexema.equals("[")) {
            analisar.remove(0);
            atualizar();
            if (isNumeroRegexp(lexema)) {
                t.chamada1(Integer.parseInt(lexema));
                analisar.remove(0);
                atualizar();
            } else {
                t.chamada1((int) Double.MAX_VALUE);
                String erro = "Esperado um número inteiro maior que zero - Entrada lida " + lexema + " na linha " + linha;
                listaErros.add(erro);
                analisar.remove(0);
                atualizar();
            }
            analisar.remove(0);
            atualizar();
            tratamentoMatriz( t );
        }

    }

    private void tratamentoMatriz(TratamentoVariavel t ) {
        if (lexema.equals("[")) {
            analisar.remove(0);
            atualizar();
            if (isNumeroRegexp(lexema)) {
                t.chamada2(Integer.parseInt(lexema));
                analisar.remove(0);
                atualizar();
            } else {
                t.chamada2((int) Double.MAX_VALUE);
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
