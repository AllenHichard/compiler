/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelSintatico;

import ModelLexico.AnalisadorLexico;
import ModelLexico.Token;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author allen
 */
public final class AnalisadorSintatico {

    private final ArquivoSintatico arquivo;
    private final TiposErros erros;
    private List<Token> tabelaSimbolos;
    private Token token;
    private String lexema;
    private String nomeToken;
    private int linha;
    private boolean aux1;
    private boolean aux2;
    private boolean vazio = true;
    private boolean classe;
    private boolean notIsEmpty;
    private boolean controle;
    private boolean controleAritmetico = true;
    private boolean erro;
    private boolean operation = true;
    private boolean metodo = false;
    private boolean expressaoBoleana = true;
    private boolean vazioExpression = true;

    public AnalisadorSintatico() throws FileNotFoundException, IOException {
        this.arquivo = new ArquivoSintatico();
        this.erros = new TiposErros();

        while (arquivo.proxArquivo()) {
            tabelaSimbolos = arquivo.carregarProxTabela();
            tabelaSimbolos.add(new Token("", "Fim", 0));
            classe = false;
            metodo = false;
            Inicio();
            if (!classe) {
                erros.addErro(TiposErros.erroClasse, null, 0);
            }
            if (erros.isEmpty()) {
                arquivo.SalvarArquivo(erros.getListErros(), true);
            } else {
                arquivo.SalvarArquivo(erros.getListErros(), false);
            }
            erros.zerar();
        }

    }

    //***********************************************************************
    // Início do programa
    //**********************************************************************
    public void Inicio() {
        VariavelConstanteObjeto();
        classe();

    }

    //***********************************************************************
    // Métodos com erros tratados
    //**********************************************************************
    public void TratamentoConstante() {
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("=")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(";")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                } else if (!nomeToken.equals("IDENTIFICADOR") && !nomeToken.equals("NUMERO")
                        && !lexema.equals("(") && !lexema.equals("-") && !lexema.equals("+")
                        && !lexema.equals("!") && !nomeToken.equals("STRING")
                        && !lexema.equals("true") && !lexema.equals("false")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                    consumirAtePontoVigula();
                } else {
                    atualizar();
                    operation();
                    atualizar();
                    geradorConstante(lexema);

                }
            } else {
                erros.addErro(TiposErros.erroAtribuicao, lexema, linha);
                atualizar();
                if (lexema.equals(";")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                } else if (!nomeToken.equals("IDENTIFICADOR") && !nomeToken.equals("NUMERO")
                        && !lexema.equals("(") && !lexema.equals("-") && !lexema.equals("+")
                        && !lexema.equals("!") && !nomeToken.equals("STRING")
                        && !lexema.equals("true") && !lexema.equals("false")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                    consumirAtePontoVigula();
                } else {
                    atualizar();
                    operation();
                    atualizar();
                    geradorConstante(lexema);

                }

            }
        } else {
            erros.addErro(TiposErros.erroIdentificador, lexema, linha);
            atualizar();
            if (lexema.equals("=")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(";")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                } else if (!nomeToken.equals("IDENTIFICADOR") && !nomeToken.equals("NUMERO")
                        && !lexema.equals("(") && !lexema.equals("-") && !lexema.equals("+")
                        && !lexema.equals("!") && !nomeToken.equals("STRING")
                        && !lexema.equals("true") && !lexema.equals("false")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                    consumirAtePontoVigula();
                } else {
                    atualizar();
                    operation();
                    atualizar();
                    geradorConstante(lexema);

                }
            } else {
                erros.addErro(TiposErros.erroAtribuicao, lexema, linha);
                atualizar();
                if (lexema.equals(";")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                } else if (!nomeToken.equals("IDENTIFICADOR") && !nomeToken.equals("NUMERO")
                        && !lexema.equals("(") && !lexema.equals("-") && !lexema.equals("+")
                        && !lexema.equals("!") && !nomeToken.equals("STRING")
                        && !lexema.equals("true") && !lexema.equals("false")) {
                    erros.addErro(TiposErros.erroInicializacao, lexema, linha);
                    consumirAtePontoVigula();
                } else {
                    atualizar();
                    operation();
                    atualizar();
                    geradorConstante(lexema);

                }

            }

        }

    }

    public boolean geradorConstante(String lexema) {
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            TratamentoConstante();
            return true;
        } else {
            return false;
        }
    }

    public void TratamentoVariavel() {
        Variaveis();
        atualizar();
        if (lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            System.out.println("Linha sintaticamente Correta");
            VariavelConstanteObjeto();
        } else {
            erros.esperado(linha, ";", lexema);
            consumirAtePontoVigula();
            VariavelConstanteObjeto();
        }
    }

    public void Variaveis() {
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            fatoracaoVariaveis();
        } else {
            erros.addErro(TiposErros.erroIdentificador, lexema, linha);
            consumirAtePontoVigula();
            tabelaSimbolos.add(0, new Token(null, ";", linha));
        }
    }

    public void fatoracaoVariaveis() {
        atualizar();
        if (acrescentar(lexema)) {

        } else {
            if (lexema.equals("[")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (nomeToken.equals("NUMERO")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                        fatoracaoFatoracaoVariaveis();
                    } else {
                        erros.esperado(linha, "]", lexema);
                        fatoracaoFatoracaoVariaveis();
                    }
                } else {
                    erros.esperado(linha, "NUMERO", lexema);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                        fatoracaoFatoracaoVariaveis();
                    } else {
                        erros.esperado(linha, "]", lexema);
                        fatoracaoFatoracaoVariaveis();
                    }

                }
            }
        }
    }

    public void fatoracaoFatoracaoVariaveis() {
        atualizar();
        if (acrescentar(lexema)) {

        } else {
            if (lexema.equals("[")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (nomeToken.equals("NUMERO")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.esperado(linha, "]", lexema);
                    }
                } else {
                    erros.esperado(linha, "NUMERO", lexema);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.esperado(linha, "]", lexema);
                    }
                }
            }
        }
    }

    public boolean acrescentar(String lexema) {
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            Variaveis();
            return true;
        } else {
            return false;
        }
    }

    public void VariavelConstanteObjeto() {
        atualizar();
        if (!notIsEmpty) {
            return;
        }
        if (Tipo(lexema)) {
            TratamentoVariavel();
        } else if (lexema.equals("final")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (Tipo(lexema)) {
                TratamentoConstante();
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    System.out.println("Linha sintaticamente Correta");
                    VariavelConstanteObjeto();
                } else {
                    erros.addErro(TiposErros.pontoVirgula, lexema, linha);
                    VariavelConstanteObjeto();
                }
            } else {
                erros.addErro(TiposErros.erroTipo, lexema, linha);
                consumirAtePontoVigula();
                VariavelConstanteObjeto();
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            criarObjetoLinha();
            VariavelConstanteObjeto();
        } else if (!classe && notIsEmpty && !lexema.equals("class")) {
            erros.addErro(TiposErros.erroLine, lexema, linha);
            tabelaSimbolos.remove(0);
            VariavelConstanteObjeto();
        } else if (!lexema.equals(":") && classe && !metodo && !lexema.equals("}")) {
            erros.addErro(TiposErros.erroLine, lexema, linha);
            tabelaSimbolos.remove(0);
            VariavelConstanteObjeto();
        }
    }

    public boolean criarObjetoLinha() {
        atualizar();
        Token aux;
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            aux = token;
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                fatoracaoVariaveis();
                variosObjetos();
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    System.out.println("Linha sintaticamente Correta");
                    return true;
                } else if (!metodo) {
                    erros.addErro(TiposErros.pontoVirgula, lexema, linha);
                    consumirAtePontoVigula();
                    VariavelConstanteObjeto();
                } else {
                    erros.addErro(TiposErros.pontoVirgula, lexema, linha);
                    consumirAtePontoVigula();
                    Program();
                }
            } else if (!classe) {
                erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                consumirAtePontoVigula();
                VariavelConstanteObjeto();
            } else if (!metodo) {
                erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                consumirAtePontoVigula();
                VariavelConstanteObjeto();
            } else {
                // qualquer coisa que tiver erro, prestar atenção maior aqui
                //erros.addErro(TiposErros.erroTipo, lexema, linha);
                //consumirAtePontoVigula();
                tabelaSimbolos.add(0, aux);
            }
        }
        return false;
    }

    public void variosObjetos() {
        atualizar();
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                variosObjetos();
            } else if (!classe) {
                erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                consumirAtePontoVigula();
                VariavelConstanteObjeto();
            }
        }
    }

    public void classe() {
        System.out.println("classe");
        metodo = false;
        atualizar();
        if (!notIsEmpty) {
            return;
        }
        if (lexema.equals("class")) {
            classe = true;
            tabelaSimbolos.remove(0);
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                herancaOuNao();
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    VariavelConstanteObjeto();
                    Metodo();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        variasClasses();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        variasClasses();
                    }
                } else {
                    erros.esperado(linha, "{", lexema);
                    atualizar();
                    VariavelConstanteObjeto();
                    Metodo();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        variasClasses();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        variasClasses();
                    }
                }

            } else {
                erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                herancaOuNao();
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    VariavelConstanteObjeto();
                    Metodo();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        variasClasses();
                    } else {
                        erros.addErro(TiposErros.erroFechaChaves, lexema, linha);
                        variasClasses();
                    }
                } else {
                    erros.addErro(TiposErros.erroAbreChaves, lexema, linha);
                    tabelaSimbolos.remove(0);
                    VariavelConstanteObjeto();
                    Metodo();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        variasClasses();
                    } else {
                        erros.addErro(TiposErros.erroFechaChaves, lexema, linha);
                        variasClasses();
                    }

                }
            }
        }
    }

    public void herancaOuNao() {
        atualizar();
        if (lexema.equals("<")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("-")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(">")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (nomeToken.equals("IDENTIFICADOR")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                        atualizar();
                    }
                } else {
                    erros.esperado(linha, ">", lexema);
                    atualizar();
                    if (nomeToken.equals("IDENTIFICADOR")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                        atualizar();
                    }
                    atualizar();
                }
            } else {
                erros.esperado(linha, "-", lexema);
                atualizar();
                if (lexema.equals(">")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (nomeToken.equals("IDENTIFICADOR")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                        atualizar();
                    }
                } else {
                    erros.esperado(linha, ">", lexema);
                    atualizar();
                    if (nomeToken.equals("IDENTIFICADOR")) {
                        tabelaSimbolos.remove(0);
                    } else {
                        erros.addErro(TiposErros.erroIdentificador, lexema, linha);
                        atualizar();
                    }
                    atualizar();
                }
            }
        }
    }

    public void variasClasses() {
        atualizar();
        if (lexema.equals("Fim")) {
            return;
        }
        if (!lexema.equals("class")) {
            consumirEntreClasses();
        }
        if (!tabelaSimbolos.isEmpty()) {
            metodo = false;
            classe();
        }
    }

    public boolean Tipo(String lexema) {
        if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string") || lexema.equals("bool")) {
            tabelaSimbolos.remove(0);
            return true;
        } else {
            return false;
        }
    }

    public void Metodo() {
        atualizar();
        if (metodo) {
            consumirEntreMetodos();
        }
        if (lexema.equals(":")) {
            metodo = true;
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals(":")) {
                tabelaSimbolos.remove(0);
                comOuSemRetorno();
            } else {
                erros.esperado(linha, ":", lexema);
                comOuSemRetorno();
            }
        }
    }

    public void comOuSemRetorno() {
        Main();
        atualizar();
        if (Tipo(lexema)) {
            atualizar();
            comOuSemRetorno2();
        } else {
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR") && tabelaSimbolos.get(1).getNome().equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                atualizar();
            }
            comOuSemRetorno2();
        }
    }

    public void aux() {
        if (lexema.equals("(")) {
            tabelaSimbolos.remove(0);
            parametros();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }
                } else {
                    erros.esperado(linha, "{", lexema);
                    //consumirAteOabreChaves();
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }

                }
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }
                } else {
                    erros.esperado(linha, "{", lexema);
                    //consumirAteOabreChaves();
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }

                }
            }
        } else {
            erros.esperado(linha, "(", lexema);
            parametros();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }
                } else {
                    erros.esperado(linha, "{", lexema);
                    //consumirAteOabreChaves();
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }

                }
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
                if (lexema.equals("{")) {
                    tabelaSimbolos.remove(0);
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }
                } else {
                    erros.esperado(linha, "{", lexema);
                    //consumirAteOabreChaves();
                    consumirAtePontoVigula();
                    atualizar();
                    Program();
                    atualizar();
                    if (lexema.equals("}")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        Metodo();
                    } else {
                        erros.esperado(linha, "}", lexema);
                        atualizar();
                        Metodo();
                    }

                }
            }
        }
    }

    //Observacao
    public void comOuSemRetorno2() {
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            atualizar();
            aux();
        } else {
            if (lexema.equals("}") || lexema.equals("class")) {
                return;
            }
            erros.esperado(linha, "IDENTIFICADOR", lexema);
            atualizar();
            aux();
        }

    }

    public void Main() {
        atualizar();
        Token aux;
        if (lexema.equals("bool")) {
            aux = token;
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("main")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals("(")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (lexema.equals(")")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        if (lexema.equals("{")) {
                            tabelaSimbolos.remove(0);
                            consumirAtePontoVigula();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }
                        } else {
                            erros.esperado(linha, "{", lexema);
                            consumirAtePontoVigula();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }

                        }
                    } else {
                        erros.esperado(linha, ")", lexema);
                        atualizar();
                        if (lexema.equals("{")) {
                            tabelaSimbolos.remove(0);
                            consumirAtePontoVigula();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }
                        } else {
                            erros.esperado(linha, "{", lexema);
                            consumirAtePontoVigula();
                            atualizar();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }

                        }
                    }
                } else {
                    erros.esperado(linha, "(", lexema);
                    atualizar();
                    if (lexema.equals(")")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        if (lexema.equals("{")) {
                            tabelaSimbolos.remove(0);
                            consumirAtePontoVigula();
                            atualizar();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }
                        } else {
                            erros.esperado(linha, "{", lexema);
                            consumirAtePontoVigula();
                            atualizar();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }

                        }
                    } else {
                        erros.esperado(linha, ")", lexema);
                        atualizar();
                        if (lexema.equals("{")) {
                            tabelaSimbolos.remove(0);
                            consumirAtePontoVigula();
                            atualizar();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }
                        } else {
                            erros.esperado(linha, "{", lexema);
                            //consumirAteOabreChaves();
                            consumirAtePontoVigula();
                            atualizar();
                            Program();
                            atualizar();
                            if (lexema.equals("}")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                Metodo();
                            } else {
                                erros.esperado(linha, "}", lexema);
                                atualizar();
                                Metodo();
                            }

                        }
                    }
                }
            } else {
                tabelaSimbolos.add(0, aux);
            }
        }
    }

    public void acharFollowParametro() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, "int|float|bool|String|Identificador", lexema);
            atualizar();
        }
    }

    public void parametros() {
        atualizar();
        if (Tipo(lexema)) {
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                fatoracaoParametro();
                atualizar();
            } else {
                erros.esperado(linha, "IDENTIFICADOR", lexema);
                fatoracaoParametro();
                atualizar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                atualizar();
                fatoracaoParametro();
            } else {
                erros.esperado(linha, "IDENTIFICADOR", lexema);
                fatoracaoParametro();
                atualizar();
            }
        } else if (lexema.equals(",")) {
            erros.esperado(linha, "Parametro Antes da Virgula", lexema);
            acrescentarparametros();
        } else if (tabelaSimbolos.size() > 0 && !lexema.equals(")")) {
            acharFollowParametro();
            parametros();
        }
    }

    public void fatoracaoParametro() {
        atualizar();
        if (lexema.equals("[")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("]")) {
                tabelaSimbolos.remove(0);
                atualizar();
                parametroMatriz();
                acrescentarparametros();
            } else {
                erros.esperado(linha, "]", nomeToken);
                atualizar();
                parametroMatriz();
                acrescentarparametros();
            }
        } else {
            acrescentarparametros();
        }
    }

    public void parametroMatriz() {
        atualizar();
        if (lexema.equals("[")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("]")) {
                tabelaSimbolos.remove(0);
                atualizar();
            } else {
                erros.esperado(linha, "]", nomeToken);
                atualizar();
                parametroMatriz();
                acrescentarparametros();
            }
        }
    }

    public void acrescentarparametros() {
        atualizar();
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (!lexema.equals(")")) {
                parametros();
            } else {
                erros.esperado(linha, "int|float|bool|String|Identificador", nomeToken);
            }
        } else if (!lexema.equals(")")) {
            erros.esperado(linha, "int|float|bool|String|Identificador", nomeToken);

        }
    }

    public void criarVariacel() {
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR")) {
            criarObjeto();
        } else if (Tipo(lexema)) {
            Variaveis();
            atualizar();
            if (lexema.equals(";")) {
                System.out.println("Linha sintaticamente Correta");
                tabelaSimbolos.remove(0);
                atualizar();
                consumirAtePontoVigula();
                Program();
            } else {
                erros.esperado(linha, ";", lexema);
                atualizar();
                consumirAtePontoVigula();
                Program();
            }
        }
    }

    public void print2() {
        impressao();
        MultiplasImpressoes();
        atualizar();
        if (!aux2 && lexema.equals(")")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals(";")) {
                System.out.println("Linha sintaticamente Correta");
                tabelaSimbolos.remove(0);
                atualizar();
                consumirAtePontoVigula();
                Program();
            } else {
                erros.esperado(linha, ";", lexema);
                consumirAtePontoVigula();
                Program();
            }
        } else {
            erros.esperado(linha, ")", lexema);
            atualizar();
            if (lexema.equals(";")) {
                System.out.println("Linha sintaticamente Correta");
                tabelaSimbolos.remove(0);
                atualizar();
                consumirAtePontoVigula();
                Program();
            } else {
                erros.esperado(linha, ";", lexema);
                consumirAtePontoVigula();
                Program();
            }

        }
    }

    public void print() {
        atualizar();
        if (lexema.equals("print")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(")") || lexema.equals(";") || lexema.equals("}")) {
                    erros.esperado(linha, "Expressão", lexema);
                }
                print2();

            } else {
                erros.esperado(linha, "(", lexema);
                atualizar();
                if (lexema.equals(")") || lexema.equals(";") || lexema.equals("}")) {
                    erros.esperado(linha, "Expressão", lexema);
                }
                print2();
            }
        }
    }

    public void impressao() {
        operation();
    }

    public void MultiplasImpressoes() {
        atualizar();
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals(")")) {
                erros.esperado(linha, "Expressão", lexema);
            } else {
                impressao();
                MultiplasImpressoes();
            }
        }
    }

    public void scan2() {
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            MultiplasLeituras();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(";")) {
                    System.out.println("Linha sintaticamente Correta");
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
                if (lexema.equals(";")) {
                    System.out.println("Linha sintaticamente Correta");
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }

            }
        } else {
            erros.esperado(linha, "IDENTIFICADOR", lexema);
            MultiplasLeituras();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(";")) {
                    System.out.println("Linha sintaticamente Correta");
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
                if (lexema.equals(";")) {
                    System.out.println("Linha sintaticamente Correta");
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }
            }
        }
    }

    public void scan() {
        atualizar();
        if (lexema.equals("scan")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                scan2();
            } else {
                erros.esperado(linha, "(", lexema);
                atualizar();
                scan2();
            }

        }
    }

    public void MultiplasLeituras() {
        atualizar();
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (nomeToken.equals("IDENTIFICADOR")) {
                tabelaSimbolos.remove(0);
                MultiplasLeituras();
            } else {
                erros.esperado(linha, "IDENTIFICADOR", lexema);
                MultiplasLeituras();
            }
        }
    }

    public boolean acessoVetorMatriz() { // retirei o identificador
        atualizar();
        Token aux;
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            aux = token;
            atualizar();
            if (lexema.equals("[")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (nomeToken.equals("NUMERO")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        return fatoracaoAcessoVatorMatriz();
                    } else {
                        erros.esperado(linha, "]", lexema);
                        return true;
                    }

                } else {
                    erros.esperado(linha, "NUMERO", lexema);
                    atualizar();
                    if (lexema.equals("]")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        return fatoracaoAcessoVatorMatriz();
                    } else {
                        erros.esperado(linha, "]", lexema);
                        return true;
                    }
                }
            } else {
                tabelaSimbolos.add(0, aux);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean fatoracaoAcessoVatorMatriz() {
        atualizar();
        if (lexema.equals("[")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (nomeToken.equals("NUMERO")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals("]")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                } else {
                    erros.esperado(linha, "]", lexema);
                }
            } else {
                erros.esperado(linha, "NUMERO", lexema);
                atualizar();
                if (lexema.equals("]")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                } else {
                    erros.esperado(linha, "]", lexema);
                }
            }
        }
        return true;
    }

    //***********************************************************************
    // 
    // 
    //**********************************************************************
    //***********************************************************************
    // Métodos de Utilidade para pegar os first follow e atualizar os tokens atuais
    // para analises
    //**********************************************************************
    public void consumirAtePontoVigula() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(";") && !tratamento()) {
            tabelaSimbolos.remove(0);
            atualizar();
        }
        if (tabelaSimbolos.size() > 0 && lexema.equals(";")) {
            tabelaSimbolos.remove(0);
        }
        atualizar();
    }

    public boolean tratamento() {
        if (!metodo && !classe) {
            if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                    || lexema.equals("bool") || nomeToken.equals("IDENTIFICADOR") || lexema.equals("class")
                    || lexema.equals("final")) {
                return true;
            }
        } else if (!metodo && classe) {
            if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                    || lexema.equals("bool") || nomeToken.equals("IDENTIFICADOR") || lexema.equals("class")
                    || lexema.equals("final") || lexema.equals(":") || lexema.equals("}")
                    || lexema.equals("(") || lexema.equals(")") || lexema.equals("{")) {
                return true;
            }
        } else if (metodo) {
            if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                    || lexema.equals("bool") || nomeToken.equals("IDENTIFICADOR") || lexema.equals("class")
                    || lexema.equals("if") || lexema.equals("for") || lexema.equals(":")
                    || lexema.equals("}") || lexema.equals("(") || lexema.equals(")") || lexema.equals("{")
                    || lexema.equals("print") || lexema.equals("scan") || lexema.equals("<") || lexema.equals("-")) {
                return true;
            } else {
                erros.esperado(linha, "Codigo correto dentro do método", lexema);
            }
        }
        return false;
    }

    public boolean tratamentoMetodo() {
        if (lexema.equals("int") || lexema.equals("float") || lexema.equals("string")
                || lexema.equals("bool") || nomeToken.equals("IDENTIFICADOR")
                || lexema.equals("if") || lexema.equals("for") || lexema.equals(":")
                || lexema.equals("print") || lexema.equals("scan") || lexema.equals("<")) {
            return true;
        }
        return false;

    }

    public void consumirEntreClasses() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals("class")) {
            erros.addErro(TiposErros.erroEntreClasses, lexema, linha);
            tabelaSimbolos.remove(0);
            atualizar();
        }
    }

    public void consumirEntreMetodos() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(":") && !lexema.equals("class") && !lexema.equals("}")) {
            erros.addErro(TiposErros.erroEntreClasses, lexema, linha);
            tabelaSimbolos.remove(0);
            atualizar();
        }
    }

    public void atualizar() {
        if (!tabelaSimbolos.isEmpty()) {
            token = tabelaSimbolos.get(0); // variavel, constante, objeto, class
            lexema = token.getLexema();
            nomeToken = token.getNome();
            linha = token.getLinha();
            notIsEmpty = true;
        } else {
            notIsEmpty = false;
        }
    }

    public void Return() {
        atualizar();
        if (lexema.equals("<")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("<")) {
                tabelaSimbolos.remove(0);
                TiposReturn();
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }
            } else {
                erros.esperado(linha, "<", lexema);
                TiposReturn();
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                } else {
                    erros.esperado(linha, ";", lexema);
                    atualizar();
                    consumirAtePontoVigula();
                    Program();
                }
            }
        }
    }

    public void TiposReturn() {
        operation();
    }

    public void criarObjeto() {
        if (criarObjetoLinha()) {
            Program();
        }
    }

    public void instancia() {
        atualizar();
        Token aux;
        Token aux2;
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            aux = token;
            atualizar();
            if (lexema.equals("=")) {
                tabelaSimbolos.remove(0);
                aux2 = token;
                atualizar();
                if (lexema.equals(">")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    if (nomeToken.equals("IDENTIFICADOR")) {
                        tabelaSimbolos.remove(0);
                        atualizar();
                        if (lexema.equals("(")) {
                            tabelaSimbolos.remove(0);
                            atualizar();
                            passagemParametros();
                            atualizar();
                            if (lexema.equals(")")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    consumirAtePontoVigula();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }

                            } else {
                                erros.esperado(linha, ")", lexema);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }
                            }
                        } else {
                            erros.esperado(linha, "(", lexema);
                            atualizar();
                            passagemParametros();
                            atualizar();
                            if (lexema.equals(")")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }

                            } else {
                                erros.esperado(linha, ")", lexema);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }
                            }
                        }

                    } else {
                        erros.esperado(linha, "IDENTIFICADOR", lexema);
                        atualizar();
                        if (lexema.equals("(")) {
                            tabelaSimbolos.remove(0);
                            atualizar();
                            passagemParametros();
                            atualizar();
                            if (lexema.equals(")")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }

                            } else {
                                erros.esperado(linha, ")", lexema);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }
                            }
                        } else {
                            erros.esperado(linha, "(", lexema);
                            atualizar();
                            passagemParametros();
                            atualizar();
                            if (lexema.equals(")")) {
                                tabelaSimbolos.remove(0);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }

                            } else {
                                erros.esperado(linha, ")", lexema);
                                atualizar();
                                if (lexema.equals(";")) {
                                    System.out.println("Linha sintaticamente Correta");
                                    tabelaSimbolos.remove(0);
                                    atualizar();
                                    Program();
                                } else {
                                    erros.esperado(linha, ";", lexema);
                                    consumirAtePontoVigula();
                                    Program();
                                }
                            }
                        }
                    }
                } else {
                    tabelaSimbolos.add(0, aux2);
                    tabelaSimbolos.add(0, aux);
                }
            } else {
                tabelaSimbolos.add(0, aux);
            }
        }
    }

    public void chamadaMetodospontovirgula() {
        atualizar();
        boolean foi = chamadaMetodos();
        atualizar();
        if (lexema.equals(";")) {
            System.out.println("Linha sintaticamente Correta");
            tabelaSimbolos.remove(0);
            atualizar();
            consumirAtePontoVigula();
            Program();
        } else if (foi && !lexema.equals(";")) {
            erros.esperado(linha, ";", lexema);
            atualizar();
            consumirAtePontoVigula();
            Program();
        }
        // Observação    

    }

    public boolean chamadaMetodos() {
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR")) {
            tabelaSimbolos.remove(0);
            return fatoracaoChamadaMetodos();
        }
        return false;
    }

    public boolean fatoracaoChamadaMetodos() {
        boolean foi = false;
        atualizar();
        Token aux = token;
        if (lexema.equals(":")) {
            foi = true;
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals(":")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (nomeToken.equals("IDENTIFICADOR")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    fatoracaodafatoracao();
                } else {
                    erros.esperado(linha, "IDENTIFICADOR", lexema);
                    atualizar();
                    fatoracaodafatoracao();
                }
            } else {
                erros.esperado(linha, ":", lexema);
                atualizar();
                if (nomeToken.equals("IDENTIFICADOR")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    fatoracaodafatoracao();
                } else {
                    erros.esperado(linha, "IDENTIFICADOR", lexema);
                    atualizar();
                    fatoracaodafatoracao();
                }
            }
        } else if (lexema.equals("(")) {
            foi = true;
            tabelaSimbolos.remove(0);
            atualizar();
            passagemParametros();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
            }
        }
        return foi;
    }

    public void fatoracaodafatoracao() {
        atualizar();
        if (lexema.equals("(")) {
            tabelaSimbolos.remove(0);
            atualizar();
            passagemParametros();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
            }

        }
    }

    public void Program() {
        atualizar();
        IF();
        FOR();
        instancia();
        classificarVariavel();
        criarVariacel();
        print();
        scan();
        chamadaMetodospontovirgula();
        Return();

    }

    //***********************************************************************
    // 
    // 
    //**********************************************************************
    // para tratar
    //////////////////////////////////////////////////
    /////////////////////////////////////////////////
    ///////////////////////////////// tratar problemas entre classes
    public static void main(String[] args) throws FileNotFoundException, IOException {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico();
        analisadorLexico.analisador();
        AnalisadorSintatico a = new AnalisadorSintatico();

    }

    public void passagemParametros() {
        operation();
        maisPassagens();
    }

    public void acharFollowPassagem() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, ") ou ,", lexema);
            atualizar();
        }
    }

    public void maisPassagens() {
        atualizar();
        if (lexema.equals(",")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (!lexema.equals(")")) {
                passagemParametros();
            } else {
                erros.esperado(linha, "valor", nomeToken);
            }
        } else if (!lexema.equals(")")) {
            erros.esperado(linha, ",", lexema);
            acharFollowPassagem();
        }
    }

    /*public void maisPassagens() {
     atualizar();
     if (lexema.equals(",")) {
     tabelaSimbolos.remove(0);
     atualizar();
     passagemParametros();
     }
     }*/
    public void classificarVariavel() {
        atualizar();
        Token aux;
        if (lexema.equals("-")) {
            tabelaSimbolos.remove(0);
            aux = token;
            atualizar();
            if (lexema.equals("-")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(">")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    operationLinhe();

                } else {
                    erros.esperado(linha, ">", lexema);
                    atualizar();
                    operationLinhe();

                }
            } else if (lexema.equals(">")) {
                tabelaSimbolos.remove(0);
                atualizar();
                operationLinhe();
            } else {
                erros.esperado(linha, "- | >", lexema);
                atualizar();
                operationLinhe();
            }
        } else {
            operationLinhe();
        }
    }

    public boolean operationFor() {
        Token aux;
        atualizar();
        if (nomeToken.equals("IDENTIFICADOR")) {
            aux = token;
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("=")) {
                tabelaSimbolos.remove(0);
                atualizar();
                operation();
                return true;
            } else {
                tabelaSimbolos.add(0, aux);
                atualizar();
                acessoVetorMatriz();
                atualizar();
                if (lexema.equals("=")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    operation();
                    return true;
                }
            }
        }
        return false;
    }

    private void operationLinhe() {
        atualizar();
        boolean a = operationFor();
        atualizar();
        if (lexema.equals(";")) {
            System.out.println("Linha sintaticamente Correta");
            tabelaSimbolos.remove(0);
            atualizar();
            consumirAtePontoVigula();
            Program();
        } else if (a) {
            erros.esperado(linha, ";", lexema);
            atualizar();
            consumirAtePontoVigula();
            Program();
        }

    }

    public void if2() {

        atualizar();
        if (lexema.equals("{")) {
            tabelaSimbolos.remove(0);
            atualizar();
            Program();
            atualizar();
            if (lexema.equals("}")) {
                tabelaSimbolos.remove(0);
                atualizar();
                elseOpcional();
            } else {
                erros.esperado(linha, "}", lexema);
                atualizar();
                elseOpcional();
            }
        } else {
            erros.esperado(linha, "{", lexema);
            atualizar();
            Program();
            atualizar();
            if (lexema.equals("}")) {
                tabelaSimbolos.remove(0);
                atualizar();
                elseOpcional();
            } else {
                erros.esperado(linha, "}", lexema);
                atualizar();
                elseOpcional();
            }
        }
    }

    public void acharFollowIf() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals("{") && !lexema.equals("}")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, "expressao relacional ou logica", lexema);
            atualizar();
        }
    }

    public void IF() {
        atualizar();
        if (lexema.equals("if")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                if (lexema.equals(")")) {
                    erros.esperado(linha, "Expressão booleana relacional", lexema);
                } else {
                    expressionLogicaRelacional();
                    if (!expressaoBoleana) {
                        acharFollowIf();
                        expressaoBoleana = true;
                    }
                    atualizar();
                }
                if (lexema.equals(")")) {
                    tabelaSimbolos.remove(0);
                    if2();
                } else {
                    erros.esperado(linha, ")", lexema);
                    if2();
                }
            } else {
                erros.esperado(linha, "(", lexema);
                atualizar();
                if (lexema.equals(")")) {
                    erros.esperado(linha, "Expressão booleana relacional", lexema);
                } else {
                    expressionLogicaRelacional();
                    if (!expressaoBoleana) {
                        acharFollowIf();
                        expressaoBoleana = true;
                    }
                    atualizar();
                }
                if (lexema.equals(")")) {
                    tabelaSimbolos.remove(0);
                    if2();
                } else {
                    erros.esperado(linha, ")", lexema);
                    if2();
                }
            }
        }

    }

    public void elseOpcional() {
        atualizar();
        if (lexema.equals("else")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("{")) {
                tabelaSimbolos.remove(0);
                atualizar();
                Program();
                atualizar();
                if (lexema.equals("}")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    Program();
                } else {
                    erros.esperado(linha, "}", lexema);
                    atualizar();
                    Program();
                }
            } else {
                erros.esperado(linha, "{", lexema);
                atualizar();
                Program();
                atualizar();
                if (lexema.equals("}")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    Program();
                } else {
                    erros.esperado(linha, "}", lexema);
                    atualizar();
                    Program();
                }

            }
        } else {
            Program();
        }
    }

    //'for' '(' <operationFor> ';' <ExpressionLogicaRelacional> ';' <operationFor> ')' '{' <program> '}' <program>
    public void FOR2() {
        atualizar();
        if (lexema.equals("{")) {
            tabelaSimbolos.remove(0);
            atualizar();
            Program();
            atualizar();
            if (lexema.equals("}")) {
                tabelaSimbolos.remove(0);
                atualizar();
                Program();
            } else {
                erros.esperado(linha, "}", lexema);
                atualizar();
                Program();
            }
        } else {
            erros.esperado(linha, "{", lexema);
            atualizar();
            Program();
            atualizar();
            if (lexema.equals("}")) {
                tabelaSimbolos.remove(0);
                atualizar();
                Program();
            } else {
                erros.esperado(linha, "}", lexema);
                atualizar();
                Program();
            }

        }

    }

    public void acharFollowfor2() {
        atualizar();
        
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals("{") && !lexema.equals("}") && !lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, "expressao relacional ou logica", lexema);
            atualizar();
            
        }
        
    }

    public void acharFollowfor1() {
        atualizar();
        
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals("{") && !lexema.equals("}") && !lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, "Inicializa", lexema);
            atualizar();
        }
        
    }

    public void acharFollowfor3() {
        atualizar();
        while (tabelaSimbolos.size() > 0 && !lexema.equals(")") && !lexema.equals("{") && !lexema.equals("}")) {
            tabelaSimbolos.remove(0);
            erros.esperado(linha, "Mudar valor", lexema);
            atualizar();
            
        }
    }

    public void FOR3() {
        atualizar();
        expressionLogicaRelacional();
        if (!expressaoBoleana && !lexema.equals(";")) {
            erros.esperado(linha, "expressao relacional ou logica", lexema);
            acharFollowfor2();
            expressaoBoleana = true;
        }
        atualizar();
        if (lexema.equals(";")) {
            tabelaSimbolos.remove(0);
            atualizar();
            boolean a = operationFor();
            if (!(a && !vazioExpression)) {
                erros.esperado(linha, "Mudar valor", lexema);
                acharFollowfor3();
                vazioExpression = true;
            }
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                FOR2();
            } else {
                erros.esperado(linha, ")", nomeToken);
                FOR2();
            }
        } else {
            erros.esperado(linha, ";", nomeToken);
            atualizar();
            boolean a = operationFor();
            if (!(a && !vazioExpression)) {
                erros.esperado(linha, "Mudar valor", lexema);
                acharFollowfor3();
                vazioExpression = true;
            }
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                FOR2();
            } else {
                erros.esperado(linha, ")", nomeToken);
                FOR2();
            }
        }
    }

    public void FOR() {
        atualizar();
        if (lexema.equals("for")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                boolean a = operationFor();
                if (!(a && !vazioExpression)) {
                    erros.esperado(linha, "Inicializar", lexema);
                    acharFollowfor1();
                    vazioExpression = true;
                }
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    FOR3();
                } else {
                    erros.esperado(linha, ";", lexema);
                    FOR3();
                }
            } else {
                erros.esperado(linha, "(", lexema);
                atualizar();
                boolean a = operationFor();
                if (!(a && !vazioExpression)) {
                    erros.esperado(linha, "Inicializar", lexema);
                    acharFollowfor1();
                    vazioExpression = true;
                }
                atualizar();
                if (lexema.equals(";")) {
                    tabelaSimbolos.remove(0);
                    FOR3();
                } else {
                    erros.esperado(linha, ";", lexema);
                    FOR3();
                }
            }
        }
    }

    // ultimos códigos para validar
    public void operation() {
        vazioExpression = true;
        expressaoBoleana = true;
        atualizar();
        List<Token> aux = new ArrayList<>();
        aux.addAll(tabelaSimbolos);
        List listaDecisao = null;
        if (operation) {
            listaDecisao = new ArrayList<>();
            listaDecisao.addAll(erros.getListErros());
            operation = false;
        }
        expressionLogicaRelacional();

        if (!controle) {
            if (listaDecisao != null) {
                erros.serListErros(listaDecisao);
            }
            tabelaSimbolos = aux;
            ExpressionAritmeticas();
            if (vazioExpression && !lexema.equals(")")) {
                erros.esperado(linha, "Value", lexema);
            }
        }
    }

    public void ExpressionAritmeticas() {
        relacionalAritmetico();
    }

    public boolean operadorAritmeticos() {
        atualizar();
        if (lexema.equals("+") || lexema.equals("-") || lexema.equals("*") || lexema.equals("/") || lexema.equals("%")) {
            tabelaSimbolos.remove(0);
            atualizar();
            return true;
        } else {

            return false;
        }
    }

    public void relacionalAritmetico() {
        atualizar();
        if (lexema.equals("-")) {
            controleAritmetico = false;
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                addValor();
                operadorAritmeticos();
                relacionalAritmetico();
                if (lexema.equals(")")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                    continuar();
                } else {
                    erros.esperado(linha, ")", lexema);
                    atualizar();
                    continuar();
                }
            }
        } else if (lexema.equals("(")) {
            controleAritmetico = false;
            tabelaSimbolos.remove(0);
            atualizar();
            addValor();
            operadorAritmeticos();
            relacionalAritmetico();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
                continuar();
            } else {
                erros.esperado(linha, ")", lexema);
                atualizar();
                continuar();
            }
        } else if (nomeToken.equals("IDENTIFICADOR") || nomeToken.equals("NUMERO") || nomeToken.equals("STRING")
                || lexema.equals("true") || lexema.equals("false")) {
            controleAritmetico = false;
            if (addValor()) {
                vazioExpression = false;
                fatoracaorelacionalAritmetico();
            }
        }

    }

    public boolean addValor() {
        atualizar();
        if (lexema.equals("-")) {
            tabelaSimbolos.remove(0);
            atualizar();
            return value();
        } else {
            return value();
        }
    }

    public void fatoracaorelacionalAritmetico() {
        if (operadorAritmeticos()) {
            relacionalAritmetico();
        }
    }

    public void continuar() {
        if (operadorAritmeticos()) {
            relacionalAritmetico();
        }
    }

    public boolean value() {
        Token aux = token;
        if (acessoVetorMatriz()) {
            return true;
        } else if (chamadaMetodos()) {
            return true;
        } else if (nomeToken.equals("IDENTIFICADOR") || nomeToken.equals("NUMERO") || nomeToken.equals("STRING")
                || lexema.equals("true") || lexema.equals("false")) {
            if (aux.getNome().equals("IDENTIFICADOR")) {
                tabelaSimbolos.add(0, aux);
            }
            tabelaSimbolos.remove(0);
            atualizar();
            return true;
        } else if (aux.getNome().equals("IDENTIFICADOR") || aux.getNome().equals("NUMERO") || aux.getNome().equals("STRING")
                || aux.getLexema().equals("true") || aux.getLexema().equals("false")) {
            return true;
        } else {
            return false;
        }

    }

    public void expressionLogicaRelacional() {
        atualizar();
        relacional();
        variasExpression();
    }

    public boolean operadorRelacional() {
        atualizar();
        if (lexema.equals("=")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("=")) {
                tabelaSimbolos.remove(0);
                atualizar();
                controle = true;
                return true;
            }
        } else if (lexema.equals("!=") || lexema.equals("<") || lexema.equals(">")
                || lexema.equals(">=") || lexema.equals("<=")) {
            tabelaSimbolos.remove(0);
            atualizar();
            controle = true;
            return true;
        }
        controle = false;
        return false;
    }

    public boolean operadorLogico() {
        atualizar();
        if (lexema.equals("&&") || lexema.equals("||")) {
            tabelaSimbolos.remove(0);
            atualizar();
            controle = true;
            return true;
        } else {
            controle = false;
            return false;
        }

    }

    public void relacional() {
        atualizar();
        if (lexema.equals("(")) {
            tabelaSimbolos.remove(0);
            atualizar();
            expressionLogicaRelacional();
            atualizar();
            if (lexema.equals(")")) {
                tabelaSimbolos.remove(0);
                atualizar();
            } else {
                erros.addErro(TiposErros.erroParenteses, lexema, linha);
                atualizar();
            }
        } else if (lexema.equals("!")) {
            tabelaSimbolos.remove(0);
            atualizar();
            if (lexema.equals("(")) {
                tabelaSimbolos.remove(0);
                atualizar();
                expressionLogicaRelacional();
                atualizar();
                if (lexema.equals(")")) {
                    tabelaSimbolos.remove(0);
                    atualizar();
                } else {
                    erros.addErro(TiposErros.erroParenteses, lexema, linha);
                    atualizar();
                }
            }
        } else if (nomeToken.equals("IDENTIFICADOR") || nomeToken.equals("NUMERO") || nomeToken.equals("STRING")
                || lexema.equals("true") || lexema.equals("false")) {
            if (addValor()) {
                if (operadorRelacional()) {
                    if (addValor()) {

                    } else {
                        expressaoBoleana = false;
                    }
                } else {
                    expressaoBoleana = false;
                }
            } else {
                expressaoBoleana = false;
            }

        } else {
            expressaoBoleana = false;
        }

    }

    public boolean variasExpression() {
        if (lexema.equals("&&") || lexema.equals("||")) {
            operadorLogico();
            variasExp();
            operation = false;
            return true;
        } else if (lexema.equals(";")) {
            operation = true;
            return true;
        } else if (!lexema.equals(")")) {
            //erros.esperado(linha, ";", lexema);
            operation = true;
            return false;
        } else {
            operation = true;
            return false;
        }

    }

    public void variasExp() {
        expressionLogicaRelacional();
    }
}
// Observação 
