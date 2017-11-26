/*
Encapsulation for deleted MyHashElement nodes. i used only for a linear chain type hashtable. The current implemented
hashtable is an open address version which does not require the deleted class.
 */

public class Deleted extends MyHashElement {
    public Deleted(String k, int v) {
        super(v,k);
    }
}
