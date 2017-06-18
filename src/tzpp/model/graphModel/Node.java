package tzpp.model.graphModel;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class Node implements Serializable, Comparable<Node> {
    private NodeCircle nodeCircle;
    private Integer resource;
    private NodeType type;
    private Integer id;

    public Node(NodeCircle nodeCircle, Integer resource, NodeType type, Integer id) {
        this.nodeCircle = nodeCircle;
        this.resource = resource;
        this.type = type;
        this.id = id;
    }

    @Override
    public int compareTo(Node o) {
        return id.compareTo(o.getId());
    }

    public enum NodeType { OUTPUT, INPUT, TRANSIT}

    public NodeCircle getNodeCircle() {
        return nodeCircle;
    }

    public void setNodeCircle(NodeCircle nodeCircle) {
        this.nodeCircle = nodeCircle;
    }

    public Integer getResource() {
        return resource;
    }

    public void setResource(Integer resource) {
        this.resource = resource;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public void draw(GraphicsContext gc,Integer size, Color color,int  x,int y){
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(size*x, size*y, size, size, 0, 0);
        gc.setFill(color);
        gc.fillRoundRect(size*x + 1, size*y + 1, size - 1, size - 1, 0, 0);
        gc.setFill(Color.BLACK);
        gc.fillText(id.toString(), size*x + 1, size*(y+1) - size/2.7);
    }
}
