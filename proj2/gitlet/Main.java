package gitlet;

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
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
