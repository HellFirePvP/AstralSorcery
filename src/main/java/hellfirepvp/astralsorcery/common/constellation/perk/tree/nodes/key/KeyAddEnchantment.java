/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAddEnchantment
 * Created by HellFirePvP
 * Date: 23.11.2018 / 20:43
 */
public class KeyAddEnchantment extends KeyPerk {

    private List<DynamicEnchantment> enchantments = Lists.newArrayList();

    public KeyAddEnchantment(String name, int x, int y) {
        super(name, x, y);
    }

    public KeyAddEnchantment addEnchantment(Enchantment ench, int level) {
        return addEnchantment(DynamicEnchantment.Type.ADD_TO_SPECIFIC, ench, level);
    }

    public KeyAddEnchantment addEnchantment(DynamicEnchantment.Type type, Enchantment ench, int level) {
        this.enchantments.add(new DynamicEnchantment(type, ench, level));
        return this;
    }

    @SubscribeEvent
    public void onEnchantmentAdd(DynamicEnchantmentEvent.Add event) {
        EntityPlayer player = event.getResolvedPlayer();
        if (player != null) {
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                List<DynamicEnchantment> listedEnchantments = event.getEnchantmentsToApply();
                for (DynamicEnchantment ench : this.enchantments) {
                    DynamicEnchantment added = MiscUtils.iterativeSearch(listedEnchantments, e ->
                            (e.getEnchantment() == null ? ench.getEnchantment() == null : e.getEnchantment().equals(ench.getEnchantment())) &&
                            e.getType().equals(ench.getType()));
                    if (added != null) {
                        added.setLevelAddition(added.getLevelAddition() + ench.getLevelAddition());
                    } else {
                        listedEnchantments.add(ench);
                    }
                }
            }
        }
    }

}
