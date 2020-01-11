/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base.render;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGatedVisibility
 * Created by HellFirePvP
 * Date: 30.05.2019 / 18:33
 */
public interface ItemGatedVisibility {

    default public PlayerProgress getClientProgress() {
        return ResearchHelper.getClientProgress();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack);

}
