package cesta;

import esd.ListaSequencial;

public class Cesta {
    private final ListaSequencial<ItemCesta> itens = new ListaSequencial<>();

    public void adiciona(String termoBusca, int quantidade) {
        adiciona(new ItemCesta(termoBusca, quantidade));
    }

    public void adiciona(ItemCesta item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }
        itens.adiciona(item);
    }

    public ItemCesta remove(int indice) {
        return itens.remove(indice);
    }

    public ItemCesta obtem(int indice) {
        return itens.obtem(indice);
    }

    public int comprimento() {
        return itens.comprimento();
    }

    public boolean estaVazia() {
        return itens.esta_vazia();
    }

    public void limpa() {
        itens.limpa();
    }
}