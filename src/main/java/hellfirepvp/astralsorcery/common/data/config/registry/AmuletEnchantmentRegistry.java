/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.AmuletEnchantmentEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantmentRegistry
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:42
 */
public class AmuletEnchantmentRegistry extends ConfigDataAdapter<AmuletEnchantmentEntry> {

    private static final Random rand = new Random();
    public static final AmuletEnchantmentRegistry INSTANCE = new AmuletEnchantmentRegistry();

    private AmuletEnchantmentRegistry() {}

    @Override
    public List<AmuletEnchantmentEntry> getDefaultValues() {
        List<AmuletEnchantmentEntry> enchantments = new LinkedList<>();
        for (Enchantment e : ForgeRegistries.ENCHANTMENTS.getValues()) {
            if (!e.isCurse()) { //Cause fck curses on this.
                enchantments.add(new AmuletEnchantmentEntry(e, e.getRarity().getWeight()));
            }
        }
        return enchantments;
    }

    @Nullable
    public static Enchantment getRandomEnchant() {
        List<AmuletEnchantmentEntry> cfgValues = INSTANCE.getConfiguredValues();
        if (cfgValues.isEmpty()) {
            return null;
        }
        AmuletEnchantmentEntry entry = MiscUtils.getWeightedRandomEntry(cfgValues, rand, AmuletEnchantmentEntry::getWeight);
        if (entry == null) {
            return null;
        }
        return entry.getEnchantment();
    }

    public static boolean canBeInfluenced(Enchantment ench) {
        for (AmuletEnchantmentEntry e : INSTANCE.getConfiguredValues()) {
            if (e.getEnchantment().equals(ench)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSectionName() {
        return "amulet_enchantments";
    }

    @Override
    public String getCommentDescription() {
        return "Defines a whitelist of which enchantments can be rolled and buffed by the enchantment-amulet. The higher the weight, the more likely that roll is selected." +
                "Format: <enchantment-registry-name>;<weight>";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String && AmuletEnchantmentEntry.deserialize((String) obj) != null;
    }

    @Nullable
    @Override
    public AmuletEnchantmentEntry deserialize(String string) throws IllegalArgumentException {
        return AmuletEnchantmentEntry.deserialize(string);
    }
}
