package io.github.goldeneas.cosmicmining.utils;

import org.bukkit.util.Vector;

public class MathUtils {

    public static Vector rotateAroundX(Vector vector, double radians) {
        double y, z, sin, cos;
        sin = Math.sin(radians);
        cos = Math.cos(radians);
        y = vector.getY() * cos - vector.getZ() * sin;
        z = vector.getY() * sin + vector.getZ() * cos;

        return vector.setY(y).setZ(z);
    }

    public static Vector rotateAroundY(Vector vector, double radians) {
        double x, z, sin, cos;
        sin = Math.sin(radians);
        cos = Math.cos(radians);
        x = vector.getX() * cos + vector.getZ() * sin;
        z = vector.getX() * cos - vector.getZ() * sin;

        return vector.setX(x).setZ(z);
    }

    public static Vector rotateAroundZ(Vector vector, double radians) {
        double x, y, sin, cos;
        sin = Math.sin(radians);
        cos = Math.cos(radians);
        x = vector.getX() * cos - vector.getY() * sin;
        y = vector.getX() * sin + vector.getY() * cos;

        return vector.setX(x).setY(y);
    }
}
