package com.devsectech.photomo.customView.beans;

public class Vertice {

    public Ponto f153p1;
    public Ponto f154p2;

    public static class PacoteDistanciaVertice implements Comparable<PacoteDistanciaVertice> {
        public double distance;
        public Vertice edge;

        public PacoteDistanciaVertice(Vertice vertice, double d) {
            this.edge = vertice;
            this.distance = d;
        }

        public int compareTo(PacoteDistanciaVertice pacoteDistanciaVertice) {
            return Double.compare(this.distance, pacoteDistanciaVertice.distance);
        }
    }

    public Vertice(Ponto ponto, Ponto ponto2) {
        this.f153p1 = ponto;
        this.f154p2 = ponto2;
    }

    public float coeficienteAngular() {
        return (this.f154p2.getYInit() - this.f153p1.getYInit()) / (this.f154p2.getXInit() - this.f153p1.getXInit());
    }

    public boolean equals(Object obj) {
        Vertice vertice = (Vertice) obj;
        return (vertice.f153p1.equals(this.f153p1) || vertice.f153p1.equals(this.f154p2)) && (vertice.f154p2.equals(this.f153p1) || vertice.f154p2.equals(this.f154p2));
    }
}
