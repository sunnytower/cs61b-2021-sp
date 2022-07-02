package IntList;

import static org.junit.Assert.*;

import jh61b.junit.In;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes1(){
        IntList lst=IntList.of(1,2,3,4,5,7);
        boolean changed=IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 4 -> 9 -> 4 -> 25 -> 49",lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes2(){
        IntList lst=IntList.of(1,2);
        boolean changed=IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 4",lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes3(){
        IntList lst=IntList.of(3,2);
        boolean changed=IntListExercises.squarePrimes(lst);
        assertEquals("9 -> 4",lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes4(){
        IntList lst=IntList.of(341);
        boolean changed=IntListExercises.squarePrimes(lst);
        assertEquals("341",lst.toString());
    }
}
