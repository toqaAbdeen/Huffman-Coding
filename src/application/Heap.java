package application;

public class Heap {
    private Node[] element;
    private int size;
    private int capacity;

    public Heap(int capacity) {
        this.capacity = capacity;
        this.element = new Node[capacity + 1];  // +1 for 1-based indexing
        this.size = 0;
    }

    public Heap() {
        this(10);  // Default initial capacity
    }

    public Node[] getElement() {
        Node[] ret = new Node[size + 1];
        System.arraycopy(element, 1, ret, 1, size); // Copy elements from heap array to ret
        return ret;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Add a new element to the heap
    public void addElement(Node elementNode) {
        if (size + 1 >= capacity) {
            resize();  // Resize the heap if full
        }

        int i = ++size;
        while (i > 1 && element[i / 2].getFreq() > elementNode.getFreq()) {
            element[i] = element[i / 2];
            i /= 2;
        }
        element[i] = elementNode;
    }

    // Delete the element with the minimum frequency
    public Node deleteElement() {
        if (size == 0) return null;

        Node min = element[1];
        Node last = element[size--];

        int i = 1;
        int child;
        while (i * 2 <= size) {
            child = i * 2;
            if (child < size && element[child].getFreq() > element[child + 1].getFreq()) {
                child++;
            }

            if (last.getFreq() > element[child].getFreq()) {
                element[i] = element[child];
                i = child;
            } else {
                break;
            }
        }
        element[i] = last;
        return min;
    }

    // Resize the heap if the capacity is full
    private void resize() {
        capacity *= 2;  // Double the capacity
        Node[] newElement = new Node[capacity + 1];  // +1 for 1-based indexing
        System.arraycopy(element, 1, newElement, 1, size);
        element = newElement;
    }
}
