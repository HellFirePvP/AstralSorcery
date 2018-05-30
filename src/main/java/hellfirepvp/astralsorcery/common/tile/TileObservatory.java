/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.entities.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:36
 */
public class TileObservatory extends TileEntityTick {

    private UUID entityHelperRef;
    private Integer entityIdServerRef = null;

    public float observatoryYaw = 0, prevObservatoryYaw = 0;
    public float observatoryPitch = -45, prevObservatoryPitch = -45;

    @Override
    protected void onFirstTick() {}

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if(this.entityHelperRef == null) {
                createNewObservatoryEntity();
            } else {
                Entity e;
                if((e = resolveEntity(this.entityHelperRef)) == null || e.isDead) {
                    createNewObservatoryEntity();
                } else {
                    double xOffset = -0.85;
                    double zOffset = 0.15;
                    double yawRad = -Math.toRadians(this.observatoryYaw);
                    double xComp = 0.5F + Math.sin(yawRad) * xOffset - Math.cos(yawRad) * zOffset;
                    double zComp = 0.5F + Math.cos(yawRad) * xOffset + Math.sin(yawRad) * zOffset;
                    Vector3 pos = new Vector3(getPos()).add(xComp, 0.4F, zComp);
                    e.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
    }

    public boolean isUsable() {
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                if(xx == 0 && zz == 0) {
                    continue;
                }
                BlockPos other = pos.add(xx, 0, zz);
                if (!world.canSeeSky(other)) {
                    return false;
                }
            }
        }
        return world.canSeeSky(pos.up());
    }

    private Entity createNewObservatoryEntity() {
        this.entityHelperRef = null;
        this.entityIdServerRef = null;

        EntityObservatoryHelper helper = new EntityObservatoryHelper(this.world, this.pos);
        helper.setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 0,0);
        this.world.spawnEntity(helper);

        this.entityHelperRef = helper.getUniqueID();
        this.entityIdServerRef = helper.getEntityId();
        return helper;
    }

    @Nullable
    private Entity resolveEntity(UUID entityUUID) {
        if(entityUUID == null) return null;
        for (Entity e : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(-3, -1, -3), pos.add(3, 2, 3)))) {
            if (e.getUniqueID().equals(entityUUID)) {
                this.entityIdServerRef = e.getEntityId();
                return e;
            }
        }
        return null;
    }

    @Nullable
    public Entity findRideableObservatoryEntity() {
        if(this.entityHelperRef == null || this.entityIdServerRef == null) {
            return null;
        }
        return this.world.getEntityByID(this.entityIdServerRef);
    }

    @Nullable
    public UUID getEntityHelperRef() {
        return entityHelperRef;
    }

    public void setEntityHelperRef(UUID entityHelperRef) {
        this.entityHelperRef = entityHelperRef;
        markForUpdate();
    }

    public void updatePitchYaw(float pitch, float prevPitch, float yaw, float prevYaw) {
        this.observatoryPitch = pitch;
        this.prevObservatoryPitch = prevPitch;
        this.observatoryYaw = yaw;
        this.prevObservatoryYaw = prevYaw;
        markForUpdate();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return TileObservatory.INFINITE_EXTENT_AABB;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasUniqueId("entity")) {
            this.entityHelperRef = compound.getUniqueId("entity");
        } else {
            this.entityHelperRef = null;
        }
        this.observatoryYaw = compound.getFloat("oYaw");
        this.observatoryPitch = compound.getFloat("oPitch");
        this.prevObservatoryYaw = compound.getFloat("oYawPrev");
        this.prevObservatoryPitch = compound.getFloat("oPitchPrev");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if(this.entityHelperRef != null) {
            compound.setUniqueId("entity", this.entityHelperRef);
        }
        compound.setFloat("oYaw", this.observatoryYaw);
        compound.setFloat("oPitch", this.observatoryPitch);
        compound.setFloat("oYawPrev", this.prevObservatoryYaw);
        compound.setFloat("oPitchPrev", this.prevObservatoryPitch);
    }
}
