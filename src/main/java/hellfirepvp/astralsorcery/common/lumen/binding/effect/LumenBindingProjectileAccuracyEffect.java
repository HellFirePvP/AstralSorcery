/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.event.ProjectileInaccuracyEvent;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingProjectileAccuracyEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingProjectileAccuracyEffect extends LumenBindingEffect {

    public static final LumenBindingProjectileAccuracyEffect INSTANCE = new LumenBindingProjectileAccuracyEffect();
    public static final MapCodec<LumenBindingProjectileAccuracyEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingProjectileAccuracyEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingProjectileAccuracyEffect::onProjectileAccuracy);
    }

    private static void onProjectileAccuracy(ProjectileInaccuracyEvent event) {
        if (event.getProjectile().getOwner() instanceof LivingEntity shooter) {
            forEachEffect(shooter, LumenBindingProjectileAccuracyEffect.class, (stack, effect) -> {
                event.setInaccuracy(0F);
            });
        }
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.projectile_accuracy"));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.PROJECTILE_ACCURACY;
    }
}
