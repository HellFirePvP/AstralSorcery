/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.lib.TagsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryTags
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:08
 */
public class RegistryTags {

    private RegistryTags() {}

    public static void registerTags() {
        DUSTS_STARDUST = makeWrapperTag("stardust");
        DUSTS_STARDUST = makeWrapperTag("starmetal");
    }

    private static Tag<Item> makeWrapperTag(String name) {
        return new ItemTags.Wrapper(new ResourceLocation(AstralSorcery.MODID, name));
    }

}
