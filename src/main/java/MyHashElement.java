import java.io.Serializable;

public class MyHashElement implements Serializable {

    private int val;
    private String key;
    private MyHashElement next;

    public MyHashElement(int v, String k) {
        val = v;
        key = k;
        MyHashElement next = null;
    }

    public String getKey() {return key;}
    public int getVal() {return val;}
    public MyHashElement getNext() {return next;}
    public void setVal(int v) {val = v;}
    public void setNext(MyHashElement n) {next = n;}
}
