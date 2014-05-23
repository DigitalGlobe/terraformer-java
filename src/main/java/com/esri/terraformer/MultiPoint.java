package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;

public final class MultiPoint extends Geometry<Point> {
    private static final String EXCEPTION_PREFIX = "Error while parsing MultiPoint: ";

    /**
     * A valid MultiPoint contains 2 or more non-null {@link Point}'s.
     *
     * @param points
     */
    public MultiPoint(Point... points) {
        addAll(Arrays.asList(points));
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.MULTIPOINT;
    }

    @Override
    public boolean isValid() {
        for (Point p : this) {
            if (p == null || !p.isValid()) {
                return false;
            }
        }

        return size() > 1;
    }

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return obj.containsAll(this) && containsAll(obj);
    }

    public static MultiPoint decodeMultiPoint(String json) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        return fromJsonObject(getObject(json));
    }

    /**
     * Package private.
     *
     * @param object
     * @return
     * @throws TerraformerException
     */
    static MultiPoint fromJsonObject(JsonObject object) throws TerraformerException {
        if (!(getType(object) == GeoJsonType.MULTIPOINT)) {
            throw new TerraformerException(EXCEPTION_PREFIX, TerraformerException.NOT_OF_TYPE + "\"MultiPoint\"");
        }

        return fromCoordinates(getCoordinates(object));
    }

    /**
     * Package private.
     *
     * @param coordsElem
     * @return
     * @throws TerraformerException
     */
    static MultiPoint fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2);

        MultiPoint returnVal = new MultiPoint();
        for (JsonElement elem : coords) {
            returnVal.add(Point.fromCoordinates(elem));
        }

        return returnVal;
    }
}
