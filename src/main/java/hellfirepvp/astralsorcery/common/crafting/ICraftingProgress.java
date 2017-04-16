package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ICraftingProgress
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:21
 */
public interface ICraftingProgress {

    //True if the recipe progressed, false if the recipe should be stuck..
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick);

}
