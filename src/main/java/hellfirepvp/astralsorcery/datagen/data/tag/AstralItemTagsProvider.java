/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tag;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;

import static hellfirepvp.astralsorcery.common.lib.TagsAS.Items.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralItemTagsProvider
 * Created by HellFirePvP
 * Date: 06.03.2020 / 21:15
 */
public class AstralItemTagsProvider extends ItemTagsProvider {

    public AstralItemTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        this.tag(CURIOS_NECKLACE)
                .add(ItemsAS.ENCHANTMENT_AMULET);

        this.tag(COLORED_LENS)
                .add(ItemsAS.COLORED_LENS_BREAK)
                .add(ItemsAS.COLORED_LENS_DAMAGE)
                .add(ItemsAS.COLORED_LENS_FIRE)
                .add(ItemsAS.COLORED_LENS_GROWTH)
                .add(ItemsAS.COLORED_LENS_PUSH)
                .add(ItemsAS.COLORED_LENS_REGENERATION)
                .add(ItemsAS.COLORED_LENS_SPECTRAL);
        this.tag(DUSTS_STARDUST)
                .add(ItemsAS.STARDUST);
        this.tag(INGOTS_STARMETAL)
                .add(ItemsAS.STARMETAL_INGOT);
    }

    private Tag.Builder<Item> tag(Tag<Item> tag) {
        return this.getBuilder(tag);
    }
}
