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
        Bistek bistek = new Bistek();
        Giassi giassi = new Giassi();
        Fort fort = new Fort();

        ComparadorPrecos comparador = criaComparador(bistek, giassi, fort);
        BuscadorCesta buscador = criaBuscador(bistek, giassi, fort);
        Cesta cesta = new Cesta();

        System.out.println("=========================================");
        System.out.println("  Buscador do Melhor Preço de Cesta");
        System.out.println("=========================================");

        while (true) {
            mostraMenu();
            System.out.print("Opção: ");
            if (!scanner.hasNextLine()) {
                System.out.println("");
                return;
            }
            String linha = scanner.nextLine().trim();
            switch (linha.trim()) {
                case "1" -> adicionarProduto(cesta, buscador);
                case "2" -> listarCesta(cesta);
                case "3" -> {
                    terminarCesta(cesta, comparador);
                    return;
                }
                case "4" -> {
                    System.out.println("Até logo!");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    static void mostraMenu() {
        System.out.println("");
        System.out.println("--- Menu ---");
        System.out.println("1) Adicionar produto");
        System.out.println("2) Listar cesta de produtos");
        System.out.println("3) Terminar cesta");
        System.out.println("4) Sair");
    }

    static void adicionarProduto(Cesta cesta, BuscadorCesta buscador) {
        String termo = leLinha("Produto: ");
        if (termo.isEmpty()) {
            System.out.println("Nome vazio — cancelado.");
            return;
        }

        System.out.println("Buscando em todos os supermercados... (aguarde)");
        ListaSequencial<ProdutoComum> comuns = buscador.buscaEmTodos(termo);

        if (comuns.esta_vazia()) {
            System.out.println("Nenhum produto em comum encontrado para \"" + termo + "\".");
            System.out.println("Tente um termo mais genérico.");
            return;
        }

        System.out.println("");
        System.out.println("Produtos encontrados em todos os supermercados:");
        for (int i = 0; i < comuns.comprimento(); i++) {
            ProdutoComum pc = comuns.obtem(i);
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1).append(". ").append(pc.nome());
            for (int j = 0; j < pc.precos().comprimento(); j++) {
                ProdutoComum.EntradaPreco ep = pc.precos().obtem(j);
                if (j == 0) sb.append("\n   ");
                sb.append(ep.nomeSm()).append(": R$ ").append(String.format("%.2f", ep.preco()));
                if (j < pc.precos().comprimento() - 1) sb.append(" | ");
            }
            System.out.println(sb.toString());
        }

        int escolha = leInteiro("Escolha (1-" + comuns.comprimento() + ") ou 0 para cancelar: ");
        if (escolha == 0) return;
        if (escolha < 1 || escolha > comuns.comprimento()) {
            System.out.println("Opção inválida — cancelado.");
            return;
        }

        ProdutoComum escolhido = comuns.obtem(escolha - 1);

        int qtd = leInteiro("Quantidade: ");
        if (qtd <= 0) {
            System.out.println("Quantidade inválida — cancelado.");
            return;
        }

        HashMap<String, String> ids = new HashMap<>();
        for (int i = 0; i < escolhido.precos().comprimento(); i++) {
            ProdutoComum.EntradaPreco ep = escolhido.precos().obtem(i);
            ids.put(ep.nomeSm(), ep.productId());
        }
        ItemCesta item = ItemCesta.comIds(termo, escolhido.nome(), ids, qtd);
        cesta.adiciona(item);
        System.out.println("Adicionado: " + qtd + " x \"" + escolhido.nome() + "\"");
    }

    static void listarCesta(Cesta cesta) {
        if (cesta.estaVazia()) {
            System.out.println("Cesta vazia.");
            return;
        }
        System.out.println("Cesta atual:");
        for (int i = 0; i < cesta.comprimento(); i++) {
            System.out.println("  - " + cesta.obtem(i));
        }
    }

    static void terminarCesta(Cesta cesta, ComparadorPrecos comparador) {
        if (cesta.estaVazia()) {
            System.out.println("Cesta vazia. Nada a calcular.");
            return;
        }

        System.out.println("");
        System.out.println("Calculando preços nos supermercados...");
        ListaSequencial<PrecoSupermercado> resultados = comparador.compara(cesta);

        System.out.println("");
        System.out.println("Supermercados ordenados pelo preço da cesta:");
        for (int i = 0; i < resultados.comprimento(); i++) {
            PrecoSupermercado p = resultados.obtem(i);
            System.out.println(String.format("  %d. %s: R$ %.2f%s",
                    i + 1,
                    p.nomeSupermercado(),
                    p.total(),
                    p.cestaCompleta() ? "" : " (cesta incompleta)"));
        }
    }

    static ComparadorPrecos criaComparador(Supermercado bistek, Supermercado giassi, Supermercado fort) {
        ComparadorPrecos c = new ComparadorPrecos();
        c.adicionaSupermercado("Giassi", giassi);
        c.adicionaSupermercado("Bistek", bistek);
        c.adicionaSupermercado("Fort Atacadista", fort);
        return c;
    }

    static BuscadorCesta criaBuscador(Supermercado bistek, Supermercado giassi, Supermercado fort) {
        BuscadorCesta b = new BuscadorCesta();
        b.adicionaSupermercado("Giassi", giassi);
        b.adicionaSupermercado("Bistek", bistek);
        b.adicionaSupermercado("Fort Atacadista", fort);
        return b;
    }

    static String leLinha(String prompt) {
        System.out.print(prompt);
        if (scanner.hasNextLine()) return scanner.nextLine().trim();
        return "";
    }

    static int leInteiro(String prompt) {
        try {
            return Integer.parseInt(leLinha(prompt));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
