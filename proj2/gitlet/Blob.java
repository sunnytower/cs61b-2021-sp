package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** file object. same filename + contents is the same blob. */
    private String filename;
    private String id;
    private byte[] contents;
    /** set up the property. */
    public Blob(String filename, File cwd) {
        this.filename = filename;
        File file = join(cwd, filename);
        if (file.exists()) {
            contents = readContents(file);
            id = sha1(filename, contents);
        } else {
            /** if file don't exists, content == null.
             *  for check file.exists.
             * */
            contents = null;
        }
    }
    // the corresponding file exists.
    public boolean exists() {
        return contents != null;
    }
    public void save(File file) {
        writeObject(file, this);
    }
    public String getFilename() {
        return filename;
    }
    public String getBlobId() {
        return id;
    }
    public byte[] getContents() {
        return contents;
    }
    public String getContentsAsString() {
        return new String(contents);
    }
    /**
     * @param blobId
     * @param path : maybe STAGING_DIR or BLOBS_DIR.
     * @return find blob corresponding to blobId.
     */
    public static Blob idToBlob(String blobId, File path) {
        if (blobId == null) {
            return null;
        }
        File file = join(path, blobId);
        if (!file.exists()) {
            return null;
        }
        return readObject(file, Blob.class);
    }
}
