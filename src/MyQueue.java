import java.util.LinkedList;

/**
 * Created by Meir on 3/20/2016.
 */
public class MyQueue {
    LinkedList<GNode> L;

    public MyQueue() {
        L = new LinkedList<GNode>();
    }

    void enqueue(GNode u) {
        L.add(u);
    }

    boolean isEmpty() {
        return L.size() == 0;
    }

    GNode dequeue() {
        return L.removeFirst();
    }

    public static void main(String[] args) {
        MyQueue Q = new MyQueue();
        Q.enqueue(new GNode(7));
        Q.enqueue(new GNode(4));
        Q.enqueue(new GNode(6));
        Q.enqueue(new GNode(2));
        System.out.println(Q.dequeue().data);
        System.out.println(Q.dequeue().data);
        System.out.println(Q.dequeue().data);
        System.out.println(Q.dequeue().data);
    }
}
