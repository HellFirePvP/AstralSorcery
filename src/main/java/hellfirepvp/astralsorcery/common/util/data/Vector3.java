/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    protected double x;
    protected double y;
    protected double z;

    public Vector3() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    public Vector3(Vector3 copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
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

    public Vector3(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vector3(TileEntity te) {
        this(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
    }

    public Vector3(Vec3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public static Vector3 atEntityCorner(Entity entity) {
        return new Vector3(entity.getPosX(), entity.getPosY(), entity.getPosZ());
    }

    public static Vector3 atEntityCenter(Entity entity) {
        Vector3 offset = atEntityCorner(entity);
        return offset.add(entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth() / 2);
    }

    public static Vector3 getMin(AxisAlignedBB box) {
        return new Vector3(box.minX, box.minY, box.minZ);
    }

    public static Vector3 getMax(AxisAlignedBB box) {
        return new Vector3(box.maxX, box.maxY, box.maxZ);
    }

    public Vector3 add(Vec3i vec) {
        this.x += vec.getX();
        this.y += vec.getY();
        this.z += vec.getZ();
        return this;
    }

    public Vector3 add(Vec3d vec) {
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

    public Vector3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
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

    public Vector3 subtract(double vX, double vY, double vZ) {
        this.x -= vX;
        this.y -= vY;
        this.z -= vZ;
        return this;
    }

    public Vector3 subtract(Entity e) {
        this.x -= e.getPosX();
        this.y -= e.getPosY();
        this.z -= e.getPosZ();
        return this;
    }

    public Vector3 subtract(Vec3i vec) {
        this.x -= vec.getX();
        this.y -= vec.getY();
        this.z -= vec.getZ();
        return this;
    }

    public Vector3 subtract(Vec3d vec) {
        this.x -= vec.getX();
        this.y -= vec.getY();
        this.z -= vec.getZ();
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

    public Vector3 negate() {
        return this.multiply(-1);
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

    public double distance(Entity e) {
        return Math.sqrt(distanceSquared(e));
    }

    public double distanceSquared(Entity e) {
        return distanceSquared(Vector3.atEntityCorner(e));
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

    public double distance(Vec3i o) {
        return Math.sqrt(distanceSquared(o));
    }

    public double distanceSquared(Vec3i o) {
        double difX = x - o.getX();
        double difY = y - o.getY();
        double difZ = z - o.getZ();
        return difX * difX + difY * difY + difZ * difZ;
    }

    public double distance(Vec3d o) {
        return Math.sqrt(distanceSquared(o));
    }

    public double distanceSquared(Vec3d o) {
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

    public Vector3 abs() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        return this;
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

    //In rad's
    public Vector3 rotate(double angle, Vector3 axis) {
        Quat.buildQuatFrom3DVector(axis.clone().normalize(), angle).rotateWithMagnitude(this);
        return this;
    }

    public Vector3 normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public Vector3 fNormalize() {
        double lengthSq = lengthSquared();
        lengthSq = fastInvSqrt(lengthSq);

        this.x *= lengthSq;
        this.y *= lengthSq;
        this.z *= lengthSq;
        return this;
    }

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

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static Vector3 fromBytes(ByteBuf buf) {
        return new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static Vector3 random() {
        return new Vector3(RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1));
    }

    public static Vector3 random(Random rand) {
        return new Vector3(rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), rand.nextDouble() * (rand.nextBoolean() ? 1 : -1));
    }

    public static Vector3 positiveRandom() {
        return new Vector3(RAND.nextDouble(), RAND.nextDouble(), RAND.nextDouble());
    }

    public static Vector3 positiveRandom(Random rand) {
        return new Vector3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    public static Vector3 positiveYRandom() {
        Vector3 rand = random();
        return rand.setY(Math.abs(rand.getY()));
    }

    public static Vector3 positiveYRandom(Random r) {
        Vector3 rand = random(r);
        return rand.setY(Math.abs(rand.getY()));
    }

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
        return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(MathHelper.floor(x) >> 4, MathHelper.floor(z) >> 4);
    }

    public Vector3 vectorFromHereTo(Vector3 target) {
        return new Vector3(target.x - x, target.y - y, target.z - z);
    }

    public Vector3 vectorFromHereTo(double tX, double tY, double tZ) {
        return new Vector3(tX - x, tY - y, tZ - z);
    }

    //copy & converts to polar coordinates (in degrees)
    //Order: Distance, Theta, Phi
    public Vector3 copyToPolar() {
        double length = length();
        double theta = Math.acos(y / length);
        double phi = Math.atan2(x, z);
        theta = Math.toDegrees(theta);
        phi = 180 + Math.toDegrees(phi);
        return new Vector3(length, theta, phi);
    }

    public Vector3 copyInterpolateWith(Vector3 next, float partial) {
        return new Vector3(
                (x == next.x ? x : x + ((next.x - x) * partial)),
                (y == next.y ? y : y + ((next.y - y) * partial)),
                (z == next.z ? z : z + ((next.z - z) * partial)));
    }

    @OnlyIn(Dist.CLIENT)
    public IVertexBuilder drawPos(IVertexBuilder buf) {
        buf.pos((float) this.x, (float) this.y, (float) this.z);
        return buf;
    }

    @OnlyIn(Dist.CLIENT)
    public IVertexBuilder drawPos(Matrix4f renderMatrix, IVertexBuilder buf) {
        buf.pos(renderMatrix, (float) this.x, (float) this.y, (float) this.z);
        return buf;
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

        return (Math.abs(this.x - other.x) < 1.0E-004D) && (Math.abs(this.y - other.y) < 1.0E-004D) && (Math.abs(this.z - other.z) < 1.0E-004D) && (getClass().equals(obj.getClass()));
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

        public double i;
        public double j;
        public double k;
        public double s;

        public Quat() {
            this(1D);
        }

        public Quat(double zeroMag) {
            this.s = zeroMag;
            this.i = 0D;
            this.j = 0D;
            this.k = 0D;
        }

        public Quat(Quat quat) {
            this.i = quat.i;
            this.j = quat.j;
            this.k = quat.k;
            this.s = quat.s;
        }

        public Quat(double w, double i, double j, double k) {
            this.i = i;
            this.j = j;
            this.k = k;
            this.s = w;
        }

        public void set(Quat quat) {
            this.i = quat.i;
            this.j = quat.j;
            this.k = quat.k;
            this.s = quat.s;
        }

        public static Quat buildQuatWithAngle(double ax, double ay, double az, double angle) {
            angle *= 0.5D;
            double d4 = Math.sin(angle);
            return new Quat(Math.cos(angle), ax * d4, ay * d4, az * d4);
        }

        public void leftMultiply(Quat quat) {
            double d =  this.s * quat.s - this.i * quat.i - this.j * quat.j - this.k * quat.k;
            double d1 = this.s * quat.i + this.i * quat.s - this.j * quat.k + this.k * quat.j;
            double d2 = this.s * quat.j + this.i * quat.k + this.j * quat.s - this.k * quat.i;
            double d3 = this.s * quat.k - this.i * quat.j + this.j * quat.i + this.k * quat.s;
            this.s = d;
            this.i = d1;
            this.j = d2;
            this.k = d3;
        }

        public void rightMultiply(Quat quat) {
            double d =  this.s * quat.s - this.i * quat.i - this.j * quat.j - this.k * quat.k;
            double d1 = this.s * quat.i + this.i * quat.s + this.j * quat.k - this.k * quat.j;
            double d2 = this.s * quat.j - this.i * quat.k + this.j * quat.s + this.k * quat.i;
            double d3 = this.s * quat.k + this.i * quat.j - this.j * quat.i + this.k * quat.s;
            this.s = d;
            this.i = d1;
            this.j = d2;
            this.k = d3;
        }

        public double mag() {
            return Math.sqrt(this.i * this.i + this.j * this.j + this.k * this.k + this.s * this.s);
        }

        public void normalize() {
            double d = mag();
            if (d == 0.0D) {
                return;
            }
            d = 1.0D / d;
            this.i *= d;
            this.j *= d;
            this.k *= d;
            this.s *= d;
        }

        public void rotateWithMagnitude(Vector3 vec) {
            double d = -this.i * vec.x - this.j * vec.y - this.k * vec.z;
            double d1 = this.s * vec.x + this.j * vec.z - this.k * vec.y;
            double d2 = this.s * vec.y - this.i * vec.z + this.k * vec.x;
            double d3 = this.s * vec.z + this.i * vec.y - this.j * vec.x;
            vec.x = (d1 * this.s - d * this.i - d2 * this.k + d3 * this.j);
            vec.y = (d2 * this.s - d * this.j + d1 * this.k - d3 * this.i);
            vec.z = (d3 * this.s - d * this.k - d1 * this.j + d2 * this.i);
        }

        public String toString() {
            return String.format("Quaternion: { s=%f, i=%f, j=%f, k=%f }", this.s, this.i, this.j, this.k);
        }

        public static Quat buildQuatFrom3DVector(Vector3 axis, double angle) {
            return buildQuatWithAngle(axis.x, axis.y, axis.z, angle);
        }

    }

    public static class RotAxis {

        public static final Vector3 X_AXIS = new Vector3(1, 0, 0);
        public static final Vector3 Y_AXIS = new Vector3(0, 1, 0);
        public static final Vector3 Z_AXIS = new Vector3(0, 0, 1);

    }

}
