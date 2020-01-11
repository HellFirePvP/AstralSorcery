/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:31
 */
public class ConstellationPerk extends AttributeModifierPerk {

    private IConstellation constellation;

    public ConstellationPerk(ResourceLocation name, IConstellation cst, int x, int y) {
        super(name, x, y);
        this.constellation = cst;
    }

    @Override
    protected PerkTreePoint<? extends ConstellationPerk> initPerkTreePoint() {
        return new PerkTreeConstellation<>(this, getOffset(),
                this.constellation, PerkTreeConstellation.MINOR_SPRITE_SIZE);
    }

    public IConstellation getConstellation() {
        return constellation;
    }
}
