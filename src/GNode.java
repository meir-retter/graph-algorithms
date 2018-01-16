import java.util.ArrayList;

/**
 * Created by Meir on 3/20/2016.
 */
public class GNode implements Comparable<GNode> {

   public enum Color {
       WHITE,
       GRAY,
       BLACK
   }
    int data;
    Color color;
    int d; // discovery time
    int f; // finishing time
    GNode pred;
    ArrayList<GNode> children = new ArrayList<GNode>();
    int lowAttribute;

    public int compareTo(GNode other) {
        return Integer.compare(d, other.d);
    }

    public GNode(int i) {
        data = i;
    }
}
