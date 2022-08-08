package gitlet;

import java.net.ResponseCache;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author sunnytower
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.checkArgument(args.length, 1);
                Repository.init();
                break;
            case "add":
                Repository.checkInitial();
                //add a file at a time.
                Repository.checkArgument(args.length, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 1);
                Repository.log();
                break;
            case "global-log":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 1);
                Repository.globalLog();
                break;
            case "find":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 2);
                Repository.find(args[1]);
                break;
            case "status":
                Repository.checkInitial();
                Repository.checkArgument(args.length, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.checkInitial();
                int length = args.length;
                if (length > 4 | length < 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                } else if (length == 3){
                    //checkout -- filename.
                    Repository.checkoutFile(args[2]);
                } else if (length == 4) {
                    //checkout [commit id] -- filename.
                    Repository.checkoutCommit(args[1], args[3]);
                } else {
                    //checkout branch
                    Repository.checkoutBranch(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
