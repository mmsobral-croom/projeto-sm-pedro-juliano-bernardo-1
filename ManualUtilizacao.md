# Manual de Utilização

## 1. Como executar

Clone o repositório:

```bash
git clone https://github.com/mmsobral-croom/projeto-sm-pedro-juliano-bernardo-1.git
```

Abra um terminal na pasta raiz do projeto (`projeto-definitivo-sm`) e rode:

```bash
./gradlew run
```

Ao iniciar, o programa exibirá o menu principal

---

## 2. Menu principal

O programa apresenta quatro opções, escolhidas digitando o número correspondente e pressionando **Enter**.

```
=========================================
  Buscador do Melhor Preço de Cesta
=========================================

--- Menu ---
1) Adicionar produto
2) Listar cesta de produtos
3) Terminar cesta
4) Sair
Opção:
```

Qualquer entrada diferente dessas exibirá `Opção inválida.` e o menu será mostrado novamente.

---

## 3. Operações detalhadas

### 3.1. Adicionar produto (opção `1`)

1. O programa pede um termo de busca:
   ```
   Produto:
   ```
   Digite o nome (ou parte do nome) do produto desejado, por exemplo: `tapioca`, `arroz`, `leite integral`.

2. O sistema consulta **simultaneamente os três supermercados** e filtra apenas os produtos disponíveis em **todos** eles, exibindo a lista numerada com o preço em cada um:

   ```
   Produtos encontrados em todos os supermercados:
   1. Tapioca Goma 500g
      Giassi: R$ 8,99 | Bistek: R$ 9,49 | Fort Atacadista: R$ 8,79
   2. Tapioca Granulada 1kg
      Giassi: R$ 14,90 | Bistek: R$ 15,20 | Fort Atacadista: R$ 13,99
   ```

3. Escolha o item digitando o número correspondente, ou `0` para cancelar:
   ```
   Escolha (1-2) ou 0 para cancelar:
   ```

4. Informe a quantidade desejada (deve ser inteiro positivo):
   ```
   Quantidade: 3
   ```

5. O programa confirmará a adição:
   ```
   Adicionado: 3 x "Tapioca Goma 500g"
   ```

**Observação importante:**
- Se o termo retornar **nenhum produto comum** entre os três supermercados, será exibida a mensagem `Nenhum produto em comum encontrado`.

---

### 3.2. Listar cesta (opção `2`)

Mostra todos os itens já adicionados, no formato `quantidade x "nome do produto"`:

```
Cesta atual:
  - 3 x "Tapioca Goma 500g"
  - 2 x "Leite Integral 1L"
```

Se a cesta estiver vazia, exibe:
```
Cesta vazia.
```

---

### 3.3. Terminar cesta (opção `3`)

Calcula o preço total da cesta em **cada supermercado** e exibe os resultados **ordenados do menor para o maior preço**:

```
Calculando preços nos supermercados

Supermercados ordenados pelo preço da cesta:
  1. Fort Atacadista: R$ 38,37
  2. Giassi: R$ 41,67
  3. Bistek: R$ 44,87
```

Após exibir o resultado o programa **encerra automaticamente**.

**Observação:**
- Caso algum produto da cesta não esteja disponível em algum supermercado, ele aparecerá com a marcação ` (cesta incompleta)` ao lado do total.

---

### 3.4. Sair (opção `4`)

Encerra o programa.

---

## 4. Limitação

- Só permite adicionar produtos que estejam **disponíveis simultaneamente nos três supermercados**.