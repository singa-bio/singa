package de.bioforscher.singa.mathematics.graphs.voronoi;

class Edge {

    public double a = 0;
    public double b = 0;
    public double c = 0;

    Site[] ep; // JH: End points?
    Site[] reg; // JH: Sites this edge bisects?
    int edgenbr;

    Edge() {
        this.ep = new Site[2];
        this.reg = new Site[2];
    }
}
