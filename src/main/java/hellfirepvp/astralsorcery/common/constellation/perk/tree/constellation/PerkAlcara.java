/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAlcara
 * Created by HellFirePvP
 * Date: 20.11.2018 / 21:07
 */
public class PerkAlcara extends ConstellationPerk {

    public PerkAlcara(int x, int y) {
        super("cst_alcara", Constellations.alcara, x, y);
        setCategory(CATEGORY_FOCUS);
        this.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                if (modifier.getAttributeType().equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP)) {
                    switch (modifier.getMode()) {
                        case ADDITION:
                        case ADDED_MULTIPLY:
                            return modifier.convertModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, modifier.getMode(), modifier.getValue(player, progress) * 0.5F);
                        case STACKING_MULTIPLY:
                            float val = modifier.getValue(player, progress) - 1;
                            val *= 0.5F;
                            return modifier.convertModifier(AttributeTypeRegistry. ATTR_TYPE_INC_PERK_EFFECT, modifier.getMode(), val + 1F);
                        default:
                            break;
                    }
                }
                return modifier;
            }
        });
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        return super.mayUnlockPerk(progress, player) &&
                !MiscUtils.contains(progress.getAppliedPerks(), perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }

}
