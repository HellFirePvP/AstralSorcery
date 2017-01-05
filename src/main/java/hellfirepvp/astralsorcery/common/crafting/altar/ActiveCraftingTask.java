/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

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

    public ActiveCraftingTask(AbstractAltarRecipe recipeToCraft, UUID playerCraftingUUID) {
        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    @Nullable
    public EntityPlayer tryGetCraftingPlayerServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerCraftingUUID);
    }

    public void tick(TileAltar altar) {
        ticksCrafting++;
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

    public void forceTick(int tick) {
        this.ticksCrafting = tick;
    }
}
