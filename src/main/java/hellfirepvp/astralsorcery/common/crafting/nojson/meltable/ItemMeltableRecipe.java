/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.meltable;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicates;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemMeltableRecipe
 * Created by HellFirePvP
 * Date: 29.11.2019 / 23:01
 */
public class ItemMeltableRecipe extends WorldMeltableRecipe {

    private final BiFunction<WorldBlockPos, BlockState, ItemStack> outputGenerator;

    public ItemMeltableRecipe(ResourceLocation key, BlockPredicate matcher, ItemStack output) {
        this(key, matcher, (worldPos, state) -> ItemUtils.copyStackWithSize(output, output.getCount()));
    }

    public ItemMeltableRecipe(ResourceLocation key, BlockPredicate matcher, BiFunction<WorldBlockPos, BlockState, ItemStack> outputGenerator) {
        super(key, matcher);
        this.outputGenerator = outputGenerator;
    }

    public static ItemMeltableRecipe of(BlockState stateIn, ItemStack itemOut) {
        return new ItemMeltableRecipe(AstralSorcery.key(stateIn.getBlock().getRegistryName().getPath()),
                BlockPredicates.isState(stateIn), itemOut);
    }

    public static ItemMeltableRecipe of(Tag<Block> blockTagIn, ItemStack itemOut) {
        return new ItemMeltableRecipe(AstralSorcery.key(String.format("tag_%s", blockTagIn.getId().getPath())),
                BlockPredicates.isInTag(blockTagIn), itemOut);
    }

    @Override
    public void doOutput(World world, BlockPos pos, BlockState state, Consumer<ItemStack> itemOutput) {
        if (world.removeBlock(pos, false)) {
            ItemStack generated = this.outputGenerator.apply(WorldBlockPos.wrapServer(world, pos), state);
            if (!generated.isEmpty()) {
                itemOutput.accept(generated);
            }
        }
    }
}
