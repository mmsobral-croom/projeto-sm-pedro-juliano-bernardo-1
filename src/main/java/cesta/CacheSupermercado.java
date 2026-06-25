package cesta;

import esd.ListaSequencial;
import esd.TabHash;
import sm.Produto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// Os produtos são indexados pelo productId em uma TabHash, o que dá busca O(1) por id (usada para atualizar preço/disponibilidade)
// A busca por nome (ou parte do nome) ou por EAN é feita por varredura
public class CacheSupermercado {
    // Produtos indexados pelo id (productId do supermercado)
    private final TabHash<String, Produto> porId = new TabHash<>();

    private final String arquivo;

    public CacheSupermercado(String arquivo) {

        // Verifica se o caminho do arquivo é válido
        if (arquivo == null || arquivo.isBlank()) {
            throw new IllegalArgumentException("Arquivo da cache não pode ser vazio");
        }

        this.arquivo = arquivo;
    }

    // Guarda (ou atualiza) um produto na cache, usando o id como chave
    public void guarda(Produto p) {

        // Ignora produtos inválidos
        if (p == null || p.getId() == null) return;

        porId.adiciona(p.getId(), p);
    }

    public void guardaTodos(ListaSequencial<Produto> produtos) {

        if (produtos == null) return;

        for (int i = 0; i < produtos.comprimento(); i++) {
            guarda(produtos.obtem(i));
        }
    }

    // Retorna o produto com o id informado, ou null se não estiver na cache
    public Produto obtemPorId(String id) {

        if (id == null) return null;

        return porId.obtem_ou_default(id, null);
    }

    public boolean estaVazia() {
        return porId.esta_vazia();
    }

    // Busca os produtos que o nome contém o termo ou que o EAN é igual ao termo
    // Mesma semântica de busca por nome (ou parte do nome) do projeto 1.
    public ListaSequencial<Produto> busca(String termo) {

        ListaSequencial<Produto> encontrados = new ListaSequencial<>();

        if (termo == null || termo.isBlank()) return encontrados;

        // Normaliza o termo para comparação por nome
        String alvo = termo.toLowerCase().trim();

        ListaSequencial<Produto> todos = porId.valores();

        for (int i = 0; i < todos.comprimento(); i++) {

            Produto p = todos.obtem(i);

            if (p == null || p.getNome() == null) continue;

            boolean casaNome = p.getNome().toLowerCase().contains(alvo);

            boolean casaEan = p.getEan() != null && p.getEan().equals(termo.trim());

            if (casaNome || casaEan) {
                encontrados.adiciona(p);
            }
        }

        return encontrados;
    }

    // Lê a cache do arquivo, se ele existir, no inicio do sistema
    public void carrega() {

        File f = new File(arquivo);

        // Sem arquivo ainda, começa com a cache vazia
        if (!f.exists()) return;

        try {

            String conteudo = Files.readString(f.toPath());

            JSONArray arr = new JSONArray(conteudo);

            for (int i = 0; i < arr.length(); i++) {
                guarda(produtoDeJson(arr.getJSONObject(i)));
            }

        } catch (IOException | JSONException e) {

            // Se não conseguir ler ou o arquivo estiver corrompido, segue com a cache vazia em vez de derrubar o programa
        }
    }

    // Grava a cache no arquivo no fim do sistema
    public void salva() {

        JSONArray arr = new JSONArray();

        // Converte cada produto guardado para JSON
        ListaSequencial<Produto> todos = porId.valores();

        for (int i = 0; i < todos.comprimento(); i++) {
            arr.put(produtoParaJson(todos.obtem(i)));
        }

        try {

            File f = new File(arquivo);

            File pasta = f.getParentFile();
            if (pasta != null) pasta.mkdirs();

            Files.writeString(f.toPath(), arr.toString());

        } catch (IOException e) {
            // Se não conseguir gravar a cache só não persiste
        }
    }

    // Converte um Produto em JSON
    private JSONObject produtoParaJson(Produto p) {

        JSONObject o = new JSONObject();

        // put com valor null remove a chave e na leitura tratamos com optString
        o.put("nome", p.getNome());
        o.put("id", p.getId());
        o.put("marca", p.getMarca());
        o.put("preco", p.getPreco());
        o.put("ean", p.getEan());
        o.put("disponivel", p.isDisponivel());

        return o;
    }

    // Reconstrói um Produto a partir do JSON
    private Produto produtoDeJson(JSONObject o) {

        return Produto.builder()
                .nome(o.optString("nome", null))
                .id(o.optString("id", null))
                .marca(o.optString("marca", null))
                .preco((float) o.optDouble("preco", 0))
                .ean(o.optString("ean", null))
                .disponivel(o.optBoolean("disponivel", false))
                .build();
    }
}