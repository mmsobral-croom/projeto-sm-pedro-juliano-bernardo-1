package cesta;

import esd.ListaSequencial;

// Classe responsável por armazenar os itens da cesta
public class Cesta {

    // Lista que guarda os itens da cesta
    private final ListaSequencial<ItemCesta> itens = new ListaSequencial<>();

    // Adiciona um item na cesta usando termo de busca e quantidade
    public void adiciona(String termoBusca, int quantidade) {

        // Cria um novo ItemCesta e adiciona na lista
        adiciona(new ItemCesta(termoBusca, quantidade));
    }

    // Adiciona um objeto ItemCesta na lista
    public void adiciona(ItemCesta item) {

        // Verifica se o item é nulo
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        // Adiciona o item na lista
        itens.adiciona(item);
    }

    // Remove um item da cesta pelo índice
    public ItemCesta remove(int indice) {
        return itens.remove(indice);
    }

    // Retorna um item específico da cesta
    public ItemCesta obtem(int indice) {
        return itens.obtem(indice);
    }

    // Retorna a quantidade de itens na cesta
    public int comprimento() {
        return itens.comprimento();
    }

    // Verifica se a cesta está vazia
    public boolean estaVazia() {
        return itens.esta_vazia();
    }

    // Remove todos os itens da cesta
    public void limpa() {
        itens.limpa();
    }
}