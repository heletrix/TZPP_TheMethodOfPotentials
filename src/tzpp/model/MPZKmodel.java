package tzpp.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import tzpp.controller.Common;
import tzpp.model.graphModel.Edge;
import tzpp.model.graphModel.Node;

import java.util.ArrayList;

public class MPZKmodel {

    private TZLPmodel TZLP;                     // транспортна задача лінійного програмування
    private ArrayList<Double> a;                // запаси виробників
    private ArrayList<Double> b;                // потреби споживачів
    private ArrayList<Pair<Edge, Double>> dbr;  // допустимий базисний розв'язок
    private Double F;                            // цільова функція
    private Double[][] X;

    public MPZKmodel(TZLPmodel TZLP) {
        this.TZLP = TZLP;
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        this.dbr = new ArrayList<>();
        X = new Double[TZLP.getOutputNodes().size()][TZLP.getInputNodes().size()];
        for (int i = 0; i < X[0].length; i++)
            for (int j = 0; j < X[1].length; j++)
                X[i][j] = null;
    }

    Double[][] getX() {
        return X;
    }

    Double getF() {
        return F;
    }

    TZLPmodel getTZLP() {
        return TZLP;
    }

    public void setTZLP(TZLPmodel TZLP) {
        this.TZLP = TZLP;
    }

    public ArrayList<Double> getA() {
        return a;
    }

    private void setA(ArrayList<Double> a) {
        this.a = a;
    }

    public ArrayList<Double> getB() {
        return b;
    }

    private void setB(ArrayList<Double> b) {
        this.b = b;
    }

    public ArrayList<Pair<Edge, Double>> getDbr() {
        return dbr;
    }

    public void setDbr(ArrayList<Pair<Edge, Double>> dbr) {
        this.dbr = dbr;
    }

    public void getSolution() {
        setA(TZLP.getOutputNodesResource());
        setB(TZLP.getInputNodesResource());
        int i = 0;
        int j = 0;
        while (i != a.size() - 1 || j != b.size() - 1) {
            if (a.get(i) >= b.get(j)) {
                dbr.add(new Pair<>(getEdgeByIndexes(i, j), b.get(j)));
                X[i][j] = b.get(j);
                a.set(i, a.get(i) - (b.get(j)));
                j++;
            } else {
                dbr.add(new Pair<>(getEdgeByIndexes(i, j), a.get(i)));
                X[i][j]=a.get(i);
                b.set(j, b.get(j) - (a.get(i)));
                i++;
            }
        }
        dbr.add(new Pair<>(getEdgeByIndexes(i, j), a.get(i)));
        X[i][j]=a.get(i);
    }

    public void drawSolution(GraphicsContext gc, Integer size) {

        int it = 1;
        for (Node node : TZLP.getInputNodes()) {
            node.draw(gc, size, Color.LIGHTBLUE, it++, 0);
        }
        int ik = 1;
        for (Node node : TZLP.getOutputNodes()) {
            node.draw(gc, size, Color.LIGHTBLUE, 0, ik++);
        }
        Common.setGc(gc);
        for (int i = 0; i < TZLP.getTableTZLP().length; i++) {
            for (int j = 0; j < TZLP.getTableTZLP()[0].length; j++) {
                Edge edge = getEdgeByIndexes(i, j);
                String str = dbr.stream().filter(e -> e.getKey().getFirstNode().getId().equals(edge.getFirstNode().getId()) &&
                e.getKey().getSecondNode().getId().equals(edge.getSecondNode().getId())).findFirst()
                        .map(n -> n.getValue().toString()).orElseGet(() -> " ");
                Common.drawCellWithTwoText( Color.LIGHTYELLOW, j + 1, i + 1,
                        TZLP.getTableTZLP()[i][j].getCosts().toString(), str);
            }
        }
    }


    private Edge getEdgeByIndexes(int i, int j) {
        return new Edge(TZLP.getOutputNodes().get(i), TZLP.getInputNodes().get(j),
                TZLP.getTableTZLP()[i][j].getCosts().intValue());
    }

    // Підрахунок транспортних витрат
    public String getTransportCost(){
        String summString = "Транспортні витрати становлять:\n";
        Double dbrSum = 0.0;
        for (Pair<Edge, Double> pair : dbr){
            Double currentSum = (pair.getValue() * (pair.getKey().getValue()));
            summString += (currentSum.toString()) + " + " ;
            dbrSum += currentSum;
        }
        int index = summString.length() - 3;
        return summString.substring(0, index) + "\n = " + dbrSum;
    }
}
