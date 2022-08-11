package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    //update: <filename, blobId>
    private Map<String, String> added;
    //filename.
    private Set<String> removed;

    public StagingArea() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }
    public void add(String filename, String blobId) {
        added.put(filename, blobId);
        removed.remove(filename);
    }

    /**
     * if file is staged for addition, unstage it, else add to removed set.
     * @param filename
     */
    public void remove(String filename) {
        String stageBId = getAdded().getOrDefault(filename, null);
        if (stageBId != null) {
            getAdded().remove(filename);
        } else {
            getRemoved().add(filename);
        }
    }
    public Map<String, String> getAdded() {
        return added;
    }
    public Set<String> getRemoved() {
        return removed;
    }
    public void save(File file) {
        writeObject(file, this);
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }
}
