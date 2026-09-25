package com.simaomonteiro18.lisbonpitches.playground;

public class Loja {

    private Armazem armazem;

    public Loja(Armazem armazem) {
        this.armazem = armazem;
    }

    public double precoComDesconto(String produto) {

        double preco = armazem.procurarPreco(produto) * 0.9;

        return preco;

    }

}
