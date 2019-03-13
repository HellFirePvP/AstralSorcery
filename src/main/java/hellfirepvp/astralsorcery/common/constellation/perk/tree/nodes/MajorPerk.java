/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MajorPerk
 * Created by HellFirePvP
 * Date: 17.07.2018 / 18:54
 */
public class MajorPerk extends AttributeModifierPerk {

    public MajorPerk(String name, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_MAJOR);
    }

    @Override
    protected PerkTreePoint<? extends MajorPerk> initPerkTreePoint() {
        return new PerkTreeMajor<>(this, this.getOffset());
    }
}
