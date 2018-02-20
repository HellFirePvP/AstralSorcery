/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementRelay
 * Created by HellFirePvP
 * Date: 27.03.2017 / 17:53
 */
public class TileAttunementRelay extends TileInventoryBase {

    private static final float MAX_DST = (float) (Math.sqrt(Math.sqrt(2.0D) + 1) * 16.0D);

    private BlockPos linked = null;
    private float collectionMultiplier = 1F;

    private boolean canSeeSky = false, hasMultiblock = false;

    public TileAttunementRelay() {
        super(1);
    }

    public void updatePositionData(@Nullable BlockPos closestAltar, double dstSqOtherRelay) {
        this.linked = closestAltar;
        dstSqOtherRelay = Math.sqrt(dstSqOtherRelay);
        if(dstSqOtherRelay <= 1E-4) {
            collectionMultiplier = 1F;
        } else {
            collectionMultiplier = 1F - ((float) (MathHelper.clamp(dstSqOtherRelay, 0, MAX_DST) / MAX_DST));
        }
        markForUpdate();
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState();
        }

        if((ticksExisted & 31) == 0) {
            updateMultiblockState();
        }

        ItemStack slotted = getInventoryHandler().getStackInSlot(0);
        if(!world.isRemote) {
            if(!slotted.isEmpty()) {
                if(!world.isAirBlock(pos.up())) {
                    ItemStack in = getInventoryHandler().getStackInSlot(0);
                    ItemStack out = ItemUtils.copyStackWithSize(in, in.getCount());
                    ItemUtils.dropItem(world, pos.getX(), pos.getY(), pos.getZ(), out);
                    getInventoryHandler().setStackInSlot(0, ItemStack.EMPTY);
                }

                if(ItemUtils.matchStackLoosely(slotted, ItemCraftingComponent.MetaType.GLASS_LENS.asStack())) {
                    if(linked != null) {
                        TileAltar ta = MiscUtils.getTileAt(world, linked, TileAltar.class, true);
                        if(ta == null) {
                            linked = null;
                            markForUpdate();
                        } else if(hasMultiblock && doesSeeSky()) {
                            WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
                            int yLevel = getPos().getY();
                            if(handle != null && yLevel > 40) {
                                double coll = 0.3;

                                float dstr;
                                if(yLevel > 120) {
                                    dstr = 1F;
                                } else {
                                    dstr = (yLevel - 40) / 80F;
                                }

                                coll *= dstr;
                                coll *= collectionMultiplier;
                                coll *= (0.2 + (0.8 * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(getWorld())));
                                ta.receiveStarlight(null, coll);
                            }
                        }
                    }
                }
            }
        } else {
            if(!slotted.isEmpty() && hasMultiblock) {
                if(ItemUtils.matchStackLoosely(slotted, ItemCraftingComponent.MetaType.GLASS_LENS.asStack())) {
                    if(rand.nextInt(3) == 0) {
                        Vector3 at = new Vector3(this);
                        at.add(rand.nextFloat() * 2.6 - 0.8, 0, rand.nextFloat() * 2.6 - 0.8);
                        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                        p.setAlphaMultiplier(0.7F);
                        p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                        p.gravity(0.01).scale(0.3F + rand.nextFloat() * 0.1F);
                        if(rand.nextBoolean()) {
                            p.setColor(Color.WHITE);
                        }
                    }

                    if(linked != null && doesSeeSky() && rand.nextInt(4) == 0) {
                        Vector3 at = new Vector3(this);
                        Vector3 dir = new Vector3(linked).subtract(new Vector3(this)).normalize().multiply(0.05);
                        at.add(rand.nextFloat() * 0.4 + 0.3, rand.nextFloat() * 0.3 + 0.1, rand.nextFloat() * 0.4 + 0.3);
                        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                        p.setAlphaMultiplier(0.7F);
                        p.motion(dir.getX(), dir.getY(), dir.getZ());
                        p.setMaxAge((int) (15 + rand.nextFloat() * 30));
                        p.gravity(0.015).scale(0.2F + rand.nextFloat() * 0.04F);
                        if(rand.nextBoolean()) {
                            p.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
    }

    private void updateMultiblockState() {
        boolean found = MultiBlockArrays.patternCollectorRelay.matches(world, getPos());
        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
        }
    }

    private void updateSkyState() {
        boolean seesSky = world.canSeeSky(getPos());
        boolean update = canSeeSky != seesSky;
        this.canSeeSky = seesSky;
        if(update) {
            markForUpdate();
        }
    }

    public boolean doesSeeSky() {
        return canSeeSky;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("seesSky", this.canSeeSky);
        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setFloat("colMultiplier", this.collectionMultiplier);
        if(linked != null) {
            NBTTagCompound pos = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(linked, pos);
            compound.setTag("linked", pos);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.canSeeSky = compound.getBoolean("seesSky");
        this.hasMultiblock = compound.getBoolean("mbState");
        this.collectionMultiplier = compound.getFloat("colMultiplier");
        if(compound.hasKey("linked")) {
            this.linked = NBTUtils.readBlockPosFromNBT(compound.getCompoundTag("linked"));
        } else {
            linked = null;
        }
    }

}
