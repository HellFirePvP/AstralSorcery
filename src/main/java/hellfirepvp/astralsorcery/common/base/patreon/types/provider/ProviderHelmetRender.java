/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types.provider;

import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeHelmetRender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProviderHelmetRender
 * Created by HellFirePvP
 * Date: 05.04.2020 / 11:37
 */
public class ProviderHelmetRender implements PatreonEffectProvider<TypeHelmetRender> {

    @Override
    public TypeHelmetRender buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception {
        UUID effectUniqueId = UUID.fromString(effectParameters.get(0));

        if (effectParameters.get(1).equals("astralsorcery:blockaltar;3")) {
            effectParameters.set(1, "astralsorcery:altar_radiance");
        }

        String[] itemInfo = effectParameters.get(1).split(";");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemInfo[0]));
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("Unknown item: " + itemInfo[0]);
        }
        ItemStack stack = new ItemStack(item);
        if (itemInfo.length > 1) {
            int data = Integer.parseInt(itemInfo[1]);
            stack.setDamage(data);
        }
        FlareColor flColor = effectParameters.size() > 2 ?
                FlareColor.valueOf(effectParameters.get(2)) : null;
        return new TypeHelmetRender(effectUniqueId, flColor, playerUUID, stack);
    }
}
