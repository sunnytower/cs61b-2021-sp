package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.COMMITS_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author sunnytower
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timeStamp;
    //update: <Filename, blobId>
    private Map<String, String> blobs;
    //using id to find the parents.
    private List<String> parents;
    private String id;
    /**  constructor for init.*/
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(timeStamp);
    }
    public Commit() {
        message = "initial commit";
        parents = new ArrayList<>();
        blobs = new HashMap<>();
        timeStamp = new Date(0);
        id = sha1(message, timeStamp.toString());
    }
    public Commit(String message, Commit parents, StagingArea stage) {
        this.message = message;
        this.timeStamp = new Date();
        this.parents = new ArrayList<>(2);
        blobs = new HashMap<>();
        this.parents.add(parents.getId());
        blobs.putAll(parents.getBlobs());
        //change blobs according to stagingArea.
        blobs.putAll(stage.getAdded());
        Set<String> removed = stage.getRemoved();
        for (String filename : removed) {
            blobs.remove(filename);
        }
        this.id = sha1(message, timeStamp.toString(), this.parents.toString(), this.blobs.toString());
    }
    //default commit get contents from its parents.
    public Commit(String message, List<Commit> parents, StagingArea stage) {
        this.message = message;
        this.timeStamp = new Date();
        this.parents = new ArrayList<>(2);
        blobs = new HashMap<>();
        for (Commit c : parents) {
            this.parents.add(c.getId());
            blobs.putAll(c.getBlobs());
        }
        //change blobs according to stagingArea.
        blobs.putAll(stage.getAdded());
        Set<String> removed = stage.getRemoved();
        for (String filename : removed) {
            blobs.remove(filename);
        }
        this.id = sha1(message, timeStamp, this.parents, this.blobs);
    }
    public String getId() {
        return id;
    }
    public Map<String, String> getBlobs() {
        return blobs;
    }
    public List<String> getParents() {
        return parents;
    }
    public String getMessage() {
        return message;
    }
    public void save(File file) {
        writeObject(file, this);
    }
    public String getSelfLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit " + this.id + "\n");
        if (parents.size() == 2) {
            sb.append("Merge: " + parents.get(0).substring(0, 7) + " " + parents.get(1).substring(0, 7));
        }
        sb.append("Date: " + getDate() + "\n");
        sb.append(message + "\n");
        sb.append("\n");
        return sb.toString();
    }
    public String getFirstParentId() {
        if (parents.isEmpty()) {
            return null;
        }
        return parents.get(0);
    }
    /**
     * @param commitId maybe the abbreviation of commitId.
     * @return find commit corresponding to commitId.
     */
    public static Commit idToCommit(String commitId) {
        if (commitId == null) {
            return null;
        }
        if (commitId.length() == UID_LENGTH) {
            File file = join(COMMITS_DIR, commitId);
            if (!file.exists()) {
                return null;
            }
            return readObject(file, Commit.class);
        } else {
            // abbreviation of commitId.
            for (String filename : COMMITS_DIR.list()) {
                if (filename.startsWith(commitId)) {
                    return readObject(join(COMMITS_DIR, filename), Commit.class);
                }
            }
        }
        return null;
    }
}
