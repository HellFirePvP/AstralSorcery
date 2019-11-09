/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.infusion;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.ChaliceHelper;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveLiquidInfusionRecipe
 * Created by HellFirePvP
 * Date: 09.11.2019 / 11:24
 */
public class ActiveLiquidInfusionRecipe {

    private static final Random rand = new Random();

    private final LiquidInfusion recipeToCraft;
    private final UUID playerCraftingUUID;

    private int ticksCrafting = 0;
    private Set<BlockPos> supportingChalices = new HashSet<>();
    private CompoundNBT craftingData = new CompoundNBT();

    public ActiveLiquidInfusionRecipe(World world, BlockPos center, LiquidInfusion recipeToCraft, UUID playerCraftingUUID) {
        this(recipeToCraft, playerCraftingUUID);

        this.findChalices(world, center);
    }

    private ActiveLiquidInfusionRecipe(LiquidInfusion recipeToCraft, UUID playerCraftingUUID) {
        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
    }

    private void findChalices(World world, BlockPos center) {
        ChaliceHelper.findNearbyChalicesCombined(world, center, this.getChaliceRequiredFluidInput(), 8)
                .ifPresent(chalices -> chalices.forEach(chalice -> this.supportingChalices.add(chalice.getPos())));
    }

    public boolean matches(TileInfuser infuser) {
        if (!this.getRecipeToCraft().matches(infuser, this.tryGetCraftingPlayerServer(), LogicalSide.SERVER)) {
            return false;
        }

        if (!this.supportingChalices.isEmpty()) {
            if (!ChaliceHelper.doChalicesContainCombined(infuser.getWorld(), this.supportingChalices, this.getChaliceRequiredFluidInput())) {
                this.supportingChalices.clear();
            }
        }
        return true;
    }

    public void tick() {
        this.ticksCrafting++;
    }

    public void createItemOutputs(TileInfuser infuser, Consumer<ItemStack> output) {
        Consumer<ItemStack> informer = stack -> ResearchManager.informCraftedInfuser(infuser, this, stack);

        Consumer<ItemStack> handleCrafted = informer.andThen(output);
        handleCrafted.accept(this.getRecipeToCraft().getOutput(infuser));
        this.getRecipeToCraft().onRecipeCompletion(infuser);
    }

    public void consumeInputs(TileInfuser infuser) {
        ItemUtils.decrementItem(infuser::getItemInput, infuser::setItemInput, infuser::dropItemOnTop);
    }

    public FluidStack getChaliceRequiredFluidInput() {
        int amount = Math.round(FluidAttributes.BUCKET_VOLUME * recipeToCraft.getConsumptionChance());
        amount *= 0.75; //Bonus for using chalices

        amount = recipeToCraft.doesConsumeMultipleFluids() ? amount * TileInfuser.getLiquidOffsets().size() : amount;
        return new FluidStack(this.getRecipeToCraft().getLiquidInput(), amount);
    }

    public int getTotalCraftingTime() {
        int tickTime = this.recipeToCraft.getCraftingTickTime();

        int fixTime = Math.round(tickTime * 0.25F);
        int chaliceTime = Math.round(tickTime * 0.75F);
        chaliceTime /= this.supportingChalices.size() + 1;

        return fixTime + chaliceTime;
    }

    public CompoundNBT getCraftingData() {
        return craftingData;
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public LiquidInfusion getRecipeToCraft() {
        return recipeToCraft;
    }

    public boolean isFinished() {
        return getTicksCrafting() >= getTotalCraftingTime();
    }

    @Nullable
    public PlayerEntity tryGetCraftingPlayerServer() {
        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        return srv.getPlayerList().getPlayerByUUID(this.getPlayerCraftingUUID());
    }
    @Nullable
    public static ActiveLiquidInfusionRecipe deserialize(CompoundNBT compound) {
        RecipeManager mgr = RecipeHelper.getRecipeManager();
        if (mgr == null) {
            return null;
        }

        ResourceLocation recipeKey = new ResourceLocation(compound.getString("recipeToCraft"));
        Optional<?> recipe = mgr.getRecipe(recipeKey);
        if (!recipe.isPresent() || !(recipe.get() instanceof LiquidInfusion)) {
            AstralSorcery.log.info("Recipe with unknown/invalid name found: " + recipeKey);
            return null;
        }
        LiquidInfusion altarRecipe = (LiquidInfusion) recipe.get();

        UUID uuidCraft = compound.getUniqueId("playerCraftingUUID");
        int tick = compound.getInt("ticksCrafting");
        ListNBT chalices = compound.getList("supportingChalices", Constants.NBT.TAG_COMPOUND);

        Set<BlockPos> chalicePositions = new HashSet<>();
        for (int i = 0; i < chalices.size(); i++) {
            CompoundNBT tag = chalices.getCompound(i);
            chalicePositions.add(NBTHelper.readBlockPosFromNBT(tag));
        }

        ActiveLiquidInfusionRecipe task = new ActiveLiquidInfusionRecipe(altarRecipe, uuidCraft);
        task.ticksCrafting = tick;
        task.craftingData = compound.getCompound("craftingData");
        task.supportingChalices.addAll(chalicePositions);
        return task;
    }

    @Nonnull
    public CompoundNBT serialize() {
        ListNBT chalicePositions = new ListNBT();
        this.supportingChalices.forEach(pos -> chalicePositions.add(NBTHelper.writeBlockPosToNBT(pos, new CompoundNBT())));

        CompoundNBT compound = new CompoundNBT();
        compound.putString("recipeToCraft", getRecipeToCraft().getId().toString());
        compound.putUniqueId("playerCraftingUUID", getPlayerCraftingUUID());
        compound.putInt("ticksCrafting", getTicksCrafting());
        compound.put("craftingData", craftingData);
        compound.put("supportingChalices", chalicePositions);
        return compound;
    }

}
