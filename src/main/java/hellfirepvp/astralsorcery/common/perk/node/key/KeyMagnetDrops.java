/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import java.util.Iterator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyMagnetDrops
 * Created by HellFirePvP
 * Date: 31.08.2019 / 21:21
 */
public class KeyMagnetDrops extends KeyPerk {

    public KeyMagnetDrops(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Override
    protected void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOWEST, this::onEntityLoot);
    }

    private void onEntityLoot(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                Iterator<ItemEntity> iterator = event.getDrops().iterator();
                while (iterator.hasNext()) {
                    ItemEntity item = iterator.next();
                    ItemStack drop = item.getItem();
                    if (drop.isEmpty()) {
                        continue;
                    }
                    if (player.addItemStackToInventory(drop)) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
