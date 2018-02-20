/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ICraftingProgress;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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
    private Map<TraitRecipeSlot, ItemHandle> matchTraitStacks = new HashMap<>();
    private IConstellation requiredConstellation = null;

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public TraitRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.TRAIT_CRAFT, recipe);
        setPassiveStarlightRequirement(7500);
    }

    public TraitRecipe setInnerTraitItem(Item i, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(i), slots);
    }

    public TraitRecipe setInnerTraitItem(Block b, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(b), slots);
    }

    public TraitRecipe setInnerTraitItem(ItemStack stack, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(stack), slots);
    }

    public TraitRecipe setInnerTraitItem(String oreDict, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(oreDict), slots);
    }

    public TraitRecipe setInnerTraitItem(FluidStack fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(fluid), slots);
    }

    public TraitRecipe setInnerTraitItem(Fluid fluid, int mbAmount, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new FluidStack(fluid, mbAmount), slots);
    }

    public TraitRecipe setInnerTraitItem(Fluid fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(fluid, 1000, slots);
    }

    public TraitRecipe setInnerTraitItem(ItemHandle handle, TraitRecipeSlot... slots) {
        for (TraitRecipeSlot slot : slots) {
            matchTraitStacks.put(slot, handle);
        }
        return this;
    }

    public TraitRecipe addOuterTraitItem(Item i) {
        return addOuterTraitItem(new ItemStack(i));
    }

    public TraitRecipe addOuterTraitItem(Block b) {
        return addOuterTraitItem(new ItemStack(b));
    }

    public TraitRecipe addOuterTraitItem(ItemStack stack) {
        return addOuterTraitItem(new ItemHandle(stack));
    }

    public TraitRecipe addOuterTraitItem(String oreDict) {
        return addOuterTraitItem(new ItemHandle(oreDict));
    }

    public TraitRecipe addOuterTraitItem(FluidStack fluid) {
        return addOuterTraitItem(new ItemHandle(fluid));
    }

    public TraitRecipe addOuterTraitItem(Fluid fluid, int mbAmount) {
        return addOuterTraitItem(new FluidStack(fluid, mbAmount));
    }

    public TraitRecipe addOuterTraitItem(Fluid fluid) {
        return addOuterTraitItem(fluid, 1000);
    }

    public TraitRecipe addOuterTraitItem(ItemHandle handle) {
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

    @Nonnull
    public List<ItemStack> getInnerTraitItems(TraitRecipeSlot slot) {
        ItemHandle handle = matchTraitStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getInnerTraitItemHandle(TraitRecipeSlot slot) {
        return matchTraitStacks.get(slot);
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
        return 1400;
    }

    @Override
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick) {
        if(!fulfillesStarlightRequirement(altar)) {
            return false; //Duh.
        }

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
            if(focus == null) return false;
            if(!req.equals(focus)) return false;
        }
        for (TraitRecipeSlot slot : TraitRecipeSlot.values()) {
            ItemHandle expected = matchTraitStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    public void consumeOuterInputs(TileAltar altar, ActiveCraftingTask craftingTask) {
        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingTask.getCraftingData());
        for (CraftingFocusStack stack : stacks) {
            if(stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

            ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
            TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
            if(tar != null) {
                //We take a leap of faith and assume the required matches the found itemstack in terms of crafting matching
                //It should match since we literally check in the same tick as we finish the recipe if it's valid...
                ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                if(required.getFluidTypeAndAmount() != null) {
                    if (!found.isEmpty()) {
                        FluidActionResult fas = ItemUtils.drainFluidFromItem(found, required.getFluidTypeAndAmount(), true);
                        if (fas.isSuccess()) {
                            tar.getInventoryHandler().setStackInSlot(0, fas.getResult());
                            tar.markForUpdate();
                        }
                    }
                } else if(!ForgeHooks.getContainerItem(found).isEmpty()) {
                    tar.getInventoryHandler().setStackInSlot(0, ForgeHooks.getContainerItem(found));
                    tar.markForUpdate();
                } else {
                    ItemUtils.decrStackInInventory(tar.getInventoryHandler(), 0);
                    tar.markForUpdate();
                }
            }
        }
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
        Vector3 thisAltar = new Vector3(altar).add(0.5, 0.5, 0.5);

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
                        if(rand.nextInt(5) == 0) {
                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                                    altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                                    altar.getPos().getY() + 0.02,
                                    altar.getPos().getZ() - 3 + rand.nextFloat() * 7
                            );
                            p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.15F);
                            p.setColor(c);
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

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            EntityFXFacingParticle p;
            if(rand.nextInt(4) == 0) {
                p = EffectHelper.genericFlareParticle(
                        altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                        altar.getPos().getY() + 0.02,
                        altar.getPos().getZ() - 3 + rand.nextFloat() * 7
                );
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.15F);
                p.setColor(Color.WHITE);
            }

            for (int i = 0; i < 1; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(1.3 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(2 +  + rand.nextFloat() * 0.4));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }
            for (int i = 0; i < 2; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(2 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(1.1 + rand.nextFloat() * 0.4));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }

            if(rand.nextInt(20) == 0) {
                Vector3 from = new Vector3(
                        altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                        altar.getPos().getY() + 0.02,
                        altar.getPos().getZ() - 3 + rand.nextFloat() * 7);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
                lightbeam.setColorOverlay(Color.WHITE);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileAltar altar, double x, double y, double z, float partialTicks) {
        super.onCraftTESRRender(altar, x, y, z, partialTicks);
        ActiveCraftingTask act = altar.getActiveCraftingTask();
        if(act != null) {
            List<CraftingFocusStack> stacks = collectCurrentStacks(act.getCraftingData());
            for (CraftingFocusStack stack : stacks) {
                if (stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

                ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
                TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
                if(tar != null) {
                    ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                    if(found.isEmpty() || !required.matchCrafting(found)) {
                        NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                        int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                        ItemStack element = stacksApplicable.get(MathHelper.floor(
                                MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                        renderTranslucentItem(element, x + stack.offset.getX(), y + stack.offset.getY(), z + stack.offset.getZ(), partialTicks);
                    }
                } else {
                    NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                    int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                    ItemStack element = stacksApplicable.get(MathHelper.floor(
                            MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                    renderTranslucentItem(element, x + stack.offset.getX(), y + stack.offset.getY(), z + stack.offset.getZ(), partialTicks);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderTranslucentItem(ItemStack stack, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();

        IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        float sinBobY = MathHelper.sin((ClientScheduler.getClientTick() + partialTicks) / 10.0F) * 0.1F + 0.1F;
        GlStateManager.translate(x + 0.5, y + sinBobY + 0.25F, z + 0.5);
        float ageRotate = ((ClientScheduler.getClientTick() + partialTicks) / 20.0F) * (180F / (float)Math.PI);
        GlStateManager.rotate(ageRotate, 0.0F, 1.0F, 0.0F);
        bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GROUND, false);

        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        RenderingUtils.tryRenderItemWithColor(stack, bakedModel, Color.WHITE, 0.1F);

        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Blending.DEFAULT.applyStateManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
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

    public static enum TraitRecipeSlot {

        UPPER_CENTER(21),
        LEFT_CENTER(22),
        RIGHT_CENTER(23),
        LOWER_CENTER(24);

        private final int slotId;

        TraitRecipeSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
