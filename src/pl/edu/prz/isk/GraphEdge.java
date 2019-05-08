package pl.edu.prz.isk;

/**
 * Klasa reprezentująca krawędź posiadająca parę wierzchołków grafu
 */

class GraphEdge {

    private final int vertexSrc;
    private final int vertexDst;

    public GraphEdge(int vertexSrc, int vertexDst) {
        this.vertexSrc = vertexSrc;
        this.vertexDst = vertexDst;
    }

    public int getVertexSrc() {
        return vertexSrc;
    }

    public int getVertexDst() {
        return vertexDst;
    }
}