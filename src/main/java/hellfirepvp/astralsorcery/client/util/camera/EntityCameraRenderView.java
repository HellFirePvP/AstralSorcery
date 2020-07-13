package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCameraRenderView
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:10
 */
public abstract class EntityCameraRenderView extends ClientPlayerEntity {

    private Vector3 cameraFocus = null;

    public EntityCameraRenderView() {
        super(Minecraft.getInstance(),
                Minecraft.getInstance().world,
                Minecraft.getInstance().player.connection,
                Minecraft.getInstance().player.getStats(),
                Minecraft.getInstance().player.getRecipeBook());

        abilities.allowFlying = true;
        abilities.isFlying = true;
        abilities.disableDamage = true;
    }

    @Nullable
    public Vector3 getCameraFocus() {
        return cameraFocus;
    }

    public void setCameraFocus(@Nullable Vector3 cameraFocus) {
        this.cameraFocus = cameraFocus;
    }

    public void setAsRenderViewEntity() {
        Minecraft.getInstance().setRenderViewEntity(this);
    }

    public void transformToFocusOnPoint(Vector3 toFocus, float pTicks, boolean propagate) {
        Vector3 angles = Vector3.atEntityCorner(this).subtract(toFocus).copyToPolar();
        Vector3 prevAngles = new Vector3(prevPosX, prevPosY, prevPosZ).subtract(toFocus).copyToPolar();
        double pitch = 90 - angles.getY();
        double pitchPrev = 90 - prevAngles.getY();
        double yaw = -angles.getZ();
        double yawPrev = -prevAngles.getZ();

        if (propagate) {
            ClientCameraUtil.positionCamera(this, pTicks, getPosX(), getPosY(), getPosZ(), prevPosX, prevPosY, prevPosZ, yaw, yawPrev, pitch, pitchPrev);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void rotateTowards(double yaw, double pitch) {}

    public abstract void moveEntityTick(EntityCameraRenderView entity, EntityClientReplacement replacementEntity, int ticksExisted);

    public abstract void onStopTransforming();

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, @Nullable ItemStack stack) {}

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }
}
