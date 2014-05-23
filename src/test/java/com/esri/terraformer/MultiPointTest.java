package com.esri.terraformer;

import junit.framework.TestCase;
import org.junit.Test;

public class MultiPointTest extends TestCase {
    private static final String VALID_MULTIPOINT = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    private static final String VALID_DIFF_ORDER = "{\"type\":\"MultiPoint\",\"coordinates\":[[101.0,1.0],[100.0,0.0]]}";
    private static final String WRONG_TYPE = "{\"type\":\"Point\",\"coordinates\":[100.0,0.0]}";
    private static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    private static final String INVALID_INNER_TYPE = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,3.0],[100.0,\"squid\"],[101.0,1.0]]}";
    private static final String NO_COORDINATES = "{\"type\":\"MultiPoint\"}";
    private static final String COORDS_NOT_ARRAY = "{\"type\":\"MultiPoint\",\"coordinates\":\"horse\"}";

    @Test
    public void testGetType() throws Exception {
        assertTrue(validMultiPoint().getType() == GeoJsonType.MULTIPOINT);
        assertTrue(new MultiPoint().getType() == GeoJsonType.MULTIPOINT);
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(validMultiPoint().isValid());
        assertFalse(new MultiPoint().isValid());
        assertFalse(new MultiPoint(new Point(100d)).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiPoint mp = validMultiPoint();
        MultiPoint otherMp = new MultiPoint(new Point(100d, 0d), new Point(101d, 1d));
        MultiPoint anotherMp;
        try {
            anotherMp = MultiPoint.decodeMultiPoint(VALID_DIFF_ORDER);
        } catch (TerraformerException e) {
            fail(e.getMessage());
            return;
        }

        assertTrue(mp.isEquivalentTo(otherMp));
        assertTrue(mp.isEquivalentTo(anotherMp));
        assertTrue(otherMp.isEquivalentTo(mp));
        assertTrue(otherMp.isEquivalentTo(anotherMp));
        assertTrue(anotherMp.isEquivalentTo(mp));
        assertTrue(anotherMp.isEquivalentTo(otherMp));
        assertFalse(mp.isEquivalentTo(new Point(100d, 0d)));
        assertFalse(mp.isEquivalentTo(new MultiPoint()));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d))));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d), new Point(100d, 1d))));
    }

    @Test
    public void testDecodeMultiPoint() throws Exception {
        assertEquals(VALID_MULTIPOINT, validMultiPoint().toJson());

        boolean gotException = false;

        try {
            MultiPoint.decodeMultiPoint(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains("type"));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains("not a JSON Object"));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains("type"));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains("not numeric"));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains("key not found"));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(Geometry.COORDINATES_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    public MultiPoint validMultiPoint() {
        MultiPoint mp;
        try {
            mp = MultiPoint.decodeMultiPoint(VALID_MULTIPOINT);
        } catch (TerraformerException e) {
            fail(e.getMessage());
            return null;
        }

        return mp;
    }
}
