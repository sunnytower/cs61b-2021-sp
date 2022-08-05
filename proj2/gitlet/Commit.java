package gitlet;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.sha1;

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
    private String id;
    //update: use filename -> blobsId.
    private Map<String, String> blobs;
    //using sha1 id to find the parents.
    private List<String> parent;
    /**  constructor for init.*/
    private String dateToTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(timeStamp);
    }
    public Commit() {
        message = "initial commit";
        parent = new ArrayList<>();
        blobs = new HashMap<>();
        timeStamp = new Date(0);
        id = sha1(message, timeStamp.toString());
    }
    public String getId() {
        return id;
    }


}
