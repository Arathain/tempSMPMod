package arathain.mason.util;

import org.joml.Quaternionf;

import static org.joml.Math.*;

public class MathUtil {
    public static Quaternionf initQuaternionButSimple(float x, float y, float z, boolean isInDegrees) {
        Quaternionf quat = new Quaternionf();

        if (isInDegrees) {
            x *= 0.017453292F;
            y *= 0.017453292F;
            z *= 0.017453292F;
        }

        float f = sin(0.5F * x);
        float g = cos(0.5F * x);
        float h = sin(0.5F * y);
        float i = cos(0.5F * y);
        float j = sin(0.5F * z);
        float k = cos(0.5F * z);
        quat.x = f * i * k + g * h * j;
        quat.y = g * h * k - f * i * j;
        quat.z = f * h * k + g * i * j;
        quat.w = g * i * k - f * h * j;

        return quat;
    }
}
