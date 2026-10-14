package com.forgenote.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FileExporterTest {

    @Test
    fun testEscapeHtml_escapesMaliciousTags() {
        val input = "<script>alert('XSS')</script>"
        val expected = "&lt;script&gt;alert(&#x27;XSS&#x27;)&lt;/script&gt;"
        val result = FileExporter.escapeHtml(input)
        assertEquals("HTML tags and quotes should be correctly escaped", expected, result)
    }

    @Test
    fun testEscapeHtml_escapesAmpersandsAndQuotes() {
        val input = "Text with & and \"quotes\""
        val expected = "Text with &amp; and &quot;quotes&quot;"
        val result = FileExporter.escapeHtml(input)
        assertEquals("Ampersands and double quotes should be correctly escaped", expected, result)
    }

    @Test
    fun testEscapeHtml_normalTextRemainsUnchanged() {
        val input = "This is a normal title 123"
        val expected = "This is a normal title 123"
        val result = FileExporter.escapeHtml(input)
        assertEquals("Normal text should remain unchanged", expected, result)
    }
}
