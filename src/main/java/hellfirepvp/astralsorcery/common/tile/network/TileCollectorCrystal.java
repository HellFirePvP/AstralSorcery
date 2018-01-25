/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectCollector;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
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

    public static final BlockPos[] offsetsLiquidStarlight = new BlockPos[] {
            new BlockPos(-1, -4, -1),
            new BlockPos( 0, -4, -1),
            new BlockPos( 1, -4, -1),
            new BlockPos( 1, -4,  0),
            new BlockPos( 1, -4,  1),
            new BlockPos( 0, -4,  1),
            new BlockPos(-1, -4,  1),
            new BlockPos(-1, -4,  0),
    };

    private static final Random rand = new Random();

    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private CrystalProperties usedCrystalProperties;
    private boolean playerMade;
    private boolean enhanced = false;
    private IWeakConstellation associatedType;

    private Object[] orbitals = new Object[4];

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if(ticksExisted > 4 && associatedType == null) {
                getWorld().setBlockToAir(getPos());
            }
            if(isEnhanced() && getTicksExisted() % 10 == 0) {
                checkAdjacentBlocks();
            }
            if(type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL && getTicksExisted() % 40 == 0) {
                boolean match = usedCrystalProperties != null &&
                        MultiBlockArrays.patternCollectorEnhancement.matches(world, pos);
                if (match != enhanced) {
                    setEnhanced(match);
                }
            }
        } else {
            if(!doesSeeSky()) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                        getPos().getX() + 0.5,
                        getPos().getY() + 0.5,
                        getPos().getZ() + 0.5);
                p.motion((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                         (rand.nextFloat() * 0.04F) * (rand.nextBoolean() ? 1 : -1),
                         (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
                p.scale(0.2F).setMaxAge(35);
                if(type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                    p.setColor(Color.CYAN);
                } else {
                    p.setColor(Color.WHITE);
                }
            } else {
                if(isEnhanced() && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                    playEnhancedEffects();
                }
            }
        }
    }

    private void checkAdjacentBlocks() {
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    if(xx == 0 && yy == 0 && zz == 0) continue;

                    BlockPos other = getPos().add(xx, yy, zz);
                    if(!getWorld().isAirBlock(other)) {
                        setEnhanced(false);
                        return;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEnhancedEffects() {
        if(Minecraft.isFancyGraphicsEnabled()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    getPos().getX() + 0.5,
                    getPos().getY() + 0.5,
                    getPos().getZ() + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(Color.CYAN).setMaxAge(25);
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
                if(ctrl.canRemove() || ctrl.isRemoved()) {
                    orbitals[i] = null;
                }
            }
        }

        BlockPos randomPos = offsetsLiquidStarlight[rand.nextInt(offsetsLiquidStarlight.length)].add(pos);
        Vector3 from = new Vector3(randomPos).add(rand.nextFloat(), 0.8, rand.nextFloat());
        Vector3 to = new Vector3(this).add(0.5, 0.5, 0.5);
        Vector3 mov = to.clone().subtract(from).normalize().multiply(0.1);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
        p.motion(mov.getX(), mov.getY(), mov.getZ()).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.gravity(0.004).scale(0.25F).setMaxAge(30 + rand.nextInt(10));
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

        if(usedCrystalProperties != null && (usedCrystalProperties.getPurity() > 90 || usedCrystalProperties.getCollectiveCapability() > 90) && rand.nextInt(100) == 0) {
            AstralSorcery.proxy.fireLightning(world, to, from, c);
        }
    }

    @Override
    public boolean onSelect(EntityPlayer player) {
        if(player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            player.sendMessage(new TextComponentTranslation("misc.link.unlink.all").setStyle(new Style().setColor(TextFormatting.GREEN)));
            return false;
        }
        return true;
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public IWeakConstellation getConstellation() {
        return associatedType;
    }

    public void onPlace(IWeakConstellation constellation, CrystalProperties properties, boolean player, BlockCollectorCrystalBase.CollectorCrystalType type) {
        this.associatedType = constellation;
        this.playerMade = player;
        this.usedCrystalProperties = properties;
        this.type = type;

        this.needsUpdate = true;
        markDirty();
    }

    public void setEnhanced(boolean enhanced) {
        if(!world.isRemote && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            this.enhanced = enhanced;
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
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
    public boolean hasBeenLinked() {
        return !playerMade;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerMade = compound.getBoolean("player");
        this.associatedType = (IWeakConstellation) IConstellation.readFromNBT(compound);
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.enhanced = compound.getBoolean("enhanced");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("player", playerMade);
        if (associatedType != null) {
            associatedType.writeToNBT(compound);
        }
        if (usedCrystalProperties != null) {
            usedCrystalProperties.writeToNBT(compound);
        }
        if(type != null) {
            compound.setInteger("collectorType", type.ordinal());
        }
        compound.setBoolean("enhanced", enhanced);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return Block.FULL_BLOCK_AABB.grow(1).offset(getPos());
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockcollectorcrystal.name";
    }

    @Override
    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource(usedCrystalProperties, associatedType, doesSeeSky, hasBeenLinked(), type);
    }

    @Override
    @Nonnull
    public ITransmissionSource provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }

}
