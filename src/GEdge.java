/**
 * Created by Meir on 3/20/2016.
 */
public class GEdge {

    public enum EdgeType {
        TREE,
        FORWARD,
        BACK,
        CROSS
    }

    GNode first;
    GNode second;
    int weight;
    EdgeType type;
    int flow;

    public GEdge(GNode f, GNode s) {
        first = f;
        second = s;
        weight = 1;
    }

    public GEdge(GNode f, GNode s, int w) {
        first = f;
        second = s;
        weight = w;
    }

}
