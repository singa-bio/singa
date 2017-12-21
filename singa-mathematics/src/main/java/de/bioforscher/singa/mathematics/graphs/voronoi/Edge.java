package de.bioforscher.singa.mathematics.graphs.voronoi;

class Edge {

    final Site[] ep; // JH: End points?
    final Site[] reg; // JH: Sites this edge bisects?
    public double a = 0;
    public double b = 0;
    public double c = 0;
    int edgenbr;

    Edge() {
        ep = new Site[2];
        reg = new Site[2];
    }
}
