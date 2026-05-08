package cesta;

import esd.ListaSequencial;
import sm.Produto;
import sm.Supermercado;

import java.util.HashMap;
import java.util.Map;

// Classe responsável por buscar produtos em vários supermercados
public class BuscadorCesta {

    // Limite máximo de produtos analisados por busca
    static final int LIMITE_VARREDURA = 100;

    // Lista de supermercados cadastrados
    private final ListaSequencial<EntradaSupermercado> supermercados = new ListaSequencial<>();

    // Adiciona um supermercado na lista
    public void adicionaSupermercado(String nome, Supermercado sm) {
        supermercados.adiciona(new EntradaSupermercado(nome, sm));
    }

    // Retorna a quantidade de supermercados cadastrados
    public int quantidadeSupermercados() {
        return supermercados.comprimento();
    }

    // Busca um produto em todos os supermercados
    public ListaSequencial<ProdutoComum> buscaEmTodos(String termo) {

        int numSms = supermercados.comprimento();

        // Retorna lista vazia se não houver supermercados
        if (numSms == 0) return new ListaSequencial<>();

        // Mapa usado para agrupar produtos por EAN
        // EAN -> supermercado -> produto
        Map<String, Map<String, Produto>> porEan = new HashMap<>();

        // Mapa usado para produtos sem EAN
        // nome normalizado -> supermercado -> produto
        Map<String, Map<String, Produto>> porNome = new HashMap<>();

        // Percorre todos os supermercados
        for (int i = 0; i < numSms; i++) {

            EntradaSupermercado entrada = supermercados.obtem(i);

            // Realiza a busca do produto
            Supermercado.Resultado resultado = entrada.sm.busca(termo);

            if (resultado == null) continue;

            int verificados = 0;

            // Percorre os produtos encontrados
            for (Produto p : resultado) {

                // Limita a quantidade de produtos analisados
                if (verificados >= LIMITE_VARREDURA) break;

                verificados++;

                // Ignora produtos inválidos
                if (p == null || !p.disponivel() || p.preco() <= 0f) continue;

                // Se o produto possuir EAN
                if (p.ean() != null && !p.ean().isBlank()) {

                    // Agrupa pelo código EAN
                    porEan.computeIfAbsent(p.ean(), k -> new HashMap<>())
                            .put(entrada.nome, p);

                } else {

                    // Normaliza o nome do produto
                    String nomeNorm = normaliza(p.nome());

                    // Agrupa pelo nome
                    porNome.computeIfAbsent(nomeNorm, k -> new HashMap<>())
                            .put(entrada.nome, p);
                }
            }
        }

        // Lista final de produtos encontrados em todos os supermercados
        ListaSequencial<ProdutoComum> resultado = new ListaSequencial<>();

        // Procura produtos com EAN presentes em todos os supermercados
        for (Map.Entry<String, Map<String, Produto>> entry : porEan.entrySet()) {

            // Verifica se o produto apareceu em todos os mercados
            if (entry.getValue().size() < numSms) continue;

            // Monta o objeto ProdutoComum
            ProdutoComum pc = montaProdutoComum(entry.getValue(), entry.getKey());

            if (pc != null) resultado.adiciona(pc);
        }

        // Procura produtos sem EAN usando nome normalizado
        for (Map.Entry<String, Map<String, Produto>> entry : porNome.entrySet()) {

            // Verifica se apareceu em todos os supermercados
            if (entry.getValue().size() < numSms) continue;

            ProdutoComum pc = montaProdutoComum(entry.getValue(), null);

            if (pc != null) resultado.adiciona(pc);
        }

        // Ordena os produtos pelo preço médio
        ordenaPorPrecoMedio(resultado);

        return resultado;
    }

    // Monta um objeto ProdutoComum
    private ProdutoComum montaProdutoComum(Map<String, Produto> porSm, String ean) {

        // Lista de preços do produto
        ListaSequencial<ProdutoComum.EntradaPreco> precos = new ListaSequencial<>();

        String nome = null;

        // Percorre todos os supermercados
        for (int i = 0; i < supermercados.comprimento(); i++) {

            String nomeSm = supermercados.obtem(i).nome;

            Produto p = porSm.get(nomeSm);

            // Se faltar em algum supermercado, retorna null
            if (p == null) return null;

            // Define o nome do produto
            if (nome == null) nome = p.nome();

            // Adiciona o preço do supermercado
            precos.adiciona(
                    new ProdutoComum.EntradaPreco(nomeSm, p.preco(), p.id())
            );
        }

        // Retorna o produto completo
        return new ProdutoComum(nome, ean, precos);
    }

    // Ordena os produtos pelo menor preço médio
    private void ordenaPorPrecoMedio(ListaSequencial<ProdutoComum> lista) {

        int n = lista.comprimento();

        // Algoritmo Bubble Sort
        for (int i = 0; i < n - 1; i++) {

            for (int j = 0; j < n - 1 - i; j++) {

                // Compara os preços médios
                if (lista.obtem(j).precoMedio() >
                        lista.obtem(j + 1).precoMedio()) {

                    // Troca os produtos de posição
                    ProdutoComum tmp = lista.obtem(j);

                    lista.substitui(j, lista.obtem(j + 1));

                    lista.substitui(j + 1, tmp);
                }
            }
        }
    }

    // Normaliza o nome do produto para facilitar comparações
    private static String normaliza(String nome) {

        // Retorna vazio caso o nome seja nulo
        if (nome == null) return "";

        // Converte para minúsculo e remove espaços extras
        return nome.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }

    // Classe usada para armazenar supermercado e nome
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