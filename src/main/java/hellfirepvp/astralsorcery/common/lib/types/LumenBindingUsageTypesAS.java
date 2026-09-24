/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingUsageTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingUsageTypesAS {

    public static final DeferredRegister<LumenBindingUsage.Type<?>> LUMEN_BINDING_USAGE_TYPES_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_LUMEN_BINDING_USAGE_TYPES, AstralSorcery.MODID);

    public static final LumenBindingUsage.DeferredType<LumenBindingUsage> NONE =
            register("none", MapCodec.unit(LumenBindingUsage.NONE), StreamCodec.unit(LumenBindingUsage.NONE));
    public static final LumenBindingUsage.DeferredType<CombinedLumenBindingUsage> COMBINED =
            register("combined", CombinedLumenBindingUsage.CODEC, CombinedLumenBindingUsage.STREAM_CODEC);
    public static final LumenBindingUsage.DeferredType<LumenBindingUsageBlockBreak> BLOCK_BREAK =
            register("block_break", LumenBindingUsageBlockBreak.CODEC, LumenBindingUsageBlockBreak.STREAM_CODEC);
    public static final LumenBindingUsage.DeferredType<LumenBindingUsageDamageDealt> DAMAGE_DEALT =
            register("damage_dealt", LumenBindingUsageDamageDealt.CODEC, LumenBindingUsageDamageDealt.STREAM_CODEC);
    public static final LumenBindingUsage.DeferredType<LumenBindingUsageDamageTaken> DAMAGE_TAKEN =
            register("damage_taken", LumenBindingUsageDamageTaken.CODEC, LumenBindingUsageDamageTaken.STREAM_CODEC);
    public static final LumenBindingUsage.DeferredType<LumenBindingUsageMovement> MOVEMENT =
            register("movement", LumenBindingUsageMovement.CODEC, LumenBindingUsageMovement.STREAM_CODEC);
    public static final LumenBindingUsage.DeferredType<LumenBindingUsageHealthRecovered> HEALTH_RECOVERED =
            register("health_recovered", LumenBindingUsageHealthRecovered.CODEC, LumenBindingUsageHealthRecovered.STREAM_CODEC);

    private static <T extends LumenBindingUsage> LumenBindingUsage.DeferredType<T> register(String name,
                                                                                            MapCodec<T> codec,
                                                                                            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new LumenBindingUsage.DeferredType<>(LUMEN_BINDING_USAGE_TYPES_REGISTER.register(name,
                () -> new LumenBindingUsage.Type<>(codec, streamCodec)));
    }
}
