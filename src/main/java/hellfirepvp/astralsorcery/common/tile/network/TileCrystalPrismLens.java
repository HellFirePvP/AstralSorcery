package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalPrismLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:15
 */
public class TileCrystalPrismLens extends TileCrystalLens {

    @Override
    public void update() {
        super.update();

        if(worldObj.isRemote && getLinkedPositions().size() > 0) {
            playPrismEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playPrismEffects() {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().thePlayer;
        if(rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
        Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        particle.motion(
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockPrism.name";
    }

    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalPrismTransmissionNode(at, getCrystalProperties());
    }
}
