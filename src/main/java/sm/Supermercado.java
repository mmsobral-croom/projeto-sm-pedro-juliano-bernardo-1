package sm;

import esd.ListaSequencial;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Classe responsável por realizar buscas de produtos no supermercado
public class Supermercado {

    // Cliente usado para enviar requisições HTTP
    HttpClient cliente;

    // URL base da API do supermercado
    String url;

    // Expressão regular usada para interpretar informações de paginação
    final Pattern re_resources = Pattern.compile("(\\d+)-(\\d+)/(\\d+)", Pattern.CASE_INSENSITIVE);

    // Quantidade máxima de produtos por consulta
    final int query_len = 40;

    // Classe que representa o resultado de uma busca
    public class Resultado implements Iterable<Produto> {

        // Produto pesquisado
        String produto;

        // Quantidade total de resultados
        int total;

        // Lista de produtos encontrados
        ListaSequencial<Produto> produtos;

        // Referência ao supermercado
        Supermercado sm;

        // Construtor da classe
        Resultado(Supermercado sm, String produto, ListaSequencial<Produto> produtos, int total) {
            this.produto = produto;
            this.produtos = produtos;
            this.total = total;
            this.sm = sm;
        }

        // Retorna um iterador para percorrer os produtos
        @Override
        public Iterador iterator() {
            return new Iterador(produtos, total);
        }

        // Cria um Stream dos produtos
        public Stream<Produto> stream() {
            return StreamSupport.stream(new ResultIterator(this), false);
        }

        // Classe usada para suporte ao Stream
        class ResultIterator implements Spliterator<Produto> {

            Iterador it;

            ResultIterator(Resultado res) {
                this.it = res.iterator();
            }

            // Percorre todos os produtos restantes
            public void forEachRemaining(Consumer<? super Produto> action) {
                while (it.hasNext()) {
                    action.accept(it.next());
                }
            }

            // Avança um produto por vez
            public boolean tryAdvance(Consumer<? super Produto> action) {
                if (it.hasNext()) {
                    action.accept(it.next());
                    return true;
                }
                else
                    return false;
            }

            // Não divide o processamento
            public Spliterator<Produto> trySplit() {
                return null;
            }

            // Retorna quantidade aproximada de itens restantes
            public long estimateSize() {
                return (long)(it.total - it.inicio);
            }

            // Define características da coleção
            public int characteristics() {
                return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
            }
        }

        // Classe usada para percorrer os produtos da busca
        class Iterador implements Iterator<Produto> {

            // Total de produtos
            int total;

            // Índice atual
            int inicio = 0;

            // Lista de produtos carregados
            ListaSequencial<Produto> produtos;

            // Construtor do iterador
            Iterador(ListaSequencial<Produto> produtos, int total) {
                this.produtos = produtos;
                this.total = total;
            }

            // Verifica se ainda existem produtos
            @Override
            public boolean hasNext() {
                return total > inicio;
            }

            // Retorna o próximo produto
            @Override
            public Produto next() {

                // Verifica se ainda há itens
                if (! hasNext()) {
                    throw new NoSuchElementException("fim da iteração");
                }

                // Obtém o produto atual
                Produto prod = produtos.obtem(inicio++);

                // Verifica se precisa carregar mais produtos
                if (inicio >= produtos.comprimento()) {

                    if (inicio < total) {

                        // Busca mais produtos da próxima página
                        var mais_produtos = sm.busca_proximo(produto, inicio);

                        if (produtos != null) {

                            // Adiciona os novos produtos na lista
                            for (int j=0; j < mais_produtos.comprimento(); j++) {
                                produtos.adiciona(mais_produtos.obtem(j));
                            }
                        }
                    }
                }

                return prod;
            }
        }
    }

    // Construtor da classe
    public Supermercado(String url)  {

        // Cria o cliente HTTP
        cliente = HttpClient.newHttpClient();

        // Define a URL base da API
        this.url = url + "/api/catalog_system/pub/products/search/";
    }

    // Monta a URL de busca de produtos
    String make_url(String produto, int inicio) {

        StringBuilder sb = new StringBuilder();

        sb.append(this.url);
        sb.append("?ft=");

        // Codifica o texto da busca
        sb.append(URLEncoder.encode(produto, StandardCharsets.UTF_8));

        sb.append("&_from=");
        sb.append(Integer.toString(inicio));

        sb.append("&_to=");
        sb.append(Integer.toString(inicio + query_len - 1));

        return sb.toString();
    }

    // Monta a URL para buscar um produto pelo ID
    String make_get_url(String produtoId) {

        StringBuilder sb = new StringBuilder();

        sb.append(this.url);
        sb.append("?fq=productId:");
        sb.append(produtoId);

        return sb.toString();
    }

    // Monta a URL para buscar um produto pelo EAN
    String make_ean_url(String ean) {

        StringBuilder sb = new StringBuilder();

        sb.append(this.url);
        sb.append("?fq=ean:");

        // Codifica o EAN
        sb.append(URLEncoder.encode(ean, StandardCharsets.UTF_8));

        return sb.toString();
    }

    // Envia uma requisição HTTP
    HttpResponse<String> envia(String url) {

        HttpResponse<String> response = null;

        try {

            // Cria a requisição HTTP
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()

                    // Define o navegador usado na requisição
                    .setHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64; rv:140.0) Gecko/20100101 Firefox/140.0")
                    .build();

            try {

                // Envia a requisição
                response = cliente.send(req, HttpResponse.BodyHandlers.ofString());

            } catch (IOException | InterruptedException e) {

                // Ignora erros de envio
            }

        } catch (URISyntaxException e) {

            // Ignora erros de URL inválida
        }

        return response;
    }

    // Extrai os produtos do JSON retornado pela API
    ListaSequencial<Produto> extrai_produtos(HttpResponse<String> response) {

        ListaSequencial<Produto> r = new ListaSequencial<>();

        var headers = response.headers().map();

        // Verifica se a resposta é JSON
        boolean isJson = headers.get("content-type")
                .stream()
                .anyMatch(x -> x.startsWith("application/json"));

        if (isJson) {

            // Converte o corpo da resposta em JSONArray
            JSONArray jo = new JSONArray(response.body());

            // Percorre os produtos encontrados
            for (var o : jo) {

                JSONObject obj = (JSONObject) o;

                // Converte JSON em objeto Produto
                r.adiciona(Produto.fromJson(obj));
            }
        }

        return r;
    }

    // Busca a próxima página de produtos
    ListaSequencial<Produto> busca_proximo(String produto, int inicio) {

        HttpResponse<String> response = envia(make_url(produto, inicio));

        if (response != null) {

            int status = response.statusCode();

            // Verifica se a resposta foi bem sucedida
            if (status == 200 || status == 206) {
                return extrai_produtos(response);
            }
        }

        return null;
    }

    // Obtém informações da paginação
    int[] obtem_info_paginas(HttpResponse<String> response) {

        var headers = response.headers().map();

        // Obtém o cabeçalho de paginação
        String faixa = headers.get("resources").getFirst();

        var m = re_resources.matcher(faixa);

        // Vetor com informações da página
        int paginas[] = {0, 9, 10};

        // Extrai os valores da expressão regular
        if (m.find()) {

            paginas[0] = Integer.parseInt(m.group(1));
            paginas[1] = Integer.parseInt(m.group(2));
            paginas[2] = Integer.parseInt(m.group(3));

            paginas[1] = Math.min(paginas[1], paginas[2]);
        }

        return paginas;
    }

    // Busca produtos pelo nome
    public Resultado busca(String produto) {

        Resultado res = null;

        HttpResponse<String> response = envia(make_url(produto, 0));

        if (response != null) {

            int status = response.statusCode();

            // Verifica se a resposta foi válida
            if (status == 200 || status == 206) {

                // Extrai os produtos encontrados
                ListaSequencial<Produto> r = extrai_produtos(response);

                // Obtém informações da paginação
                int[] faixa = obtem_info_paginas(response);

                // Cria o resultado da busca
                res = new Resultado(this, produto, r, faixa[2]);
            }
        }

        return res;
    }

    // Busca produtos pelo código EAN
    public Resultado buscaPorEan(String ean) {

        Resultado res = null;

        HttpResponse<String> response = envia(make_ean_url(ean));

        if (response != null) {

            int status = response.statusCode();

            // Verifica se a resposta foi válida
            if (status == 200 || status == 206) {

                ListaSequencial<Produto> r = extrai_produtos(response);

                int[] faixa = obtem_info_paginas(response);

                res = new Resultado(this, ean, r, faixa[2]);
            }
        }

        return res;
    }

    // Busca um produto específico pelo ID
    public Produto obtem(String produto_id) {

        Produto prod = null;

        HttpResponse<String> response = envia(make_get_url(produto_id));

        if (response != null) {

            int status = response.statusCode();

            // Verifica se a resposta foi bem sucedida
            if (status == 200) {

                var headers = response.headers().map();

                // Verifica se o retorno é JSON
                boolean isJson = headers.get("content-type")
                        .stream()
                        .anyMatch(x -> x.startsWith("application/json"));

                if (isJson) {

                    // Converte o JSON em produto
                    JSONArray jo = new JSONArray(response.body());

                    prod = Produto.fromJson(jo.getJSONObject(0));
                }
            }
        }

        return prod;
    }
}