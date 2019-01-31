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
 * Class: PerkGelu
 * Created by HellFirePvP
 * Date: 20.11.2018 / 21:05
 */
public class PerkGelu extends ConstellationPerk {

    public PerkGelu(int x, int y) {
        super("cst_gelu", Constellations.gelu, x, y);
        setCategory(CATEGORY_FOCUS);
        this.addModifier(new PerkAttributeModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, PerkAttributeModifier.Mode.ADDED_MULTIPLY, 0.03F) {
            @Override
            protected void initModifier() {
                super.initModifier();

                this.setAbsolute();
            }

            @Override
            public float getValue(EntityPlayer player, PlayerProgress progress) {
                return getFlatValue() * progress.getAppliedPerks().size();
            }

            @Override
            public boolean hasDisplayString() {
                return false;
            }
        });
        this.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                if (modifier.getAttributeType().equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT) &&
                        owningPerk != null &&
                        !owningPerk.equals(PerkGelu.this)) {
                    return modifier.convertModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, PerkAttributeModifier.Mode.STACKING_MULTIPLY, 1F);
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
