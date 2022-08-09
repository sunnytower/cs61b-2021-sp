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
    public void remove(String filename) {
        added.remove(filename);
        removed.remove(filename);
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
    public Blob filenameToBlob(String filename, File blobPath) {
        if (!added.containsKey(filename)) {
            return null;
        }
        String blobId = added.get(filename);
        File file = join(blobPath, blobId);
        Blob blob = readObject(file, Blob.class);
        return blob;
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }
}
