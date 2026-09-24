/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenStackList;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.tank.FluidContainerList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenAlchemyArray
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenAlchemyArray extends TileLumenArray {

    public TileLumenAlchemyArray(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.LUMEN_ALCHEMY_ARRAY, pos, blockState);
    }

    protected TileLumenAlchemyArray(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Codec<TileLumenArray.Data> dataCodec() {
        return MiscUtil.cast(Data.CODEC);
    }

    public static class Data extends TileLumenArray.Data {

        public static final Codec<TileLumenAlchemyArray.Data> CODEC = RecordCodecBuilder.create(inst -> lumenArrayFields(inst).apply(inst, Data::new));

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, Optional<UUID> ownerId, LumenStackList lumenContents, FluidContainerList fluidContents, InventoryStackList inventoryContents, boolean allowExtendedLumenTransfer, Lumen assignedLumen) {
            super(ticksExisted, hasStructure, skyObstructions, ownerId, lumenContents, fluidContents, inventoryContents, allowExtendedLumenTransfer, assignedLumen);
        }

        @Override
        protected Optional<RecipeHolder<LumenGenerationRecipe>> findMatchingRecipe(ItemStack stack) {
            return RecipeFinder.of()
                    .flatMap(finder -> finder.findLumenGenerationRecipe(stack, this.getAssignedLumen(), true));
        }
    }
}
