package cesta;

import java.util.HashMap;

public class ItemCesta {
    private final String termoBusca;
    private final String nome;
    private final HashMap<String, String> idsPorSm;
    private final int quantidade;

    public ItemCesta(String termoBusca, int quantidade) {
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.termoBusca = termoBusca.trim();
        this.nome = null;
        this.idsPorSm = null;
        this.quantidade = quantidade;
    }

    private ItemCesta(String termoBusca, String nome, HashMap<String, String> idsPorSm, int quantidade) {
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.termoBusca = termoBusca.trim();
        this.nome = nome;
        this.idsPorSm = idsPorSm;
        this.quantidade = quantidade;
    }

    public static ItemCesta comIds(String termoBusca, String nome, HashMap<String, String> idsPorSm, int quantidade) {
        return new ItemCesta(termoBusca, nome, idsPorSm, quantidade);
    }

    public String termoBusca() { return termoBusca; }
    public String nome() { return nome; }
    public int quantidade() { return quantidade; }

    public String idParaSm(String nomeSm) {
        if (idsPorSm == null) return null;
        return idsPorSm.get(nomeSm);
    }

    @Override
    public String toString() {
        String exibicao = (nome != null) ? nome : termoBusca;
        return quantidade + " x \"" + exibicao + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCesta outro)) return false;
        return quantidade == outro.quantidade && termoBusca.equals(outro.termoBusca);
    }

    @Override
    public int hashCode() {
        return termoBusca.hashCode() * 31 + quantidade;
    }
}
