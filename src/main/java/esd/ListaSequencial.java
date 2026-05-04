package esd;

import java.util.Random;

public class ListaSequencial<T> {

    T[] area;
    int len = 0;
    final int defcap = 8;

    @SuppressWarnings("unchecked")
    public ListaSequencial() {
        area = (T[])new Object[defcap];
    }

    public boolean esta_vazia() {
        // retorna tue se lista estiver vazia, ou false caso contrário
        return len == 0;
    }

    public int capacidade() {
        // retorna um inteiro que representa a capacidade da lista
        return area.length;
    }

    public void adiciona(T elemento) {
        // adiciona um valor ao final da lista
        if (len == area.length) {
            expande();
        }
        area[len++] = elemento;
    }

    public void insere(T elemento) {
        // insere um valor no início (posição 0)
        // move uma posição para frente os valores a partir dessa posição
        // dispara IndexOutOfBoundsException se "indice" for inválido
        insere(0, elemento);
    }

    public void insere(int indice, T elemento) {
        // insere um valor na posição indicada por "indice"
        // move uma posição para frente os valores a partir dessa posição
        // dispara IndexOutOfBoundsException se "indice" for inválido
        if (indice < 0 || indice > len){
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        if (len == area.length){
            expande();
        }
        for (int i = len; i > indice; i--)
            area[i] = area[i - 1];
        area[indice] = elemento;
        len++;
    }

    @SuppressWarnings("unchecked")
    public void insere_ordenado(Comparable valor) {
        // procura a posição onde inserir o valor
        int i = 0;
        while (i < len && ((Comparable) area[i]).compareTo(valor) <= 0) i++;
        insere(i, (T) valor);
    }

    public T remove(int indice) {
        // remove um valor da posição indicada pelo parâmetro "indice"
        // move uma posição para trás os valores das próximas posições
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        // retorna o valor que foi removido da lista
        if (indice < 0 || indice >= len){
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        T removido = area[indice];
        for (int i = indice; i < len - 1; i++)
            area[i] = area[i + 1];
        area[--len] = null;
        return removido;
    }

    public T remove_rapido(int indice) {
        // remove um valor da posição indica pelo parãmetro índice
        // move o último dado da lista para essa posição
        // dispara IndexOutOfBoundsException se indice for inválido
        // retorna o valor que ofi removido da lista
        if (indice < 0 || indice >= len){
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        T removido = area[indice];
        area[indice] = area[--len];
        area[len] = null;
        return removido;
    }

    public T remove_ultimo() {
        // remove o último valor da lista
        // disparar uma exceção IndexOutOfBoundsException caso lista vazia
        // retorna o valor que foi removido da lista
        if (esta_vazia()){
            throw new IndexOutOfBoundsException("Lista vazia.");
        }
        T removido = area[--len];
        area[len] = null;
        return removido;
    }

    public void remove(T valor) {
        // todo
        int pos = procura(valor);
        if (pos != -1){
            remove(pos);
        }
    }

    public int procura(T valor) {
        // retorna um inteiro que representa aposição onde valor foi encontrado pela primeira vez (contando do início da lista)
        // retorna -1 se não o encontrar !
        for (int i = 0; i < len; i++) {
            if (area[i].equals(valor)) return i;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public boolean esta_ordenada() {
        // implemente aqui o método
        for (int i = 0; i < len - 1; i++) {
            if (((Comparable) area[i]).compareTo(area[i + 1]) > 0){
                return false;
            }
        }
        return true;
    }

    public T obtem(int indice) {
        // retorna o valor armazenado na posição indica pelo parâmetro "indice"
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (indice < 0 || indice >= len){
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        return area[indice];
    }

    public T primeiro() {
        // retorna o valor armazenado no início da lista
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (esta_vazia()){
            throw new IndexOutOfBoundsException("Lista vazia.");
        }
        return area[0];
    }

    public T ultimo() {
        // retorna o valor armazenado no final da lista
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (esta_vazia()){
            throw new IndexOutOfBoundsException("Lista vazia.");
        }
        return area[len - 1];
    }

    public void substitui(int indice, T valor) {
        // armazena o valor na posição indicada por "indice", substituindo o valor lá armazenado atualmente
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (indice < 0 || indice >= len){
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        area[indice] = valor;
    }

    public int comprimento() {
        // retorna um inteiro que representa o comprimento da lista (quantos valores estão armazenados)
        return len;
    }

    public void limpa() {
        // esvazia a lista
        for (int i = 0; i < len; i++){
            area[i] = null;
        }
        len = 0;
    }

    @SuppressWarnings("unchecked")
    public int busca_binaria(Comparable valor) {
        int esq = 0, dir = len - 1;
        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            int cmp = ((Comparable) area[meio]).compareTo(valor);
            if (cmp == 0) return meio;
            else if (cmp < 0) esq = meio + 1;
            else dir = meio - 1;
        }
        return -1;
    }
    @SuppressWarnings("unchecked")
    public void ordena() {
        for (int i = 1; i < len; i++) {
            T chave = area[i];
            int j = i - 1;
            while (j >= 0 && ((Comparable) area[j]).compareTo(chave) > 0) {
                area[j + 1] = area[j];
                j--;
            }
            area[j + 1] = chave;
        }
    }

    @SuppressWarnings("unchecked")
    public void embaralha() {
        Random rng = new Random();
        for (int i = len - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            T tmp = area[i];
            area[i] = area[j];
            area[j] = tmp;
        }
    }

    @SuppressWarnings("unchecked")
    void expande() {
        int capacidadeDobrada = area.length * 2;

        T[] areaExpandida = (T[]) new Object[capacidadeDobrada];

        for (int i = 0; i < len; i++) {
            areaExpandida[i] = area[i];
        }
        area = areaExpandida;
    }
}