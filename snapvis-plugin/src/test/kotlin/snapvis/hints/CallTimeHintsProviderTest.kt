package snapvis.hints

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.utils.inlays.InlayHintsProviderTestCase
import snapvis.CalculatorLightProjectDescriptor
import snapvis.SampleCalculatorSnapshot
import java.io.File
import kotlin.test.Test

class CallTimeHintsProviderTest : InlayHintsProviderTestCase() {
    override fun setUp() {
        super.setUp()
        SampleCalculatorSnapshot.load(project)
    }

    override fun tearDown() {
        SampleCalculatorSnapshot.clear(project)
        super.tearDown()
    }

    @Test
    fun testTokenizer() {
        doTest("src/test/testData/hints/callTime/Tokenizer.kt")
    }

    private fun doTest(testPath: String) {
        with(CallTimeHintsProvider()) {
            val fileContents = FileUtil.loadFile(File(testPath), true)
            val settings = createSettings()
            testProvider("Tokenizer.kt", fileContents, this, settings, true)
        }
    }

    override fun getTestDataPath(): String = "src/test/testData/hints/callTime"
    override fun getProjectDescriptor(): LightProjectDescriptor = CalculatorLightProjectDescriptor
}
