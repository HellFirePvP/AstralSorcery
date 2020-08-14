/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MajorPerk
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:20
 */
public class MajorPerk extends AttributeModifierPerk {

    public MajorPerk(ResourceLocation name, float x, float y) {
        super(name, x, y);
        this.setCategory(CATEGORY_MAJOR);
    }

    @Override
    protected PerkTreePoint<? extends MajorPerk> initPerkTreePoint() {
        return new PerkTreeMajor<>(this, this.getOffset());
    }
}
