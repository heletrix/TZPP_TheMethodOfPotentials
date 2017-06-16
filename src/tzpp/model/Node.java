package tzpp.model;

public class Node {
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

    public enum NodeType {INPUT, OUTPUT, TRANSIT}

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
    public String toString(){
        return id.toString();
    }
}
