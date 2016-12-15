package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.TreeSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientCameraManager
 * Created by HellFirePvP
 * Date: 13.12.2016 / 00:10
 */
public class ClientCameraManager implements ITickHandler {

    private static final ClientCameraManager instance = new ClientCameraManager();

    private ClientCameraManager() {}

    private TreeSet<ICameraTransformer> transformers = new TreeSet<>((t1, t2) -> t1.getPriority() - t2.getPriority());
    private ICameraTransformer lastTransformer = null;

    public static ClientCameraManager getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if(type == TickEvent.Type.RENDER) {
            float pTicks = (float) context[0];
            if(!transformers.isEmpty()) {
                ICameraTransformer prio = transformers.last();
                if(prio != lastTransformer) {
                    if(lastTransformer != null) {
                        lastTransformer.onStopTransforming(pTicks);
                    }
                    prio.onStartTransforming(pTicks);
                    lastTransformer = prio;
                }
                prio.transformRenderView(pTicks);
                if(prio.needsRemoval()) {
                    prio.onStopTransforming(pTicks);
                    transformers.remove(prio);
                }
            } else {
                if(lastTransformer != null) {
                    lastTransformer.onStopTransforming(pTicks);
                    lastTransformer = null;
                }
            }
        } else {
            if(!transformers.isEmpty()) {
                ICameraTransformer prio = transformers.last();
                prio.onClientTick();
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.RENDER, TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.START;
    }

    @Override
    public String getName() {
        return "Client Camera Manager";
    }

    public void removeAllAndCleanup() {
        if(!transformers.isEmpty()) {
            ICameraTransformer last = transformers.last();
            last.onStopTransforming(0);
        }
        transformers.clear();
    }

    public void addTransformer(ICameraTransformer transformer) {
        this.transformers.add(transformer);
    }
    
    public boolean hasActiveTransformer() {
        return !transformers.isEmpty();
    }

    public static interface ICameraTransformer {

        public int getPriority();

        public boolean needsRemoval();

        public void onClientTick();

        public void onStartTransforming(float pTicks);

        public void onStopTransforming(float pTicks);

        public void transformRenderView(float pTicks);

    }

    public static abstract class CameraTransformerSettingsCache implements ICameraTransformer {

        private boolean active = false;

        private boolean viewBobbing = false, hideGui = false;
        private int thirdPersonView = 0;

        @Override
        public void onStartTransforming(float pTicks) {
            this.viewBobbing = Minecraft.getMinecraft().gameSettings.viewBobbing;
            this.hideGui = Minecraft.getMinecraft().gameSettings.hideGUI;
            this.thirdPersonView = Minecraft.getMinecraft().gameSettings.thirdPersonView;
            this.active = true;
        }

        @Override
        public void onStopTransforming(float pTicks) {
            if(active) {
                GameSettings settings = Minecraft.getMinecraft().gameSettings;
                settings.viewBobbing = viewBobbing;
                settings.hideGUI = hideGui;
                settings.thirdPersonView = thirdPersonView;
                this.active = false;
            }
        }

        @Override
        public void transformRenderView(float pTicks) {
            if(!active) return;
            GameSettings settings = Minecraft.getMinecraft().gameSettings;
            settings.hideGUI = true;
            settings.viewBobbing = false;
            settings.thirdPersonView = 0;
        }

    }

    public static class CameraTransformerRenderReplacement extends CameraTransformerSettingsCache {

        private final EntityRenderViewReplacement entity;
        private final PersistencyFunction func;

        public CameraTransformerRenderReplacement(EntityRenderViewReplacement renderView, PersistencyFunction func) {
            this.entity = renderView;
            this.func = func;
        }

        @Override
        public void onStartTransforming(float pTicks) {
            super.onStartTransforming(pTicks);

            entity.setAsRenderViewEntity();
        }

        @Override
        public void onStopTransforming(float pTicks) {
            super.onStopTransforming(pTicks);

            RenderingUtils.unsafe_resetCamera();
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void transformRenderView(float pTicks) {
            super.transformRenderView(pTicks);

            Vector3 focus = entity.getCameraFocus();
            if(focus != null) {
                entity.transformToFocusOnPoint(focus, pTicks, true);
            }
        }

        @Override
        public boolean needsRemoval() {
            return func.needsRemoval();
        }

        @Override
        public void onClientTick() {
            entity.ticksExisted++;

            entity.moveEntityTick(entity, entity.ticksExisted);
        }

    }

    public static interface PersistencyFunction {

        public boolean needsRemoval();

    }

    public static abstract class EntityRenderViewReplacement extends EntityLivingBase {

        private Vector3 cameraFocus = null;

        public EntityRenderViewReplacement() {
            super(Minecraft.getMinecraft().world);
        }

        @Nullable
        public Vector3 getCameraFocus() {
            return cameraFocus;
        }

        public void setCameraFocus(@Nullable Vector3 cameraFocus) {
            this.cameraFocus = cameraFocus;
        }

        public void setAsRenderViewEntity() {
            Minecraft.getMinecraft().setRenderViewEntity(this);
        }

        public void transformToFocusOnPoint(Vector3 toFocus, float pTicks, boolean propagate) {
            Vector3 angles = new Vector3(this).subtract(toFocus).toPolar();
            Vector3 prevAngles = new Vector3(prevPosX, prevPosY, prevPosZ).subtract(toFocus).toPolar();
            double pitch = 90 - angles.getY();
            double pitchPrev = 90 - prevAngles.getY();
            double yaw = -angles.getZ();
            double yawPrev = -prevAngles.getZ();

            this.rotationYawHead =     (float) yaw;
            this.rotationYaw =         (float) yaw;
            this.prevRotationYaw =     (float) yawPrev;
            this.prevRotationYawHead = (float) yawPrev;
            this.rotationPitch =       (float) pitch;
            this.prevRotationPitch =   (float) pitchPrev;
            if(propagate) {
                RenderingUtils.unsafe_preRenderHackCamera(this, posX, posY, posZ, prevPosX, prevPosY, prevPosZ, yaw, yawPrev, pitch, pitchPrev);
            }
        }

        public abstract void moveEntityTick(EntityRenderViewReplacement entity, int ticksExisted);

        @Override
        public Iterable<ItemStack> getArmorInventoryList() {
            return Collections.emptyList();
        }

        @Nullable
        @Override
        public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
            return null;
        }

        @Override
        public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {}

        @Override
        public EnumHandSide getPrimaryHand() {
            return EnumHandSide.RIGHT;
        }
    }

}
