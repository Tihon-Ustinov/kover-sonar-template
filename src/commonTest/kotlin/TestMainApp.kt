package tests

import ksonar.MainApp
import kotlin.test.Test
import kotlin.test.assertEquals


class TestMainApp {

    @Test
    fun testSum() {
        val app = MainApp()
        assertEquals(4, app.sum(2, 2))
    }
}