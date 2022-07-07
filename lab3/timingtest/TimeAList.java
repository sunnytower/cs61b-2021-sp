package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        int initLength = 1000;
        int trytime = 0;
        int expecttime = 10;
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        while (trytime != expecttime) {
            AList<Integer> list = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < initLength; ++j) {
                list.addLast(j);
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(initLength);
            times.addLast(timeInSeconds);
            opCounts.addLast(initLength);

            trytime++;
            initLength *= 2;
        }
        printTimingTable(Ns, times, opCounts);

    }
}
