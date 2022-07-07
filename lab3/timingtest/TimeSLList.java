package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        int initLength = 1000;
        int trytime = 0;
        int expecttime = 8;
        int count = 10000;
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        while (trytime != expecttime) {
            SLList<Integer> list = new SLList<>();
            for (int i = 0; i < initLength; ++i) {
                list.addLast(i);
            }
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < count; ++i) {
                list.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(initLength);
            times.addLast(timeInSeconds);
            opCounts.addLast(count);
            trytime++;
            initLength *=2;
        }
        printTimingTable(Ns, times, opCounts);
    }

}
