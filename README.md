# 🧩 Compilador em Java — Analisadores Léxico, Sintático e Semântico

Este repositório contém a implementação de um **compilador desenvolvido em Java**, estruturado em módulos independentes para as três principais etapas da análise de compilação: **léxica**, **sintática** e **semântica**.  
O projeto tem como objetivo didático demonstrar o funcionamento interno de um compilador e servir como base para estudos em **linguagens formais, teoria da computação e engenharia de compiladores**.

---

## ⚙️ Estrutura do Compilador

A arquitetura do compilador é organizada em três módulos principais:

### 🔹 Analisador Léxico
Responsável por:
- Ler o código-fonte caractere por caractere;
- Identificar **tokens** válidos (identificadores, palavras reservadas, números, operadores, etc.);
- Detectar **erros léxicos** (como símbolos inválidos ou identificadores malformados);
- Gerar a sequência de tokens que será usada nas etapas seguintes.

> 💡 Implementado com base em expressões regulares e autômatos finitos determinísticos (AFD).

---

### 🔹 Analisador Sintático
Responsável por:
- Verificar a **estrutura gramatical** do programa, conforme as regras da linguagem;
- Construir a **árvore sintática (parse tree)**;
- Detectar **erros de sintaxe** e reportá-los de forma amigável.

> 💡 Implementado utilizando **análise preditiva LL(1)** ou **descendente recursiva**, com base na gramática definida para a linguagem.

---

### 🔹 Analisador Semântico
Responsável por:
- Realizar a **verificação de tipos** e **declarações de variáveis**;
- Garantir a **consistência semântica** do código (ex: operações entre tipos incompatíveis);
- Construir e gerenciar a **tabela de símbolos**;
- Preparar os dados para uma futura etapa de **geração de código intermediário**.

> 💡 Implementado com estrutura de **tabela de símbolos hierárquica**, associando identificadores, tipos e escopos.

---

## 📁 Estrutura de Diretórios

```
📦 compilador-java
├── 📁 src/
│   ├── 📁 lexico/
│   │   └── Lexico.java
│   ├── 📁 sintatico/
│   │   └── Sintatico.java
│   ├── 📁 semantico/
│   │   └── Semantico.java
│   └── Main.java
├── 📄 README.md
└── 📄 exemplo.txt   # Código-fonte de teste
```

---

## 🚀 Execução

### Compilação
```bash
javac -d bin src/**/*.java
```

### Execução
```bash
java -cp bin Main exemplo.txt
```

---

## 🧠 Conceitos Envolvidos

- **Autômatos finitos determinísticos (AFD)**
- **Gramáticas livres de contexto (GLC)**
- **Tabelas preditivas LL(1)**
- **Tabela de símbolos**
- **Verificação semântica e escopos**

---

## 🎯 Objetivo

Fornecer uma **implementação educacional e modular de um compilador**, permitindo:
- Entendimento prático do pipeline de compilação;
- Extensão do projeto para **geração de código intermediário** ou **montagem final**;
- Aplicação de conceitos de **linguagens formais** e **teoria de compiladores**.

---

## 🧑‍💻 Tecnologias Utilizadas

- **Java 17+**
- **IDE recomendada:** IntelliJ IDEA / Eclipse / VS Code
- **Gramática:** Definida manualmente ou via ferramentas como JFlex / CUP (opcional)

---

## 📚 Referências

- Aho, A.V., Lam, M.S., Sethi, R., Ullman, J.D.  
  *Compilers: Principles, Techniques, and Tools* (2ª edição). Pearson, 2006.  
- Grune, D. & Jacobs, C.J.H.  
  *Parsing Techniques: A Practical Guide.* Springer, 2008.  
- Louden, K.C.  
  *Compiler Construction: Principles and Practice.* PWS Publishing, 1997.
