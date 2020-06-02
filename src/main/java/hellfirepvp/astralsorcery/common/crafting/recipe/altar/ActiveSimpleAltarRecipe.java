/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingFocusStack;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveSimpleAltarRecipe
 * Created by HellFirePvP
 * Date: 13.08.2019 / 20:59
 */
public class ActiveSimpleAltarRecipe {

    private static final Random rand = new Random();

    private Map<Integer, Object> clientEffectContainer = new HashMap<>();

    private final SimpleAltarRecipe recipeToCraft;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;
    private int totalCraftingTime;

    private CraftingState state;
    private CompoundNBT craftingData = new CompoundNBT();

    private List<CraftingFocusStack> focusStacks = new LinkedList<>();

    private ActiveSimpleAltarRecipe(SimpleAltarRecipe recipeToCraft, UUID playerCraftingUUID) {
        this(recipeToCraft, 1, playerCraftingUUID);
    }

    public ActiveSimpleAltarRecipe(SimpleAltarRecipe recipeToCraft, int durationDivisor, UUID playerCraftingUUID) {
        Objects.requireNonNull(recipeToCraft);

        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
        this.state = CraftingState.ACTIVE;
        this.totalCraftingTime = recipeToCraft.getDuration() / durationDivisor;
    }

    private void recoverContainedEffects(@Nullable ActiveSimpleAltarRecipe previous) {
        if (previous != null && previous.getRecipeToCraft().getId().equals(this.recipeToCraft.getId())) {
            this.clientEffectContainer.putAll(previous.clientEffectContainer);
        }
    }

    public CompoundNBT getCraftingData() {
        return craftingData;
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    public void setState(CraftingState state) {
        this.state = state;
    }

    public CraftingState getState() {
        return state;
    }

    public List<CraftingFocusStack> getFocusStacks() {
        return focusStacks;
    }

    @Nullable
    public PlayerEntity tryGetCraftingPlayerServer() {
        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        return srv.getPlayerList().getPlayerByUUID(this.getPlayerCraftingUUID());
    }

    @OnlyIn(Dist.CLIENT)
    public <T> T getEffectContained(int index, Function<Integer, T> provider) {
        return (T) clientEffectContainer.computeIfAbsent(index, provider);
    }

    public void createItemOutputs(TileAltar altar, Consumer<ItemStack> output) {
        Consumer<ItemStack> informer = stack -> ResearchManager.informCraftedAltar(altar, this, stack);

        Consumer<ItemStack> handleCrafted = informer.andThen(output);
        this.getRecipeToCraft().getOutputs(altar).forEach(handleCrafted);
        this.getRecipeToCraft().onRecipeCompletion(altar, this);
    }

    public void consumeInputs(TileAltar altar) {
        TileInventory inv = altar.getInventory();

        for (int slot = 0; slot < AltarRecipeGrid.MAX_INVENTORY_SIZE; slot++) {
            ItemUtils.decrementItem(inv, slot, altar::dropItemOnTop);
        }

        for (CraftingFocusStack input : this.focusStacks) {
            TileSpectralRelay tar = MiscUtils.getTileAt(altar.getWorld(), input.getRealPosition(), TileSpectralRelay.class, true);
            if (tar != null) {
                TileInventory tarInventory = tar.getInventory();
                ItemUtils.decrementItem(tarInventory, 0, altar::dropItemOnTop);
            }
        }
    }

    public boolean matches(TileAltar altar, boolean ignoreStarlightRequirement) {
        if (!this.getRecipeToCraft().matches(LogicalSide.SERVER, this.tryGetCraftingPlayerServer(), altar, ignoreStarlightRequirement)) {
            return false;
        }

        List<WrappedIngredient> listIngredients = this.getRecipeToCraft().getRelayInputs();
        for (CraftingFocusStack stack : this.focusStacks) {
            if (stack.getStackIndex() >= 0 && stack.getStackIndex() < listIngredients.size()) {
                TileSpectralRelay relay = MiscUtils.getTileAt(altar.getWorld(), stack.getRealPosition(), TileSpectralRelay.class, true);
                if (relay == null) {
                    return false;
                }
            } else {
                //The recipe changed?
                return false;
            }
        }
        return true;
    }
    //True if the recipe progressed, false if it's stuck

    public CraftingState tick(TileAltar altar) {
        if (recipeToCraft instanceof AltarCraftingProgress) {
            if (!((AltarCraftingProgress) recipeToCraft).tryProcess(altar, this, craftingData, ticksCrafting, totalCraftingTime)) {
                return CraftingState.WAITING;
            }
        }

        List<WrappedIngredient> iIngredients = this.getRecipeToCraft().getRelayInputs();

        if (!iIngredients.isEmpty()) {
            boolean shouldWait = tickCraftingRelayInputs(altar, iIngredients);
            if (shouldWait) {
                return CraftingState.WAITING;
            }
        }

        ticksCrafting++;
        return CraftingState.ACTIVE;
    }

    private boolean tickCraftingRelayInputs(TileAltar altar, List<WrappedIngredient> iIngredients) {
        //Start the surrounding stack intake past 66%
        int part = totalCraftingTime / 3;
        //Every input is evenly spread
        int cttPart = part / (iIngredients.size()) + 1;


        boolean waitMissingInputs = false;
        for (int i = 0; i < iIngredients.size(); i++) {
            int index = i;

            int offset = part + (index * cttPart);
            if (this.ticksCrafting >= offset) {
                CraftingFocusStack found = MiscUtils.iterativeSearch(this.focusStacks, fStack -> fStack.getStackIndex() == index);
                if (found == null) {
                    Set<BlockPos> relays = altar.nearbyRelays();
                    relays.removeIf(pos -> MiscUtils.contains(this.focusStacks, fs -> fs.getRealPosition().equals(pos)));
                    //No unused relay found for a new focus-stack..
                    if (relays.isEmpty()) {
                        waitMissingInputs = true;
                        continue;
                    }
                    BlockPos at = MiscUtils.getRandomEntry(relays, rand);
                    TileSpectralRelay tar = MiscUtils.getTileAt(altar.getWorld(), at, TileSpectralRelay.class, true);
                    if (tar == null) { // We were lied to. lol.
                        waitMissingInputs = true;
                        continue;
                    }
                    found = new CraftingFocusStack(index, at);
                    this.focusStacks.add(found);
                }
                TileSpectralRelay tar = MiscUtils.getTileAt(altar.getWorld(), found.getRealPosition(), TileSpectralRelay.class, true);
                if (tar == null) {
                    waitMissingInputs = true;
                    continue;
                }
                WrappedIngredient input = iIngredients.get(i);
                if (!input.getIngredient().test(tar.getInventory().getStackInSlot(0))) {
                    waitMissingInputs = true;
                }
            }
        }
        return waitMissingInputs;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public int getTotalCraftingTime() {
        return totalCraftingTime;
    }

    public SimpleAltarRecipe getRecipeToCraft() {
        return recipeToCraft;
    }

    public boolean isFinished() {
        return ticksCrafting >= totalCraftingTime;
    }

    @Nullable
    public static ActiveSimpleAltarRecipe deserialize(CompoundNBT compound, @Nullable ActiveSimpleAltarRecipe previous) {
        RecipeManager mgr = RecipeHelper.getRecipeManager();
        if (mgr == null) {
            return null;
        }

        ResourceLocation recipeKey = new ResourceLocation(compound.getString("recipeToCraft"));
        Optional<?> recipe = mgr.getRecipe(recipeKey);
        if (!recipe.isPresent() || !(recipe.get() instanceof SimpleAltarRecipe)) {
            AstralSorcery.log.info("Recipe with unknown/invalid name found: " + recipeKey);
            return null;
        }
        SimpleAltarRecipe altarRecipe = (SimpleAltarRecipe) recipe.get();
        UUID uuidCraft = compound.getUniqueId("playerCraftingUUID");
        int tick = compound.getInt("ticksCrafting");
        int total = compound.getInt("totalCraftingTime");
        CraftingState state = CraftingState.values()[compound.getInt("state")];
        List<CraftingFocusStack> stacks = new LinkedList<>();
        ListNBT listStacks = compound.getList("focusStacks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listStacks.size(); i++) {
            stacks.add(new CraftingFocusStack(listStacks.getCompound(i)));
        }

        ActiveSimpleAltarRecipe task = new ActiveSimpleAltarRecipe(altarRecipe, uuidCraft);
        task.ticksCrafting = tick;
        task.totalCraftingTime = total;
        task.setState(state);
        task.craftingData = compound.getCompound("craftingData");
        task.focusStacks = stacks;
        task.recoverContainedEffects(previous);
        return task;
    }

    @Nonnull
    public CompoundNBT serialize() {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("recipeToCraft", getRecipeToCraft().getId().toString());
        compound.putUniqueId("playerCraftingUUID", getPlayerCraftingUUID());
        compound.putInt("ticksCrafting", getTicksCrafting());
        compound.putInt("totalCraftingTime", getTotalCraftingTime());
        compound.putInt("state", getState().ordinal());
        compound.put("craftingData", craftingData);

        ListNBT list = new ListNBT();
        for (CraftingFocusStack stack : this.focusStacks) {
            list.add(stack.serialize());
        }
        compound.put("focusStacks", list);
        return compound;
    }

    public static enum CraftingState {

        ACTIVE, //All valid, continuing to craft.

        WAITING //Potentially waiting for user interaction. Recipe itself is fully valid.

    }

}
