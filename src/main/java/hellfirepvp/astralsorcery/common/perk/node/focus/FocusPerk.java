/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.focus;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.perk.node.ConstellationPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FocusPerk
 * Created by HellFirePvP
 * Date: 27.11.2020 / 18:22
 */
public abstract class FocusPerk extends ConstellationPerk {

    public FocusPerk(ResourceLocation name, IConstellation cst, float x, float y) {
        super(name, cst, x, y);
        this.setCategory(CATEGORY_FOCUS);
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        return super.mayUnlockPerk(progress, player) &&
                !progress.getPerkData().hasPerkEffect(perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }
}
