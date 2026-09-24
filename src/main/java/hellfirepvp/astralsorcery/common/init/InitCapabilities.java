/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.init;

import hellfirepvp.astralsorcery.common.item.wand.ArchitectWandItem;
import hellfirepvp.astralsorcery.common.item.wand.BlinkWandItem;
import hellfirepvp.astralsorcery.common.item.wand.ExchangeWandItem;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InitCapabilities
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InitCapabilities {

    public static void init(RegisterCapabilitiesEvent event) {
        // -------------------- BLOCKS/TILES --------------------
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.FOCUS_RELAY.type(),
                tileInventory(TileFocusRelay.Data::getInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.ALTAR.type(),
                tileInventory(TileAltar.Data::getAltarInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.LUMEN_ARRAY.type(),
                tileInventory(TileLumenArray.Data::getInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.LUMEN_ALCHEMY_ARRAY.type(),
                tileInventory(TileLumenArray.Data::getInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.LUMEN_CRYSTALLIZER.type(),
                tileInventory(TileLumenCrystallizer.Data::getInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.LIGHTWELL.type(),
                tileInventory(TileLightwell.Data::getInventory));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileEntitiesAS.INFUSER.type(),
                tileInventory(TileInfuser.Data::getInventory));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TileEntitiesAS.LUMEN_ARRAY.type(),
                fluidHandler(TileLumenArray.Data::getFluidTank));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TileEntitiesAS.LUMEN_ALCHEMY_ARRAY.type(),
                fluidHandler(TileLumenArray.Data::getFluidTank));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TileEntitiesAS.LUMEN_CRYSTALLIZER.type(),
                fluidHandler(TileLumenCrystallizer.Data::getFluidTank));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TileEntitiesAS.LIGHTWELL.type(),
                fluidHandler(TileLightwell.Data::getFluidTank));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TileEntitiesAS.CHALICE.type(),
                fluidHandler(TileChalice.Data::getFluidTank));

        event.registerBlockEntity(ILumenHandler.BLOCK, TileEntitiesAS.LUMEN_ARRAY.type(),
                lumenHandler(TileLumenArray.Data::getLumenHandler));
        event.registerBlockEntity(ILumenHandler.BLOCK, TileEntitiesAS.LUMEN_ALCHEMY_ARRAY.type(),
                lumenHandler(TileLumenArray.Data::getLumenHandler));
        event.registerBlockEntity(ILumenHandler.BLOCK, TileEntitiesAS.LUMEN_CRYSTALLIZER.type(),
                lumenHandler(TileLumenCrystallizer.Data::getLumenHandler));
        event.registerBlockEntity(ILumenHandler.BLOCK, TileEntitiesAS.TREE_BEACON.type(),
                lumenHandler(TileTreeBeacon.Data::getLumenHandler));

        // -------------------- ITEMS --------------------
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new FluidBucketWrapper(stack),
                FluidsAS.LIQUID_STARLIGHT.getBucket());
    }

    private static <O extends TileEntitySynchronized.Data> ICapabilityProvider<TileEntitySynchronized<?>, Direction, IItemHandler> tileInventory(Function<O, InventoryView> invFn) {
        return (tile, dir) -> invFn.apply(MiscUtil.cast(tile.getTileData())).getInventoryAccess(dir);
    }

    private static <O extends TileEntitySynchronized.Data> ICapabilityProvider<TileEntitySynchronized<?>, Direction, IFluidHandler> fluidHandler(Function<O, FluidTankView> handlerFn) {
        return (tile, dir) -> handlerFn.apply(MiscUtil.cast(tile.getTileData())).getFluidAccess(dir);
    }

    private static <O extends TileEntitySynchronized.Data> ICapabilityProvider<TileEntitySynchronized<?>, Direction, ILumenHandler> lumenHandler(Function<O, LumenHandlerView> handlerFn) {
        return (tile, dir) -> handlerFn.apply(MiscUtil.cast(tile.getTileData())).getLumenAccess(dir);
    }
}
