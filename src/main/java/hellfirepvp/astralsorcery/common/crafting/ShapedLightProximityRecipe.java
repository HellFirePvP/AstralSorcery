/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedLightProximityRecipe
 * Created by HellFirePvP
 * Date: 02.08.2016 / 22:57
 */
public class ShapedLightProximityRecipe extends ShapedOreRecipe {

    public ShapedLightProximityRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    public ShapedLightProximityRecipe(Item result, Object... recipe) {
        super(result, recipe);
    }

    public ShapedLightProximityRecipe(Block result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        if(!super.matches(inv, worldIn)) return false;
        Container c = inv.eventHandler;
        if(!(c instanceof ContainerWorkbench)) return false;
        ContainerWorkbench workbench = (ContainerWorkbench) c;
        BlockPos pos = workbench.pos;
        if(pos == null) return false;
        if(worldIn.isRemote) {
            GuiScreen sc = Minecraft.getMinecraft().currentScreen;
            if(sc == null || !(sc instanceof GuiCrafting)) return false;
            c = ((GuiCrafting) sc).inventorySlots;
            if(c == null || !(c instanceof ContainerWorkbench)) return false;
            pos = ((ContainerWorkbench) c).pos;
            if(!((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS)).doesPositionReceiveStarlightClient(worldIn, pos)) return false;
        } else {
            if(!((DataLightBlockEndpoints) SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS)).doesPositionReceiveStarlightServer(worldIn, pos)) return false;
        }
        return true;
    }

}
