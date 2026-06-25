[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/0aiXsnlU)
# Projeto 1: Melhor Preço

No projeto 1 sua equipe deve desenvolver um programa que descubra em qual supermercado se pode comprar um conjunto de produtos pelo melhor preço.

O usuário deve criar uma cesta de compras, e então o preço total dessa cesta deve ser calculado para cada um dos supermercados cadastrados. Ao final, o programa deve mostrar os supermercados e respectivos preços de cesta ordenados pelo preço.

Os supermercados cadastrados até o momento são:
* Giassi
* Bistek
* Fort Atacadista

Para desenvolver esse software, usem os buscadores de preço disponibilizados neste repositório inicial. Existe um buscador para cada supermercado implementado em uma classe na package _sm_. A interface dos buscadores é a mesma:

* __ListaSequencial\<Produto\> busca(String nome)__: busca todos produtos cujos nomes contenham _nome_. O resultado é uma lista de objetos _Produto_.
* __Produto obtem(String productId)__: busca a descrição de um produto identificado pelo _productId_. Os valores de _productId_ são específicos de cada supermercado.

A classe Produto contém a descrição de um produto, e possui os seguintes métodos para acessar as informações:
* __String nome()__: o nome do produto, conforme definido pelo supermercado 
* __String id()__: o valor do _productId_ definido pelo supermercado
* __String marca()__: a marca do produto
* __float preco()__: o preço do produto
* __boolean disponivel()__: se o produto está disponível no supermercado

Um exemplo de consulta a produtos de um supermercado está contido em _Main.java_:

```java
public class Main {
    static void main() {

        // cria um acessador para o Giassi
        Giassi sm = new Giassi();

        // procura todos produtos cujo nome contenha "tapioca"
        ListaSequencial<Produto> produtos = sm.busca("tapioca");

        // Mostra cada um dos produtos encontrados
        for (int pos=0; pos < produtos.comprimento(); pos++) {
            IO.println(produtos.obtem(pos));
        }

    }
}
```

# Projeto 2: Melhor Preço (com cache persistente)

Programa que monta uma cesta de compras e descobre em qual supermercado
(Giassi, Bistek ou Fort Atacadista) ela sai mais barata.

O projeto 2 estende o projeto 1 adicionando uma **cache persistente** dos
produtos, para reduzir os acessos às APIs dos supermercados.

## Como executar

```bash
./gradlew run
```

O uso do programa está descrito em [ManualUtilizacao.md](ManualUtilizacao.md).

## O que o projeto 2 acrescenta

- **Cache por supermercado** (`cesta/CacheSupermercado.java`): guarda os
  produtos já buscados, indexados pelo `id` em uma tabela hash
  (`esd/TabHash.java`). A busca por nome (ou parte do nome) ou por EAN é
  feita por varredura, como no projeto 1.
- **Busca consulta a cache primeiro**: a API de um supermercado só é
  acessada quando a cache não tem nenhum produto que satisfaça o termo, e o
  resultado é então guardado na cache.
- **Preço atualizado em lote**: ao terminar a cesta, os preços são obtidos
  por `id` em uma única chamada por supermercado
  (`Supermercado.obtem(ListaSequencial<String>)`).
- **Persistência**: a cache é gravada em `cache/` (um arquivo JSON por
  supermercado) ao encerrar e recuperada ao iniciar.

## Estrutura

- `esd/` — estruturas de dados próprias (`ListaSequencial`, `TabHash`)
- `sm/` — buscadores dos supermercados (fornecidos)
- `cesta/` — montagem da cesta, comparação de preços e cache
- `cache/` — criada após execução, guarda arquivos JSON de cada supermercado
- `Main.java` — interface de menu