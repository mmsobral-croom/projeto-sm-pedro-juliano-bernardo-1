package cesta;

import esd.ListaSequencial;
import sm.Produto;
import sm.Supermercado;

public class ComparadorPrecos {
    static final int LIMITE_PRODUTOS_VARREDURA = 50;

    private final ListaSequencial<EntradaSupermercado> supermercados = new ListaSequencial<>();

    public void adicionaSupermercado(String nome, Supermercado sm) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do supermercado não pode ser vazio");
        }
        if (sm == null) {
            throw new IllegalArgumentException("Supermercado não pode ser nulo");
        }
        supermercados.adiciona(new EntradaSupermercado(nome, sm));
    }

    public int quantidadeSupermercados() {
        return supermercados.comprimento();
    }

    public ListaSequencial<PrecoSupermercado> compara(Cesta cesta) {
        if (cesta == null) {
            throw new IllegalArgumentException("Cesta não pode ser nula");
        }
        ListaSequencial<PrecoSupermercado> resultados = new ListaSequencial<>();
        for (int i = 0; i < supermercados.comprimento(); i++) {
            EntradaSupermercado entrada = supermercados.obtem(i);
            PrecoSupermercado preco = calculaParaSupermercado(entrada.nome, entrada.sm, cesta);
            resultados.insere_ordenado(preco);
        }
        return resultados;
    }

    PrecoSupermercado calculaParaSupermercado(String nomeSm, Supermercado sm, Cesta cesta) {
        float total = 0f;
        ListaSequencial<Produto> escolhidos = new ListaSequencial<>();
        ListaSequencial<ItemCesta> faltantes = new ListaSequencial<>();

        for (int i = 0; i < cesta.comprimento(); i++) {
            ItemCesta item = cesta.obtem(i);
            Produto melhor;
            String productId = item.idParaSm(nomeSm);
            if (productId != null) {
                melhor = sm.obtem(productId);
                if (melhor != null && (!melhor.disponivel() || melhor.preco() <= 0f)) melhor = null;
            } else {
                melhor = produtoMaisBarato(sm.busca(item.termoBusca()));
            }
            if (melhor == null) {
                faltantes.adiciona(item);
            } else {
                escolhidos.adiciona(melhor);
                total += melhor.preco() * item.quantidade();
            }
        }
        return new PrecoSupermercado(nomeSm, total, escolhidos, faltantes);
    }

    static Produto produtoMaisBarato(Iterable<Produto> produtos) {
        if (produtos == null) return null;

        Produto melhor = null;
        int verificados = 0;
        for (Produto p : produtos) {
            verificados++;
            if (p == null) continue;
            if (!p.disponivel()) continue;
            if (p.preco() <= 0f) continue;
            if (melhor == null || p.preco() < melhor.preco()) {
                melhor = p;
            }
            if (verificados >= LIMITE_PRODUTOS_VARREDURA) break;
        }
        return melhor;
    }

    static class EntradaSupermercado {
        final String nome;
        final Supermercado sm;

        EntradaSupermercado(String nome, Supermercado sm) {
            this.nome = nome;
            this.sm = sm;
        }
    }
}