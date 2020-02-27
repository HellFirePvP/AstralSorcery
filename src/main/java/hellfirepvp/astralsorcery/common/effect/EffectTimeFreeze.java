/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.util.time.TimeStopController;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectTimeFreeze
 * Created by HellFirePvP
 * Date: 26.08.2019 / 20:09
 */
public class EffectTimeFreeze extends EffectCustomTexture {

    public EffectTimeFreeze() {
        super(EffectType.HARMFUL, ColorsAS.EFFECT_TIME_FREEZE);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>(0);
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.addListener(EventPriority.HIGHEST, this::onLivingTick);
        bus.addListener(this::onDestroy);
    }

    private void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        if (TimeStopController.skipLivingTick(event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    private void onDestroy(LivingDestroyBlockEvent event) {
        if (event.getEntityLiving().isPotionActive(EffectsAS.EFFECT_TIME_FREEZE)) {
            event.setCanceled(true);
        }
    }

    @Override
    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.GUI, 1, 1, "effect", "time_freeze");
    }
}
