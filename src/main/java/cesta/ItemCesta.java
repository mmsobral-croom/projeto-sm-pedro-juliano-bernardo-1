package cesta;

public class ItemCesta {
    private final String termoBusca;
    private final int quantidade;

    public ItemCesta(String termoBusca, int quantidade) {
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.termoBusca = termoBusca.trim();
        this.quantidade = quantidade;
    }

    public String termoBusca() {
        return termoBusca;
    }

    public int quantidade() {
        return quantidade;
    }

    @Override
    public String toString() {
        return quantidade + " x \"" + termoBusca + "\"";
    }
}