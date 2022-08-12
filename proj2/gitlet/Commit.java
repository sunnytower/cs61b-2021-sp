package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;
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

    //default commit get contents from its parents.
    public Commit(String mes, List<Commit> ps, StagingArea stage) {
        message = mes;
        timeStamp = new Date();
        parents = new ArrayList<>(2);
        blobs = new HashMap<>();
        for (Commit c : ps) {
            this.parents.add(c.getId());
            blobs.putAll(c.getBlobs());
        }
        //change blobs according to stagingArea.
        blobs.putAll(stage.getAdded());
        Set<String> removed = stage.getRemoved();
        for (String filename : removed) {
            blobs.remove(filename);
        }
        id = sha1(message, timeStamp.toString(), parents.toString(), blobs.toString());
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
            String p0 = parents.get(0).substring(0, 7);
            String p1 = parents.get(1).substring(0, 7);
            sb.append("Merge: " + p0 + " " + p1 + "\n");
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

    /**
     * write all files in the commit to the dir.
     * @param dir
     */
    public void writeAllFiles(File dir) {
        List<Blob> getBlobs = new ArrayList<>();
        for (String blobId : blobs.values()) {
            getBlobs.add(Blob.idToBlob(blobId, BLOBS_DIR));
        }
        for (Blob blob : getBlobs) {
            writeContents(join(dir, blob.getFilename()), blob.getContents());
        }
    }

    public void checkUntrackedFile(List<String> untracked, File dir) {
        if (untracked.isEmpty()) {
            return;
        }
        String e = "There is an untracked file in the way; delete it, or add and commit it first.";
        for (String file : untracked) {
            String currFileId = new Blob(file, dir).getBlobId();
            String blobId = blobs.getOrDefault(file, null);
            if (!currFileId.equals(blobId)) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
    public static Commit branchToCommit(String name) {
        File branch = join(HEADS_DIR, name);
        return Commit.idToCommit(readContentsAsString(branch));
    }

    /**
     *
     * @param a
     * @return return a's parents list backward.
     */
    private static Set<String> getParentsList(Commit a) {
        Set<String> res = new HashSet<>();
        Queue<Commit> queue = new LinkedList<>();
        queue.add(a);
        while (!queue.isEmpty()) {
            Commit commit = queue.poll();
            res.add(commit.getId());
            if (!commit.getParents().isEmpty()) {
                for (String parentId : commit.getParents()) {
                    queue.add(Commit.idToCommit(parentId));
                }
            }
        }
        return res;
    }

    /**
     *
     * @param a
     * @param b
     * @return return the common ancestor.
     */
    public static Commit findCommonAncestor(Commit a, Commit b) {
        Set<String> aParents = getParentsList(a);
        Queue<Commit> queue = new LinkedList<>();
        queue.add(b);
        while (!queue.isEmpty()) {
            Commit commit = queue.poll();
            if (aParents.contains(commit.getId())) {
                return commit;
            }
            if (!commit.getParents().isEmpty()) {
                for (String parentId : commit.getParents()) {
                    queue.add(Commit.idToCommit(parentId));
                }
            }
        }
        return null;
    }

    /**
     *
     * @param blob
     * @return if deleted or modified.
     */
    public boolean isModified(Blob blob) {
        String myBlobId = blobs.getOrDefault(blob.getFilename(), null);
        return myBlobId == null && !myBlobId.equals(blob.getBlobId());
    }
    public boolean isExists(Blob blob) {
        String tmp = blobs.getOrDefault(blob.getFilename(), null);
        return tmp != null;
    }
    public Blob nameToBlob(String name) {
        String blobId = blobs.getOrDefault(name, null);
        return Blob.idToBlob(blobId, BLOBS_DIR);
    }
}
