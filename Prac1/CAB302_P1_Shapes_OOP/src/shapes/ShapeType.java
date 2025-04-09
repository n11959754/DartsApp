/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

    /*
     * Enumerated types for Shape2D Objects
    */

import java.util.HashMap;
import java.util.Map;

public enum ShapeType {
    TRIANGLE(1),
    CIRCLE(2),
    RECTANGLE(3),
    SQUARE(4);

    private final int value;

    ShapeType(int value) {
        this.value = value;
    }

    /**
     * Map ShapeType to ShapeType id
     */
    private static final Map<Integer, ShapeType> _map = new HashMap<Integer, ShapeType>();
    static
    {
        for (ShapeType st : ShapeType.values())
            _map.put(st.value, st);
    }

    /**
     * Get ShapeType from value
     * @param value Value
     * @return ShapeType
     */
    public static ShapeType from(int value)
    {
        return _map.get(value);
    }
}
