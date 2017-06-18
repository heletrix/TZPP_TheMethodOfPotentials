package tzpp.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tzpp.controller.MainController;

class TableCell extends AbstractCell{
    // Транспортні витрати
    private Double costs;

    TableCell(GraphicsContext graphicsContext, Color color, Double costs){
        super(graphicsContext, color);
        this.costs = costs;
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
        if(costs >= MainController.getM())
        graphicsContext.fillText("M", cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        else {
            graphicsContext.fillText(costs.toString(), cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        }
    }

    Double getCosts() {
        return costs;
    }

    public void setCosts(Double costs) {
        this.costs = costs;
    }
}
