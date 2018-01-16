import com.sun.deploy.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Meir on 3/20/2016.
 */

/*Assignments 2,3,4 Graph algorithms

Write classes ALGraph, GNode, GEdge, and implement the following graph algorithms using common data structures:
-BFS
-DFS
-Dijkstra's Algorithm
-Complete edge classification for directed graphs
-calculation of low for directed graphs
-Biconnected components, bridges, articulation points – undirected graphs
-max flow using augmentation algorithm from textbook

Please start working on this assignment now. There will be NO extension*/


public class ALGraph {
    HashMap<GNode, ArrayList<GNode>> adjacencyLists;
    HashMap<GNode, ArrayList<Integer>> adjacencyWeights;
    boolean directedOrNot;

    ArrayList<GNode> vertices;
    ArrayList<GEdge> edges;
    static int time;
    static int HIGH = 10000; // not using Integer.MAX_VALUE because of addition overflow
    public ALGraph() {
        adjacencyLists = new HashMap<GNode, ArrayList<GNode>>();
        adjacencyWeights = new HashMap<GNode, ArrayList<Integer>>();
        vertices = new ArrayList<GNode>();
        edges = new ArrayList<GEdge>();
    }

    void addVertex(GNode u) {
        if (!vertices.contains(u)) {
            vertices.add(u);
            adjacencyLists.put(u, new ArrayList<GNode>());
            adjacencyWeights.put(u, new ArrayList<Integer>());
        }
    }

    void addEdge(GEdge e) {
        // assumes both vertices exist in `vertices`
        addVertex(e.first);
        addVertex(e.second);
        adjacencyLists.get(e.first).add(e.second);
        adjacencyWeights.get(e.first).add(e.weight);
        edges.add(e);
    }

    GEdge findEdge(GNode u, GNode v) {
        // if this edge is in `this`, then return it. else return null
        for (GEdge edge : edges) {
            if (edge.first.equals(u) && edge.second.equals(v)) {
                return edge;
            }
        }
        return null;
    }

    GNode findNode(int x) {
        for (GNode n : vertices) {
            if (n.data == x) {
                return n;
            }
        }
        return null;
    }

    ArrayList<GNode> adj(GNode u) {
        return adjacencyLists.get(u);
    }

    int w(GNode u, GNode v) {
        int loc = adj(u).indexOf(v);
        return adjacencyWeights.get(u).get(loc);
    }

    int w(GEdge e) {
        return w(e.first, e.second);
    }

    void clearSearchInfo() {
        // removes characteristics of the graph dependent on a search
        for (GNode u : vertices) {
            u.color = null;
            u.d = 0;
            u.f = 0;
            u.pred = null;
            u.children.clear();
        }
        for (GEdge e : edges) {
            e.type = null;
        }
    }

    public static int[] stringToTriple(String s) {
        //converts "3 5 12" into {3, 5, 12}
        //converts "1 2" into {1, 2, 1}

        int[] ret = new int[3];
        String[] strings = s.trim().split("\\s+");
        ret[0] = Integer.parseInt(strings[0]);
        ret[1] = Integer.parseInt(strings[1]);
        if (strings.length > 2) {
            ret[2] = Integer.parseInt(strings[2]);
        } else {
            ret[2] = 1;
        }
        return ret;
    }

    public static ALGraph makeGraph(String fileName) {
        ALGraph ret = new ALGraph();
        Scanner sc = null;
        try {
            FileReader fr = new FileReader(fileName);
            sc = new Scanner(fr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        boolean isDirected = Integer.parseInt(sc.nextLine()) != 0;
        int numberOfNodes = Integer.parseInt(sc.nextLine());
        for (int i = 1; i <= numberOfNodes; i++) {
            ret.addVertex(new GNode(i));
        }
        while (sc.hasNext()) {
            int[] triple = stringToTriple(sc.nextLine());
            ret.addEdge(new GEdge(ret.findNode(triple[0]), ret.findNode(triple[1]), triple[2]));
            if (!isDirected) {
                assert (triple[0] != triple[1]); //undirected graphs can't have edge from node to itself
                ret.addEdge(new GEdge(ret.findNode(triple[1]), ret.findNode(triple[0]), triple[2]));
            }

        }
        ret.directedOrNot = isDirected;
        return ret;
    }

    public static ArrayList<GNode> descendants(GNode u) {
        ArrayList<GNode> ret = new ArrayList<GNode>();
        ret.add(u);
        for (GNode child : u.children) {
            for (GNode descendant : descendants(child)) {
                ret.add(descendant);
            }
        }
        return ret;
    }

    public static void printBiconnectedComponents(ALGraph G) {
        G.clearSearchInfo();
        Stack<GEdge> stack = new Stack<GEdge>();
        for (GNode u : G.vertices) {
            u.color = GNode.Color.WHITE;
            u.pred = null;
        }
        time = 0;
        for (GNode u : G.vertices) {
            if (u.color == GNode.Color.WHITE) {
                BCCDFSVisit(G, u, stack);
            }
        }
    }

    public static void BCCDFSVisit(ALGraph G, GNode u, Stack<GEdge> S) {
        time++;
        u.d = time;
        u.color = GNode.Color.GRAY;
        u.lowAttribute = u.d;
        for (GNode v : G.adj(u)) {
            if (v.color == GNode.Color.WHITE) {
                S.push(G.findEdge(u, v));
                v.pred = u;
                u.children.add(v);
                if (v.d == 0) { // v is just being discovered now
                    G.findEdge(u, v).type = GEdge.EdgeType.TREE;
                }
                BCCDFSVisit(G, v, S);
                if (v.lowAttribute >= u.d) {
                    printBCC(G.findEdge(u,v), S);
                }
                u.lowAttribute = Math.min(u.lowAttribute, v.lowAttribute);
            } else if (!(u.pred == (v)) && v.d < u.d) {
                S.push(G.findEdge(u,v));
                u.lowAttribute = Math.min(u.lowAttribute, v.d);
            }
        }
    }

    public static void printBCC(GEdge e, Stack<GEdge> S) {
        System.out.println("New biconnected component:");
        GEdge e2;
        do {
            e2 = S.pop();
            System.out.printf("(%d,%d)\n", e2.first.data, e2.second.data);
        } while (e2 != e);
    }

    public static ArrayList<GEdge> bridges(ALGraph G) {
        ArrayList<GEdge> ret = new ArrayList<GEdge>();
        G.clearSearchInfo();
        DFS(G);
        for (GEdge e : G.edges) {
            if (low(G, e.second) > e.first.d) {
                ret.add(e);
            }
        }
        return ret;
    }

    public static int low(ALGraph G, GNode v) {
        int ret = v.d;
        for (GNode u : descendants(v)) {
            for (GEdge e : G.edges) {
                if (e.first.equals(u) && e.type == GEdge.EdgeType.BACK && (G.findEdge(e.second, e.first) != null) && G.findEdge(e.second, e.first).type != GEdge.EdgeType.TREE) {
                    GNode w = e.second;
                    if (w.d < ret) {
                        ret = w.d;
                    }
                }
            }
        }
        return ret;
    }

    public static boolean canJumpAbove(ALGraph G, GNode child, GNode parent) {
        for (GNode from : descendants(child)) {
            for (GNode to : ancestors(parent)) {
                GEdge e = G.findEdge(from, to);
                if (e != null && e.type == GEdge.EdgeType.BACK) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<GNode> articulationPoints(ALGraph G) {
        ArrayList<GNode> ret = new ArrayList<GNode>();
        for (GNode v : G.vertices) {
            if (v.equals(G.vertices.get(0))) {
                if (v.children.size() >= 2) {
                    ret.add(v);
                }
            } else {
                for (GNode s : v.children) {
                    if (!canJumpAbove(G, s, v) && !ret.contains(v)) {
                        ret.add(v);
                    }
                }
            }
        }
        return ret;
    }

    public static ArrayList<GNode> ancestors(GNode u) {
        // returns the PROPER ancestors of u
        ArrayList<GNode> ret = new ArrayList<GNode>();
        GNode x = u;
        while (x.pred != null) {
            x = x.pred;
            ret.add(x);
        }
        return ret;
    }

    public static void BFS(ALGraph G, GNode start) {
        for (GNode u : G.vertices) {
            if (!u.equals(start)) {
                u.color = GNode.Color.WHITE;
                u.d = HIGH;
                u.pred = null;
            }
        }
        start.color = GNode.Color.GRAY;
        start.d = 0;
        start.pred = null;
        MyQueue Q = new MyQueue();
        Q.enqueue(start);
        while (!Q.isEmpty()) {
            GNode u = Q.dequeue();
            for (GNode v : G.adj(u)) {
                if (v.color == GNode.Color.WHITE) {
                    v.color = GNode.Color.GRAY;
                    v.d = u.d + 1;
                    v.pred = u;
                    Q.enqueue(v);
                }
            }
            u.color = GNode.Color.BLACK;
        }
    }

    public static void DFS(ALGraph G) {
        for (GNode u : G.vertices) {
            u.color = GNode.Color.WHITE;
            u.pred = null;
        }
        time = 0;
        for (GNode u : G.vertices) {
            if (u.color == GNode.Color.WHITE) {
                DFSVisit(G, u);
            }
        }
        // now classify non-TREE edges
        for (GEdge edge : G.edges) {
            if (edge.type != GEdge.EdgeType.TREE) {
                GNode u = edge.first;
                GNode v = edge.second;
                if ((u.d < v.d) && (v.d < v.f) && (v.f < u.f)) {
                    edge.type = GEdge.EdgeType.FORWARD;
                } else if ((v.d <= u.d) && (u.d < u.f) && (u.f <= v.f)) {
                    edge.type = GEdge.EdgeType.BACK;
                } else if ((v.d < v.f) && (v.f < u.d) && (u.d <= u.f)) {
                    edge.type = GEdge.EdgeType.CROSS;
                }
            }
        }
    }

    public static void DFSVisit(ALGraph G, GNode u) {
        time++;
        u.d = time;
        u.color = GNode.Color.GRAY;
        for (GNode v : G.adj(u)) {
            if (v.color == GNode.Color.WHITE) {
                v.pred = u;
                u.children.add(v);
                if (v.d == 0) { // v is just being discovered now
                    G.findEdge(u, v).type = GEdge.EdgeType.TREE;
                }
                DFSVisit(G, v);
            }
        }

        u.color = GNode.Color.BLACK;
        time++;
        u.f = time;
    }

    public static void initializeSingleSource(ALGraph G, GNode s) {
        for (GNode v : G.vertices) {
            v.d = HIGH;
            v.pred = null;
        }
        s.d = 0;
    }

    public static void relax(ALGraph G, GNode u, GNode v) {
        if (v.d > u.d + G.w(u, v)) {
            v.d = u.d + G.w(u, v);
            v.pred = u;
        }
    }

    public static void dijkstra(ALGraph G, GNode s) {
        initializeSingleSource(G, s);
        PriorityQueue<GNode> Q = new PriorityQueue<GNode>();
        for (GNode u : G.vertices) {
            Q.add(u);
        }
        while (!Q.isEmpty()) {
            GNode u = Q.poll();
            for (GNode v : G.adj(u)) {
                relax(G, u, v);
            }
        }
    }

    public static ArrayList<GNode> findPath(ALGraph G, GNode u, GNode v) {
        //returns null if there is no such path
        //otherwise returns the path
        G.clearSearchInfo();
        BFS(G, u);
        if (v.d == HIGH) {
            return null;
        }
        ArrayList<GNode> ret = new ArrayList<GNode>();
        GNode x = v;
        while (x != u) {
            ret.add(x);
            x = x.pred;
        }
        ret.add(x); //finally add u
        Collections.reverse(ret);
        return ret;
    }



    public static ALGraph residualGraph(ALGraph G) {
        //assumes a flow is assigned to each edge of G
        ALGraph ret = new ALGraph();
        for (GNode u : G.vertices) {
            ret.addVertex(u);
        }
        for (GEdge e : G.edges) {
            if (e.flow > 0) {
                ret.addEdge(new GEdge(e.second, e.first, e.flow));
            }
            if (G.w(e) > e.flow) {
                ret.addEdge(new GEdge(e.first, e.second, G.w(e) - e.flow));
            }
        }
        return ret;
    }

    public static void fordFulkerson(ALGraph G, GNode s, GNode t) {
        //CLRS page 724
        for (GEdge e : G.edges) {
            e.flow = 0;
        }
        ArrayList<GNode> p;
        ALGraph R;
        int x;
        GEdge e;
        while ((p = findPath((R = residualGraph(G)), s, t)) != null) {
            int cf = HIGH;
            for (int i = 0; i < p.size()-1; i++) {
                if ((x = R.w(p.get(i), p.get(i+1))) < cf) {
                    cf = x;
                }
            }
            for (int i = 0; i < p.size()-1; i++) {
                if ((e = G.findEdge(p.get(i), p.get(i+1))) != null) {
                    e.flow += cf;
                } else {
                    G.findEdge(p.get(i+1), p.get(i)).flow -= cf;
                }
            }

//            for (GEdge e2 : G.edges) {
//                System.out.printf("(%d,%d), %d/%d\n", e2.first.data,
//                        e2.second.data, e2.flow, G.w(e2));
//            }
//            System.out.println();
//            for (GNode u : p) {
//                System.out.print(u.data + ",");
//            }
//            System.out.println('\n');

        }
    }

    public static void fordFulkerson(ALGraph G) {
        fordFulkerson(G, G.vertices.get(0), G.vertices.get(G.vertices.size()-1));
    }


    public static void testEdgeClassification(ALGraph G) {
        G.clearSearchInfo();
        System.out.println("Edge classification:\n");
        DFS(G);
        for (GEdge edge : G.edges) {
            System.out.printf("(%d,%d): type = %s\n", edge.first.data, edge.second.data, edge.type);
        }
    }

    public static void testDFS(ALGraph G) {
        G.clearSearchInfo();
        System.out.println("DFS, beginning with Node 1:\n");
        DFS(G);
        for (GNode node : G.vertices) {
            Integer predData;
            if (node.pred == null) {
                predData = null;
            } else {
                predData = node.pred.data;
            }
            System.out.printf("Node %d: discovery time = %d, finish time = %d, low = %d, predecessor = %d\n", node.data, node.d, node.f, low(G, node), predData);
        }
        System.out.println();
    }

    public static void testBFS(ALGraph G, GNode a) {
        G.clearSearchInfo();
        System.out.println("\nBFS, beginning with node 1:\n");
        BFS(G, a);
        for (GNode node : G.vertices) {
            Integer predData;
            if (node.pred == null) {
                predData = null;
            } else {
                predData = node.pred.data;
            }
            System.out.printf("Node %d: discovery time = %d, predecessor = %d\n", node.data, node.d, predData);
        }
    }

    public static void testDijkstra(ALGraph G, GNode a) {
        G.clearSearchInfo();
        System.out.println("\nDijkstra, beginning with node 1:\n");
        dijkstra(G, a);
        for (GNode node : G.vertices) {
            Integer predData;
            if (node.pred == null) {
                predData = null;
            } else {
                predData = node.pred.data;
            }
            System.out.printf("Node %d: discovery time = %d, predecessor = %d\n", node.data, node.d, predData);
        }
        System.out.println();
    }

    public static void testDirectedGraph(ALGraph G) {
        System.out.println("\nTesting algorithms on a directed graph:\n");
        testDFS(G);
        testEdgeClassification(G);
        testBFS(G, G.vertices.get(0));
        testDijkstra(G, G.vertices.get(0));
        G.clearSearchInfo();
        System.out.println("Testing the Ford-Fulkerson method for max flow.");
        System.out.println("The source is node 1. The sink is node n, where n is the number of nodes.\n");
        System.out.println("Maximum flow is achieved as follows:\n");
        fordFulkerson(G);
        for (GEdge e : G.edges) {
            System.out.printf("(%d,%d): flow/capacity = %d/%d\n", e.first.data,
                    e.second.data, e.flow, G.w(e));
        }
    }

    public static void testUndirectedGraph(ALGraph G) {
        System.out.println("\nTesting algorithms on an undirected graph:\n");
        testDFS(G);
        System.out.println("\nArticulation Points:\n");
        for (GNode u : articulationPoints(G)) {
            System.out.println(u.data);
        }
        testBFS(G, G.vertices.get(0));



        System.out.println();
        System.out.println("Bridges:");
        for (GEdge e : bridges(G)) {
            System.out.printf("(%d,%d)\n", e.first.data, e.second.data);
        }
        System.out.println();

        System.out.println("Printing out biconnected components, one at a time:\n");
        printBiconnectedComponents(G);





    }



    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new FileOutputStream("output.txt")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


//        ALGraph G = makeGraph("testfile.txt"); // basic directed graph, p.590
//        ALGraph G2 = makeGraph("testfile2.txt"); // for dijkstra, p.659
//        ALGraph G3 = makeGraph("testfile3.txt"); // very simple undirected graph
        ALGraph G4 = makeGraph("testfile4.txt"); /* big undirected graph for bridges, bcc,
                                                    articulation pts, etc. CLRS p.622*/
        ALGraph G5 = makeGraph("testfile5.txt"); // directed graph for max flow. CLRS p.726


        if (args.length > 0) {
            String fileName = args[0];
            ALGraph yourGraph = makeGraph(fileName);
            if (yourGraph.directedOrNot) {
                testDirectedGraph(yourGraph);
            } else {
                testUndirectedGraph(yourGraph);
            }
        } else {
            testDirectedGraph(G5);
            System.out.println("\n\n\n");
            System.out.println("------------------------------------");
            System.out.println("\n\n\n");
            testUndirectedGraph(G4);
        }



    }




}
