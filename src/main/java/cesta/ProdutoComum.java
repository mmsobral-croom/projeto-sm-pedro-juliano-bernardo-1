package cesta;

import esd.ListaSequencial;

// Classe que representa um produto encontrado em vários supermercados
public class ProdutoComum {

    // Nome do produto
    private final String nome;

    // Código EAN do produto
    private final String ean;

    // Lista com os preços do produto em diferentes supermercados
    private final ListaSequencial<EntradaPreco> precos;

    // Construtor da classe
    public ProdutoComum(String nome, String ean, ListaSequencial<EntradaPreco> precos) {
        this.nome = nome;
        this.ean = ean;
        this.precos = precos;
    }

    // Retorna o nome do produto
    public String nome() {
        return nome;
    }

    // Retorna o código EAN
    public String ean() {
        return ean;
    }

    // Retorna a lista de preços
    public ListaSequencial<EntradaPreco> precos() {
        return precos;
    }

    // Verifica se o produto possui código EAN
    public boolean temEan() {
        return ean != null;
    }

    // Calcula o preço médio do produto
    public float precoMedio() {

        // Retorna 0 caso não existam preços cadastrados
        if (precos.esta_vazia()) return 0f;

        float soma = 0f;

        // Soma todos os preços da lista
        for (int i = 0; i < precos.comprimento(); i++) {
            soma += precos.obtem(i).preco();
        }

        // Retorna a média dos preços
        return soma / precos.comprimento();
    }

    // Classe interna usada para armazenar o preço em um supermercado
    public static class EntradaPreco {

        // Nome do supermercado
        private final String nomeSm;

        // Preço do produto
        private final float preco;

        // ID do produto no supermercado
        private final String productId;

        // Construtor da classe
        public EntradaPreco(String nomeSm, float preco, String productId) {
            this.nomeSm = nomeSm;
            this.preco = preco;
            this.productId = productId;
        }

        // Retorna o nome do supermercado
        public String nomeSm() {
            return nomeSm;
        }

        // Retorna o preço do produto
        public float preco() {
            return preco;
        }

        // Retorna o ID do produto
        public String productId() {
            return productId;
        }
    }
}