package ModelSintatico;


import ModelLexico.Token;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTMLEditorKit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allen
 */
public class ArquivoSintatico {
    private File arquivos[];
    private List<File> arquivosTratados;
    private File diretorio;
    private BufferedReader lerArq;
    private List<Token> tabelaSimbolos;
    private int prox;

    public ArquivoSintatico() throws FileNotFoundException {
        diretorio = new File("entrada");
        arquivos = diretorio.listFiles();
        arquivosTratados = new ArrayList<>();
        this.tabelaSimbolos = new ArrayList<>();
        retirarArquivosSaidas();
    }

    private void retirarArquivosSaidas() {
        for (int i = 0; i < arquivos.length; i++) {
            if (arquivos[i].toString().contains("lexico - ") && !arquivos[i].toString().contains("Sintatico - ")) {
                arquivosTratados.add(arquivos[i]);
            }
        }
    }

    public boolean proxArquivo() throws FileNotFoundException {
        if (prox < arquivosTratados.size()) {
            FileReader arq = new FileReader(arquivosTratados.get(prox));
            lerArq = new BufferedReader(arq);
            tabelaSimbolos = new ArrayList<>();
            return true;
        }
        return false;
    }
    
    public List carregarProxTabela() throws IOException{
        String linha = lerArq.readLine();
        while(linha != null && !linha.equals("")){
            String[] token = linha.split(" - ");
            tabelaSimbolos.add(new Token(token[1], token[2], Integer.parseInt(token[0])));
            linha = lerArq.readLine();
        }
        return tabelaSimbolos;
    }

    

    

    public void SalvarArquivo(List tokens, boolean erro) throws IOException {
        //new File("saida - "+arquivos[prox].getName()).delete();
        String [] a = arquivosTratados.get(prox).getName().split("lexico -");
        arquivosTratados.get(prox).deleteOnExit();
        BufferedWriter writer = new BufferedWriter(new FileWriter("entrada\\" + "Sintatico - " + a[1]));
        for (int i = 0; i < tokens.size(); i++) {
            writer.write(tokens.get(i).toString() + "\r\n");
        }
        if(erro){
            writer.write("\r\n\r\n Código Sintaticamente correto - Sucesso");
        }
        writer.close();
        lerArq.close();
        prox++;
    }
}
