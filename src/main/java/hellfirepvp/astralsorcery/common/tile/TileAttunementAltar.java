package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementAltar
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:26
 */
public class TileAttunementAltar extends TileReceiverBase {

    private Object texPlaneHalo;

    @Override
    public void update() {
        super.update();

        if(worldObj.isRemote) {
            renderHalo();
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderHalo() {
        if(true) return;

        TextureSpritePlane spr = (TextureSpritePlane) texPlaneHalo;
        if(texPlaneHalo == null || spr.canRemove() || spr.isRemoved()) {
            spr = EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteHalo2, Axis.Y_AXIS);
            spr.setPosition(new Vector3(this).add(0.5, 0.15, 0.5));
            spr.setAlphaOverDistance(true);
            spr.setNoRotation(45);
            spr.setRefreshFunc(() -> !isInvalid());
            spr.setScale(6.5F);
            texPlaneHalo = spr;
        }
        if(getTicksExisted() % 30 == 0) {
            /*OrbitalEffectLucerna luc = new OrbitalEffectLucerna();
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(luc, luc, luc);
            ctrl.setOffset(new Vector3(this).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(1 + worldObj.rand.nextFloat() * 3.5);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(40 + worldObj.rand.nextInt(20));*/
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockAttunementAltar.name";
    }

    private void receiveStarlight(IMajorConstellation type, double amount) {}

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAttunementAltar(at);
    }

    public static class TransmissionReceiverAttunementAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAttunementAltar(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IMajorConstellation type, double amount) {
            if(isChunkLoaded) {
                TileAttunementAltar ta = MiscUtils.getTileAt(world, getPos(), TileAttunementAltar.class, false);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AttunementAltarReceiverProvider();
        }

    }

    public static class AttunementAltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAttunementAltar provideEmptyNode() {
            return new TransmissionReceiverAttunementAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAttunementAltar";
        }

    }

}
