package tzpp.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import tzpp.model.graphModel.Edge;

import java.util.ArrayList;

public class MPZKmodel {

    private TZLPmodel TZLP;
    private ArrayList<Double> a;
    private ArrayList<Double> b;
    private ArrayList<Pair<Edge, Double>> dbr;

    public MPZKmodel(TZLPmodel TZLP) {
        this.TZLP = TZLP;
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        this.dbr = new ArrayList<>();
    }

    public TZLPmodel getTZLP() {
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
                a.set(i, a.get(i) - (b.get(j)));
                j++;
            } else {
                dbr.add(new Pair<>(getEdgeByIndexes(i, j), a.get(i)));
                b.set(j, b.get(j) - (a.get(i)));
                i++;
            }
        }
        dbr.add(new Pair<>(getEdgeByIndexes(i, j), a.get(i)));

    }

    public void drawSolution(GraphicsContext gc, Integer size) {

        for (int i = 0; i < TZLP.getTableTZLP()[0].length; i++) {
            for (int j = 0; j < TZLP.getTableTZLP()[1].length; j++) {
                Edge edge = getEdgeByIndexes(i, j);
                String str = dbr.stream().filter(e -> e.getKey().getFirstNode().getId().equals(edge.getFirstNode().getId()) &&
                e.getKey().getSecondNode().getId().equals(edge.getSecondNode().getId())).findFirst()
                        .map(n -> n.getValue().toString()).orElseGet(() -> " ");
                drawMPZKCell(gc, size, Color.LIGHTYELLOW, j + 1, i + 1,
                        TZLP.getTableTZLP()[i][j].getValue().toString(), str);
            }
        }
    }

    private void drawMPZKCell(GraphicsContext gc, Integer size, Color fillColor, int x, int y, String topText, String bottomText) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(size * x, size * y, size, size, 0, 0);
        gc.setFill(fillColor);
        gc.fillRoundRect(size * x + 1, size * y + 1, size - 1, size - 1, 0, 0);
        gc.setFill(Color.DARKGREY);
        if(topText.equals("Infinity")) topText = "M";
        gc.fillText(topText, size * x + size / 4 + 2, size * y + 12);
        gc.setFill(Color.BLACK);
        gc.fillText(bottomText, size * x + 2, size * (y + 1) - 3);
    }

    private Edge getEdgeByIndexes(int i, int j) {
        return new Edge(TZLP.getOutputNodes().get(i), TZLP.getInputNodes().get(j),
                TZLP.getTableTZLP()[i][j].getValue().intValue());
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
