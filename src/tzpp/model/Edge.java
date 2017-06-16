package tzpp.model;

public class Edge {
    private Node firstNode;
    private Node secondNode;
    private Integer value;

    public Edge(Node firstNode, Node secondNode, Integer value) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.value = value;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(Node firstNode) {
        this.firstNode = firstNode;
    }

    public Node getSecondNode() {
        return secondNode;
    }

    public void setSecondNode(Node secondNode) {
        this.secondNode = secondNode;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
