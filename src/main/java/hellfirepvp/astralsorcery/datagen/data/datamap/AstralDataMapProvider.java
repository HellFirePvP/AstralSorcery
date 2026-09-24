/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.datamap;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralDataMapProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralDataMapProvider extends DataMapProvider {

    public AstralDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Item> items = provider.lookupOrThrow(Registries.ITEM);

        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(TagsAS.Items.INFUSED_WOOD, new FurnaceFuel(300), false)
                .add(item(BlocksAS.INFUSED_WOOD_SLAB), new FurnaceFuel(150), false)
                .add(item(BlocksAS.INFUSED_WOOD_STAIRS), new FurnaceFuel(300), false);
    }

    private static ResourceKey<Item> item(DeferredHolder<Block, ? extends Block> block) {
        return ResourceKey.create(Registries.ITEM, block.getKey().location());
    }
}
