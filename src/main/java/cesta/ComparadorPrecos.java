package cesta;

import esd.ListaSequencial;
import sm.Produto;
import sm.Supermercado;

// Classe responsável por comparar preços entre supermercados
public class ComparadorPrecos {

    // Limite máximo de produtos analisados na busca
    static final int LIMITE_PRODUTOS_VARREDURA = 50;

    // Lista de supermercados cadastrados
    private final ListaSequencial<EntradaSupermercado> supermercados = new ListaSequencial<>();

    // Adiciona um supermercado na lista
    public void adicionaSupermercado(String nome, Supermercado sm) {

        // Verifica se o nome é válido
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do supermercado não pode ser vazio");
        }

        // Verifica se o supermercado existe
        if (sm == null) {
            throw new IllegalArgumentException("Supermercado não pode ser nulo");
        }

        // Adiciona o supermercado na lista
        supermercados.adiciona(new EntradaSupermercado(nome, sm));
    }

    // Retorna a quantidade de supermercados cadastrados
    public int quantidadeSupermercados() {
        return supermercados.comprimento();
    }

    // Compara os preços da cesta em todos os supermercados
    public ListaSequencial<PrecoSupermercado> compara(Cesta cesta) {

        // Verifica se a cesta foi informada
        if (cesta == null) {
            throw new IllegalArgumentException("Cesta não pode ser nula");
        }

        // Lista que armazenará os resultados
        ListaSequencial<PrecoSupermercado> resultados = new ListaSequencial<>();

        // Percorre todos os supermercados
        for (int i = 0; i < supermercados.comprimento(); i++) {

            // Obtém um supermercado da lista
            EntradaSupermercado entrada = supermercados.obtem(i);

            // Calcula o preço total da cesta nesse supermercado
            PrecoSupermercado preco = calculaParaSupermercado(entrada.nome, entrada.sm, cesta);

            // Insere o resultado em ordem
            resultados.insere_ordenado(preco);
        }

        return resultados;
    }

    // Calcula o valor total da cesta em um supermercado específico
    PrecoSupermercado calculaParaSupermercado(String nomeSm, Supermercado sm, Cesta cesta) {

        // Valor total da compra
        float total = 0f;

        // Lista de produtos encontrados
        ListaSequencial<Produto> escolhidos = new ListaSequencial<>();

        // Lista de produtos que não foram encontrados
        ListaSequencial<ItemCesta> faltantes = new ListaSequencial<>();

        // Percorre todos os itens da cesta
        for (int i = 0; i < cesta.comprimento(); i++) {

            // Obtém o item atual
            ItemCesta item = cesta.obtem(i);

            Produto melhor;

            // Busca o ID do produto no supermercado
            String productId = item.idParaSm(nomeSm);

            // Se já existir um ID salvo, busca diretamente o produto
            if (productId != null) {

                melhor = sm.obtem(productId);

                // Verifica se o produto está disponível e possui preço válido
                if (melhor != null && (!melhor.disponivel() || melhor.preco() <= 0f)) {
                    melhor = null;
                }

            } else {

                // Busca o produto mais barato usando o termo de pesquisa
                melhor = produtoMaisBarato(sm.busca(item.termoBusca()));
            }

            // Caso o produto não seja encontrado
            if (melhor == null) {

                faltantes.adiciona(item);

            } else {

                // Adiciona o produto encontrado na lista
                escolhidos.adiciona(melhor);

                // Soma o valor ao total
                total += melhor.preco() * item.quantidade();
            }
        }

        // Retorna o resultado final do supermercado
        return new PrecoSupermercado(nomeSm, total, escolhidos, faltantes);
    }

    // Retorna o produto mais barato da lista
    static Produto produtoMaisBarato(Iterable<Produto> produtos) {

        // Verifica se a lista é nula
        if (produtos == null) return null;

        Produto melhor = null;

        // Conta quantos produtos foram verificados
        int verificados = 0;

        // Percorre os produtos encontrados
        for (Produto p : produtos) {

            verificados++;

            // Ignora produtos nulos
            if (p == null) continue;

            // Ignora produtos indisponíveis
            if (!p.disponivel()) continue;

            // Ignora produtos sem preço válido
            if (p.preco() <= 0f) continue;

            // Verifica se é o produto mais barato até agora
            if (melhor == null || p.preco() < melhor.preco()) {
                melhor = p;
            }

            // Para a busca ao atingir o limite definido
            if (verificados >= LIMITE_PRODUTOS_VARREDURA) break;
        }

        return melhor;
    }

    // Classe usada para armazenar informações de um supermercado
    static class EntradaSupermercado {

        // Nome do supermercado
        final String nome;

        // Objeto do supermercado
        final Supermercado sm;

        // Construtor da classe
        EntradaSupermercado(String nome, Supermercado sm) {
            this.nome = nome;
            this.sm = sm;
        }
    }
}