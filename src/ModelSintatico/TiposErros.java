/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSintatico;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jacia
 */
public class TiposErros {

    public static int erroTipoFinal = 1;
    public static int erroTipo = 2;
    public static int erroClasse = 3;
    public static int pontoVirgula = 4;
    public static int erroAtribuicao = 5;
    public static int erroInicializacao = 6;
    public static int erroParenteses = 7;
    public static int erroIdentificador = 8;
    public static int erroCriarVariavel = 9;
    public static int erroVirgula = 10;
    public static int erroNumero = 11;
    public static int errofechaColcehetes = 12;
    public static int erroLine = 13;
    public static int erroAbreChaves = 14;
    public static int erroFechaChaves = 15;
    public static int erroHeranca = 16;
    public static int erroEntreClasses = 17;
    public static int erroMetodoPonto = 18;
    public static int erroMetodoIdenficador = 19;
    public static int erroAbreParenteses = 20;
    public static int erroFechaParenteses = 21;
    private List<String> listaErros;

    public TiposErros() {
        this.listaErros = new ArrayList<>();
    }

    public void addErro(int erro, String leituraErro, int linha) {
        String formato;
        if (erroTipoFinal == erro) {
            formato = "Linha " + linha + " - Esperado final | int | float | bool | string | identificador - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroTipo == erro) {
            formato = "Linha " + linha + " - Esperado  | int | float | bool | string  - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (pontoVirgula == erro) {
            formato = "Linha " + linha + " - Faltando o ponto e virgula";
            listaErros.add(formato);
        } else if (erroAtribuicao == erro) {
            formato = "Linha " + linha + " - Esperando o = - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroInicializacao == erro) {
            formato = "Linha " + linha + " - Erro ao inicializar variável";
            listaErros.add(formato);
        } else if (erroParenteses == erro) {
            formato = "Linha " + linha + " - Faltando parenteses";
            listaErros.add(formato);
        } else if (erroIdentificador == erro) {
            formato = "Linha " + linha + " - Esperando um identificador - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroCriarVariavel == erro) {
            formato = "Linha " + linha + " - Esperando um , ou ; ou [ - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroVirgula == erro) {
            formato = "Linha " + linha + " - Esperando um , - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroNumero == erro) {
            formato = "Linha " + linha + " - Esperando um numero - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (errofechaColcehetes == erro) {
            formato = "Linha " + linha + " - Esperando um ] - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroLine == erro) {
            formato = "Linha " + linha + " - Caracter inesperado " + leituraErro;
            listaErros.add(formato);
        } else if (erroAbreChaves == erro) {
            formato = "Linha " + linha + " - Caracter esperado { - token incorreto" + leituraErro;
            listaErros.add(formato);
        } else if (erroFechaChaves == erro) {
            formato = "Linha " + linha + " - Caracter esperado } - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroHeranca == erro) {
            formato = "Linha " + linha + " Conjunto herança correto <-> ";
            listaErros.add(formato);
        } else if (erroEntreClasses == erro) {
            formato = "Linha " + linha + " excluir - " + leituraErro;
            listaErros.add(formato);
        } else if (erroClasse == erro) {
            formato = "Nenhuma classe declarada";
            listaErros.add(formato);
        } else if (erroMetodoPonto == erro) {
            formato = "Linha " + linha + " Faltando um :";
            listaErros.add(formato);
        } else if (erroMetodoIdenficador == erro) {
            formato = "Linha " + linha + " - esperando um identificador - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroAbreParenteses == erro) {
            formato = "Linha " + linha + " - Caracter esperado ( - token incorreto " + leituraErro;
            listaErros.add(formato);
        } else if (erroFechaParenteses == erro) {
            formato = "Linha " + linha + " - Caracter esperado ) - token incorreto " + leituraErro;
            listaErros.add(formato);
        }
    }
    
    public void esperado(int linha, String esperado, String incorreto){
        String formato = "Linha " + linha + " - Caracter esperado " + esperado + " token incorreto " + incorreto;
        listaErros.add(formato);
    
    }

    /*
    public static int erroMetodoPonto = 18;
    public static int erroMetodoIdenficador = 19;
    public static int erroAbreParenteses = 20;
    public static int erroFechaParenteses = 21;
     */
    public boolean isEmpty() {
        return listaErros.isEmpty();
    }

    public List getListErros() {
        return listaErros;
    }

    public void serListErros(List lista) {
        listaErros = new ArrayList<>();
        listaErros.addAll(lista);
    }

    public void zerar() {
        listaErros = new LinkedList<>();
    }
}
