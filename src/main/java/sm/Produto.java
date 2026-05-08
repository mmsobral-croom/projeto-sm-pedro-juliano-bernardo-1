package sm;

import lombok.*;
import lombok.experimental.Accessors;
import org.json.JSONArray;
import org.json.JSONObject;

// Cria automaticamente construtor, getters e outros métodos
@Builder
@Value

// Permite usar métodos no formato produto.nome()
@Accessors(fluent = true)

// Classe que representa um produto
public class Produto {

    // Nome do produto
    String nome;

    // ID do produto
    String id;

    // Marca do produto
    String marca;

    // Preço do produto
    float preco;

    // Código EAN do produto
    String ean;

    // Indica se o produto está disponível
    boolean disponivel;

    // Obtém as informações de oferta do JSON
    static JSONObject getOffer(JSONObject obj) {

        // Lista de itens do produto
        JSONArray items = obj.getJSONArray("items");

        // Pega o primeiro item
        JSONObject item = items.getJSONObject(0);

        // Lista de vendedores
        JSONArray sellers = item.getJSONArray("sellers");

        // Pega o primeiro vendedor
        JSONObject seller = sellers.getJSONObject(0);

        // Obtém as informações comerciais da oferta
        JSONObject offer = seller.getJSONObject("commertialOffer");

        return offer;
    }

    // Cria um builder de Produto a partir de um JSON
    static Produto.ProdutoBuilder fromJsonBuilder(JSONObject obj) {

        // Define informações básicas do produto
        Produto.ProdutoBuilder pb = Produto.builder()
                .nome((String)obj.get("productName"))
                .id((String)obj.get("productId"))
                .marca((String)obj.get("brand"));

        try {

            // Obtém a lista de itens
            JSONArray items = obj.getJSONArray("items");

            // Pega o primeiro item
            JSONObject item = items.getJSONObject(0);

            // Obtém o código EAN
            String ean = item.getString("ean");

            // Define o EAN no builder
            pb.ean(ean);

        } catch (Exception e) {

            // Ignora erros caso o EAN não exista
//                        IO.println(e);
        }

        try {

            // Obtém os dados da oferta
            JSONObject offer = Produto.getOffer(obj);

            // Obtém o preço do produto
            float preco = offer.getBigDecimal("Price").floatValue();

            // Define o preço
            pb.preco(preco);

            // Verifica disponibilidade
            boolean available = offer.getBoolean("IsAvailable");

            // Define disponibilidade
            pb.disponivel(available);

        } catch (Exception e) {

            // Ignora erros caso os dados da oferta não existam
//                        IO.println(e);
        }

        return pb;
    }

    // Cria um objeto Produto completo a partir do JSON
    static Produto fromJson(JSONObject obj) {

        // Cria o builder com os dados do JSON
        var pb = Produto.fromJsonBuilder(obj);

        // Retorna o produto pronto
        return pb.build();
    }

}