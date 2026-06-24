package esd;

import java.security.InvalidParameterException;

public class TabHash<K, V> {
    public class Par {
        K chave;
        V valor;

        Par(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }

        public K obtemChave() {
            return chave;
        }

        public V obtemValor() {
            return valor;
        }

        @Override
        public boolean equals(Object outro) {
            if (!(outro instanceof TabHash.Par)) return false;
            Par _outro = (Par) outro;
            return chave.equals(_outro.chave);
        }
    }

    ListaSequencial<Par>[] tab;
    int len = 0; // quantos pares estao armazenados
    final int defcap = 31;
    double maxFatorCarga;

    public TabHash() {
        // dimensiona a tabela
        tab = inicia_tabela(defcap);
        maxFatorCarga = 0.75;
    }

    @SuppressWarnings("unchecked")
    ListaSequencial<Par>[] inicia_tabela(int linhas) {
        // inicia o array de listas com essa quantidade de linhas
        ListaSequencial<Par>[] nova = (ListaSequencial<Par>[]) new ListaSequencial[linhas];
        return nova;
    }

    public void setMaxFatorCarga(double fator) {
        if (fator < 0) throw new InvalidParameterException("Fator inválido");
        maxFatorCarga = fator;
    }

    public double fator_carga() {
        double f = len;
        return f / tab.length;
    }

    void expande() {
        var old = tab;
        tab = inicia_tabela(2 * tab.length);
        len = 0; // sera recontado por adiciona() abaixo

        for (var lp : old) {
            if (lp != null) {
                for (int i = 0; i < lp.comprimento(); i++) {
                    Par p = lp.obtem(i);
                    adiciona(p.chave, p.valor);
                }
            }
        }
    }

    public void adiciona(K chave, V valor) {
        if (fator_carga() > maxFatorCarga) {
            expande();
        }
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares == null) {
            pares = new ListaSequencial<>();
            tab[linha] = pares;
        }
        for (int i = 0; i < pares.comprimento(); i++) {
            Par p = pares.obtem(i);
            if (chave.equals(p.chave)) {
                p.valor = valor;
                return;
            }
        }
        pares.adiciona(new Par(chave, valor));
        len++;
    }

    public V obtem(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares != null) {
            for (int i = 0; i < pares.comprimento(); i++) {
                Par p = pares.obtem(i);
                if (chave.equals(p.chave)) {
                    return p.valor;
                }
            }
        }
        throw new IndexOutOfBoundsException("chave inexistente");
    }

    public void remove(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares != null) {
            for (int i = 0; i < pares.comprimento(); i++) {
                Par p = pares.obtem(i);
                if (chave.equals(p.chave)) {
                    pares.remove(i);
                    len--;
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException("chave inexistente");
    }

    public boolean contem(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares != null) {
            for (int i = 0; i < pares.comprimento(); i++) {
                Par p = pares.obtem(i);
                if (chave.equals(p.chave)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean esta_vazia() {
        return len == 0;
    }

    public V obtem_ou_default(K chave, V defval) {
        if (contem(chave)) {
            return obtem(chave);
        }
        return defval;
    }

    public ListaSequencial<K> chaves() {
        ListaSequencial<K> lk = new ListaSequencial<>();
        for (ListaSequencial<Par> pares : tab) {
            if (pares != null) {
                for (int i = 0; i < pares.comprimento(); i++) {
                    Par p = pares.obtem(i);
                    lk.adiciona(p.chave);
                }
            }
        }
        return lk;
    }

    public ListaSequencial<V> valores() {
        ListaSequencial<V> lv = new ListaSequencial<>();
        for (ListaSequencial<Par> pares : tab) {
            if (pares != null) {
                for (int i = 0; i < pares.comprimento(); i++) {
                    Par p = pares.obtem(i);
                    lv.adiciona(p.valor);
                }
            }
        }
        return lv;
    }

    public ListaSequencial<Par> items() {
        ListaSequencial<Par> lp = new ListaSequencial<>();
        for (ListaSequencial<Par> pares : tab) {
            if (pares != null) {
                for (int i = 0; i < pares.comprimento(); i++) {
                    lp.adiciona(pares.obtem(i));
                }
            }
        }
        return lp;
    }

    public int comprimento() {
        return len;
    }

    public void limpa() {
        // limpa os pares
        tab = inicia_tabela(defcap);
        len = 0;
    }
}