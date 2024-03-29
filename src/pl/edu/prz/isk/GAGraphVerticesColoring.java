package pl.edu.prz.isk;

import com.softtechdesign.ga.ChromStrings;
import com.softtechdesign.ga.Crossover;
import com.softtechdesign.ga.GAException;
import com.softtechdesign.ga.GAStringsSeq;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GAGraphVerticesColoring extends GAStringsSeq {

    private static final DataSet currentData = DataSet.MYCIEL3;
    final static String fileName = currentData.getFilename();
    static String[] possibleColors;
    static int[] graphVertices;
    static ArrayList<GraphEdge> graphEdges;
    static Random rand = new Random(System.currentTimeMillis());
    static List<String> possibleColorsList;

    public GAGraphVerticesColoring() throws GAException {
        super(graphVertices.length, //size of chromosome
                200, //population has N chromosomes
                0.4, //crossover probability
                10, //random selection chance % (regardless of fitness)
                1000, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                20, //max generations per prelim run
                0.4, //chromosome mutation prob.
                0, //number of decimal places in chrom
                possibleColors, //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statisitics
    }

    public static void main(String[] args) throws GAException {
        try {
            readFile(fileName);
            System.out.println("Running GA on: " + fileName);
            System.out.println("Vertices: " + graphVertices.length);
            System.out.println("Edges: " + graphEdges.size());
            GAGraphVerticesColoring ga = new GAGraphVerticesColoring();
            Thread threadGraph = new Thread(ga);
            threadGraph.setPriority(Thread.MAX_PRIORITY);
            threadGraph.start();
            threadGraph.join();
            if (!threadGraph.isAlive()) {
                if (ga.getFittestChromosomesFitness() != 0) {
                    boolean isValid = true;
                    Set<String> usedColors = new HashSet<>(graphVertices.length);
                    
                    // liczba genów zależy od liczy wierzcholków
                    // chromosom to sposob kolorowania grafu
                    //gen reprezentuje wierzcholek i jego kolor
                    ChromStrings chromosome = (ChromStrings) ga.getFittestChromosome();
                    String genes[] = chromosome.getGenes();
                    
                    for (GraphEdge graphEdge : graphEdges) {
                        String colorDst = genes[graphEdge.getVertexDst() - 1];
                        String colorSrc = genes[graphEdge.getVertexSrc() - 1];
                        if (colorSrc.equals(colorDst)) {
                            isValid = false;
                        }
                    }
                    Collections.addAll(usedColors, genes);
                    int numOfUsedColors = usedColors.size();
                    System.out.println("Is coloring valid: " + isValid);
                    System.out.println("Number of used colors in solution: " + numOfUsedColors);
                    System.out.println("Expected optimal number of colors: " + currentData.getChromaticNumber());
                }
            }
        } catch (FileNotFoundException | GAException | InterruptedException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "an exception was thrown:", ex);
        }
    }

    @Override
    protected double getFitness(int chromeIndex) {
        ChromStrings chromosome = getChromosome(chromeIndex);
        String genes[] = chromosome.getGenes();
        
        // unique colors from genes
        Set<String> usedColors = new HashSet<>(Arrays.asList(genes));
        
        for (GraphEdge graphEdge : graphEdges) {
            String colorDst = genes[graphEdge.getVertexDst() - 1];
            String colorSrc = genes[graphEdge.getVertexSrc() - 1];

            if (colorDst.equals(colorSrc)) {
                return 0;
            }
        }
        return (graphVertices.length + 1 - usedColors.size());
    }

    public static void readFile(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(fileName));
        Set<Integer> vertices = new HashSet<>();
        int numOfVertices;
        int numOfEdges = scanner.nextInt();
        graphEdges = new ArrayList<>(numOfEdges);
        while (scanner.hasNextLine()) {
            int edgeNumber = scanner.nextInt();
            int verticeIndexStart = scanner.nextInt();
            int verticeIndexEnd = scanner.nextInt();
            vertices.add(verticeIndexStart);
            vertices.add(verticeIndexEnd);
            graphEdges.add(new GraphEdge(verticeIndexStart, verticeIndexEnd));
        }
        numOfVertices = vertices.size();
        graphVertices = new int[numOfVertices];
        int index = 0;
        for (Integer i : vertices) {
            graphVertices[index++] = i;
        }
        possibleColors = new String[numOfVertices];
        for (int i = 0; i < numOfVertices; i++) {
            possibleColors[i] = intToStringSeq(i + 1);
        }
        possibleColorsList = Arrays.asList(possibleColors);
    }

    public static String intToStringSeq(int index) {
        if (index < 27) {
            return Character.toString((char) (index + 96));
        } else {
            if (index % 26 == 0) {
                return intToStringSeq((index / 26) - 1) + intToStringSeq(((index - 1) % 26 + 1));
            } else {
                return intToStringSeq(index / 26) + intToStringSeq(index % 26);
            }
        }
    }
}
