import cesta.*;
import esd.ListaSequencial;
import sm.Bistek;
import sm.Fort;
import sm.Giassi;
import sm.Supermercado;

import java.util.HashMap;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    static void main() {

        // Cria os supermercados
        Bistek bistek = new Bistek();
        Giassi giassi = new Giassi();
        Fort fort = new Fort();

        // Cria o comparador de preços
        ComparadorPrecos comparador = criaComparador(bistek, giassi, fort);

        // Cria o buscador de produtos
        BuscadorCesta buscador = criaBuscador(bistek, giassi, fort);

        // Cria a cesta de compras
        Cesta cesta = new Cesta();

        System.out.println("=========================================");
        System.out.println("  Buscador do Melhor Preço de Cesta");
        System.out.println("=========================================");

        // Loop principal do menu
        while (true) {

            mostraMenu();

            System.out.print("Opção: ");

            // Verifica se existe entrada do usuário
            if (!scanner.hasNextLine()) {
                System.out.println("");
                return;
            }

            // Lê a opção digitada
            String linha = scanner.nextLine().trim();

            // Executa a opção escolhida
            switch (linha.trim()) {

                // Adicionar produto na cesta
                case "1" -> adicionarProduto(cesta, buscador);

                // Mostrar itens da cesta
                case "2" -> listarCesta(cesta);

                // Finalizar cesta e comparar preços
                case "3" -> {
                    terminarCesta(cesta, comparador);
                    return;
                }

                // Encerrar programa
                case "4" -> {
                    System.out.println("Até logo!");
                    return;
                }

                // Caso a opção seja inválida
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    // Mostra o menu principal
    static void mostraMenu() {

        System.out.println("");
        System.out.println("--- Menu ---");
        System.out.println("1) Adicionar produto");
        System.out.println("2) Listar cesta de produtos");
        System.out.println("3) Terminar cesta");
        System.out.println("4) Sair");
    }

    // Adiciona um produto na cesta
    static void adicionarProduto(Cesta cesta, BuscadorCesta buscador) {

        // Lê o nome do produto
        String termo = leLinha("Produto: ");

        // Verifica se o nome foi informado
        if (termo.isEmpty()) {
            System.out.println("Nome vazio — cancelado.");
            return;
        }

        System.out.println("Buscando em todos os supermercados... (aguarde)");

        // Busca produtos em comum nos supermercados
        ListaSequencial<ProdutoComum> comuns = buscador.buscaEmTodos(termo);

        // Verifica se encontrou resultados
        if (comuns.esta_vazia()) {

            System.out.println("Nenhum produto em comum encontrado para \"" + termo + "\".");
            System.out.println("Tente um termo mais genérico.");

            return;
        }

        System.out.println("");
        System.out.println("Produtos encontrados em todos os supermercados:");

        // Exibe os produtos encontrados
        for (int i = 0; i < comuns.comprimento(); i++) {

            ProdutoComum pc = comuns.obtem(i);

            StringBuilder sb = new StringBuilder();

            // Exibe número e nome do produto
            sb.append(i + 1).append(". ").append(pc.nome());

            // Exibe os preços em cada supermercado
            for (int j = 0; j < pc.precos().comprimento(); j++) {

                ProdutoComum.EntradaPreco ep = pc.precos().obtem(j);

                if (j == 0) sb.append("\n   ");

                sb.append(ep.nomeSm())
                        .append(": R$ ")
                        .append(String.format("%.2f", ep.preco()));

                // Adiciona separador entre os preços
                if (j < pc.precos().comprimento() - 1) sb.append(" | ");
            }

            System.out.println(sb.toString());
        }

        // Lê a opção escolhida
        int escolha = leInteiro("Escolha (1-" + comuns.comprimento() + ") ou 0 para cancelar: ");

        // Cancela caso escolha 0
        if (escolha == 0) return;

        // Verifica se a escolha é válida
        if (escolha < 1 || escolha > comuns.comprimento()) {

            System.out.println("Opção inválida — cancelado.");

            return;
        }

        // Obtém o produto escolhido
        ProdutoComum escolhido = comuns.obtem(escolha - 1);

        // Lê a quantidade desejada
        int qtd = leInteiro("Quantidade: ");

        // Verifica se a quantidade é válida
        if (qtd <= 0) {

            System.out.println("Quantidade inválida — cancelado.");

            return;
        }

        // Mapa com os IDs do produto em cada supermercado
        HashMap<String, String> ids = new HashMap<>();

        // Salva os IDs dos produtos
        for (int i = 0; i < escolhido.precos().comprimento(); i++) {

            ProdutoComum.EntradaPreco ep = escolhido.precos().obtem(i);

            ids.put(ep.nomeSm(), ep.productId());
        }

        // Cria o item da cesta
        ItemCesta item = ItemCesta.comIds(
                termo,
                escolhido.nome(),
                ids,
                qtd
        );

        // Adiciona na cesta
        cesta.adiciona(item);

        System.out.println("Adicionado: " + qtd + " x \"" + escolhido.nome() + "\"");
    }

    // Lista os produtos da cesta
    static void listarCesta(Cesta cesta) {

        // Verifica se a cesta está vazia
        if (cesta.estaVazia()) {

            System.out.println("Cesta vazia.");

            return;
        }

        System.out.println("Cesta atual:");

        // Percorre todos os itens da cesta
        for (int i = 0; i < cesta.comprimento(); i++) {

            System.out.println("  - " + cesta.obtem(i));
        }
    }

    // Finaliza a cesta e mostra os preços
    static void terminarCesta(Cesta cesta, ComparadorPrecos comparador) {

        // Verifica se a cesta possui itens
        if (cesta.estaVazia()) {

            System.out.println("Cesta vazia. Nada a calcular.");

            return;
        }

        System.out.println("");
        System.out.println("Calculando preços nos supermercados...");

        // Compara os preços da cesta
        ListaSequencial<PrecoSupermercado> resultados = comparador.compara(cesta);

        System.out.println("");
        System.out.println("Supermercados ordenados pelo preço da cesta:");

        // Exibe os resultados
        for (int i = 0; i < resultados.comprimento(); i++) {

            PrecoSupermercado p = resultados.obtem(i);

            System.out.println(String.format(
                    "  %d. %s: R$ %.2f%s",
                    i + 1,
                    p.nomeSupermercado(),
                    p.total(),

                    // Exibe aviso caso a cesta esteja incompleta
                    p.cestaCompleta() ? "" : " (cesta incompleta)"
            ));
        }
    }

    // Cria o comparador de preços
    static ComparadorPrecos criaComparador(
            Supermercado bistek,
            Supermercado giassi,
            Supermercado fort
    ) {

        ComparadorPrecos c = new ComparadorPrecos();

        // Adiciona supermercados no comparador
        c.adicionaSupermercado("Giassi", giassi);
        c.adicionaSupermercado("Bistek", bistek);
        c.adicionaSupermercado("Fort Atacadista", fort);

        return c;
    }

    // Cria o buscador de produtos
    static BuscadorCesta criaBuscador(
            Supermercado bistek,
            Supermercado giassi,
            Supermercado fort
    ) {

        BuscadorCesta b = new BuscadorCesta();

        // Adiciona supermercados no buscador
        b.adicionaSupermercado("Giassi", giassi);
        b.adicionaSupermercado("Bistek", bistek);
        b.adicionaSupermercado("Fort Atacadista", fort);

        return b;
    }

    // Lê uma linha digitada pelo usuário
    static String leLinha(String prompt) {

        System.out.print(prompt);

        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }

        return "";
    }

    // Lê um número inteiro
    static int leInteiro(String prompt) {

        try {

            return Integer.parseInt(leLinha(prompt));

        } catch (NumberFormatException e) {

            // Retorna -1 caso o valor seja inválido
            return -1;
        }
    }
}