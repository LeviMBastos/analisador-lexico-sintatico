# 📘 Analisador Léxico e Sintático

Este projeto implementa um analisador léxico e sintático, que lida com expressões envolvendo identificadores, parênteses, operadores aritméticos e operadores de acesso a arrays.

---

## 📜 Descrição da Gramática

A gramática fornecida permite expressões com operadores aritméticos e acesso a arrays:

```
E → E + T | T  
T → T * F | T / F | F  
F → id [ E ] | ( E ) | id
```

Essa gramática aceita expressões como:

- `id`
- `id + id`
- `id[id + id * id]`
- `(id + id) * id[id]`

---

## 🧠 Conjuntos FIRST e FOLLOW

### FIRST

| Não-Terminal | FIRST    |
|--------------|----------|
| E            | { id, ( }|
| T            | { id, ( }|
| F            | { id, ( }|

### FOLLOW

| Não-Terminal | FOLLOW           |
|--------------|------------------|
| E            | { ], )}          |
| T            | { +, ], )}       |
| F            | { *, /, +, ], )} |

---

## 🛠️ Implementação do Analisador

A implementação está na linguagem Java e composta por:

- **Analisador Léxico:** converte a string de entrada em tokens (`ID`, `+`, `*`, `(`, `)`, `[`, `]`, etc).
- **Analisador Sintático:** baseado em descida recursiva, analisa os tokens com base na gramática fornecida.
- **Árvore Sintática:** gerada com indentação para facilitar visualização.
- **Mensagens de Erro Descritivas:** explicações claras para erros sintáticos e léxicos.

---

## ✅ Casos de Teste e Resultados

### ✅ Casos Válidos

| Expressão                      | Resultado      |
|-------------------------------|----------------|
| `id`                          | Válido         |
| `id + id`                     | Válido         |
| `id[id]`                      | Válido         |
| `(id + id) * id[id]`          | Válido         |
| `id[id + id * id]`            | Válido         |
| `id * id + id / id`           | Válido         |

### ❌ Erros Sintáticos

| Expressão         | Erro                            |
|------------------|----------------------------------|
| `id +`           | Expressão incompleta             |
| `id[id +`        | Colchete de array não fechado    |
| `id + * id`      | Operador inesperado              |

### ❌ Erros Léxicos

| Expressão        | Erro                     |
|------------------|--------------------------|
| `id # id`        | Caractere inválido       |
| `$id`            | Símbolo não permitido    |
| `id@id`          | Token mal formado        |

### ❗ Casos Extremos

| Expressão  | Resultado             |
|------------|-----------------------|
| ``         | Inválido (vazio)      |
| `(`        | Inválido (sem fechar) |
| `id[]`     | Inválido (array vazio)|

---

## 📌 Observações

- O analisador léxico rejeita qualquer símbolo fora da gramática.
- Números são reconhecidos mas não permitidos na análise sintática.
- A árvore sintática é exibida com identação hierárquica para melhor entendimento.

---

## 👤 Autores

Levi Bastos
Gabriel Alves
