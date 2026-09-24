/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class CreativeTabBucketItem extends BucketItem implements CreativeTabItem {

    public CreativeTabBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }
}
