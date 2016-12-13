package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.RENDER);
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

    public static interface ICameraTransformer {

        public int getPriority();

        public boolean needsRemoval();

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

    public static class EntityRenderViewReplacement extends Entity {

        public EntityRenderViewReplacement() {
            super(Minecraft.getMinecraft().theWorld);
        }

        public void setAsRenderViewEntity() {
            Minecraft.getMinecraft().setRenderViewEntity(this);
        }

        @Override
        protected void entityInit() {}

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {}

        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {}

    }

}
