package IntList;
import static org.junit.Assert.*;

import org.junit.Test;
public class TestIntList {
    @Test
    public void testAll(){
        IntList lst=IntList.of(11,22,13,24,17,6);
        IntListExercises.setToZeroIfMaxFEL(lst);
        assertEquals("11 -> 22 -> 13 -> 24 -> 17 -> 0",lst.toString());
        IntListExercises.addConstant(lst, 2);
        assertEquals("13 -> 24 -> 15 -> 26 -> 19 -> 2",lst.toString());
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("169 -> 24 -> 15 -> 26 -> 361 -> 4", lst.toString());
        assertTrue(changed);
    }
}
