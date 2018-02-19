package ModelLexico;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author allen
 */
public class Estrutura {

    private final List<Gramatica> gramaticas;
    private final List<Gramatica> delimitadores;
    private Pattern identificador;
    private Pattern numero;
    private Pattern digito;
    private Pattern letra;
    private Pattern opAritmetico;
    private Pattern opRelacional;
    private Pattern opLogico;
    private Pattern delimitador;
    private Pattern simbolo;
    private Pattern string;
    private Pattern espaco;

    public Estrutura() {
        this.gramaticas = new ArrayList<>();
        this.delimitadores = new ArrayList<>();
        this.estruturaLexica();
        this.addGramatica();
    }

    private void estruturaLexica() {

        this.digito = Pattern.compile("[0-9]");
        this.letra = Pattern.compile("[a-zA-Z]");
        this.identificador = Pattern.compile(this.letra + "[" + this.letra + this.digito + "_]*");
        this.numero = Pattern.compile("[-]?\\p{Space}*" + this.digito + "+(\\x2E" + this.digito + "+)?");
        this.opAritmetico = Pattern.compile("[+/*%-]");
        this.opRelacional = Pattern.compile("([!<>]=|<|=|>)");
        this.opLogico = Pattern.compile("!|&&|[|][|]");
        this.delimitador = Pattern.compile(";|,|:|\\x7B|\\x7D|\\x5B|\\x5D|\\x28|\\x29");
        this.simbolo = Pattern.compile("[\\x20-\\x7E&&[^\\x22]]");

        this.string = Pattern.compile("\"([" + this.letra + this.simbolo + this.digito + "]*(\\x5C\")*)*\"");
        this.espaco = Pattern.compile("\\x09|\\x0A|\\x0D|\\x20");

    }

    private void addGramatica() {
        this.gramaticas.add(new Gramatica(this.identificador, "IDENTIFICADOR"));
        this.gramaticas.add(new Gramatica(this.numero, "NUMERO"));
        this.gramaticas.add(new Gramatica(this.opAritmetico, "OP.ARITMETICO"));
        this.gramaticas.add(new Gramatica(this.opRelacional, "OP.RELACIONAL"));
        this.gramaticas.add(new Gramatica(this.opLogico, "OP.LOGICO"));
        this.gramaticas.add(new Gramatica(this.delimitador, "DELIMITADOR"));
        this.gramaticas.add(new Gramatica(this.string, "STRING"));

        this.delimitadores.add(new Gramatica(this.opRelacional, "OP.RELACIONAL"));
        this.delimitadores.add(new Gramatica(this.opLogico, "OP.LOGICO"));
        this.delimitadores.add(new Gramatica(this.opAritmetico, "OP.ARITMETICO"));
        this.delimitadores.add(new Gramatica(this.delimitador, "DELIMITADOR"));
        this.delimitadores.add(new Gramatica(this.espaco, "ESPACO"));
    }

    public boolean pertence(String expressao) {
        for (Gramatica gramatica : gramaticas) {
            if (gramatica.pertence(expressao)) {
                return true;
            }
        }
        return false;
    }

    public String tipo(String expressao) {
        for (Gramatica gramatica : gramaticas) {
            if (gramatica.pertence(expressao)) {
                return gramatica.toString();
            }
        }
        return null;
    }

    public boolean isDelimitador(String token) {
        for (Gramatica gramatica : delimitadores) {
            Matcher m = gramatica.getpattern().matcher(token);
            if (m.matches()) {
                return m.matches();
            }
        }
        return false;
    }

    public String TipoDelimitador(String token) {
        for (Gramatica gramatica : delimitadores) {
            Matcher m = gramatica.getpattern().matcher(token);
            if (m.matches()) {
                return gramatica.toString();
            }
        }
        return null;
    }

    public boolean isLetra(String expressao) {
        return letra.matcher(expressao).matches();
    }
    public boolean isDigito(String expressao) {
        return digito.matcher(expressao).matches();
    }
    public boolean isSimbolo(String expressao) {
        return simbolo.matcher(expressao).matches();
    }
    
    public boolean isEspaco(String expressao) {
        return espaco.matcher(expressao).matches();
    }
    
    
    public void isNumero(String expressao) {
        System.out.println(numero.matcher(expressao).matches()); 
    }
    
    public Token excecao(String primeiroCaracter, String palavra, int linha){
        if(primeiroCaracter.equals("\"")){
            return new Token("STRING MAL FORMADA", palavra, linha);
        } else if(digito.matcher(primeiroCaracter).matches()){
            return new Token("NUMERAL MAL FORMADO", palavra, linha);
        } else if(letra.matcher(primeiroCaracter).matches()){
            return new Token("IDENTIFICADOR MAL FORMADO", palavra, linha);
        } else if(primeiroCaracter.equals("-")){
            return new Token("NUMERAL MAL FORMADO", palavra, linha);
        } 
        else{
            return new Token("ERROR DE SIMBOLO", palavra, linha);
        }
        
            
    }
   //else if(simbolo.matcher(primeiroCaracter).matches() && !letra.matcher(primeiroCaracter).matches()) 
    
    
}
