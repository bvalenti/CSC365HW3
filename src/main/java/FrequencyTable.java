import java.io.Serializable;

public class FrequencyTable implements Serializable {
    MyHashTable frequencies;

    FrequencyTable(int k) {}

    FrequencyTable() {
        frequencies = new MyHashTable();
    }
}
