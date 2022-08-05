package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** file object. */
    /** relative path.*/
    private String filename;
    private String id;
    private byte[] contents;
    /** setup the property. */
    public Blob(String filename, File cwd) {
        this.filename = filename;
        File file = join(cwd, filename);
        if (file.exists()) {
            contents = readContents(file);
            id = sha1(filename, contents.toString());
        } else {
            contents = null;
        }
    }
    public boolean exists() {
        return contents != null;
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
}
