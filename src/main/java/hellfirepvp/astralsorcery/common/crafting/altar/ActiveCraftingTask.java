/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.ICraftingProgress;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveCraftingTask
 * Created by HellFirePvP
 * Date: 22.09.2016 / 12:18
 */
public class ActiveCraftingTask {

    private Map<Integer, Object> clientEffectContainer = new HashMap<>();

    private final AbstractAltarRecipe recipeToCraft;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;

    private CraftingState state;
    private NBTTagCompound craftingData = new NBTTagCompound();

    public ActiveCraftingTask(AbstractAltarRecipe recipeToCraft, UUID playerCraftingUUID) {
        Objects.requireNonNull(recipeToCraft);

        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
        this.state = CraftingState.ACTIVE;
    }

    private void attemptRecoverEffects(@Nullable ActiveCraftingTask previous) {
        if (previous != null && previous.recipeToCraft.getUniqueRecipeId() == this.recipeToCraft.getUniqueRecipeId()) {
            this.clientEffectContainer.putAll(previous.clientEffectContainer);
        }
    }

    public CraftingState getState() {
        return state;
    }

    public NBTTagCompound getCraftingData() {
        return craftingData;
    }

    public void setState(CraftingState state) {
        this.state = state;
    }

    public boolean shouldPersist(TileAltar ta) {
        return recipeToCraft instanceof TraitRecipe || ta.getAltarLevel().ordinal() >= TileAltar.AltarLevel.TRAIT_CRAFT.ordinal();
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    @Nullable
    public EntityPlayer tryGetCraftingPlayerServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerCraftingUUID);
    }

    @SideOnly(Side.CLIENT)
    public <T> T getEffectContained(int index, Function<Integer, T> provider) {
        return (T) clientEffectContainer.computeIfAbsent(index, provider);
    }

    //True if the recipe progressed, false if it's stuck
    public boolean tick(TileAltar altar) {
        if(recipeToCraft instanceof ICraftingProgress) {
            if (!((ICraftingProgress) recipeToCraft).tryProcess(altar, this, craftingData, ticksCrafting)) {
                return false;
            }
        }
        ticksCrafting++;
        return true;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public AbstractAltarRecipe getRecipeToCraft() {
        return recipeToCraft;
    }

    public boolean isFinished() {
        return ticksCrafting >= recipeToCraft.craftingTickTime();
    }

    @Nullable
    public static ActiveCraftingTask deserialize(NBTTagCompound compound, @Nullable ActiveCraftingTask previous) {
        int recipeId = compound.getInteger("recipeId");
        AbstractAltarRecipe recipe = AltarRecipeRegistry.getRecipe(recipeId);
        if(recipe == null) {
            AstralSorcery.log.info("[AstralSorcery] Recipe with unknown/invalid ID found: " + recipeId);
            return null;
        } else {
            UUID uuidCraft = compound.getUniqueId("crafterUUID");
            int tick = compound.getInteger("recipeTick");
            CraftingState state = CraftingState.values()[compound.getInteger("craftingState")];
            ActiveCraftingTask task = new ActiveCraftingTask(recipe, uuidCraft);
            task.ticksCrafting = tick;
            task.setState(state);
            task.craftingData = compound.getCompoundTag("craftingData");
            task.attemptRecoverEffects(previous);
            return task;
        }
    }

    @Nonnull
    public NBTTagCompound serialize() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("recipeId", getRecipeToCraft().getUniqueRecipeId());
        compound.setInteger("recipeTick", getTicksCrafting());
        compound.setUniqueId("crafterUUID", getPlayerCraftingUUID());
        compound.setInteger("craftingState", getState().ordinal());
        compound.setTag("craftingData", craftingData);
        return compound;
    }

    public static enum CraftingState {

        ACTIVE, //All valid, continuing to craft.

        WAITING, //Potentially waiting for user interaction. Recipe itself is fully valid.

        PAUSED //Something of the recipe is not valid, waiting with continuation; nothing user-related.

    }

}
