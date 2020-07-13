/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.transmutation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.builder.BlockTransmutationBuilder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationRecipeProvider
 * Created by HellFirePvP
 * Date: 08.03.2020 / 09:52
 */
public class BlockTransmutationRecipeProvider {

    public static void registerTransmutationRecipes(Consumer<IFinishedRecipe> registrar) {
        BlockTransmutationBuilder.builder(AstralSorcery.key("iron_starmetal"))
                .multiplyStarlightCost(0.5F)
                .addInputCheck(Blocks.IRON_ORE)
                .setOutput(BlocksAS.STARMETAL_ORE)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("craftingtable_altar"))
                .multiplyStarlightCost(0.6F)
                .addInputCheck(Blocks.CRAFTING_TABLE)
                .setOutput(BlocksAS.ALTAR_DISCOVERY)
                .build(registrar);

        BlockTransmutationBuilder.builder(AstralSorcery.key("pumpkin_cake"))
                .multiplyStarlightCost(2.5F)
                .addInputCheck(Blocks.PUMPKIN)
                .setOutput(Blocks.CAKE)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("magma_obsidian"))
                .multiplyStarlightCost(2F)
                .addInputCheck(Blocks.MAGMA_BLOCK)
                .setOutput(Blocks.OBSIDIAN)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("sand_clay"))
                .addInputCheck(Blocks.SAND)
                .setOutput(Blocks.CLAY)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("diamond_emerald"))
                .multiplyStarlightCost(5F)
                .addInputCheck(Blocks.DIAMOND_ORE)
                .setOutput(Blocks.EMERALD_ORE)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("netherwart_soulsand"))
                .addInputCheck(Blocks.NETHER_WART_BLOCK)
                .setOutput(Blocks.SOUL_SAND)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("sealantern_lapis"))
                .addInputCheck(Blocks.SEA_LANTERN)
                .setOutput(Blocks.LAPIS_BLOCK)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("sandstone_endstone"))
                .addInputCheck(Blocks.SANDSTONE)
                .setOutput(Blocks.END_STONE)
                .build(registrar);
        BlockTransmutationBuilder.builder(AstralSorcery.key("netherrack_netherbrick"))
                .addInputCheck(Blocks.NETHERRACK)
                .setOutput(Blocks.NETHER_BRICKS)
                .build(registrar);
    }
}
