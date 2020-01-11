/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:12
 */
public class KeyPerk extends MajorPerk {

    public KeyPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.setCategory(CATEGORY_KEY);
    }
}
