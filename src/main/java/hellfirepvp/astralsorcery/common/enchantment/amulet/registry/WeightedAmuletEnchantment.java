/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WeightedAmuletEnchantment
 * Created by HellFirePvP
 * Date: 27.01.2018 / 17:44
 */
public class WeightedAmuletEnchantment extends WeightedRandom.Item implements ConfigDataAdapter.DataSet {

    private final Enchantment enchantment;

    public WeightedAmuletEnchantment(Enchantment ench, int weight) {
        super(weight);
        this.enchantment = ench;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.enchantment.getRegistryName().toString() + ":" + itemWeight;
    }

    @Nullable
    public static WeightedAmuletEnchantment deserialize(String str) {
        String[] spl = str.split(":");
        if(spl.length < 3) {
            return null;
        }
        String domain = spl[0];
        String weight = spl[spl.length - 1];
        StringBuilder path = new StringBuilder();
        for (int i = 1; i < spl.length - 1; i++) {
            if(path.length() > 0) {
                path.append(":"); //Cause that vanishes when splitting...
            }
            path.append(spl[i]);
        }
        Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(domain, path.toString()));
        if(ench == null) {
            AstralSorcery.log.info("[AstralSorcery] Ignoring whitelist entry " + str + " for amulet enchantments - Enchantment does not exist!");
            return null;
        }
        int w;
        try {
            w = Integer.parseInt(weight);
        } catch (NumberFormatException exc) {
            AstralSorcery.log.info("[AstralSorcery] Ignoring whitelist entry " + str + " for amulet enchantments - last :-separated argument is not a number!");
            return null;
        }
        return new WeightedAmuletEnchantment(ench, w);
    }

}
