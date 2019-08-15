/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemComparator;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveSimpleAltarRecipe
 * Created by HellFirePvP
 * Date: 13.08.2019 / 20:59
 */
public class ActiveSimpleAltarRecipe {

    private static final Random rand = new Random();

    //TODO registry altar crafting effects

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

    @Nullable
    public PlayerEntity tryGetCraftingPlayerServer() {
        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        return srv.getPlayerList().getPlayerByUUID(this.getPlayerCraftingUUID());
    }

    public void createItemOutputs(TileAltar altar) {
        for (ItemStack crafted : this.getRecipeToCraft().doItemOutput(altar)) {
            ResearchManager.informCraftedAltar(altar, this, crafted);
        }
    }

    public void consumeInputs(TileAltar altar) {
        TileInventory inv = altar.getInventory();

        for (int slot = 0; slot < AltarRecipeGrid.MAX_INVENTORY_SIZE; slot++) {
            int fSlot = slot;
            decrementItem(inv.getStackInSlot(slot), st -> inv.setStackInSlot(fSlot, st), altar::dropItemOnTop);
        }

        for (CraftingFocusStack input : this.focusStacks) {
            TileSpectralRelay tar = MiscUtils.getTileAt(altar.getWorld(), input.at, TileSpectralRelay.class, true);
            if (tar != null) {
                TileInventory tarInventory = tar.getInventory();
                decrementItem(tarInventory.getStackInSlot(0), st -> tarInventory.setStackInSlot(0, st), altar::dropItemOnTop);
            }
        }
    }

    private void decrementItem(ItemStack consume, Consumer<ItemStack> setIntoInventory, Consumer<ItemStack> dropOtherwise) {
        ItemStack replaceable = ItemStack.EMPTY;
        if (consume.hasContainerItem()) {
            replaceable = consume.getContainerItem();
        }

        if (!consume.isEmpty()) {
            consume.setCount(consume.getCount() - 1);
        }

        if (!replaceable.isEmpty()) {
            if (consume.isEmpty()) {
                setIntoInventory.accept(replaceable);
            } else if (ItemComparator.compare(consume, replaceable, ItemComparator.Clause.Sets.ITEMSTACK_STRICT_NOAMOUNT)) {
                replaceable.grow(consume.getCount());
                setIntoInventory.accept(replaceable);
            } else {
                //Different item, no space left. welp.
                dropOtherwise.accept(replaceable);
            }
        }
    }

    public boolean matches(TileAltar altar) {
        if (!this.getRecipeToCraft().matches(altar)) {
            return false;
        }

        List<Ingredient> listIngredients = this.getRecipeToCraft().getTraitInputIngredients();
        for (CraftingFocusStack stack : this.focusStacks) {
            if (stack.stackIndex >= 0 && stack.stackIndex < listIngredients.size()) {
                TileSpectralRelay relay = MiscUtils.getTileAt(altar.getWorld(), stack.at, TileSpectralRelay.class, true);
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

        List<Ingredient> iIngredients = this.getRecipeToCraft().getTraitInputIngredients();
        //Start the surrounding stack intake past 66%
        int part = totalCraftingTime / 3;
        //Every input is evenly spread
        int cttPart = part / (iIngredients.size()) + 1;

        boolean waitMissingInputs = false;
        for (int i = 0; i < iIngredients.size(); i++) {
            int index = i;

            int offset = part + (index * cttPart);
            if (this.ticksCrafting >= offset) {
                CraftingFocusStack found = MiscUtils.iterativeSearch(this.focusStacks, fStack -> fStack.stackIndex == index);
                if (found == null) {
                    Set<BlockPos> relays = altar.nearbyRelays();
                    relays.removeIf(pos -> MiscUtils.contains(this.focusStacks, fs -> fs.at.equals(pos)));
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
                TileSpectralRelay tar = MiscUtils.getTileAt(altar.getWorld(), found.at, TileSpectralRelay.class, true);
                if (tar != null) {
                    waitMissingInputs = true;
                    continue;
                }
                Ingredient input = iIngredients.get(i);
                if (!input.test(tar.getInventory().getStackInSlot(0))) {
                    waitMissingInputs = true;
                }
            }
        }
        if (waitMissingInputs) {
            return CraftingState.WAITING;
        }

        ticksCrafting++;
        return CraftingState.ACTIVE;
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
        RecipeManager mgr = RecipeTypesAS.TYPE_ALTAR.getRecipeManager();
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
        //task.attemptRecoverEffects(previous); //TODO altar effect recovery
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

    public static class CraftingFocusStack {

        private final int stackIndex;
        private final BlockPos at;

        CraftingFocusStack(int stackIndex, BlockPos at) {
            this.stackIndex = stackIndex;
            this.at = at;
        }

        public CraftingFocusStack(CompoundNBT nbt) {
            this.stackIndex = nbt.getInt("stackIndex");
            this.at = NBTHelper.readBlockPosFromNBT(nbt);
        }

        public CompoundNBT serialize() {
            CompoundNBT nbt = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(this.at, nbt);
            nbt.putInt("stackIndex", this.stackIndex);
            return nbt;
        }

    }
}
