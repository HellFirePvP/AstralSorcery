package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CameraTransformerPlayerFocus
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:18
 */
public class CameraTransformerPlayerFocus extends CameraTransformerSettingsCache {

    private final EntityCameraRenderView entity;
    private final ICameraPersistencyFunction func;

    private EntityClientReplacement clientEntity;

    public CameraTransformerPlayerFocus(EntityCameraRenderView renderView, ICameraPersistencyFunction func) {
        this.entity = renderView;
        this.func = func;
    }

    @Override
    public void onStartTransforming(float pTicks) {
        super.onStartTransforming(pTicks);

        EntityClientReplacement repl = new EntityClientReplacement();
        repl.read(Minecraft.getInstance().player.writeWithoutTypeId(new CompoundNBT()));
        Minecraft.getInstance().world.addPlayer(repl.getEntityId(), repl);
        this.clientEntity = repl;

        entity.setAsRenderViewEntity();
    }

    @Override
    public void onStopTransforming(float pTicks) {
        super.onStopTransforming(pTicks);

        Minecraft mc = Minecraft.getInstance();
        if (mc.world != null) {
            mc.world.removeEntityFromWorld(this.clientEntity.getEntityId());
        }

        if (mc.player != null) {
            PlayerEntity player = mc.player;
            player.setPositionAndRotation(this.clientEntity.getPosX(), this.clientEntity.getPosY(), this.clientEntity.getPosZ(), this.clientEntity.rotationYaw, this.clientEntity.rotationPitch);
            player.setVelocity(0, 0, 0);
        }

        ClientCameraUtil.resetCamera();

        if (mc.world != null) {
            entity.onStopTransforming();
        }
    }

    @Override
    public void transformRenderView(float pTicks) {
        super.transformRenderView(pTicks);

        Vector3 focus = entity.getCameraFocus();
        if (focus != null) {
            entity.transformToFocusOnPoint(focus, pTicks, true);
        }
    }

    @Override
    public ICameraPersistencyFunction getPersistencyFunction() {
        return func;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onClientTick() {
        entity.ticksExisted++;

        if (clientEntity != null) {
            entity.moveEntityTick(entity, clientEntity, entity.ticksExisted);
        }
    }
}
