import java.util.NoSuchElementException;

public class MyPriorityQueue {
    MyPriorityQueueNode array[] = new MyPriorityQueueNode[100];
    int count = 0;
    float cost = Float.MAX_VALUE;

    void add (MyPriorityQueueNode e, float w) {
        e.cost = w;
        array[count] = e;
        ++count;

        int k = count;
        while (k > 0) {
            int p = parentOf(k);
            MyPriorityQueueNode child = array[k];
            MyPriorityQueueNode parent = array[p];
            if (child.cost < parent.cost) {
                MyPriorityQueueNode t = array[k];
                array[k] = parent;
                array[p] = t;
                k = p;
            } else {
                break;
            }
        }
        e.pqindex = k;
    }

    MyPriorityQueueNode remove() {  //Log N
        if (count == 0) { throw new NoSuchElementException(); }
        MyPriorityQueueNode x = array[0];
        array[0] = array[--count];

        int k = 0;
        for (;;) {
            int l = leftOf(k);
            int r = l + 1;
            if (l >= count) {
                break;
            }
            MyPriorityQueueNode left = array[l];
            if (r >= count) {
                if (left.cost < array[k].cost) {
                    MyPriorityQueueNode t = array[l];
                    array[l] = array[k];
                    array[k] = t;
                }
                break;
            } else {
                MyPriorityQueueNode right = array[r];
                int least = (left.cost < right.cost)? l:r;
                MyPriorityQueueNode child = array[least];
                if (child.cost < array[k].cost) {
                    MyPriorityQueueNode t = child;
                    array[least] = array[k];
                    array[k] = t;
                }
            }
        }
        return x;
    }

    void reWeight(int k, float w) {
        if (k > count) { throw new NoSuchElementException(); }
        MyPriorityQueueNode e = array[k];

        if (w > e.cost) {
            e.cost = w;
            for (;;) {
                int l = leftOf(k);
                int r = l + 1;
                if (l >= count) { break; }
                MyPriorityQueueNode left = array[l];
                if (r >= count) {
                    if (left.cost < array[k].cost) {
                        MyPriorityQueueNode t = array[l];
                        array[l] = array[k];
                        array[k] = t;
                    }
                    break;
                } else {
                    MyPriorityQueueNode right = array[r];
                    int least = (left.cost < right.cost)? l:r;
                    MyPriorityQueueNode child = array[least];
                    if (child.cost < array[k].cost) {
                        MyPriorityQueueNode t = child;
                        array[least] = array[k];
                        array[k] = t;
                    }
                }
            }
        } else {
            e.cost = w;
            while(k > 0) {
                int p = parentOf(k);
                MyPriorityQueueNode child = array[k];
                MyPriorityQueueNode parent = array[p];
                if (child.cost < parent.cost) {
                    MyPriorityQueueNode t = array[k];
                    array[k] = parent;
                    array[p] = t;
                    k = p;
                } else {
                    break;
                }
            }
            e.pqindex = k;
        }

    }

    int parentOf(int k) {
        return (k - 1) >>> 1;
    }

    int leftOf(int k) {
        return (k << 1) + 1;
    }
}
