package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveCraftingTask
 * Created by HellFirePvP
 * Date: 22.09.2016 / 12:18
 */
public class ActiveCraftingTask {

    private final AbstractAltarRecipe recipeToCraft;
    private int ticksCrafting = 0;

    public ActiveCraftingTask(AbstractAltarRecipe recipeToCraft) {
        this.recipeToCraft = recipeToCraft;
    }

    public void tick(TileAltar altar) {
        ticksCrafting++;

        recipeToCraft.onCraftServerTick(altar, ticksCrafting);
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
}
