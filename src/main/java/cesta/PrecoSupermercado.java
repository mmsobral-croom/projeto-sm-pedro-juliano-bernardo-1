package cesta;

import esd.ListaSequencial;
import sm.Produto;

public class PrecoSupermercado implements Comparable<PrecoSupermercado> {
    private final String nomeSupermercado;
    private final float total;
    private final ListaSequencial<Produto> produtosEscolhidos;
    private final ListaSequencial<ItemCesta> itensFaltantes;

    public PrecoSupermercado(String nomeSupermercado,
                             float total,
                             ListaSequencial<Produto> produtosEscolhidos,
                             ListaSequencial<ItemCesta> itensFaltantes) {
        if (nomeSupermercado == null || nomeSupermercado.isBlank()) {
            throw new IllegalArgumentException("Nome do supermercado não pode ser vazio");
        }
        if (produtosEscolhidos == null || itensFaltantes == null) {
            throw new IllegalArgumentException("Listas auxiliares não podem ser nulas");
        }
        this.nomeSupermercado = nomeSupermercado;
        this.total = total;
        this.produtosEscolhidos = produtosEscolhidos;
        this.itensFaltantes = itensFaltantes;
    }

    public String nomeSupermercado() {
        return nomeSupermercado;
    }

    public float total() {
        return total;
    }

    public ListaSequencial<Produto> produtosEscolhidos() {
        return produtosEscolhidos;
    }

    public ListaSequencial<ItemCesta> itensFaltantes() {
        return itensFaltantes;
    }

    public boolean cestaCompleta() {
        return itensFaltantes.esta_vazia();
    }

    @Override
    public int compareTo(PrecoSupermercado outro) {
        if (this.cestaCompleta() && !outro.cestaCompleta()) return -1;
        if (!this.cestaCompleta() && outro.cestaCompleta()) return 1;
        return Float.compare(this.total, outro.total);
    }

    @Override
    public String toString() {
        return String.format("%s: R$ %.2f%s",
                nomeSupermercado,
                total,
                cestaCompleta() ? "" : " (cesta incompleta)");
    }
}