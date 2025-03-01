package phanastrae.mirthdew_encore.util;

import java.util.function.Supplier;

public class FloatNoise2D {

    protected final float[] data;
    private final int sizeX;
    private final int sizeZ;

    private FloatNoise2D(float[] data, int sizeX, int sizeZ) {
        this.data = data;
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
    }

    private FloatNoise2D(int sizeX, int sizeZ) {
        this(new float[sizeX*sizeZ], sizeX, sizeZ);
    }

    public float get(int x, int z) {
        x = Math.floorMod(x, this.sizeX);
        z = Math.floorMod(z, this.sizeZ);
        return getUnsafe(x, z);
    }

    public float getUnsafe(int x, int z) {
        return this.data[x + this.sizeX * z];
    }

    public static FloatNoise2D generateNoise(int sizeX, int sizeZ, Supplier<Float> intSupplier) {
        if(sizeX <= 0 || sizeZ <= 0) {
            sizeX = 0;
            sizeZ = 0;
        }

        FloatNoise2D noise = new FloatNoise2D(sizeX, sizeZ);

        for(int i = 0; i < noise.data.length; i++) {
            noise.data[i] = intSupplier.get();
        }

        return noise;
    }
}
