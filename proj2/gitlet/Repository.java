package gitlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    /** save the added blobs before commit.*/
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File STAGE = join(GITLET_DIR, "STAGE");
    public static void init() {
        //failure case.
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // setup dir and file.
        setupPersistence();
        // initial commit.
        Commit initial = new Commit();
        initial.save(join(COMMITS_DIR, initial.getId()));
        // branch master contains commit id. ----> refs/heads/master.
        String branch = "master";
        writeContents(join(HEADS_DIR, branch), initial.getId());
        // create HEAD. -> master.
        writeContents(HEAD, branch);
        // initial STAGE.
        new StagingArea().save(STAGE);
    }
    public static void add(String filename) {
        Blob blob = new Blob(filename, CWD);
        //failure case.
        if (!blob.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Commit head = getHead();
        StagingArea stage = getStage();
        String headBId = head.getBlobs().getOrDefault(filename, null);
        String stageBId = stage.getAdded().getOrDefault(filename, null);
        String blobId = blob.getBlobId();
        // if HEAD contains this blob.
        if (blobId.equals(headBId)) {
            //check stage contains the early version of file, if so, delete it.
            if (!blobId.equals(stageBId)) {
                if (stageBId != null) {
                    join(STAGING_DIR, stageBId).delete();
                }
                stage.getAdded().remove(stageBId);
                stage.getRemoved().remove(filename);
                stage.save(STAGE);
            }
        } else if (!blobId.equals(stageBId)){
            // need to update.
            if (stageBId != null) {
                join(STAGING_DIR, stageBId).delete();
            }
            blob.save(join(STAGING_DIR, blobId));
            stage.add(filename, blobId);
            stage.save(STAGE);
        }
    }
    public static void commit(String message) {
        //failure cases.
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        //if stagingArea is empty
        if (getStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //copy from parent, save the stagingArea file to blobs.
        Commit commit = new Commit(message, getHead(), getStage());
        //clean stagingArea and save to the blobs dir.
        cleanStagingAreaAndSave();
        //set the HEAD and branch to new commit. (if no branch, just update branch content)
        setHeadToNewCommit(commit);
        //save commits to files.
        commit.save(join(COMMITS_DIR, commit.getId()));
    }
    public static void rm(String filename) {
        //failure cases.
        Commit head = getHead();
        StagingArea stage = getStage();
        String headBId = head.getBlobs().getOrDefault(filename, null);
        String stageBId = stage.getAdded().getOrDefault(filename, null);
        if (headBId == null && stageBId == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        //if the file is already staged for addition, unstage it.
        if (stageBId != null) {
            stage.getAdded().remove(filename);
        }
        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getBlobId();
        // if the file is tracked in current commit, just delete it in working directory.
        if (blob.exists() && blobId.equals(headBId)) {
            stage.getRemoved().add((filename));
            restrictedDelete(join(CWD, filename));
        }
        stage.save(STAGE);
    }
    public static void log() {
        StringBuilder sb = new StringBuilder();
        Commit commit = getHead();
        while (commit != null) {
            sb.append(commit.getSelfLog());
            commit = Commit.idToCommit(commit.getFirstParentId());
        }
        System.out.println(sb);
    }
    public static void globalLog() {
        StringBuilder sb = new StringBuilder();
        List<String> filenames = plainFilenamesIn(HEADS_DIR);
        for (String filename : filenames) {
            Commit c = Commit.idToCommit(filename);
            sb.append(c.getSelfLog());
        }
        System.out.println(sb);
    }
    public static void find(String message) {
        StringBuilder sb = new StringBuilder();
        List<String> filenames = plainFilenamesIn(HEADS_DIR);
        for (String filename : filenames) {
            Commit c = Commit.idToCommit(filename);
            if (c.getMessage().contains(message)) {
                sb.append(c.getId() + "\n");
            }
        }
        if (sb.toString().isEmpty()) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(sb);
    }
    public static void status() {
        StringBuilder sb = new StringBuilder();
        //print branches.
        sb.append("=== Branches ===\n");
        String headBranchName = readContentsAsString(HEAD);
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        for (String branch : branches) {
            if (branch.equals(headBranchName)) {
                sb.append("*" + branch + "\n");
            } else {
                sb.append(branch + "\n");
            }
        }
        sb.append("\n");
        //print staged files.
        sb.append("=== Staged Files ===\n");
        Set<String> stagedFiles = getStage().getAdded().keySet();
        for (String stagedFile : stagedFiles) {
            sb.append(stagedFile + "\n");
        }
        sb.append("\n");
        //print removed files.
        sb.append("=== Removed Files ===\n");
        for (String removedFiles : getStage().getRemoved()) {
            sb.append(removedFiles + "\n");
        }
        sb.append("\n");
        //modifications not staged for commit and untracked files. leave blank for now.
        sb.append("=== Modifications Not Staged For Commit ===\n");
        sb.append("\n");
        sb.append("=== Untracked Files ===\n");
        sb.append("\n");
        System.out.println(sb);
    }
    public static void checkoutFile(String filename) {
        checkoutFileFromCommit(filename, getHead());
    }

    public static void checkoutCommit(String commitId, String filename) {
        Commit commit = Commit.idToCommit(commitId);
        // commit don't exists.
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            checkoutFileFromCommit(filename, commit);
        }
    }

    private static void checkoutFileFromCommit(String filename, Commit commit) {
        String commitId = commit.getBlobs().getOrDefault(filename, null);
        if (commitId == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            Blob blob = Blob.idToBlob(commitId, BLOBS_DIR);
            blob.writeFile(join(CWD, filename));
        }
    }
    public static void checkoutBranch(String branchName) {
        //if current branch is checkout-branch.
        String headBranchName = readContentsAsString(HEAD);
        if (headBranchName == branchName) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        //if branchName doesn't exists.
        List<String> branchesName = plainFilenamesIn(HEADS_DIR);
        if (!branchesName.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Commit otherBranch = getCommitFromBranchName(branchName);
        //check untracked files.
        otherBranch.checkUntrackedFile(getUntrakcedFile(), CWD);
        // clean stagingArea.
        cleanStagingAreaAndSave();
        // overwrite the working directory.
        // maybe not safe!!!!!!!!!
        for (File file : CWD.listFiles()) {
            restrictedDelete(file);
        }
        otherBranch.writeAllFiles(CWD);
        //change the HEAD to current branch.
        writeContents(HEAD, branchName);
    }
    public static void branch(String branchName) {
        List<String> branchesName = plainFilenamesIn(HEADS_DIR);
        if (branchesName.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(join(HEADS_DIR, branchName), getHead().getId());

    }

    private static List<String> getUntrakcedFile() {
        List<String> untracked = new ArrayList<>();
        Set<String> headFiles = getHead().getBlobs().keySet();
        Set<String> stageFiles = getStage().getAdded().keySet();
        for (String file : plainFilenamesIn(CWD)) {
            if (!headFiles.contains(file) && !stageFiles.contains(file)) {
                untracked.add(file);
            }
//            if (file.contains(".DS_")) {
//                untracked.remove(file);
//            }
        }

        return untracked;
    }
    private static Commit getCommitFromBranchName(String name) {
        File branch = join(HEADS_DIR, name);
        return Commit.idToCommit(readContentsAsString(branch));
    }
    private static Commit getHead() {
        String headBranchName = readContentsAsString(HEAD);
        File branchFile =  join(HEADS_DIR, headBranchName);
        String commitId = readContentsAsString(branchFile);
        return Commit.idToCommit(commitId);
    }
    private static void setHeadToNewCommit(Commit commit) {
        String headBranchName = readContentsAsString(HEAD);
        File branchFile =  join(HEADS_DIR, headBranchName);
        writeContents(branchFile, commit.getId());
    }
    private static StagingArea getStage() {
        return readObject(STAGE, StagingArea.class);
    }

    /**
     * source:https://sta grverflow.com/questions/4645242/how-do-i-move-a-file-from-one-location-to-another-in-java.
     * */
    private static void cleanStagingAreaAndSave() {
        File[] files = STAGING_DIR.listFiles();
        if (files == null) {
            return;
        }
        Path blobPath = BLOBS_DIR.toPath();
        for (File file : files) {
            Path source = file.toPath();
            try {
                Files.move(source, blobPath.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new StagingArea().save(STAGE);
    }
    public static void checkArgument(int actual, int expected) {
        if (actual != expected) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    private static void setupPersistence() {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
    }
    public static void checkInitial() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
