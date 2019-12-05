package hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.camera.*;
import hellfirepvp.astralsorcery.client.util.camera.path.CameraPathBuilder;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunePlayerRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktAttunePlayerConstellation;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActivePlayerAttunementRecipe
 * Created by HellFirePvP
 * Date: 02.12.2019 / 19:43
 */
public class ActivePlayerAttunementRecipe extends AttunementRecipe.Active<AttunePlayerRecipe> {

    private static final int DURATION_PLAYER_ATTUNEMENT = 800; //Duration of the player's camera flight

    private IMajorConstellation constellation;
    private UUID playerUUID;

    //Client camera flight cache, ICameraTransformer
    public Object cameraHack;

    public ActivePlayerAttunementRecipe(AttunePlayerRecipe recipe, IMajorConstellation constellation, UUID playerUUID) {
        super(recipe);
        this.constellation = constellation;
        this.playerUUID = playerUUID;
    }

    public ActivePlayerAttunementRecipe(AttunePlayerRecipe recipe, CompoundNBT nbt) {
        super(recipe);
        this.readFromNBT(nbt);
    }

    @Override
    public boolean matches(TileAttunementAltar altar) {
        if (!super.matches(altar)) {
            return false;
        }
        PlayerEntity player;
        return (player = altar.getWorld().getPlayerByUuid(this.playerUUID)) != null && player.isAlive();
    }

    @Override
    public void stopCrafting(TileAttunementAltar altar) {

    }

    @Override
    public void finishRecipe(TileAttunementAltar altar) {
        PlayerEntity player = altar.getWorld().getPlayerByUuid(this.playerUUID);
        if (player != null) {
            ResearchManager.setAttunedConstellation(player, this.constellation);
        }
    }

    @Override
    public void doTick(LogicalSide side, TileAttunementAltar altar) {
        if (side.isServer()) {

        } else {
            doClientTick(altar);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientTick(TileAttunementAltar altar) {
        if (this.cameraHack == null) {
            Vector3 offset = new Vector3(altar).add(0, 6, 0);
            CameraPathBuilder builder = CameraPathBuilder.builder(offset.clone().add(4, 0, 4), new Vector3(altar).add(0.5, 0.5, 0.5));
            builder.addCircularPoints(offset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease( 5,  0.025), 200, 2);
            builder.addCircularPoints(offset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease(10, -0.01) , 200, 2);
            builder.setTickDelegate(createTickListener(new Vector3(altar).add(0.5F, 1.2F, 0.5F)));
            builder.setStopDelegate(createAttunementListener(altar));

            this.cameraHack = builder.finishAndStart();
        }


    }

    @OnlyIn(Dist.CLIENT)
    private ICameraTickListener createTickListener(Vector3 offset) {
        return (renderView, focusedEntity) -> {
            if (focusedEntity == null) {
                return;
            }

            float floatTick = (ClientScheduler.getClientTick() % 40) / 40F;
            float sin = MathHelper.sin((float) (floatTick * 2 * Math.PI)) / 2F + 0.5F;
            focusedEntity.setCustomNameVisible(false);
            focusedEntity.setPositionAndRotation(offset.getX(), offset.getY() + sin * 0.2D, offset.getZ(), 0F, 0F);
            focusedEntity.setPositionAndRotation(offset.getX(), offset.getY() + sin * 0.2D, offset.getZ(), 0F, 0F);
            focusedEntity.rotationYawHead = 0;
            focusedEntity.prevRotationYawHead = 0;
            focusedEntity.renderYawOffset = 0;
            focusedEntity.prevRenderYawOffset = 0;
            focusedEntity.setVelocity(0, 0, 0);
        };
    }

    @OnlyIn(Dist.CLIENT)
    private ICameraStopListener createAttunementListener(TileAttunementAltar altar) {
        DimensionType dimType = altar.getWorld().getDimension().getType();
        BlockPos at = altar.getPos();
        return () -> {
            if (this.cameraHack != null) {
                ICameraTransformer transformer = (ICameraTransformer) this.cameraHack;
                ICameraPersistencyFunction persistency = transformer.getPersistencyFunction();
                if (persistency.isExpired() && !persistency.wasForciblyStopped()) {
                    PktAttunePlayerConstellation attuneRequest = new PktAttunePlayerConstellation(this.constellation, dimType, at);
                    PacketChannel.CHANNEL.sendToServer(attuneRequest);
                }
            }
        };
    }

    @Override
    public boolean isFinished(TileAttunementAltar altar) {
        return this.getTick() >= DURATION_PLAYER_ATTUNEMENT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void stopEffects(TileAttunementAltar altar) {
        if (this.cameraHack != null) {
            ClientCameraManager.INSTANCE.removeTransformer((ICameraTransformer) this.cameraHack);
        }
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        super.writeToNBT(nbt);

        nbt.putUniqueId("playerUUID", this.playerUUID);
        nbt.putString("constellation", this.constellation.getRegistryName().toString());
    }

    @Override
    protected void readFromNBT(CompoundNBT nbt) {
        super.readFromNBT(nbt);

        this.playerUUID = nbt.getUniqueId("playerUUID");
        this.constellation = (IMajorConstellation) RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(nbt.getString("constellation")));
    }
}
