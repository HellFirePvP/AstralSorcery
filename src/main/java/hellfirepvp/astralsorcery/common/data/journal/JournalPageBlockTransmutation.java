/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageBlockTransmutation;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageText;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageBlockTransmutation
 * Created by HellFirePvP
 * Date: 28.05.2020 / 22:39
 */
public class JournalPageBlockTransmutation implements JournalPage {

    private final Supplier<BlockTransmutation> recipeProvider;

    private JournalPageBlockTransmutation(Supplier<BlockTransmutation> recipeProvider) {
        this.recipeProvider = recipeProvider;
    }

    public static JournalPageBlockTransmutation fromOutput(Predicate<ItemStack> outputTest) {
        return new JournalPageBlockTransmutation(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            return mgr.getRecipes(RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getType()).values()
                    .stream()
                    .map(r -> (BlockTransmutation) r)
                    .filter(r -> outputTest.test(r.getOutputDisplay()))
                    .findFirst()
                    .orElse(null);
        });
    }

    @Override
    public RenderablePage buildRenderPage(ResearchNode node, int page) {
        BlockTransmutation recipe = this.recipeProvider.get();
        if (recipe != null) {
            return new RenderPageBlockTransmutation(node, page, recipe);
        } else {
            return new RenderPageText("astralsorcery.journal.recipe.removalinfo");
        }
    }
}
