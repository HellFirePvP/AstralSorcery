/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatedRecipe
 * Created by HellFirePvP
 * Date: 28.09.2019 / 15:46
 */
public interface GatedRecipe {

    public boolean hasProgressionServer(PlayerEntity player);

    @OnlyIn(Dist.CLIENT)
    public boolean hasProgressionClient();

    public interface Progression extends GatedRecipe {

        @Nonnull
        ResearchProgression getRequiredProgression();

        default public boolean hasProgressionServer(PlayerEntity player) {
            return ResearchHelper.getProgress(player, LogicalSide.SERVER)
                    .getResearchProgression().contains(getRequiredProgression());
        }

        @OnlyIn(Dist.CLIENT)
        default public boolean hasProgressionClient() {
            return ResearchHelper.getClientProgress().getResearchProgression().contains(getRequiredProgression());
        }

    }
}
