package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.compound.CompoundEffectSphere;
import hellfirepvp.astralsorcery.client.effect.compound.CompoundGatewayShield;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:43
 */
public class TileCelestialGateway extends TileEntityTick {

    private boolean hasMultiblock = false;
    private boolean doesSeeSky = false;

    private boolean gatewayRegistered = false;

    private Object clientSphere = null;

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            playEffects();
        } else {
            if((ticksExisted & 15) == 0) {
                updateSkyState(world.canSeeSky(getPos().up()));
            }

            if((ticksExisted & 15) == 0) {
                updateMultiblockState(MultiBlockArrays.patternCelestialGateway.matches(world, pos));
            }

            if(gatewayRegistered) {
                if(!hasMultiblock() || !doesSeeSky()) {
                    GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
                    cache.removePosition(world, pos);
                    gatewayRegistered = false;
                }
            } else {
                if(hasMultiblock() && doesSeeSky()) {
                    GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
                    cache.offerPosition(world, pos);
                    gatewayRegistered = true;
                }
            }
        }
    }

    @Override
    protected void onFirstTick() {}

    private void updateMultiblockState(boolean matches) {
        boolean update = hasMultiblock != matches;
        this.hasMultiblock = matches;
        if(update) {
            markForUpdate();
        }
    }

    private void updateSkyState(boolean seeSky) {
        boolean update = doesSeeSky != seeSky;
        this.doesSeeSky = seeSky;
        if(update) {
            markForUpdate();
        }
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        boolean prec = hasMultiblock && doesSeeSky;
        setupGatewayUI(prec);
        if(prec) {
            playFrameParticles();
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupGatewayUI(boolean preconditionsFulfilled) {
        if(preconditionsFulfilled) {
            Vector3 sphereVec = new Vector3(pos).add(0.5, 2.62, 0.5);
            if(clientSphere == null) {
                CompoundEffectSphere sphere = new CompoundGatewayShield(sphereVec.clone(), Vector3.RotAxis.Y_AXIS, 6, 8, 10);
                sphere.setRemoveIfInvisible(true).setAlphaFadeDistance(4);
                EffectHandler.getInstance().registerFX(sphere);
                clientSphere = sphere;
            }
            double playerDst = new Vector3(Minecraft.getMinecraft().player).distance(sphereVec);
            if(clientSphere != null) {
                if(!((CompoundEffectSphere) clientSphere).getPosition().equals(sphereVec)) {
                    ((CompoundEffectSphere) clientSphere).requestRemoval();

                    CompoundEffectSphere sphere = new CompoundGatewayShield(sphereVec.clone(), Vector3.RotAxis.Y_AXIS, 6, 8, 10);
                    sphere.setRemoveIfInvisible(true).setAlphaFadeDistance(4);
                    EffectHandler.getInstance().registerFX(sphere);
                    clientSphere = sphere;
                }
                if(((EntityComplexFX) clientSphere).isRemoved() && playerDst < 5) {
                    EffectHandler.getInstance().registerFX((EntityComplexFX) clientSphere);
                }
            }
            if(playerDst < 5.5) {
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
            }
            if(playerDst < 2.5) {
                EffectHandler.getInstance().requestGatewayUIFor(world, sphereVec, 5.5);
            }
        } else {
            if(clientSphere != null) {
                if(!((EntityComplexFX) clientSphere).isRemoved()) {
                    ((EntityComplexFX) clientSphere).requestRemoval();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playFrameParticles() {
        for (int i = 0; i < 2; i++) {
            Vector3 offset = new Vector3(pos).add(-2, 1, -2);
            if(rand.nextBoolean()) {
                offset.add(5 * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * 5);
            } else {
                offset.add(rand.nextFloat() * 5, 0, 5 * (rand.nextBoolean() ? 1 : 0));
            }
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
            p.gravity(0.0045).scale(0.25F + rand.nextFloat() * 0.15F).setMaxAge(40 + rand.nextInt(40));
            Color c = new Color(60, 0, 255);
            switch (rand.nextInt(4)) {
                case 0:
                    c = Color.WHITE;
                    break;
                case 1:
                    c = new Color(0x69B5FF);
                    break;
                case 2:
                    c = new Color(0x0078FF);
                    break;
            }
            p.setColor(c);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.hasMultiblock = compound.getBoolean("mbState");
        this.doesSeeSky = compound.getBoolean("skyState");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setBoolean("skyState", this.doesSeeSky);
    }

}
