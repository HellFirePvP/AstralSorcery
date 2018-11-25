/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkUlteria
 * Created by HellFirePvP
 * Date: 20.11.2018 / 21:10
 */
public class PerkUlteria extends ConstellationPerk {

    public PerkUlteria(int x, int y) {
        super("cst_ulteria", Constellations.ulteria, x, y);
        setCategory(CATEGORY_FOCUS);

        float perPoint = 0.05F;
        this.addModifier(new PerkAttributeModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, PerkAttributeModifier.Mode.STACKING_MULTIPLY, 1F + perPoint) {
            @Override
            protected void initModifier() {
                super.initModifier();

                this.setAbsolute();
            }

            @Override
            public float getValue(EntityPlayer player, PlayerProgress progress) {
                return 1F + (perPoint * progress.getAvailablePerkPoints(player));
            }

            @Override
            public boolean hasDisplayString() {
                return false;
            }
        });
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        return super.mayUnlockPerk(progress, player) &&
                !MiscUtils.contains(progress.getAppliedPerks(), perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }

}
