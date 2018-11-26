/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyVoidTrash
 * Created by HellFirePvP
 * Date: 24.11.2018 / 22:22
 */
public class KeyVoidTrash extends KeyPerk {

    private static final Random rand = new Random();
    private static String[] defaultDropList = new String[] {
            "minecraft:stone:0",
            "minecraft:dirt",
            "minecraft:cobblestone",
            "minecraft:gravel"
    };
    private static List<Predicate<ItemStack>> dropFilter = Lists.newArrayList();
    private static float chanceOre = 0.0002F;

    public KeyVoidTrash(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                dropFilter.clear();

                String[] drops = cfg.getStringList("DropList", getConfigurationSection(), defaultDropList,
                        "The list of items to delete when dropped by a player with this perk. " +
                                "Damage/metadata value is optional and 'any' damage value is matched if omitted. " +
                                "Format: <modid>:<name>(:<metadata>)");
                chanceOre = cfg.getFloat("DropRareInstead", getConfigurationSection(), chanceOre,
                        0F, 1F, "Chance that a voided drop will instead yield a " +
                                "valuable random ore out of the 'perk_void_trash_replacement' configured ore table.");

                for (String s : drops) {
                    String[] split = s.split(":");
                    if (split.length == 3) {
                        int dmg;
                        try {
                            dmg = Integer.parseInt(split[2]);
                        } catch (Exception ex) {
                            continue;
                        }
                        ResourceLocation key = new ResourceLocation(split[0], split[1]);
                        dropFilter.add(i -> i.getItem().getRegistryName().equals(key) && i.getItemDamage() == dmg);
                    } else if (split.length == 2) {
                        ResourceLocation key = new ResourceLocation(s);
                        dropFilter.add(i -> i.getItem().getRegistryName().equals(key));
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onDrops(BlockEvent.HarvestDropsEvent ev) {
        World world = ev.getWorld();
        if (world.isRemote) {
            return;
        }

        EntityPlayer player = ev.getHarvester();
        if (player == null) {
            return;
        }

        PlayerProgress prog = ResearchManager.getProgress(player, Side.SERVER);
        if (prog == null || !prog.hasPerkEffect(this)) {
            return;
        }

        float chance = PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceOre);
        List<ItemStack> drops = ev.getDrops();
        List<ItemStack> addedDrops = Lists.newArrayList();
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.isEmpty()) {
                continue;
            }
            if (MiscUtils.matchesAny(stack, dropFilter)) {
                iterator.remove();

                if (rand.nextFloat() < chance) {
                    ItemStack drop = OreTypes.PERK_VOID_TRASH_REPLACEMENT.getRandomOre(rand);
                    if (!drop.isEmpty()) {
                        addedDrops.add(ItemUtils.copyStackWithSize(drop, 1));
                    }
                }
            }
        }
        drops.addAll(addedDrops);
    }

}
