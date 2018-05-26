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
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

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
    public float observatoryPitch = 0, prevObservatoryPitch = 0;

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
        for (Entity e : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(1, 1, 1)))) {
            if (e.getUniqueID().equals(entityUUID)) {
                this.entityIdServerRef = e.getEntityId();
                return e;
            }
        }
        return null;
    }

    @Nullable
    public Entity findRidableObservatoryEntity() {
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
