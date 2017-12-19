import java.util.Set;

public class MyPriorityQueueNode {
    Set<Edge> edges;
    int pqindex;
    float cost;
    MyPriorityQueueNode parent = null;
}


class Edge {
    float weight;
    MyPriorityQueueNode src, dst;
}