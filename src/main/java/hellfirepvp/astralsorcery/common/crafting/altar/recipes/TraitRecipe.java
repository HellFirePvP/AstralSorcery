/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ICraftingProgress;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitRecipe
 * Created by HellFirePvP
 * Date: 06.03.2017 / 15:57
 */
public class TraitRecipe extends ConstellationRecipe implements ICraftingProgress {

    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
    private IConstellation requiredConstellation = null;

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public TraitRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make(new ResourceLocation(AstralSorcery.MODID, "recipes/internal/altar_compare")));
    }

    public TraitRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.TRAIT_CRAFT, recipe);
        setPassiveStarlightRequirement(6500);
    }

    public TraitRecipe addTraitItem(Item i) {
        return addTraitItem(new ItemStack(i));
    }

    public TraitRecipe addTraitItem(Block b) {
        return addTraitItem(new ItemStack(b));
    }

    public TraitRecipe addTraitItem(ItemStack stack) {
        return addTraitItem(new ItemHandle(stack));
    }

    public TraitRecipe addTraitItem(String oreDict) {
        return addTraitItem(new ItemHandle(oreDict));
    }

    public TraitRecipe addTraitItem(FluidStack fluid) {
        return addTraitItem(new ItemHandle(fluid));
    }

    public TraitRecipe addTraitItem(Fluid fluid, int mbAmount) {
        return addTraitItem(new FluidStack(fluid, mbAmount));
    }

    public TraitRecipe addTraitItem(Fluid fluid) {
        return addTraitItem(fluid, 1000);
    }

    public TraitRecipe addTraitItem(ItemHandle handle) {
        additionallyRequiredStacks.add(handle);
        return this;
    }

    @Nonnull
    public List<NonNullList<ItemStack>> getTraitItems() {
        List<NonNullList<ItemStack>> out = Lists.newArrayList();
        for (ItemHandle handle : additionallyRequiredStacks) {
            out.add(handle.getApplicableItems());
        }
        return out;
    }

    @Nonnull
    public List<ItemHandle> getTraitItemHandles() {
        return Lists.newArrayList(additionallyRequiredStacks);
    }

    public void setRequiredConstellation(IConstellation requiredConstellation) {
        this.requiredConstellation = requiredConstellation;
    }

    @Nullable
    public IConstellation getRequiredConstellation() {
        return requiredConstellation;
    }

    @Override
    public int craftingTickTime() {
        return 1000;
    }

    @Override
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick) {
        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingData);
        if(!matchFocusStacks(altar, stacks)) {
            return false;
        }

        int required = additionallyRequiredStacks.size();
        int part = craftingTickTime() / 2;
        int offset = craftingTickTime() / 10;
        int cttPart = part / required;
        for (int i = 0; i < required; i++) {
            int timing = (i * cttPart) + offset;
            if(activeCraftingTick >= timing) {
                CraftingFocusStack found = null;
                for (CraftingFocusStack stack : stacks) {
                    if(stack.stackIndex == i) {
                        found = stack;
                        break;
                    }
                }
                if(found == null) {
                    BlockPos next = findUnusedRelay(altar, stacks);
                    if(next != null) {
                        CraftingFocusStack stack = new CraftingFocusStack(i, next);
                        stacks.add(stack);
                        storeCurrentStacks(craftingData, stacks);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        IConstellation req = getRequiredConstellation();
        if(req != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if(focus != null) {
                if(!req.equals(focus)) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.RADIANCE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        ActiveCraftingTask act = altar.getActiveCraftingTask();
        if(act != null) {
            List<CraftingFocusStack> stacks = collectCurrentStacks(act.getCraftingData());
            for (CraftingFocusStack stack : stacks) {
                if(stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

                ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
                TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
                if(tar != null) { //If it's null then the server messed up or we're desynced..
                    ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                    if(!found.isEmpty() && required.matchCrafting(found)) {
                        Color c = ItemColorizationHelper.getDominantColorFromItemStack(found);
                        if(c == null) {
                            c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                        }
                        if(ClientScheduler.getClientTick() % 35 == 0) {
                            EffectLightbeam beam = EffectHandler.getInstance().lightbeam(
                                    new Vector3(tar).add(0.5, 0.1, 0.5),
                                    new Vector3(altar).add(0.5, 4.5, 0.5),
                                    0.8);
                            beam.setColorOverlay(c);
                        }
                        if(rand.nextBoolean()) {
                            Vector3 at = new Vector3(tar);
                            at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                            p.setAlphaMultiplier(0.7F);
                            p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                            p.gravity(0.01).scale(0.3F + rand.nextFloat() * 0.1F);
                            p.setColor(c);
                            if(rand.nextInt(3) == 0) {
                                p.gravity(0.004).scale(0.1F + rand.nextFloat() * 0.1F);
                                p.setColor(Color.WHITE);
                            }
                        }
                    } else {
                        NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                        if(stacksApplicable.size() > 0) {
                            int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                            ItemStack element = stacksApplicable.get(MathHelper.floor(
                                    MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                            Color c = ItemColorizationHelper.getDominantColorFromItemStack(element);
                            if(c == null) {
                                c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                            }
                            if(ClientScheduler.getClientTick() % 35 == 0) {
                                EffectLightbeam beam = EffectHandler.getInstance().lightbeam(
                                        new Vector3(tar).add(0.5, 0.1, 0.5),
                                        new Vector3(altar).add(0.5, 4.5, 0.5),
                                        0.8);
                                beam.setColorOverlay(c);
                            }
                            if(rand.nextBoolean()) {
                                Vector3 at = new Vector3(tar);
                                at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                                p.setAlphaMultiplier(0.7F);
                                p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                                p.gravity(0.01).scale(0.3F + rand.nextFloat() * 0.1F);
                                p.setColor(c);
                                if(rand.nextInt(3) == 0) {
                                    p.gravity(0.004).scale(0.1F + rand.nextFloat() * 0.1F);
                                    p.setColor(Color.WHITE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nullable
    protected BlockPos findUnusedRelay(TileAltar center, List<CraftingFocusStack> found) {
        List<BlockPos> eligableRelayOffsets = Lists.newLinkedList();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                if(xx == 0 && zz == 0) continue; //Not that it matters tho

                BlockPos offset = new BlockPos(xx, 0, zz);
                TileAttunementRelay tar = MiscUtils.getTileAt(center.getWorld(), center.getPos().add(offset), TileAttunementRelay.class, true);
                if(tar != null) {
                    eligableRelayOffsets.add(offset);
                }
            }
        }
        for (CraftingFocusStack stack : found) {
            eligableRelayOffsets.remove(stack.offset);
        }
        if(eligableRelayOffsets.size() <= 0) {
            return null;
        }
        return eligableRelayOffsets.get(center.getWorld().rand.nextInt(eligableRelayOffsets.size()));
    }

    protected boolean matchFocusStacks(TileAltar altar, List<CraftingFocusStack> stacks) {
        for (CraftingFocusStack stack : stacks) {
            int index = stack.stackIndex;
            if(index < 0 || index >= additionallyRequiredStacks.size()) continue;
            ItemHandle required = additionallyRequiredStacks.get(index);
            TileAttunementRelay relay = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
            if(relay == null) {
                return false;
            }
            ItemStack in = relay.getInventoryHandler().getStackInSlot(0);
            if(in.isEmpty() || !required.matchCrafting(in)) {
                return false;
            }
        }
        return true;
    }

    protected void storeCurrentStacks(NBTTagCompound craftingStorage, List<CraftingFocusStack> stacks) {
        NBTTagList list = new NBTTagList();
        for (CraftingFocusStack stack : stacks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("focusIndex", stack.stackIndex);
            NBTUtils.writeBlockPosToNBT(stack.offset, tag);
            list.appendTag(tag);
        }
        craftingStorage.setTag("offsetFocusList", list);
    }

    protected List<CraftingFocusStack> collectCurrentStacks(NBTTagCompound craftingStorage) {
        List<CraftingFocusStack> stacks = Lists.newLinkedList();
        NBTTagList list = craftingStorage.getTagList("offsetFocusList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cmp = list.getCompoundTagAt(i);
            int index = cmp.getInteger("focusIndex");
            BlockPos pos = NBTUtils.readBlockPosFromNBT(cmp);
            stacks.add(new CraftingFocusStack(index, pos));
        }
        return stacks;
    }

    protected static class CraftingFocusStack {

        protected final Integer stackIndex; //Index of required stack
        protected final BlockPos offset;

        protected CraftingFocusStack(Integer stackIndex, BlockPos offset) {
            this.stackIndex = stackIndex;
            this.offset = offset;
        }

    }

}
