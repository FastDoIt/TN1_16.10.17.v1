package by.supercoder.tasknumber1;

import org.junit.Test;

import dalvik.annotation.TestTargetClass;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addition_isNoCorrect()throws Exception{
        assertEquals("Numbers isn't equals!", 5, 2 + 2);
    }
    @Test(expected = NullPointerException.class)
    public void nullStringTest() {
        String str = null;
        assertTrue(str.isEmpty());
    }
    @Test(timeout = 1000)
    public void requestTest() { ; }
}