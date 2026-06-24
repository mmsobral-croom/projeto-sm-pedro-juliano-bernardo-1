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

    // Adiciona um supermercado na lista, criando sua cache persistente
    public void adicionaSupermercado(String nome, Supermercado sm) {
        CacheSupermercado cache = new CacheSupermercado("cache/" + nome + ".json");
        supermercados.adiciona(new EntradaSupermercado(nome, sm, cache));
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

            // Busca os produtos do termo, primeiro na cache, e só na API se a cache não tiver nenhum produto que satisfaça a busca
            ListaSequencial<Produto> produtos = produtosDe(entrada, termo);

            int verificados = 0;

            // Percorre os produtos encontrados
            for (int j = 0; j < produtos.comprimento(); j++) {

                Produto p = produtos.obtem(j);

                // Limita a quantidade de produtos analisados
                if (verificados >= LIMITE_VARREDURA) break;

                verificados++;

                // Ignora produtos inválidos
                if (p == null || !p.isDisponivel() || p.getPreco() <= 0f) continue;

                // Se o produto possuir EAN
                if (p.getEan() != null && !p.getEan().isBlank()) {

                    // Agrupa pelo código EAN
                    porEan.computeIfAbsent(p.getEan(), k -> new HashMap<>())
                            .put(entrada.nome, p);

                } else {

                    // Normaliza o nome do produto
                    String nomeNorm = normaliza(p.getNome());

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

    // Obtém os produtos de um supermercado para o termo de busca
    // Consulta a cache e só acessa a API quando a cache não possui nenhum produto da busca, e então armazena o resultado da consulta na cache
    private ListaSequencial<Produto> produtosDe(EntradaSupermercado entrada, String termo) {

        // 1. Tenta na cache (evita o acesso à API se já houver resultado)
        ListaSequencial<Produto> achados = entrada.cache.busca(termo);

        if (achados.comprimento() > 0) return achados;

        // 2. Cache não tem o termo, então consulta a API e guarda o resultado na cache
        Supermercado.Resultado resultado = entrada.sm.busca(termo);

        if (resultado != null) {

            int guardados = 0;

            for (Produto p : resultado) {

                if (guardados >= LIMITE_VARREDURA) break;

                entrada.cache.guarda(p);

                guardados++;
            }
        }

        // 3. Re-busca na cache, agora populada
        return entrada.cache.busca(termo);
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
            if (nome == null) nome = p.getNome();

            // Adiciona o preço do supermercado
            precos.adiciona(
                    new ProdutoComum.EntradaPreco(nomeSm, p.getPreco(), p.getId())
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

    // Classe usada para armazenar supermercado, nome e sua cache
    static class EntradaSupermercado {

        // Nome do supermercado
        final String nome;

        // Objeto do supermercado
        final Supermercado sm;

        // Cache do supermercado
        final CacheSupermercado cache;

        // Construtor da classe
        EntradaSupermercado(String nome, Supermercado sm, CacheSupermercado cache) {
            this.nome = nome;
            this.sm = sm;
            this.cache = cache;
        }
    }
}