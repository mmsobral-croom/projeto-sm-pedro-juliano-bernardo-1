package cesta;

import java.util.HashMap;

// Classe que representa um item da cesta de compras
public class ItemCesta {

    // Termo usado para buscar o produto
    private final String termoBusca;

    // Nome do produto
    private final String nome;

    // Mapa que guarda os IDs do produto em cada supermercado
    private final HashMap<String, String> idsPorSm;

    // Quantidade do produto
    private final int quantidade;

    // Construtor principal
    public ItemCesta(String termoBusca, int quantidade) {

        // Verifica se o termo de busca é válido
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio");
        }

        // Verifica se a quantidade é maior que zero
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Remove espaços extras do termo
        this.termoBusca = termoBusca.trim();

        // Nome e IDs começam vazios
        this.nome = null;
        this.idsPorSm = null;

        // Define a quantidade
        this.quantidade = quantidade;
    }

    // Construtor usado internamente para criar itens completos
    private ItemCesta(String termoBusca, String nome, HashMap<String, String> idsPorSm, int quantidade) {

        // Verifica se o termo de busca é válido
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio");
        }

        // Verifica se a quantidade é válida
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        this.termoBusca = termoBusca.trim();
        this.nome = nome;
        this.idsPorSm = idsPorSm;
        this.quantidade = quantidade;
    }

    // Cria um ItemCesta com IDs específicos para supermercados
    public static ItemCesta comIds(String termoBusca, String nome, HashMap<String, String> idsPorSm, int quantidade) {
        return new ItemCesta(termoBusca, nome, idsPorSm, quantidade);
    }

    // Retorna o termo de busca
    public String termoBusca() {
        return termoBusca;
    }

    // Retorna o nome do produto
    public String nome() {
        return nome;
    }

    // Retorna a quantidade do produto
    public int quantidade() {
        return quantidade;
    }

    // Retorna o ID do produto para um supermercado específico
    public String idParaSm(String nomeSm) {

        // Verifica se existem IDs cadastrados
        if (idsPorSm == null) return null;

        return idsPorSm.get(nomeSm);
    }

    // Define como o objeto será exibido em texto
    @Override
    public String toString() {

        // Usa o nome do produto se existir, senão usa o termo de busca
        String exibicao = (nome != null) ? nome : termoBusca;

        return quantidade + " x \"" + exibicao + "\"";
    }

    // Compara se dois objetos ItemCesta são iguais
    @Override
    public boolean equals(Object o) {

        // Verifica se é o mesmo objeto
        if (this == o) return true;

        // Verifica se o objeto é do tipo ItemCesta
        if (!(o instanceof ItemCesta outro)) return false;

        // Compara quantidade e termo de busca
        return quantidade == outro.quantidade &&
                termoBusca.equals(outro.termoBusca);
    }

    // Gera um código hash para o objeto
    @Override
    public int hashCode() {
        return termoBusca.hashCode() * 31 + quantidade;
    }
}