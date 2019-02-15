/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

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
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.PatternMatchHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 17:59
 */
public class TileCelestialGateway extends TileEntityTick implements IMultiblockDependantTile, IWorldNameable {

    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean hasMultiblock = false;
    private boolean doesSeeSky = false;

    private String display = null;
    private boolean gatewayRegistered = false;

    private Object clientSphere = null;
    private UUID placedBy;

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            playEffects();
        } else {
            if((ticksExisted & 15) == 0) {
                updateSkyState(world.provider.isNether() ||
                        MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky));
            }

            updateMultiblockState();

            if(gatewayRegistered) {
                if(!hasMultiblock() || !doesSeeSky()) {
                    GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
                    cache.removePosition(world, pos);
                    gatewayRegistered = false;
                }
            } else {
                if(hasMultiblock() && doesSeeSky()) {
                    GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
                    cache.offerPosition(world, pos, display == null ? "" : display);
                    gatewayRegistered = true;
                }
            }
        }
    }

    @Override
    public boolean hasCustomName() {
        return display != null && !display.isEmpty();
    }

    @Override
    public String getName() {
        return hasCustomName() ? display : "";
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(getName()) : new TextComponentString("");
    }

    @Override
    protected void onFirstTick() {}

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternCelestialGateway;
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    public void setGatewayName(String displayName) {
        this.display = displayName;
    }

    public void setPlacedBy(UUID placedBy) {
        this.placedBy = placedBy;
    }

    public UUID getPlacedBy() {
        return placedBy;
    }

    private void updateMultiblockState() {
        if (structureMatch == null) {
            this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
        }
        boolean matches = this.structureMatch.matches(getWorld());
        boolean update = this.hasMultiblock != matches;
        if (update) {
            this.hasMultiblock = matches;
            markForUpdate();
        }
    }

    private void updateSkyState(boolean seeSky) {
        boolean update = doesSeeSky != seeSky;
        if (update) {
            this.doesSeeSky = seeSky;
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
            Vector3 sphereVec = new Vector3(pos).add(0.5, 1.62, 0.5);
            if(clientSphere == null) {
                CompoundEffectSphere sphere = new CompoundGatewayShield(sphereVec.clone(), Vector3.RotAxis.Y_AXIS, 6, 8, 10);
                sphere.setRemoveIfInvisible(true).setAlphaFadeDistance(4);
                EffectHandler.getInstance().registerFX(sphere);
                clientSphere = sphere;
            }
            double playerDst = Vector3.atEntityCenter(Minecraft.getMinecraft().player).distance(sphereVec);
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
                EffectHandler.getInstance().requestGatewayUIFor(world, pos, sphereVec, 5.5);
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
            Vector3 offset = new Vector3(pos).add(-2, 0, -2);
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
                default:
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
        this.display = compound.getString("display");

        if (compound.hasUniqueId("placer")) {
            this.placedBy = compound.getUniqueId("placer");
        } else {
            this.placedBy = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setBoolean("skyState", this.doesSeeSky);
        compound.setString("display", display == null ? "" : display);
        if (this.placedBy != null) {
            compound.setUniqueId("placer", this.placedBy);
        }
    }

}
