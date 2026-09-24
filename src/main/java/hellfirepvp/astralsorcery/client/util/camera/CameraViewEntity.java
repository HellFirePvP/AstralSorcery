/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraViewEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class CameraViewEntity extends LivingEntity {

    private Vector3 cameraFocus = null;

    public CameraViewEntity() {
        super(EntityType.ZOMBIE, Minecraft.getInstance().level);
    }

    @Nullable
    public Vector3 getCameraFocus() {
        return this.cameraFocus;
    }

    public void setCameraFocus(@Nullable Vector3 cameraFocus) {
        this.cameraFocus = cameraFocus;
    }

    public void setAsRenderViewEntity() {
        Minecraft.getInstance().setCameraEntity(this);
    }

    public void transformToFocusOnPoint(Vector3 toFocus, float pTicks, boolean propagate) {
        Vector3 angles = new Vector3(this).subtract(toFocus).copyToPolar();
        Vector3 prevAngles = new Vector3(this.xo, this.yo, this.zo).subtract(toFocus).copyToPolar();
        double pitch = 90 - angles.getY();
        double pitchPrev = 90 - prevAngles.getY();
        double yaw = -angles.getZ();
        double yawPrev = -prevAngles.getZ();

        if (propagate) {
            CameraUtil.positionCamera(this, pTicks, this.getX(), this.getY(), this.getZ(), this.xo, this.yo, this.zo, yaw, yawPrev, pitch, pitchPrev);
        }
    }

    public abstract void moveEntityTick(LocalPlayer existingPlayer, int ticksExisted);

    public abstract void onStopTransforming();

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
