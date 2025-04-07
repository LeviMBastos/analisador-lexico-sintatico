package com.mycompany.analisadores;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal que implementa um analisador léxico e sintático para uma
 * gramática de expressões com arrays
 */
public class Analisadores {

    // Tipos de tokens suportados pela linguagem
    enum TipoToken {
        ID, // Identificador (ex: variavel)
        NUMERO, // Número inteiro
        MAIS, // Operador de adição (+)
        VEZES, // Operador de multiplicação (*)
        DIVIDIR, // Operador de divisão (/)
        ABRE_PAR, // Abre parêntese (
        FECHA_PAR, // Fecha parêntese )
        ABRE_COL, // Abre colchete [
        FECHA_COL, // Fecha colchete ]
        FIM           // Fim do arquivo/entrada
    }

    /**
     * Classe que representa um token do código fonte
     */
    static class Token {

        public final TipoToken tipo;
        public final String lexema;

        public Token(TipoToken tipo, String lexema) {
            this.tipo = tipo;
            this.lexema = lexema;
        }

        @Override
        public String toString() {
            return String.format("%s('%s')", tipo, lexema);
        }
    }

    /**
     * Classe que representa um nó da árvore sintática
     */
    static class NoSintatico {

        String rotulo;
        List<NoSintatico> filhos = new ArrayList<>();

        public NoSintatico(String rotulo) {
            this.rotulo = rotulo;
        }

        public void adicionarFilho(NoSintatico filho) {
            filhos.add(filho);
        }

        public String toString() {
            return toStringComIndentacao("", true);
        }

        /**
         * Método auxiliar para imprimir a árvore com indentação
         */
        private String toStringComIndentacao(String indentacao, boolean ehUltimo) {
            StringBuilder sb = new StringBuilder();
            sb.append(indentacao);

            if (ehUltimo) {
                sb.append("-- ");
                indentacao += "    ";
            } else {
                sb.append("|-- ");
                indentacao += "|   ";
            }

            sb.append(rotulo).append("\n");

            for (int i = 0; i < filhos.size(); i++) {
                sb.append(filhos.get(i).toStringComIndentacao(indentacao, i == filhos.size() - 1));
            }

            return sb.toString();
        }
    }

    /**
     * Classe de exceção para erros sintáticos
     */
    static class ErroSintatico extends RuntimeException {

        public ErroSintatico(String mensagem) {
            super(mensagem);
        }
    }

    public static void main(String[] args) {
        System.out.println("ANALISADOR LEXICO E SINTATICO - TESTES ESSENCIAIS\n");

        // Casos de teste organizados por categoria
        String[][] casosTeste = {
            // 1. Casos válidos simples
            {"id", "Expressao minima valida"},
            {"id + id", "Adicao basica"},
            {"id[id]", "Acesso a array simples"},
            
            // 2. Casos válidos complexos
            {"(id + id) * id[id]", "Precedencia e aninhamento"},
            {"id[id + id * id]", "Expressao aninhada em array"},
            {"id * id + id / id", "Multiplos operadores"},
            
            // 3. Erros sintáticos
            {"id + ", "Expressao incompleta"},
            {"id[id + ", "Array nao fechado"},
            {"id + * id", "Operador invalido"},
            
            // 4. Erros léxicos
            {"id # id", "Caractere invalido"},
            {"$id", "Simbolo nao permitido"},
            {"id@id", "Token mal formado"},
            
            // 5. Casos extremos
            {"", "Entrada vazia"},
            {"(", "Apenas abre parentese"},
            {"id[]", "Array vazio"}
        };

        // Executa todos os casos de teste
        for (String[] casoTeste : casosTeste) {
            String entrada = casoTeste[0];
            String descricao = casoTeste[1];

            System.out.println("\n=== TESTE: " + descricao + " ===");
            System.out.println("Expressao: '" + entrada + "'");

            try {
                // Fase de análise léxica
                List<Token> tokens = analisarLexicamente(entrada);
                System.out.println("\nTokens gerados:");
                tokens.forEach(System.out::println);

                // Fase de análise sintática
                AnalisadorSintatico analisador = new AnalisadorSintatico(tokens);
                NoSintatico arvoreSintatica = analisador.analisar();

                System.out.println("\nArvore Sintatica:");
                System.out.println(arvoreSintatica);
                System.out.println("VALIDO");
            } catch (ErroSintatico e) {
                System.out.println("\nErro sintatico: " + e.getMessage());
                System.out.println("INVALIDO (Sintaxe)");
            } catch (RuntimeException e) {
                System.out.println("\nErro lexico: " + e.getMessage());
                System.out.println("INVALIDO (Lexico)");
            }
        }
    }

    /**
     * Analisador léxico - converte a string de entrada em tokens
     */
    private static List<Token> analisarLexicamente(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int posicao = 0;

        while (posicao < entrada.length()) {
            char caractereAtual = entrada.charAt(posicao);

            // Ignora espaços em branco
            if (Character.isWhitespace(caractereAtual)) {
                posicao++;
                continue;
            }

            // Reconhece identificadores (letras seguidas de letras/dígitos)
            if (Character.isLetter(caractereAtual)) {
                StringBuilder sb = new StringBuilder();
                while (posicao < entrada.length() && Character.isLetterOrDigit(entrada.charAt(posicao))) {
                    sb.append(entrada.charAt(posicao++));
                }
                tokens.add(new Token(TipoToken.ID, sb.toString()));
                continue;
            }

            // Reconhece números inteiros
            if (Character.isDigit(caractereAtual)) {
                StringBuilder sb = new StringBuilder();
                while (posicao < entrada.length() && Character.isDigit(entrada.charAt(posicao))) {
                    sb.append(entrada.charAt(posicao++));
                }
                tokens.add(new Token(TipoToken.NUMERO, sb.toString()));
                continue;
            }

            // Reconhece símbolos e operadores
            switch (caractereAtual) {
                case '+':
                    tokens.add(new Token(TipoToken.MAIS, "+"));
                    posicao++;
                    break;
                case '*':
                    tokens.add(new Token(TipoToken.VEZES, "*"));
                    posicao++;
                    break;
                case '/':
                    tokens.add(new Token(TipoToken.DIVIDIR, "/"));
                    posicao++;
                    break;
                case '(':
                    tokens.add(new Token(TipoToken.ABRE_PAR, "("));
                    posicao++;
                    break;
                case ')':
                    tokens.add(new Token(TipoToken.FECHA_PAR, ")"));
                    posicao++;
                    break;
                case '[':
                    tokens.add(new Token(TipoToken.ABRE_COL, "["));
                    posicao++;
                    break;
                case ']':
                    tokens.add(new Token(TipoToken.FECHA_COL, "]"));
                    posicao++;
                    break;
                default:
                    throw new RuntimeException("Caractere inesperado: '" + caractereAtual + "'");
            }
        }

        // Adiciona token de fim de arquivo
        tokens.add(new Token(TipoToken.FIM, ""));
        return tokens;
    }

    /**
     * Analisador sintático - constrói a árvore sintática a partir dos tokens
     */
    static class AnalisadorSintatico {

        private final List<Token> tokens;
        private int atual = 0;

        public AnalisadorSintatico(List<Token> tokens) {
            this.tokens = tokens;
        }

        // Métodos auxiliares para navegação pelos tokens
        private Token olhar() {
            return tokens.get(atual);
        }

        private Token avancar() {
            if (!estaNoFim()) {
                atual++;
            }
            return tokens.get(atual - 1);
        }

        private boolean estaNoFim() {
            return olhar().tipo == TipoToken.FIM;
        }

        private boolean verificar(TipoToken tipo) {
            if (estaNoFim()) {
                return false;
            }
            return olhar().tipo == tipo;
        }

        private boolean casar(TipoToken... tipos) {
            for (TipoToken tipo : tipos) {
                if (verificar(tipo)) {
                    avancar();
                    return true;
                }
            }
            return false;
        }

        private Token consumir(TipoToken tipo, String mensagem) {
            if (verificar(tipo)) {
                return avancar();
            }
            throw new ErroSintatico(mensagem);
        }

        /**
         * Método principal da análise sintática
         */
        public NoSintatico analisar() {
            NoSintatico raiz = expressaoE();
            if (!estaNoFim()) {
                throw new ErroSintatico("Tokens adicionais apos o final da expressao");
            }
            return raiz;
        }

        /**
         * Regra E → E + T | T
         */
        private NoSintatico expressaoE() {
            NoSintatico no = new NoSintatico("E");
            NoSintatico esquerda = termoT();
            no.adicionarFilho(esquerda);

            while (casar(TipoToken.MAIS)) {
                NoSintatico noOperador = new NoSintatico("'+'");
                no.adicionarFilho(noOperador);
                NoSintatico direita = termoT();
                no.adicionarFilho(direita);
            }

            return no;
        }

        /**
         * Regra T → T * F | T / F | F
         */
        private NoSintatico termoT() {
            NoSintatico no = new NoSintatico("T");
            NoSintatico esquerda = fatorF();
            no.adicionarFilho(esquerda);

            while (casar(TipoToken.VEZES, TipoToken.DIVIDIR)) {
                Token operador = tokens.get(atual - 1);
                NoSintatico noOperador = new NoSintatico("'" + operador.lexema + "'");
                no.adicionarFilho(noOperador);
                NoSintatico direita = fatorF();
                no.adicionarFilho(direita);
            }

            return no;
        }

        /**
         * Regra F → id [ E ] | ( E ) | id
         */
        private NoSintatico fatorF() {
            NoSintatico no = new NoSintatico("F");

            if (casar(TipoToken.ID)) {
                Token id = tokens.get(atual - 1);
                NoSintatico noId = new NoSintatico(id.toString());
                no.adicionarFilho(noId);

                if (casar(TipoToken.ABRE_COL)) {
                    no.adicionarFilho(new NoSintatico("'['"));
                    no.adicionarFilho(expressaoE());

                    // Mensagem mais descritiva para fechamento de colchete
                    if (!verificar(TipoToken.FECHA_COL)) {
                        Token tokenAtual = olhar();
                        String mensagem = String.format(
                                "Esperado ']' para fechar acesso ao array apos expressao, mas encontrado %s('%s')",
                                tokenAtual.tipo, tokenAtual.lexema);
                        throw new ErroSintatico(mensagem);
                    }
                    no.adicionarFilho(new NoSintatico("']'"));
                    avancar(); // Consome o fecha colchete
                }
            } else if (casar(TipoToken.ABRE_PAR)) {
                no.adicionarFilho(new NoSintatico("'('"));
                no.adicionarFilho(expressaoE());

                // Mensagem mais descritiva para fechamento de parêntese
                if (!verificar(TipoToken.FECHA_PAR)) {
                    Token tokenAtual = olhar();
                    String mensagem = String.format(
                            "Esperado ')' para fechar expressao entre parenteses, mas encontrado %s('%s')",
                            tokenAtual.tipo, tokenAtual.lexema);
                    throw new ErroSintatico(mensagem);
                }
                no.adicionarFilho(new NoSintatico("')'"));
                avancar(); // Consome o fecha parêntese
            } else {
                // Mensagem mais informativa para erro no fator
                Token tokenAtual = olhar();
                String mensagem;

                if (estaNoFim()) {
                    mensagem = "Esperado identificador, '(', ou acesso a array, mas a expressao terminou abruptamente";
                } else {
                    mensagem = String.format(
                            "Esperado identificador ou '(', mas encontrado %s('%s')",
                            tokenAtual.tipo, tokenAtual.lexema);

                    // Sugestões para tokens específicos
                    if (tokenAtual.tipo == TipoToken.NUMERO) {
                        mensagem += " (numeros nao sao permitidos como identificadores)";
                    } else if (tokenAtual.tipo == TipoToken.FECHA_PAR || tokenAtual.tipo == TipoToken.FECHA_COL) {
                        mensagem += " (fechamento sem abertura correspondente)";
                    }
                }
                throw new ErroSintatico(mensagem);
            }

            return no;
        }
    }
}