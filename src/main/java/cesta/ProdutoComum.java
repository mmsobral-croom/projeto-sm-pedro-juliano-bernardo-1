package cesta;

import esd.ListaSequencial;

public class ProdutoComum {
    private final String nome;
    private final String ean;
    private final ListaSequencial<EntradaPreco> precos;

    public ProdutoComum(String nome, String ean, ListaSequencial<EntradaPreco> precos) {
        this.nome = nome;
        this.ean = ean;
        this.precos = precos;
    }

    public String nome() { return nome; }
    public String ean() { return ean; }
    public ListaSequencial<EntradaPreco> precos() { return precos; }

    public boolean temEan() {
        return ean != null;
    }

    public float precoMedio() {
        if (precos.esta_vazia()) return 0f;
        float soma = 0f;
        for (int i = 0; i < precos.comprimento(); i++) {
            soma += precos.obtem(i).preco();
        }
        return soma / precos.comprimento();
    }

    public static class EntradaPreco {
        private final String nomeSm;
        private final float preco;
        private final String productId;

        public EntradaPreco(String nomeSm, float preco, String productId) {
            this.nomeSm = nomeSm;
            this.preco = preco;
            this.productId = productId;
        }

        public String nomeSm() { return nomeSm; }
        public float preco() { return preco; }
        public String productId() { return productId; }
    }
}
