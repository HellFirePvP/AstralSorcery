/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.focus;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.ConstellationPerk;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyUlteria
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:37
 */
public class KeyUlteria extends ConstellationPerk {

    public KeyUlteria(ResourceLocation name, int x, int y) {
        super(name, ConstellationsAS.ulteria, x, y);
        setCategory(CATEGORY_FOCUS);

        float perPoint = 0.05F;
        this.addModifier(new PerkAttributeModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP, ModifierType.STACKING_MULTIPLY, 1F + perPoint) {
            @Override
            protected void initModifier() {
                super.initModifier();

                this.setAbsolute();
            }

            @Override
            public float getValue(PlayerEntity player, PlayerProgress progress) {
                return 1F + (perPoint * progress.getAvailablePerkPoints(player));
            }

            @Override
            public boolean hasDisplayString() {
                return false;
            }
        });
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        return super.mayUnlockPerk(progress, player) &&
                !MiscUtils.contains(progress.getAppliedPerks(), perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }
}
