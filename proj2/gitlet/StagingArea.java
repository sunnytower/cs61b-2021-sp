package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StagingArea implements Serializable {
    //update: filename -> blobsId.
    private Map<String, String> added;
    private Set<String> removed;

    public StagingArea() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }
    public void add(String filename, String blobId) {
        added.put(filename, blobId);
    }
    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }
    public Map<String, String> getAdded() {
        return added;
    }
    public Set<String> getRemoved() {
        return removed;
    }

}
