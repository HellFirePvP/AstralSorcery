/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.base.sets.OreEntry;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreTypes
 * Created by HellFirePvP
 * Date: 03.11.2016 / 01:16
 */
public class OreTypes implements ConfigDataAdapter<OreEntry> {

    public static final OreTypes RITUAL_MINERALIS = new OreTypes("mineralis_ritual");
    public static final OreTypes AEVITAS_ORE_PERK = new OreTypes("aevitas_ore_perk");
    public static final OreTypes TREASURE_SHRINE_GEN = new OreTypes("treasure_shrine");

    private List<OreEntry> oreDictWeights = new LinkedList<>();
    private double totalWeight = 0D;
    private final String name;

    private OreTypes(String name) {
        this.name = name;
    }

    public Iterable<OreEntry> getDefaultDataSets() {
        List<OreEntry> entries = new LinkedList<>();

        entries.add(new OreEntry("oreCoal",        5200));
        entries.add(new OreEntry("oreIron",        2500));
        entries.add(new OreEntry("oreGold",         550));
        entries.add(new OreEntry("oreLapis",        140));
        entries.add(new OreEntry("oreRedstone",     700));
        entries.add(new OreEntry("oreDiamond",      180));
        entries.add(new OreEntry("oreEmerald",      100));

        entries.add(new OreEntry("oreAluminum",     600));
        entries.add(new OreEntry("oreCopper",      1100));
        entries.add(new OreEntry("oreTin",         1500));
        entries.add(new OreEntry("oreLead",        1000));
        entries.add(new OreEntry("oreCertusQuartz", 500));
        entries.add(new OreEntry("oreNickel",       270));
        entries.add(new OreEntry("orePlatinum",      90));
        entries.add(new OreEntry("oreSilver",       180));
        entries.add(new OreEntry("oreMithril",        1));
        entries.add(new OreEntry("oreRuby",         400));
        entries.add(new OreEntry("oreSapphire",     400));
        entries.add(new OreEntry("oreUranium",      550));
        entries.add(new OreEntry("oreYellorite",    560));
        entries.add(new OreEntry("oreZinc",         300));
        entries.add(new OreEntry("oreSulfur",       600));
        entries.add(new OreEntry("oreOsmium",       950));

        return entries;
    }

    private void appendOreEntry(OreEntry entry) {
        oreDictWeights.add(entry);
        totalWeight += entry.weight;
    }

    @Nonnull
    public ItemStack getRandomOre(Random random) {
        ItemStack result = ItemStack.EMPTY;
        int runs = 0;
        while (result.isEmpty() && runs < 150) {

            String key = null;
            double randWeight = random.nextFloat() * totalWeight;
            for (OreEntry entry : oreDictWeights) {
                randWeight -= entry.weight;
                if(randWeight <= 0) {
                    key = entry.oreName;
                    break;
                }
            }
            if(key == null) {
                runs++;
                continue;
            }
            NonNullList<ItemStack> ores = OreDictionary.getOres(key);

            for (ItemStack stack : ores) {
                if(stack.isEmpty() || Block.getBlockFromItem(stack.getItem()) == Blocks.AIR) continue;
                Item i = stack.getItem();
                String regModid = i.getRegistryName().getResourceDomain();
                if(Config.modidOreGenBlacklist.contains(regModid)) continue;

                String className = i.getClass().getName();
                if(!className.toLowerCase().contains("greg")) {
                    if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) stack.setItemDamage(0);
                    result = stack;
                }
            }
            runs++;
        }

        return result;
    }

    @Override
    public String getDataFileName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Defines random ore-selection data. Items with higher weight are more likely to be selected overall. Format: <oreDictionaryName>;<integerWeight>";
    }

    @Nullable
    @Override
    public Optional<OreEntry> appendDataSet(String str) {
        OreEntry entry = OreEntry.deserialize(str);
        if(entry == null) {
            return null;
        }
        appendOreEntry(entry);
        return Optional.of(entry);
    }

}
