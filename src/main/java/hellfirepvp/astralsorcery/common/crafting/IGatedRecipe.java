/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IProgressionGatedRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 14:33
 */
public interface IGatedRecipe {

    public boolean hasProgressionServer(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public boolean hasProgressionClient();

    public interface Progression extends IGatedRecipe {

        @Nonnull
        ResearchProgression getRequiredProgression();

        default public boolean hasProgressionServer(EntityPlayer player) {
            PlayerProgress progress = ResearchManager.getProgress(player);
            return progress != null && progress.getResearchProgression().contains(getRequiredProgression());
        }

        @SideOnly(Side.CLIENT)
        default public boolean hasProgressionClient() {
            return ResearchManager.clientProgress.getResearchProgression().contains(getRequiredProgression());
        }

    }

}
