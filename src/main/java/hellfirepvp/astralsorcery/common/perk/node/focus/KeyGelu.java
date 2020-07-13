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
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.ConstellationPerk;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyGelu
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:35
 */
public class KeyGelu extends ConstellationPerk {

    public KeyGelu(ResourceLocation name, int x, int y) {
        super(name, ConstellationsAS.gelu, x, y);
        setCategory(CATEGORY_FOCUS);
        this.addModifier(new PerkAttributeModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ModifierType.ADDED_MULTIPLY, 0.03F) {
            @Override
            protected void initModifier() {
                super.initModifier();

                this.setAbsolute();
            }

            @Override
            public float getValue(PlayerEntity player, PlayerProgress progress) {
                return getRawValue() * progress.getAppliedPerks().size();
            }

            @Override
            public boolean hasDisplayString() {
                return false;
            }
        });
        this.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                if (modifier.getAttributeType().equals(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT) &&
                        owningSource != null && !owningSource.equals(KeyGelu.this)) {
                    return modifier.convertModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ModifierType.STACKING_MULTIPLY, 1F);
                }
                return modifier;
            }
        });
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        return super.mayUnlockPerk(progress, player) &&
                !MiscUtils.contains(progress.getAppliedPerks(), perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }
}
