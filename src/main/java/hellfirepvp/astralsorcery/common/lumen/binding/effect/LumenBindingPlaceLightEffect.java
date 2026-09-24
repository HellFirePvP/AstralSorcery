/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingPlaceLightEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingPlaceLightEffect extends LumenBindingEffect {

    public static final LumenBindingPlaceLightEffect INSTANCE = new LumenBindingPlaceLightEffect();
    public static final MapCodec<LumenBindingPlaceLightEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingPlaceLightEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, LumenBindingPlaceLightEffect::onBlockBreak);
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) return;
        int light = level.getRawBrightness(event.getPos(), 0);
        if (light >= 2) return;

        forEachEffect(event.getPlayer(), LumenBindingPlaceLightEffect.class, (stack, effect) -> {
            level.setBlock(event.getPos(), BlocksAS.FLARE_LIGHT.get().defaultBlockState(), Block.UPDATE_ALL);
        });
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.place_light"));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.PLACE_LIGHT;
    }
}
