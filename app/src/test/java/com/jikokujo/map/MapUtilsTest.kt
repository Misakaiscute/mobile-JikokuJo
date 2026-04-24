package com.jikokujo.map

import com.jikokujo.map.utils.MapUtils
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.RoutePathPoint
import org.junit.Assert.*
import org.junit.Test

class MapUtilsTest {
    private fun point(lat: Double, lon: Double): RoutePathPoint =
        RoutePathPoint(location = Location.Auxiliary(lat = lat, lon = lon), distanceTraveled = 0)

    private fun stop(lat: Double, lon: Double): Location.Stop =
        Location.Stop(lat = lat, lon = lon)

    @Test
    fun `findClosestLocationIndex returns 0 for single point list`() {
        val points = listOf(point(47.0, 19.0))
        val target = stop(47.0, 19.0)
        assertEquals(0, MapUtils.findClosestLocationIndex(points, target))
    }

    @Test
    fun `findClosestLocationIndex returns index of exact match`() {
        val points = listOf(
            point(47.0, 19.0),
            point(48.0, 20.0),
            point(49.0, 21.0)
        )
        val target = stop(48.0, 20.0)
        assertEquals(1, MapUtils.findClosestLocationIndex(points, target))
    }

    @Test
    fun `findClosestLocationIndex returns closest when target is between points`() {
        val points = listOf(
            point(0.0, 0.0),
            point(10.0, 0.0),
            point(20.0, 0.0)
        )
        val target = stop(10.1, 0.0)
        assertEquals(1, MapUtils.findClosestLocationIndex(points, target))
    }

    @Test
    fun `findClosestLocationIndex returns first index when all points equidistant`() {
        val points = listOf(
            point(1.0, 0.0),
            point(-1.0, 0.0)
        )
        val target = stop(0.0, 0.0)
        assertEquals(0, MapUtils.findClosestLocationIndex(points, target))
    }

    @Test
    fun `findClosestLocationIndex works with negative coordinates`() {
        val points = listOf(
            point(-50.0, -100.0),
            point(-10.0, -20.0),
            point(-1.0, -2.0)
        )
        val target = stop(-1.5, -2.5)
        assertEquals(2, MapUtils.findClosestLocationIndex(points, target))
    }

    @Test
    fun `calculateCenterPoint throws ArithmeticException for empty list`() {
        assertThrows(ArithmeticException::class.java) {
            MapUtils.calculateCenterPoint(emptyList())
        }
    }

    @Test
    fun `calculateCenterPoint returns same point for single element`() {
        val points = listOf(point(47.5, 19.1))
        val result = MapUtils.calculateCenterPoint(points)
        assertEquals(47.5, result.lat, 0.0001)
        assertEquals(19.1, result.lon, 0.0001)
    }

    @Test
    fun `calculateCenterPoint returns midpoint for two points`() {
        val points = listOf(
            point(0.0, 0.0),
            point(10.0, 10.0)
        )
        val result = MapUtils.calculateCenterPoint(points)
        assertEquals(5.0, result.lat, 0.0001)
        assertEquals(5.0, result.lon, 0.0001)
    }

    @Test
    fun `calculateCenterPoint returns correct center for multiple points`() {
        val points = listOf(
            point(0.0, 0.0),
            point(6.0, 3.0),
            point(3.0, 6.0)
        )
        val result = MapUtils.calculateCenterPoint(points)
        assertEquals(3.0, result.lat, 0.0001)
        assertEquals(3.0, result.lon, 0.0001)
    }

    @Test
    fun `calculateCenterPoint works with negative coordinates`() {
        val points = listOf(
            point(-10.0, -20.0),
            point(10.0, 20.0)
        )
        val result = MapUtils.calculateCenterPoint(points)
        assertEquals(0.0, result.lat, 0.0001)
        assertEquals(0.0, result.lon, 0.0001)
    }

    @Test
    fun `findPathBoundaries returns zero pair for empty list`() {
        val (min, max) = MapUtils.findPathBoundaries(emptyList())
        assertEquals(0.0, min.lat, 0.0001)
        assertEquals(0.0, min.lon, 0.0001)
        assertEquals(0.0, max.lat, 0.0001)
        assertEquals(0.0, max.lon, 0.0001)
    }

    @Test
    fun `findPathBoundaries returns same point for single element`() {
        val points = listOf(point(47.5, 19.1))
        val (min, max) = MapUtils.findPathBoundaries(points)
        assertEquals(47.5, min.lat, 0.0001)
        assertEquals(19.1, min.lon, 0.0001)
        assertEquals(47.5, max.lat, 0.0001)
        assertEquals(19.1, max.lon, 0.0001)
    }

    @Test
    fun `findPathBoundaries returns correct min and max for multiple points`() {
        val points = listOf(
            point(47.0, 18.0),
            point(49.0, 22.0),
            point(48.0, 19.0)
        )
        val (min, max) = MapUtils.findPathBoundaries(points)
        assertEquals(47.0, min.lat, 0.0001)
        assertEquals(18.0, min.lon, 0.0001)
        assertEquals(49.0, max.lat, 0.0001)
        assertEquals(22.0, max.lon, 0.0001)
    }

    @Test
    fun `findPathBoundaries lat and lon boundaries are independent`() {
        val points = listOf(
            point(lat = 47.0, lon = 22.0),
            point(lat = 49.0, lon = 18.0)
        )
        val (min, max) = MapUtils.findPathBoundaries(points)
        assertEquals(47.0, min.lat, 0.0001)
        assertEquals(18.0, min.lon, 0.0001)
        assertEquals(49.0, max.lat, 0.0001)
        assertEquals(22.0, max.lon, 0.0001)
    }

    @Test
    fun `findPathBoundaries works with negative coordinates`() {
        val points = listOf(
            point(-50.0, -100.0),
            point(-10.0, -20.0)
        )
        val (min, max) = MapUtils.findPathBoundaries(points)
        assertEquals(-50.0, min.lat, 0.0001)
        assertEquals(-100.0, min.lon, 0.0001)
        assertEquals(-10.0, max.lat, 0.0001)
        assertEquals(-20.0, max.lon, 0.0001)
    }
}