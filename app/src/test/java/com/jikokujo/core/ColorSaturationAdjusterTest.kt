package com.jikokujo.core

import androidx.compose.ui.graphics.Color
import com.jikokujo.core.utils.darken
import com.jikokujo.core.utils.lighten
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ColorSaturationAdjusterTest {
    @Test
    fun `lighten by 0 returns same color`() {
        val color = Color(0.5f, 0.5f, 0.5f)
        val result = color.lighten(0f)
        assertEquals(color, result)
    }

    @Test
    fun `lighten by valid amount increases RGB channels`() {
        val color = Color(0.2f, 0.3f, 0.4f)
        val result = color.lighten(0.1f)
        assertEquals(0.3f, result.red, 0.01f)
        assertEquals(0.4f, result.green, 0.01f)
        assertEquals(0.5f, result.blue, 0.01f)
    }

    @Test
    fun `lighten by 1 on black returns white`() {
        val result = Color.Black.lighten(1f)
        assertEquals(1f, result.red, 0.01f)
        assertEquals(1f, result.green, 0.01f)
        assertEquals(1f, result.blue, 0.01f)
    }

    @Test
    fun `lighten above 1 throws IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            Color.White.lighten(1.1f)
        }
    }

    @Test
    fun `lighten below 0 throws IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            Color.White.lighten(-0.1f)
        }
    }

    @Test
    fun `darken by 0 returns same color`() {
        val color = Color(0.5f, 0.5f, 0.5f)
        val result = color.darken(0f)
        assertEquals(color, result)
    }

    @Test
    fun `darken by valid amount decreases RGB channels`() {
        val color = Color(0.5f, 0.6f, 0.7f)
        val result = color.darken(0.1f)
        assertEquals(0.4f, result.red, 0.01f)
        assertEquals(0.5f, result.green, 0.01f)
        assertEquals(0.6f, result.blue, 0.01f)
    }

    @Test
    fun `darken by 1 on white returns black`() {
        val result = Color.White.darken(1f)
        assertEquals(0f, result.red, 0.01f)
        assertEquals(0f, result.green, 0.01f)
        assertEquals(0f, result.blue, 0.01f)
    }

    @Test
    fun `darken above 1 throws IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            Color.Black.darken(1.1f)
        }
    }

    @Test
    fun `darken below 0 throws IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            Color.Black.darken(-0.1f)
        }
    }
}