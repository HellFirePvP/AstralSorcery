/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.technical;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityObservatoryHelper
 * Created by HellFirePvP
 * Date: 16.02.2020 / 09:00
 */
public class EntityObservatoryHelper extends Entity {

    private static DataParameter<BlockPos> FIXED = EntityDataManager.createKey(EntityObservatoryHelper.class, DataSerializers.BLOCK_POS);

    public EntityObservatoryHelper(World worldIn) {
        super(EntityTypesAS.OBSERVATORY_HELPER, worldIn);
    }

    public static EntityType.IFactory<EntityObservatoryHelper> factory() {
        return (spawnEntity, world) -> new EntityObservatoryHelper(world);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(FIXED, BlockPos.ZERO);
    }

    public void setFixedObservatoryPos(BlockPos pos) {
        this.dataManager.set(FIXED, pos);
    }

    public BlockPos getFixedObservatoryPos() {
        return this.dataManager.get(FIXED);
    }

    @Nullable
    public TileObservatory getAssociatedObservatory() {
        BlockPos at = this.getFixedObservatoryPos();
        TileObservatory observatory = MiscUtils.getTileAt(this.world, at, TileObservatory.class, true);
        if (observatory == null) {
            return null;
        }
        UUID helperRef = observatory.getEntityHelperRef();
        if (helperRef == null || !helperRef.equals(this.getUniqueID())) {
            return null;
        }
        return observatory;
    }

    @Override
    public void tick() {
        super.tick();

        this.noClip = true;

        TileObservatory observatory;
        if ((observatory = this.getAssociatedObservatory()) == null) {
            if (!this.world.isRemote()) {
                this.remove();
            }
            return;
        }

        Entity riding = Iterables.getFirst(this.getPassengers(), null);
        if (riding instanceof PlayerEntity) {
            this.applyObservatoryRotationsFrom(observatory, (PlayerEntity) riding);
        } else {
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }
        if (!observatory.isUsable()) {
            this.removePassengers();
        }
    }

    public void applyObservatoryRotationsFrom(TileObservatory to, PlayerEntity riding) {
        if (riding.openContainer instanceof ContainerObservatory) {
            //Adjust observatory pitch and jaw to player head
            this.rotationYaw = riding.rotationYawHead;
            this.prevRotationYaw = riding.prevRotationYawHead;
            this.rotationPitch = riding.rotationPitch;
            this.prevRotationPitch = riding.prevRotationPitch;
        } else  {
            //Adjust observatory to player-body
            this.rotationYaw = riding.renderYawOffset;
            this.prevRotationYaw = riding.prevRenderYawOffset;
        }

        to.updatePitchYaw(this.rotationPitch, this.prevRotationPitch, this.rotationYaw, this.prevRotationYaw);

        double xOffset = -0.85;
        double zOffset = 0.15;
        double yawRad = -Math.toRadians(to.observatoryYaw);
        double xComp = 0.5F + Math.sin(yawRad) * xOffset - Math.cos(yawRad) * zOffset;
        double zComp = 0.5F + Math.cos(yawRad) * xOffset + Math.sin(yawRad) * zOffset;
        Vector3 pos = new Vector3(to.getPos()).add(xComp, 0.4F, zComp);
        this.forceSetPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        if (!super.canBeRidden(entityIn)) {
            return false;
        }
        TileObservatory observatory = this.getAssociatedObservatory();
        return observatory != null && observatory.isUsable();
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(BlocksAS.OBSERVATORY);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {}

    @Override
    protected void writeAdditional(CompoundNBT compound) {}

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
