package tzpp.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TableCell extends AbstractCell{
    private Double value;

    TableCell(GraphicsContext graphicsContext, Color color, Double value){
        super(graphicsContext, color);
        this.value = value;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    void draw(int x, int y, int cellSize, Color fillColor) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRoundRect(cellSize*x, cellSize*y, cellSize, cellSize, 0, 0);
        graphicsContext.setFill(fillColor);
        graphicsContext.fillRoundRect(cellSize*x + 1, cellSize*y + 1, cellSize - 1, cellSize - 1, 0, 0);
        graphicsContext.setFill(Color.BLACK);
        if(value == Double.POSITIVE_INFINITY)
        graphicsContext.fillText("M", cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        else {
            graphicsContext.fillText(value.toString(), cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        }
    }

    Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
