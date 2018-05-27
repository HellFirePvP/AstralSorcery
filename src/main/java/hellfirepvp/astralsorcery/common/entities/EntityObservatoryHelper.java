/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityObservatoryHelper
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:37
 */
public class EntityObservatoryHelper extends Entity {

    private static DataParameter<BlockPos> FIXED = EntityDataManager.createKey(EntityObservatoryHelper.class, DataSerializers.BLOCK_POS);

    public EntityObservatoryHelper(World worldIn) {
        super(worldIn);
        setSize(0, 0);
        this.isImmuneToFire = true;
    }

    public EntityObservatoryHelper(World world, BlockPos fixedPos) {
        super(world);
        setSize(0, 0);
        this.dataManager.set(FIXED, fixedPos);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(FIXED, BlockPos.ORIGIN);
    }

    public BlockPos getFixedObservatoryPos() {
        return this.dataManager.get(FIXED);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.noClip = true;

        TileObservatory to;
        if((to = isOnTelescope()) == null) {
            if(!world.isRemote) {
                setDead();
            }
            return;
        }
        List<Entity> passengers = getPassengers();
        if(!to.isUsable()) {
            passengers.forEach(Entity::dismountRidingEntity);
            return;
        }
        Entity riding = Iterables.getFirst(passengers, null);
        if(riding != null && riding instanceof EntityPlayer ) {
            if (((EntityPlayer) riding).openContainer != null && ((EntityPlayer) riding).openContainer instanceof ContainerObservatory) {
                //Adjust observatory pitch and jaw to player head
                this.rotationYaw = ((EntityPlayer) riding).rotationYawHead;
                this.prevRotationYaw = ((EntityPlayer) riding).prevRotationYawHead;
                this.rotationPitch = riding.rotationPitch;
                this.prevRotationPitch = riding.prevRotationPitch;
            } else {
                //Adjust observatory to player-body
                this.rotationYaw = ((EntityPlayer) riding).renderYawOffset;
                this.prevRotationYaw = ((EntityPlayer) riding).prevRenderYawOffset;
            }

            to.updatePitchYaw(this.rotationPitch, this.prevRotationPitch, this.rotationYaw, this.prevRotationYaw);
        }
    }

    @Nullable
    private TileObservatory isOnTelescope() {
        BlockPos at = getPosition();
        BlockPos fixed = getFixedObservatoryPos();
        if(!at.equals(fixed)) {
            return null;
        }
        TileObservatory to = MiscUtils.getTileAt(this.world, at, TileObservatory.class, true);
        if (to == null) {
            return null;
        }
        UUID helper = to.getEntityHelperRef();
        if(helper == null || !helper.equals(this.entityUniqueID)) {
            return null;
        }
        return to;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        if(!super.canBeRidden(entityIn)) return false;
        TileObservatory to = isOnTelescope();
        return to != null && to.isUsable();
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
    public boolean isOverWater() {
        return true;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {}

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(BlocksAS.blockObservatory);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
