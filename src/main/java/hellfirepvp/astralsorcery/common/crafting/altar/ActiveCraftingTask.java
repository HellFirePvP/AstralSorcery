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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveCraftingTask
 * Created by HellFirePvP
 * Date: 22.09.2016 / 12:18
 */
public class ActiveCraftingTask {

    private final AbstractAltarRecipe recipeToCraft;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;

    private CraftingState state;
    private NBTTagCompound craftingData = new NBTTagCompound();

    public ActiveCraftingTask(AbstractAltarRecipe recipeToCraft, UUID playerCraftingUUID) {
        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
        this.state = CraftingState.ACTIVE;
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
    public static ActiveCraftingTask deserialize(NBTTagCompound compound) {
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
