package gitlet;

import java.io.File;
import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *
 *  @author sunnytower
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The directory overview.
     * .gitlet
     *      Staging
     *      blobs
     *      commits
     *      refs
     *          heads
     *      HEAD(FILE)
     *      STAGE(FILE)
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File STAGE = join(GITLET_DIR, "STAGE");
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // setup dir.
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();

        // initial commit.
        Commit inital = new Commit();
        saveCommitToFile(inital);
        // branch master contains commit id. ----> refs/heads/master.
        String branch = "master";
        File master = join(HEADS_DIR, branch);
        writeContents(master, inital.getId());

        // create HEAD. -> master.
        writeContents(HEAD, branch);
        // initial STAGE.
        writeObject(STAGE, new StagingArea());
    }
    private static void saveCommitToFile(Commit c) {
        File file = join(COMMITS_DIR, c.getId());
        writeObject(file, c);
    }
    public static void add(String filename) {

    }
}
