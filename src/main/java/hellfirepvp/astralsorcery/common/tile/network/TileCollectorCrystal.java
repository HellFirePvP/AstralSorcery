package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectCollector;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:25
 */
public class TileCollectorCrystal extends TileSourceBase {

    private static final Random rand = new Random();

    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private CrystalProperties usedCrystalProperties;
    private boolean playerMade;
    private boolean enhanced = false;
    private Constellation associatedType;

    private Object[] orbitals = new Object[4];

    @Override
    public void update() {
        super.update();

        if(!worldObj.isRemote) setEnhanced(true);

        if(worldObj.isRemote && enhanced && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            playEnhancedEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEnhancedEffects() {
        if(Minecraft.isFancyGraphicsEnabled()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    getPos().getX() + 0.5,
                    getPos().getY() + 0.5,
                    getPos().getZ() + 0.5);
            p.motion((rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(Color.CYAN);
        }

        for (int i = 0; i < orbitals.length; i++) {
            OrbitalEffectController ctrl = (OrbitalEffectController) orbitals[i];
            if(ctrl == null) {
                OrbitalEffectCollector prop = new OrbitalEffectCollector(this);
                ctrl = EffectHandler.getInstance().orbital(prop, null, null);
                ctrl.setOffset(new Vector3(this).add(0.5, 0.5, 0.5));
                ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.4);
                ctrl.setOrbitAxis(Vector3.random());
                ctrl.setTicksPerRotation(60);
                orbitals[i] = ctrl;
            } else {
                if(ctrl.canRemove() && ctrl.isRemoved()) {
                    orbitals[i] = null;
                }
            }
        }
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public Constellation getConstellation() {
        return associatedType;
    }

    public void onPlace(Constellation constellation, CrystalProperties properties, boolean player, BlockCollectorCrystalBase.CollectorCrystalType type) {
        this.associatedType = constellation;
        this.playerMade = player;
        this.usedCrystalProperties = properties;
        this.type = type;
        markDirty();
    }

    public void setEnhanced(boolean enhanced) {
        if(!worldObj.isRemote && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            this.enhanced = enhanced;
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(worldObj);
            IIndependentStarlightSource source = handle.getSourceAt(getPos());
            if(source != null && source instanceof IndependentCrystalSource) {
                ((IndependentCrystalSource) source).setEnhanced(enhanced);
                handle.markDirty();
            }
            markForUpdate();
        }
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getVec().toBlockPos();
        EffectHandler.getInstance().registerFX(new EntityFXBurst(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, 3F));
    }

    public static void breakDamage(World world, BlockPos pos) {}

    public BlockCollectorCrystalBase.CollectorCrystalType getType() {
        return type;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerMade = compound.getBoolean("player");
        this.associatedType = Constellation.readFromNBT(compound);
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.enhanced = compound.getBoolean("enhanced");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("player", playerMade);
        associatedType.writeToNBT(compound);
        usedCrystalProperties.writeToNBT(compound);
        compound.setInteger("collectorType", type.ordinal());
        compound.setBoolean("enhanced", enhanced);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockCollectorCrystal.name";
    }

    @Override
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource(usedCrystalProperties, associatedType, doesSeeSky, playerMade, type);
    }

    @Override
    public ITransmissionSource provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }

}
