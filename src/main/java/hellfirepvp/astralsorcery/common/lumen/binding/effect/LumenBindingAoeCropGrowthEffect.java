/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.CropUtil;
import hellfirepvp.astralsorcery.common.visual.type.SinglePlantGrowthEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingAoeCropGrowthEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingAoeCropGrowthEffect extends LumenBindingEffect {

    private static final int RIPENING_RADIUS = 3;

    public static final LumenBindingAoeCropGrowthEffect INSTANCE = new LumenBindingAoeCropGrowthEffect();
    public static final MapCodec<LumenBindingAoeCropGrowthEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingAoeCropGrowthEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, LumenBindingAoeCropGrowthEffect::onBlockBreak);
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer sPlayer)) return;
        ServerLevel sLevel = sPlayer.serverLevel();

        RandomSource rand = sLevel.getRandom();
        BlockPos brokenPos = event.getPos();
        CropUtil.wrapPlant(sLevel, brokenPos).filter(plant -> plant.canHarvest(sLevel)).ifPresent(brokenPlant -> {
            forEachEffect(sPlayer, LumenBindingAoeCropGrowthEffect.class, (stack, effect) -> {

                BlockPos.betweenClosed(
                        brokenPos.offset(-RIPENING_RADIUS, -1, -RIPENING_RADIUS),
                        brokenPos.offset(RIPENING_RADIUS, 1, RIPENING_RADIUS)
                ).forEach(pos -> {
                    if (pos.equals(brokenPos)) return;
                    CropUtil.wrapPlant(sLevel, pos.immutable()).ifPresent(plant -> {
                        plant.tryGrow(sLevel, rand);
                        SinglePlantGrowthEffect.at(plant.getPos()).sendToNearby(sLevel);
                    });
                });
            });
        });
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.aoe_crop_growth"));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.CHRONO_RIPENING;
    }
}
