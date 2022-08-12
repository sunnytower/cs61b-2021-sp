package gitlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
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
            String err = "A Gitlet version-control system already exists in the current directory.";
            System.out.println(err);
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
        } else if (!blobId.equals(stageBId)) {
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
        setCommit(message, List.of(getHead()));
    }
    private static void setCommit(String mes, List<Commit> parents) {
        if (getStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //copy from parent, save the stagingArea file to blobs.
        Commit commit = new Commit(mes, parents, getStage());
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
        stage.remove(filename);
        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getBlobId();
        // if the file is tracked in current commit, just delete it in working directory.
        if (blob.exists() && blobId.equals(headBId)) {
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
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
        for (String filename : filenames) {
            Commit c = Commit.idToCommit(filename);
            sb.append(c.getSelfLog());
        }
        System.out.println(sb);
    }
    public static void find(String message) {
        StringBuilder sb = new StringBuilder();
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
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
        if (commit == null) {
            // failure case.
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            checkoutFileFromCommit(filename, commit);
        }
    }

    private static void checkoutFileFromCommit(String filename, Commit commit) {
        String commitId = commit.getBlobs().getOrDefault(filename, null);
        if (commitId == null) {
            // failure case.
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            Blob blob = Blob.idToBlob(commitId, BLOBS_DIR);
            blob.writeFile(join(CWD, filename));
        }
    }
    public static void checkoutBranch(String branchName) {
        //failure cases.
        String headBranchName = readContentsAsString(HEAD);
        if (headBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> branchesName = plainFilenamesIn(HEADS_DIR);
        if (!branchesName.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Commit otherBranch = Commit.branchToCommit(branchName);
        //check untracked files.
        otherBranch.checkUntrackedFile(getUntrackedFile(), CWD);
        // clean stagingArea.
        cleanStagingAreaAndSave();
        // overwrite the working directory.
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
            // failure case.
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(join(HEADS_DIR, branchName), getHead().getId());

    }
    public static void rmBranch(String branchName) {
        checkBranchNameExists(branchName);
        String headBranchName = readContentsAsString(HEAD);
        if (headBranchName.equals(branchName)) {
            // failure case.
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        //delete the branch file.
        File branchFile = join(HEADS_DIR, branchName);
        branchFile.delete();
    }
    public static void reset(String commitId) {
        //failure case.
        Commit givenCommit = Commit.idToCommit(commitId);
        if (givenCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit head = getHead();
        head.checkUntrackedFile(getUntrackedFile(), CWD);
        //Checks out all the files tracked by the given commit.
        cleanStagingAreaAndSave();
        // overwrite the working directory.
        for (File file : CWD.listFiles()) {
            restrictedDelete(file);
        }
        givenCommit.writeAllFiles(CWD);
        //change the HEAD to current branch.
        setHeadToNewCommit(givenCommit);
    }
    public static void merge(String branchName) {
        Commit head = getHead();
        String headBranchName = readContentsAsString(HEAD);
        StagingArea stage = getStage();
        //failure cases.
        if (!stage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        checkBranchNameExists(branchName);
        if (readContentsAsString(HEAD).equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        head.checkUntrackedFile(getUntrackedFile(), CWD);
        Commit other = Commit.branchToCommit(branchName);
        Commit split = Commit.findCommonAncestor(head, other);
        //if branchCommit == splitCommit. do nothing.
        if (split.getId().equals(other.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        //if headBranchCommit = splitCommit
        if (head.getId().equals(split.getId())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        // reference: https://www.youtube.com/watch?v=JR3OYCMv9b4&t=929s
        Set<String> filenames =  getAllFilenames(split, head, other);
        for (String filename : filenames) {
            //use "" to make life easier.
            String sId = split.getBlobs().getOrDefault(filename, "");
            String hId = head.getBlobs().getOrDefault(filename, "");
            String oId = other.getBlobs().getOrDefault(filename, "");
            if (sId.equals(oId) || hId.equals(oId)) {
                continue;
            } else if (hId.equals(sId)) {
                if (oId.equals("")) {
                    rm(filename);
                } else {
                    Blob blob = Blob.idToBlob(oId, BLOBS_DIR);
                    blob.writeFile(join(CWD, filename));
                    add(filename);
                }
            } else {
                String[] headContent = getContent(hId).split("\n");
                String[] otherContent = getContent(oId).split("\n");
                String conflictContent = getConflictContent(headContent, otherContent);
                File file = join(CWD, filename);
                writeContents(file, conflictContent);
                System.out.println("Encountered a merge conflict.");
            }
        }
        String mes = "Merged " + branchName + " into " + headBranchName + ".";
        setCommit(mes, List.of(head, other));
    }
    private static String getConflictContent(String[] head, String[] other) {
        StringBuilder sb = new StringBuilder();
        int len1 = head.length, len2 = other.length;
        int i = 0, j = 0;
        while (i < len1 && j < len2) {
            if (head[i].equals(other[j])) {
                sb.append(head[i]);
            } else {
                sb.append(getConflictContent(head[i], other[j]));
            }
            i++;
            j++;
        }
        while (i < len1) {
            sb.append(getConflictContent(head[i], ""));
            i++;
        }
        while (j < len2) {
            sb.append(getConflictContent("", other[j]));
            j++;
        }
        return sb.toString();
    }
    private static String getConflictContent(String head, String other) {
        StringBuilder sb = new StringBuilder();
        sb.append("<<<<<<< HEAD\n");
        sb.append(head.equals("") ? head : head + "\n");
        sb.append("=======\n");
        sb.append(other.equals("") ? other : other + "\n");
        sb.append(">>>>>>>\n");
        return sb.toString();
    }
    private static String getContent(String blobId) {
        if (blobId.equals("")) {
            return "";
        }
        return Blob.idToBlob(blobId, BLOBS_DIR).getContentsAsString();
    }
    public static void checkBranchNameExists(String name) {
        List<String> allBranchNames = plainFilenamesIn(HEADS_DIR);
        if (!allBranchNames.contains(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }
    private static Set<String> getAllFilenames(Commit split, Commit head, Commit other) {
        Set<String> set = new HashSet<>();
        set.addAll(split.getBlobs().keySet());
        set.addAll(head.getBlobs().keySet());
        set.addAll(other.getBlobs().keySet());
        return set;
    }
    /**
     *
     * @return all files not staged or in head commit.
     */
    private static List<String> getUntrackedFile() {
        List<String> untracked = new ArrayList<>();
        Set<String> headFiles = getHead().getBlobs().keySet();
        Set<String> stageFiles = getStage().getAdded().keySet();
        for (String file : plainFilenamesIn(CWD)) {
            if (!headFiles.contains(file) && !stageFiles.contains(file)) {
                untracked.add(file);
            }
        }
        return untracked;
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
    private static void cleanStagingAreaAndSave() {
        File[] files = STAGING_DIR.listFiles();
        if (files == null) {
            return;
        }
        Path blobPath = BLOBS_DIR.toPath();
        for (File file : files) {
            Path source = file.toPath();
            try {
                Path res = blobPath.resolve(source.getFileName());
                Files.move(source, res, StandardCopyOption.REPLACE_EXISTING);
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
