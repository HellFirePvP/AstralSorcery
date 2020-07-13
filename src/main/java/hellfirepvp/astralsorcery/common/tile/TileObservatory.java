/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.tile.NamedInventoryTile;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileObservatory
 * Created by HellFirePvP
 * Date: 15.02.2020 / 18:28
 */
public class TileObservatory extends TileEntityTick implements NamedInventoryTile {

    private UUID entityHelperRef;
    private Integer entityIdServerRef = null;

    public float observatoryYaw = 0, prevObservatoryYaw = 0;
    public float observatoryPitch = -45, prevObservatoryPitch = -45;

    public TileObservatory() {
        super(TileEntityTypesAS.OBSERVATORY);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("screen.astralsorcery.observatory");
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote()) {
            if (this.entityHelperRef == null) {
                this.createNewObservatoryEntity();
            } else {
                Entity helper;
                if ((helper = resolveEntity(this.entityHelperRef)) == null || !helper.isAlive()) {
                    this.createNewObservatoryEntity();
                }
            }
        }
    }

    public boolean isUsable() {
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                if (xx == 0 && zz == 0) {
                    continue;
                }
                BlockPos other = pos.add(xx, 0, zz);
                if (!MiscUtils.canSeeSky(this.getWorld(), other, false, true)) {
                    return false;
                }
            }
        }
        return MiscUtils.canSeeSky(this.getWorld(), this.getPos().up(), true, false);
    }

    private Entity createNewObservatoryEntity() {
        this.setEntityHelperRef(null);
        this.entityIdServerRef = null;

        EntityObservatoryHelper helper = EntityTypesAS.OBSERVATORY_HELPER.create(this.getWorld());
        helper.setFixedObservatoryPos(this.getPos());
        helper.setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 0,0);
        this.getWorld().addEntity(helper);

        this.setEntityHelperRef(helper.getUniqueID());
        this.entityIdServerRef = helper.getEntityId();
        return helper;
    }

    @Nullable
    private Entity resolveEntity(UUID entityUUID) {
        if (entityUUID == null) {
            return null;
        }
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
        if (this.getEntityHelperRef() == null || this.entityIdServerRef == null) {
            return null;
        }
        return this.getWorld().getEntityByID(this.entityIdServerRef);
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
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return TileObservatory.INFINITE_EXTENT_AABB;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
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
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if(this.entityHelperRef != null) {
            compound.putUniqueId("entity", this.entityHelperRef);
        }
        compound.putFloat("oYaw", this.observatoryYaw);
        compound.putFloat("oPitch", this.observatoryPitch);
        compound.putFloat("oYawPrev", this.prevObservatoryYaw);
        compound.putFloat("oPitchPrev", this.prevObservatoryPitch);
    }
}
