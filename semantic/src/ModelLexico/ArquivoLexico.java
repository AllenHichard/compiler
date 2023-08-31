
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelLexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author allen
 */
public class ArquivoLexico {

    private File arquivos[];
    private List<File> arquivosTratados;
    private File diretorio;
    private BufferedReader lerArq;
    private int prox;

    public ArquivoLexico() throws FileNotFoundException {
        diretorio = new File("entrada");
        arquivos = diretorio.listFiles();
        arquivosTratados = new ArrayList<>();
        retirarArquivosSaidas();
    }

    private void retirarArquivosSaidas() {
        for (int i = 0; i < arquivos.length; i++) {
            if (!arquivos[i].toString().contains("lexico - ") && !arquivos[i].toString().contains("Sintatico - ")
                    && !arquivos[i].toString().contains("Semantico - ")) {
                arquivosTratados.add(arquivos[i]);
            }
        }
    }

    public boolean proxArquivo() throws FileNotFoundException {
        if (prox < arquivosTratados.size()) {
            FileReader arq = new FileReader(arquivosTratados.get(prox));
            lerArq = new BufferedReader(arq);
            return true;
        }
        return false;
    }

    public String proxLinha() throws IOException {
        return lerArq.readLine();
    }

    

    public void SalvarArquivo(List tokens, boolean erro) throws IOException {
        //new File("saida - "+arquivos[prox].getName()).delete();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter("entrada\\" + "lexico - " + arquivosTratados.get(prox).getName()));
        for (int i = 0; i < tokens.size(); i++) {
            writer.write(tokens.get(i).toString() + "\r\n");
        }
        if(!erro){
            writer.write("\r\n\r\n Arquivo Sem Erro - Sucesso");
        }
        writer.close();
        lerArq.close();
        prox++;
    }
}
