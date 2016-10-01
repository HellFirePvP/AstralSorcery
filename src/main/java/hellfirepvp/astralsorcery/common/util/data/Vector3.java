package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Vector3
 * Created by HellFirePvP
 * Date: 17.11.2015 / 18:40
 */
public class Vector3 {

    private static final Random RAND = new Random();

    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    protected double x;
    protected double y;
    protected double z;

    public Vector3() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    public Vector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vector3(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }
    public Vector3(TileEntity te) {
        this(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
    }

    public Vector3(Vec3d vec) {
        this(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vector3 add(Vec3i vec) {
        this.x += vec.getX();
        this.y += vec.getY();
        this.z += vec.getZ();
        return this;
    }

    public Vector3 add(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3 addX(double x) {
        this.x += x;
        return this;
    }

    public Vector3 addY(double y) {
        this.y += y;
        return this;
    }

    public Vector3 addZ(double z) {
        this.z += z;
        return this;
    }

    public Vector3 subtract(Vector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public Vector3 multiply(Vector3 vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        return this;
    }

    public Vector3 divide(Vector3 vec) {
        this.x /= vec.x;
        this.y /= vec.y;
        this.z /= vec.z;
        return this;
    }

    public Vector3 divide(double divisor) {
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
        return this;
    }

    public Vector3 copy(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double distance(Vector3 o) {
        return Math.sqrt(distanceSquared(o));
    }

    public double distanceSquared(Vector3 o) {
        double difX = x - o.x;
        double difY = y - o.y;
        double difZ = z - o.z;
        return difX * difX + difY * difY + difZ * difZ;
    }

    public float angle(Vector3 other) {
        double dot = dot(other) / (length() * other.length());

        return (float) Math.acos(dot);
    }

    public Vector3 midpoint(Vector3 other) {
        this.x = ((this.x + other.x) / 2.0D);
        this.y = ((this.y + other.y) / 2.0D);
        this.z = ((this.z + other.z) / 2.0D);
        return this;
    }

    public Vector3 getMidpoint(Vector3 other) {
        double x = (this.x + other.x) / 2.0D;
        double y = (this.y + other.y) / 2.0D;
        double z = (this.z + other.z) / 2.0D;
        return new Vector3(x, y, z);
    }

    public Vector3 multiply(int m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public Vector3 multiply(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public Vector3 multiply(float m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 crossProduct(Vector3 o) {
        double newX = this.y * o.z - o.y * this.z;
        double newY = this.z * o.x - o.z * this.x;
        double newZ = this.x * o.y - o.x * this.y;

        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    public Vector3 perpendicular() {
        if (this.z == 0.0D) {
            return zCrossProduct();
        }
        return xCrossProduct();
    }

    public Vector3 xCrossProduct() {
        double d = this.z;
        double d1 = -this.y;
        this.x = 0.0D;
        this.y = d;
        this.z = d1;
        return this;
    }

    public Vector3 zCrossProduct() {
        double d = this.y;

        double d1 = -this.x;
        this.x = d;
        this.y = d1;
        this.z = 0.0D;
        return this;
    }

    public Vector3 yCrossProduct() {
        double d = -this.z;
        double d1 = this.x;
        this.x = d;
        this.y = 0.0D;
        this.z = d1;
        return this;
    }

    public Vector3 rotate(double angle, Vector3 axis) {
        Quat.aroundAxis(axis.clone().normalize(), angle).rotate(this);
        return this;
    }

    public Vector3 normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public Vector3 fastLowAccNormalize() {
        double lengthSq = lengthSquared();
        lengthSq = fastInvSqrt(lengthSq);

        this.x *= lengthSq;
        this.y *= lengthSq;
        this.z *= lengthSq;
        return this;
    }

    //x -> about 1/sqrt(x)
    //~50% faster than 1/Math.sqrt(x) in its ~3-4th iterations for about the same numbers.
    public static double fastInvSqrt(double x) {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        for (int it = 0; it < 4; it++) {
            x = x * (1.5d - xhalf * x * x);
        }
        return x;
    }

    public Vector3 zero() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
        return this;
    }

    public static Vector3 random() {
        return new Vector3(RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1));
    }

    public static Vector3 positiveYRandom() {
        return random().setY(Math.abs(random().getY()));
    }

    //RIP ChunkCoordinates BibleThump
    /*public static Vector3 fromCC(ChunkCoordinates cc) {
        return new Vector3(cc.posX, cc.posY, cc.posZ);
    }

    public ChunkCoordinates getAsFloatCC() {
        return new ChunkCoordinates(Float.floatToIntBits((float) this.x), Float.floatToIntBits((float) this.y), Float.floatToIntBits((float) this.z));
    }

    public static Vector3 getFromFloatCC(ChunkCoordinates cc) {
        return new Vector3(Float.intBitsToFloat(cc.posX), Float.intBitsToFloat(cc.posY), Float.intBitsToFloat(cc.posZ));
    }*/

    public boolean isInAABB(Vector3 min, Vector3 max) {
        return (this.x >= min.x) && (this.x <= max.x) && (this.y >= min.y) && (this.y <= max.y) && (this.z >= min.z) && (this.z <= max.z);
    }

    public boolean isInSphere(Vector3 origin, double radius) {
        double difX = origin.x - this.x;
        double difY = origin.y - this.y;
        double difZ = origin.z - this.z;
        return (difX * difX + difY * difY + difZ * difZ) <= (radius * radius);
    }

    public Vec3d toVec3d() {
        return new Vec3d(x, y, z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public Vector3 vectorFromHereTo(Vector3 target) {
        return new Vector3(target.x - x, target.y - y, target.z - z);
    }

    public Vector3 vectorFromHereTo(double tX, double tY, double tZ) {
        return new Vector3(tX - x, tY - y, tZ - z);
    }

    public double getX() {
        return this.x;
    }

    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    public double getY() {
        return this.y;
    }

    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }

    public Vector3 setX(int x) {
        this.x = x;
        return this;
    }

    public Vector3 setX(double x) {
        this.x = x;
        return this;
    }

    public Vector3 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3 setY(int y) {
        this.y = y;
        return this;
    }

    public Vector3 setY(double y) {
        this.y = y;
        return this;
    }

    public Vector3 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3 setZ(int z) {
        this.z = z;
        return this;
    }

    public Vector3 setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector3 setZ(float z) {
        this.z = z;
        return this;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3)) {
            return false;
        }
        Vector3 other = (Vector3) obj;

        return (Math.abs(this.x - other.x) < 1.0E-006D) && (Math.abs(this.y - other.y) < 1.0E-006D) && (Math.abs(this.z - other.z) < 1.0E-006D) && (getClass().equals(obj.getClass()));
    }

    public int hashCode() {
        int hash = 7;

        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }

    public static class Quat {

        public double x;
        public double y;
        public double z;
        public double s;

        public Quat() {
            this.s = 1.0D;
            this.x = 0.0D;
            this.y = 0.0D;
            this.z = 0.0D;
        }

        public Quat(Quat quat) {
            this.x = quat.x;
            this.y = quat.y;
            this.z = quat.z;
            this.s = quat.s;
        }

        public Quat(double d, double d1, double d2, double d3) {
            this.x = d1;
            this.y = d2;
            this.z = d3;
            this.s = d;
        }

        public void set(Quat quat) {
            this.x = quat.x;
            this.y = quat.y;
            this.z = quat.z;
            this.s = quat.s;
        }

        public static Quat aroundAxis(double ax, double ay, double az, double angle) {
            angle *= 0.5D;
            double d4 = Math.sin(angle);
            return new Quat(Math.cos(angle), ax * d4, ay * d4, az * d4);
        }

        public void multiply(Quat quat) {
            double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
            double d1 = this.s * quat.x + this.x * quat.s - this.y * quat.z + this.z * quat.y;
            double d2 = this.s * quat.y + this.x * quat.z + this.y * quat.s - this.z * quat.x;
            double d3 = this.s * quat.z - this.x * quat.y + this.y * quat.x + this.z * quat.s;
            this.s = d;
            this.x = d1;
            this.y = d2;
            this.z = d3;
        }

        public void rightMultiply(Quat quat) {
            double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
            double d1 = this.s * quat.x + this.x * quat.s + this.y * quat.z - this.z * quat.y;
            double d2 = this.s * quat.y - this.x * quat.z + this.y * quat.s + this.z * quat.x;
            double d3 = this.s * quat.z + this.x * quat.y - this.y * quat.x + this.z * quat.s;
            this.s = d;
            this.x = d1;
            this.y = d2;
            this.z = d3;
        }

        public double mag() {
            return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.s * this.s);
        }

        public void normalize() {
            double d = mag();
            if (d == 0.0D) {
                return;
            }
            d = 1.0D / d;
            this.x *= d;
            this.y *= d;
            this.z *= d;
            this.s *= d;
        }

        public void rotate(Vector3 vec) {
            double d = -this.x * vec.x - this.y * vec.y - this.z * vec.z;
            double d1 = this.s * vec.x + this.y * vec.z - this.z * vec.y;
            double d2 = this.s * vec.y - this.x * vec.z + this.z * vec.x;
            double d3 = this.s * vec.z + this.x * vec.y - this.y * vec.x;
            vec.x = (d1 * this.s - d * this.x - d2 * this.z + d3 * this.y);
            vec.y = (d2 * this.s - d * this.y + d1 * this.z - d3 * this.x);
            vec.z = (d3 * this.s - d * this.z - d1 * this.y + d2 * this.x);
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringbuilder, Locale.US);
            formatter.format("Quaternion:\n");
            formatter.format("  < %f %f %f %f >\n", this.s, this.x, this.y, this.z);
            formatter.close();
            return stringbuilder.toString();
        }

        public static Quat aroundAxis(Vector3 axis, double angle) {
            return aroundAxis(axis.x, axis.y, axis.z, angle);
        }

    }

    public static class RotAxis {

        public static final Vector3 X_AXIS = new Vector3(1, 0, 0);
        public static final Vector3 Y_AXIS = new Vector3(0, 1, 0);
        public static final Vector3 Z_AXIS = new Vector3(0, 0, 1);

    }

}
