/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.root;

import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootAevitas
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:49
 */
public class RootAevitas extends RootPerk {

    public RootAevitas(ResourceLocation name, int x, int y) {
        super(name, ConstellationsAS.aevitas, x, y);
    }
}
