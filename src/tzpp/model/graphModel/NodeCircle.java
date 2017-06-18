package tzpp.model.graphModel;

import java.io.Serializable;

public class NodeCircle implements Serializable {
    private double x;
    private double y;

    public NodeCircle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
