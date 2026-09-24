/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemCustom
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemCustom extends Item implements CreativeTabItem {

    protected final RandomSource rand = RandomSource.create();

    public ItemCustom(Properties properties) {
        super(properties);
    }
}
