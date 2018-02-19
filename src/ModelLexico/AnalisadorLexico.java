/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelLexico;

import ModelLexico.ArquivoLexico;
import ModelLexico.Estrutura;
import ModelLexico.Reservada;
import ModelLexico.Token;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author allen
 */
public class AnalisadorLexico {

    private final ArquivoLexico algoritmo;
    private final Estrutura estrutura;
    private final Reservada reservada;
    private List<Token> tabelaSimbolos;
    private int linhaAtual;
    private boolean erro;

    public AnalisadorLexico() throws FileNotFoundException {
        this.algoritmo = new ArquivoLexico();
        this.estrutura = new Estrutura();
        this.reservada = new Reservada();
        this.tabelaSimbolos = new ArrayList<>();
    }

    public void analisador() throws IOException {
        while (algoritmo.proxArquivo()) {
            this.erro = false;
            linhaAtual = 0;
            String linha = algoritmo.proxLinha();
            while (linha != null) {
                this.linhaAtual++;
                separaTokens(linha);
                linha = algoritmo.proxLinha();
            }
            algoritmo.SalvarArquivo(tabelaSimbolos, erro);
            this.tabelaSimbolos = new ArrayList<>();
        }
    }

    private void separaTokens(String linha) throws IOException {
        int fim = linha.length();
        char array[] = linha.toCharArray();
        int inicio = 0;
        for (int i = 0; i < fim; i++) {
            if (("" + array[i]).equals("/") && i + 1 < fim) {
                if (("" + array[i + 1]).equals("/")) {
                    classificar(inicio, i, linha, false);
                    return;
                } else if (("" + array[i + 1]).equals("*")) {
                    classificar(inicio, i, linha, false);
                    buscarFecheComentario(i + 2, linha); // comentário de bloco
                    return;
                } else{
                    inicio = classificar(inicio, i, linha, true);
                }
            } else if (("" + array[i]).equals("&") && i + 1 < fim && ("" + array[i + 1]).equals("&")) {
                classificar(inicio, i, linha, false);
                inicio = classificar(i, i + 2, linha, false) - 1;// classifica o que vem antes
                i = inicio - 1;
            } else if (("" + array[i]).equals("|") && i + 1 < fim && ("" + array[i + 1]).equals("|")) {
                classificar(inicio, i, linha, false);
                inicio = classificar(i, i + 2, linha, false) - 1;// classifica o que vem antes
                i = inicio - 1;
            } else if (("" + array[i]).equals("!") && i + 1 < fim && ("" + array[i + 1]).equals("=")) {
                classificar(inicio, i, linha, false);
                inicio = classificar(i, i + 2, linha, false) - 1;// classifica o que vem antes
                i = inicio - 1;
            } else if (("" + array[i]).equals("<") && i + 1 < fim && ("" + array[i + 1]).equals("=")) {
                classificar(inicio, i, linha, false);
                inicio = classificar(i, i + 2, linha, false) - 1;// classifica o que vem antes
                i = inicio - 1;
            } else if (("" + array[i]).equals(">") && i + 1 < fim && ("" + array[i + 1]).equals("=")) {
                classificar(inicio, i, linha, false);
                inicio = classificar(i, i + 2, linha, false) - 1;// classifica o que vem antes
                i = inicio - 1;
            } else if (("" + array[i]).equals("\"")) {
                classificar(inicio, i, linha, false);
                tratamentoString(i + 1, linha);
                return;
            } else if (("" + array[i]).equals("-")) {
                classificar(inicio, i, linha, false);
                tratamentoSinal(i + 1, linha);
                return;
            } else if (estrutura.isDelimitador("" + array[i])) {
                inicio = classificar(inicio, i, linha, true);
            } else if ((i + 1) == fim) {
                classificar(inicio, i + 1, linha, false);
            }
        }
    }

    public void visualizarToken() {
        for (Token token : tabelaSimbolos) {
            System.out.println(token.toString());
        }
    }

    /*
    public static void main(String[] args) throws IOException {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico();
        analisadorLexico.analisador();
        analisadorLexico.visualizarToken();
    }
    */

    private void buscarFecheComentario(int inicio, String linha) throws IOException {
        String novaLinha = linha.substring(inicio, linha.length());
        int reinicio;
        while (novaLinha != null) {
            if (novaLinha.contains("*/")) {
                reinicio = novaLinha.indexOf("*/");
                novaLinha = novaLinha.substring(reinicio + 2, novaLinha.length());
                separaTokens(novaLinha);
                return;
            } else {
                this.linhaAtual++;
                novaLinha = algoritmo.proxLinha();
            }
        }

    }

    private int classificar(int inicio, int i, String linha, boolean delimitado) {
        String token = linha.substring(inicio, i);
        if (reservada.pertence(token)) {
            tabelaSimbolos.add(new Token("RESERVADA", token, linhaAtual));
        } else if (estrutura.pertence(token)) {
            tabelaSimbolos.add(new Token(estrutura.tipo(token), token, linhaAtual));
        } else if (!token.equals("")) {
            tabelaSimbolos.add(estrutura.excecao("" + token.charAt(0), token, linhaAtual));
            this.erro = true;
        }
        if (delimitado) {
            String delimitador = linha.substring(i, i + 1);
            if (!estrutura.isEspaco(delimitador)) {
                tabelaSimbolos.add(new Token(estrutura.tipo(delimitador), delimitador, linhaAtual));
            }

        }
        return i + 1;
    }

    private void tratamentoString(int inicio, String linha) throws IOException {
        char[] array = linha.toCharArray();
        for (int i = inicio; i < linha.length(); i++) {
            if (("" + array[i]).equals("\"") && !("" + array[i - 1]).equals("\\")) {
                classificar(inicio - 1, i + 1, linha, false);
                separaTokens(linha.substring(i + 1));
                return;
            }
        }

        //String token = linha.substring(inicio-1, linha.length());
        tabelaSimbolos.add(estrutura.excecao("\"", linha.substring(inicio - 1, linha.length()), linhaAtual));
        this.erro = true;
    }

    private void tratamentoSinal(int inicio, String linha) throws IOException {
        char[] array;
        while (linha != null) {
            array = linha.toCharArray();
            for (int i = inicio; i < linha.length(); i++) {
                if (estrutura.pertence("" + array[i])) {
                    if (estrutura.isDigito("" + array[i])) {
                        tratamentoNegativoNumero(i, linha);
                        return;
                    } else if (estrutura.isLetra("" + array[i])) {
                        tratamentoNegativoLetra(i, linha);
                        return;
                    } else if (("" + array[i]).equals("-")) {
                        tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));
                        tratamentoSinal(i + 1, linha);
                        return;
                    }  
                        else if (estrutura.isDelimitador(("" + array[i]))) {
                        tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));
                        separaTokens(linha.substring(i));
                        return;
                    }
                } else if (("" + array[i]).equals("\"")) {
                    tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));
                    tratamentoString(i + 1, linha);
                    return;     
                } else if(estrutura.isSimbolo(("" + array[i])) && !("" + array[i]).equals(" ")){
                    tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));
                    separaTokens(linha.substring(i));
                    return;
                }
            }
            inicio = 0;
            linha = algoritmo.proxLinha();
            this.linhaAtual++;
        }
        tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));

    }

    private void tratamentoNegativoNumero(int primeiraPosicaoValida, String linha) throws IOException {
        String concatenar = "-";
        char[] array = linha.toCharArray();
        for (int i = primeiraPosicaoValida; i < linha.length(); i++) {
            if (estrutura.isDelimitador(("" + array[i]))) {
                concatenar += linha.substring(primeiraPosicaoValida, i + 1);
                classificar(0, concatenar.length() - 1, concatenar, false);
                separaTokens(linha.substring(i));
                return;
            } else if (("" + array[i]).equals("\"")) {
                concatenar += linha.substring(primeiraPosicaoValida, i);
                classificar(0, concatenar.length(), concatenar, false);
                tratamentoString(i + 1, linha);
                return;
            }
        }
        concatenar += linha.substring(primeiraPosicaoValida, linha.length());
        classificar(0, concatenar.length(), concatenar, false);
    }

    private void tratamentoNegativoLetra(int primeiraPosicaoValida, String linha) throws IOException {
        tabelaSimbolos.add(new Token(estrutura.tipo("-"), "-", linhaAtual));
        char[] array = linha.toCharArray();
        for (int i = primeiraPosicaoValida; i < linha.length(); i++) {
            if (estrutura.isDelimitador(("" + array[i]))) {
                classificar(primeiraPosicaoValida, i, linha, false);
                separaTokens(linha.substring(i));
                return;
            } else if (("" + array[i]).equals("\"")) {
                classificar(primeiraPosicaoValida, i, linha, false);
                tratamentoString(i + 1, linha);
                return;
            }
        }
        classificar(primeiraPosicaoValida, linha.length(), linha, false);
    }

}

/*

*/
