package cesta;

import esd.ListaSequencial;
import sm.Produto;

// Classe que representa o preço total da cesta em um supermercado
public class PrecoSupermercado implements Comparable<PrecoSupermercado> {

    // Nome do supermercado
    private final String nomeSupermercado;

    // Valor total da cesta
    private final float total;

    // Lista de produtos encontrados
    private final ListaSequencial<Produto> produtosEscolhidos;

    // Lista de itens que não foram encontrados
    private final ListaSequencial<ItemCesta> itensFaltantes;

    // Construtor da classe
    public PrecoSupermercado(String nomeSupermercado,
                             float total,
                             ListaSequencial<Produto> produtosEscolhidos,
                             ListaSequencial<ItemCesta> itensFaltantes) {

        // Verifica se o nome do supermercado é válido
        if (nomeSupermercado == null || nomeSupermercado.isBlank()) {
            throw new IllegalArgumentException("Nome do supermercado não pode ser vazio");
        }

        // Verifica se as listas foram informadas
        if (produtosEscolhidos == null || itensFaltantes == null) {
            throw new IllegalArgumentException("Listas auxiliares não podem ser nulas");
        }

        this.nomeSupermercado = nomeSupermercado;
        this.total = total;
        this.produtosEscolhidos = produtosEscolhidos;
        this.itensFaltantes = itensFaltantes;
    }

    // Retorna o nome do supermercado
    public String nomeSupermercado() {
        return nomeSupermercado;
    }

    // Retorna o valor total da cesta
    public float total() {
        return total;
    }

    // Retorna os produtos encontrados
    public ListaSequencial<Produto> produtosEscolhidos() {
        return produtosEscolhidos;
    }

    // Retorna os itens que faltaram
    public ListaSequencial<ItemCesta> itensFaltantes() {
        return itensFaltantes;
    }

    // Verifica se todos os itens da cesta foram encontrados
    public boolean cestaCompleta() {
        return itensFaltantes.esta_vazia();
    }

    // Compara os preços entre supermercados
    @Override
    public int compareTo(PrecoSupermercado outro) {

        // Supermercados com cesta completa têm prioridade
        if (this.cestaCompleta() && !outro.cestaCompleta()) return -1;

        if (!this.cestaCompleta() && outro.cestaCompleta()) return 1;

        // Se ambos forem iguais, compara pelo menor preço
        return Float.compare(this.total, outro.total);
    }

    // Define como o objeto será exibido em texto
    @Override
    public String toString() {

        return String.format("%s: R$ %.2f%s",
                nomeSupermercado,
                total,

                // Exibe aviso caso a cesta esteja incompleta
                cestaCompleta() ? "" : " (cesta incompleta)");
    }
}