/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.builder;

import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 17:02
 */
public class BlockTransmutationBuilder extends CustomRecipeBuilder<BlockTransmutation> {

    private BlockTransmutationBuilder() {}

    public static BlockTransmutationBuilder builder() {
        return new BlockTransmutationBuilder();
    }

    @Nonnull
    @Override
    protected BlockTransmutation validateAndGet() {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    protected CustomRecipeSerializer<BlockTransmutation> getSerializer() {
        return RecipeSerializersAS.BLOCK_TRANSMUTATION_SERIALIZER;
    }
}
