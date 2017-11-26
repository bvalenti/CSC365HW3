import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MyHashTable implements Serializable {

    private int tableSize = 128;
    private int elementCount;
    private MyHashElement arr[] = new MyHashElement[tableSize];

    public MyHashTable() {
        for (int i = 0; i < tableSize; i++) {
            arr[i] = null;
        }
    }

    //===========================================
    public int get(String key) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];

        if (e == null) {
            return 0;
        } else {
            while (e.getNext() != null && !e.getKey().equals(key) && !(e instanceof Deleted)) {
                e = e.getNext();
            }
            if (e.getKey().equals(key) && !(e instanceof Deleted)) {
                return e.getVal();
            } else {
                return 0;
            }
        }
    }

    //===========================================
    public int get(String key, String m) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];

        if (e == null) {
            return 0;
        } else {
            if (m.equals("meta")) {
                while (e.getNext() != null) {
                    if (e.getKey().equals(key) && !(e instanceof Deleted) && (e instanceof MetaHashNode)) {
                        break;
                    }
                    e = e.getNext();
                }
                if (e.getKey().equals(key) && !(e instanceof Deleted) && (e instanceof MetaHashNode)) {
                    return e.getVal();
                } else {
                    return 0;
                }

            } else {
                while (e.getNext() != null) {
                    if (e.getKey().equals(key) && !(e instanceof Deleted) && !(e instanceof MetaHashNode)) {
                        break;
                    }
                e = e.getNext();
                }
                if (e.getKey().equals(key) && !(e instanceof Deleted) && !(e instanceof MetaHashNode)) {
                    return e.getVal();
                } else {
                    return 0;
                }
            }
        }
    }

    //===========================================
    public MyHashElement getElement(String key) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];

        if (e == null) {
            return null;
        } else {
            while (e.getNext() != null && !e.getKey().equals(key) && !(e instanceof Deleted)) {
                e = e.getNext();
            }
            if (e.getKey().equals(key) && !(e instanceof Deleted)) {
                return e;
            } else {
                return null;
            }
        }
    }

    //===========================================
    public void put(String key, int val) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];

        if (e == null) {
            arr[i] = new MyHashElement(val, key);
            elementCount++;
        } else {
            while (e.getNext() != null) {
                if ((e.getKey().equals(key)) && !(e instanceof Deleted) && !(e instanceof MetaHashNode)) {
                    break;
                }
                e = e.getNext();
            }
            if (e.getKey().equals(key) && !(e instanceof Deleted) && !(e instanceof MetaHashNode)) {
                e.setVal(e.getVal()+1);
            } else {
                e.setNext(new MyHashElement(val, key));
                elementCount++;
            }
        }

        if (elementCount >= 0.75*tableSize) {
            resizeHashTable();
        }
    }

    //===========================================
    public void putMeta(String key, int val) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];

        if (e == null) {
            arr[i] = new MetaHashNode(val, key);
            elementCount++;
        } else {
            while (e.getNext() != null) {
                if (e.getKey().equals(key) && !(e instanceof Deleted) && (e instanceof MetaHashNode)) {
                    break;
                }
                e = e.getNext();
            }
            if (e.getKey().equals(key) && !(e instanceof Deleted) && (e instanceof MetaHashNode)) {
                e.setVal(e.getVal()+1);
            } else {
                e.setNext(new MetaHashNode(val, key));
                elementCount++;
            }
        }

        if (elementCount >= 0.75*tableSize) {
            resizeHashTable();
        }
    }

    //===========================================
    public void remove(String key) {
        int h = getHashCode(key);
        int i = h & (tableSize - 1);
        MyHashElement e = arr[i];
        MyHashElement pred = null;

        if (e == null) {
        } else {
            while (e.getNext() != null && (!e.getKey().equals(key) || !(e instanceof Deleted))) {
                e = e.getNext();
                pred = e;
            }
            if (e.getKey().equals(key)) {
                if (pred == null) {
                    arr[i] = e.getNext();
                    elementCount--;
                } else {
                    pred.setNext(e.getNext());
                    elementCount--;
                }
            }
        }
    }

    //===========================================
    public int getHashCode(String key){
        int h = 0;
        for (int i = 0; i < key.length(); i++) {
            h = h * 3 + key.charAt(i);
        }
        h = h ^ 29;
        return h;
    }

    //===========================================
    public void resizeHashTable() {
        tableSize = tableSize*2;
        MyHashElement temp[] = arr;
        arr = new MyHashElement[tableSize];
        elementCount = 0;
        MyHashElement e;

        for (int j = 0; j < temp.length; j++) {
            if (temp[j] != null) {
                e = temp[j];
                while(e.getNext() != null) {
                    if (!(e instanceof Deleted)) {
                        this.put(e.getKey(), e.getVal());
                    }
                    e = e.getNext();
                }
            }
        }
    }

    //===========================================
    public void writeObject(ObjectOutputStream os) throws IOException {
        MyHashElement e;

        os.writeInt(tableSize);
        os.writeInt(elementCount);
        for (int i = 0; i < arr.length; i++) {
            e = arr[i];
            while (e != null) {
//                os.writeChars(e.getKey());
                os.writeObject(e.getKey());
                os.writeInt(e.getVal());
                e = e.getNext();
            }
        }
    }

    //===========================================
    public void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        elementCount = 0;
        tableSize = is.readInt();
        int count = is.readInt();
        arr = new MyHashElement[tableSize];

        for (int i = 0; i < count-1; i++) {
            String key = (String) is.readObject();
            int val = is.readInt();
            put(key, val);
        }
    }

    public int getTableSize() { return tableSize; }
    public MyHashElement getElement(int ind) { return arr[ind];}
}
