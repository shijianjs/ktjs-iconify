import kotlin.test.Test
import kotlin.test.assertTrue

open class LibWorldTest {
    @Test
    open fun doTest() {
        assertTrue(getWorld().contains("World"))
    }
}
