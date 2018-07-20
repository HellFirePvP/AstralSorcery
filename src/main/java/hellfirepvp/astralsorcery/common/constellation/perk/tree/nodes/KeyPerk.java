/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.AttributeModifierPerk;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyPerk
 * Created by HellFirePvP
 * Date: 17.07.2018 / 18:24
 */
public abstract class KeyPerk extends AttributeModifierPerk {
    
    public KeyPerk(String name, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_KEY);
    }

    @Override
    public PerkTreePoint getPoint() {
        return new PerkTreeMajor(this, this.getOffset());
    }

}
