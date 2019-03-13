/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePointConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 11.11.2018 / 10:00
 */
public abstract class ConstellationPerk extends AttributeModifierPerk {

    private IConstellation constellation;

    public ConstellationPerk(String name, IConstellation cst, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_KEY);
        this.constellation = cst;
    }

    @Override
    protected PerkTreePoint<? extends ConstellationPerk> initPerkTreePoint() {
        return new PerkTreePointConstellation<>(this, getOffset(),
                this.constellation, PerkTreePointConstellation.MINOR_SPRITE_SIZE);
    }

    public IConstellation getConstellation() {
        return constellation;
    }

}
