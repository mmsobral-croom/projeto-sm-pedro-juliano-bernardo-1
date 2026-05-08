package cesta;

import esd.ListaSequencial;
import sm.Produto;
import sm.Supermercado;

import java.util.HashMap;
import java.util.Map;

public class BuscadorCesta {
    static final int LIMITE_VARREDURA = 100;

    private final ListaSequencial<EntradaSupermercado> supermercados = new ListaSequencial<>();

    public void adicionaSupermercado(String nome, Supermercado sm) {
        supermercados.adiciona(new EntradaSupermercado(nome, sm));
    }

    public int quantidadeSupermercados() {
        return supermercados.comprimento();
    }

    public ListaSequencial<ProdutoComum> buscaEmTodos(String termo) {
        int numSms = supermercados.comprimento();
        if (numSms == 0) return new ListaSequencial<>();

        // mapa EAN -> mapa nomeSm -> Produto
        Map<String, Map<String, Produto>> porEan = new HashMap<>();
        // mapa nomeNorm -> mapa nomeSm -> Produto (fallback sem EAN)
        Map<String, Map<String, Produto>> porNome = new HashMap<>();

        for (int i = 0; i < numSms; i++) {
            EntradaSupermercado entrada = supermercados.obtem(i);
            Supermercado.Resultado resultado = entrada.sm.busca(termo);
            if (resultado == null) continue;

            int verificados = 0;
            for (Produto p : resultado) {
                if (verificados >= LIMITE_VARREDURA) break;
                verificados++;
                if (p == null || !p.disponivel() || p.preco() <= 0f) continue;

                if (p.ean() != null && !p.ean().isBlank()) {
                    porEan.computeIfAbsent(p.ean(), k -> new HashMap<>())
                          .put(entrada.nome, p);
                } else {
                    String nomeNorm = normaliza(p.nome());
                    porNome.computeIfAbsent(nomeNorm, k -> new HashMap<>())
                           .put(entrada.nome, p);
                }
            }
        }

        ListaSequencial<ProdutoComum> resultado = new ListaSequencial<>();

        // produtos com EAN presentes em TODOS os supermercados
        for (Map.Entry<String, Map<String, Produto>> entry : porEan.entrySet()) {
            if (entry.getValue().size() < numSms) continue;
            ProdutoComum pc = montaProdutoComum(entry.getValue(), entry.getKey());
            if (pc != null) resultado.adiciona(pc);
        }

        // fallback: produtos sem EAN com nome normalizado igual em todos os supermercados
        for (Map.Entry<String, Map<String, Produto>> entry : porNome.entrySet()) {
            if (entry.getValue().size() < numSms) continue;
            ProdutoComum pc = montaProdutoComum(entry.getValue(), null);
            if (pc != null) resultado.adiciona(pc);
        }

        ordenaPorPrecoMedio(resultado);
        return resultado;
    }

    private ProdutoComum montaProdutoComum(Map<String, Produto> porSm, String ean) {
        ListaSequencial<ProdutoComum.EntradaPreco> precos = new ListaSequencial<>();
        String nome = null;
        for (int i = 0; i < supermercados.comprimento(); i++) {
            String nomeSm = supermercados.obtem(i).nome;
            Produto p = porSm.get(nomeSm);
            if (p == null) return null;
            if (nome == null) nome = p.nome();
            precos.adiciona(new ProdutoComum.EntradaPreco(nomeSm, p.preco(), p.id()));
        }
        return new ProdutoComum(nome, ean, precos);
    }

    private void ordenaPorPrecoMedio(ListaSequencial<ProdutoComum> lista) {
        int n = lista.comprimento();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (lista.obtem(j).precoMedio() > lista.obtem(j + 1).precoMedio()) {
                    ProdutoComum tmp = lista.obtem(j);
                    lista.substitui(j, lista.obtem(j + 1));
                    lista.substitui(j + 1, tmp);
                }
            }
        }
    }

    private static String normaliza(String nome) {
        if (nome == null) return "";
        return nome.toLowerCase().trim().replaceAll("\\s+", " ");
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
