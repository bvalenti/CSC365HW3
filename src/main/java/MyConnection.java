import java.io.Serializable;

public class MyConnection implements Serializable {
    String url;
    Double weight;
    String parent;
    Double distance;

    MyConnection() {
        distance = Double.MAX_VALUE;
    }
}
