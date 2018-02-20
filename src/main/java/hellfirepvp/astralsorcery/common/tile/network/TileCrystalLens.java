/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.base.TileTransmissionBase;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:11
 */
public class TileCrystalLens extends TileTransmissionBase {

    private CrystalProperties properties;
    private ItemColoredLens.ColorType lensColor;

    private int lensEffectTimeout = 0;

    //So we can tell the client to render beams eventhough the actual connection doesn't exist.
    private List<BlockPos> occupiedConnections = new LinkedList<>();

    public CrystalProperties getCrystalProperties() {
        return properties;
    }

    public void onPlace(CrystalProperties properties) {
        this.properties = properties;
        markForUpdate();
    }

    public void onTransmissionTick() {
        this.lensEffectTimeout = 5;
    }

    //Returns the old one.
    @Nullable
    public ItemColoredLens.ColorType setLensColor(ItemColoredLens.ColorType type) {
        ItemColoredLens.ColorType old = this.lensColor;
        this.lensColor = type;
        markForUpdate();
        IPrismTransmissionNode node = getNode();
        if(node != null) {
            boolean shouldIgnore = type == ItemColoredLens.ColorType.SPECTRAL;
            if(node instanceof CrystalTransmissionNode) {
                if(shouldIgnore != ((CrystalTransmissionNode) node).ignoresBlockCollision()) {
                    ((CrystalTransmissionNode) node).updateIgnoreBlockCollisionState(world, shouldIgnore);
                }
                if(lensColor != null) {
                    ((CrystalTransmissionNode) node)     .updateAdditionalLoss(1F - lensColor.getFlowReduction());
                } else {
                    ((CrystalTransmissionNode) node)     .updateAdditionalLoss(1F);
                }
            } else if(node instanceof CrystalPrismTransmissionNode) {
                if(shouldIgnore != ((CrystalPrismTransmissionNode) node).ignoresBlockCollision()) {
                    ((CrystalPrismTransmissionNode) node).updateIgnoreBlockCollisionState(world, shouldIgnore);
                }
                if(lensColor != null) {
                    ((CrystalPrismTransmissionNode) node)     .updateAdditionalLoss(1F - lensColor.getFlowReduction());
                } else {
                    ((CrystalPrismTransmissionNode) node)     .updateAdditionalLoss(1F);
                }
            }
        }
        return old;
    }

    public List<BlockPos> getOccupiedConnections() {
        return occupiedConnections;
    }

    @Nullable
    public ItemColoredLens.ColorType getLensColor() {
        return lensColor;
    }

    @Override
    public void update() {
        super.update();

        if(lensColor != null) {
            if(world.isRemote) {
                playColorEffects();
            } else {
                doLensColorEffects(lensColor);
            }
        }
    }

    private void doLensColorEffects(ItemColoredLens.ColorType lensColor) {
        if(getTicksExisted() % 4 != 0) return;

        this.occupiedConnections.clear();
        markForUpdate();

        if(lensEffectTimeout > 0) {
            lensEffectTimeout--;
        } else {
            return;
        }

        Vector3 thisVec = new Vector3(this).add(0.5, 0.5, 0.5);
        List<BlockPos> linked = getLinkedPositions();
        float str = 1F / ((float) linked.size());
        str *= 0.7F;
        for (BlockPos linkedTo : linked) {
            Vector3 to = new Vector3(linkedTo).add(0.5, 0.5, 0.5);
            RaytraceAssist rta = new RaytraceAssist(thisVec, to).includeEndPoint();
            if(lensColor.getType() == ItemColoredLens.TargetType.BLOCK) {
                boolean clear = rta.isClear(world);
                if(!clear && rta.blockHit() != null) {
                    BlockPos hit = rta.blockHit();
                    IBlockState hitState = world.getBlockState(hit);
                    if (!hit.equals(to.toBlockPos()) || (!hitState.getBlock().equals(BlocksAS.lens) &&
                            !hitState.getBlock().equals(BlocksAS.lensPrism))) {
                                lensColor.onBlockOccupyingBeam(world, hit, hitState, str);
                            }
                    this.occupiedConnections.add(hit);
                }
            } else if(lensColor.getType() == ItemColoredLens.TargetType.ENTITY) {
                rta.setCollectEntities(0.5);
                rta.isClear(world);
                List<Entity> found = rta.collectedEntities(world);
                float pStr = lensColor == ItemColoredLens.ColorType.FIRE ? str / 2F : str;
                for(Entity entity : found) {
                    lensColor.onEntityInBeam(thisVec, to, entity, pStr);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playColorEffects() {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().player;
        if(rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
        Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(lensColor.wrappedColor);
        particle.motion(
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);

        Vector3 source = new Vector3(this).add(0.5, 0.5, 0.5);
        Color overlay = lensColor.wrappedColor;
        if(getTicksExisted() % 40 == 0) {
            for (BlockPos dst : occupiedConnections) {
                Vector3 to = new Vector3(dst).add(0.5, 0.5, 0.5);
                EffectLightbeam beam = EffectHandler.getInstance().lightbeam(to, source, 0.6);
                beam.setColorOverlay(overlay.getRed() / 255F, overlay.getGreen() / 255F, overlay.getBlue() / 255F, 1F);
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.properties = CrystalProperties.readFromNBT(compound);
        int col = compound.getInteger("color");
        if(col >= 0 && col < ItemColoredLens.ColorType.values().length) {
            this.lensColor = ItemColoredLens.ColorType.values()[col];
        } else {
            this.lensColor = null;
        }

        this.occupiedConnections.clear();
        NBTTagList list = compound.getTagList("listOccupied", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos bp = NBTUtils.readBlockPosFromNBT(tag);
            this.occupiedConnections.add(bp);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        this.properties.writeToNBT(compound);
        compound.setInteger("color", lensColor != null ? lensColor.ordinal() : -1);

        NBTTagList list = new NBTTagList();
        for (BlockPos to : occupiedConnections) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(to, cmp);
            list.appendTag(cmp);
        }
        compound.setTag("listOccupied", list);
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        super.onLinkCreate(player, other);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blocklens.name";
    }

    @Override
    @Nonnull
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalTransmissionNode(at, getCrystalProperties());
    }

}
