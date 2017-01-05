/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveInfusionTask
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:19
 */
public class ActiveInfusionTask {

    private final AbstractInfusionRecipe recipeToCraft;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;

    public ActiveInfusionTask(AbstractInfusionRecipe recipeToCraft, UUID playerCraftingUUID) {
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

    public void tick(TileStarlightInfuser infuser) {
        ticksCrafting++;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public AbstractInfusionRecipe getRecipeToCraft() {
        return recipeToCraft;
    }

    public boolean isFinished() {
        return ticksCrafting >= recipeToCraft.craftingTickTime();
    }

    public void forceTick(int tick) {
        this.ticksCrafting = tick;
    }

}
