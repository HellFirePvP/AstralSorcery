/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.observer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import hellfirepvp.observerlib.common.util.CodecUtil;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalFilamentProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalFilamentProvider extends ObserverProvider<FocusCrystalFilamentObserver> {

    public static final Codec<FocusCrystalFilamentProvider> PROVIDER_CODEC = RegistryProviders.getRegistry().byNameCodec()
            .xmap(provider -> CodecUtil.informedCast(provider, FocusCrystalFilamentProvider.class), Function.identity());
    public static final MapCodec<FocusCrystalFilamentObserver> OBSERVER_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            PROVIDER_CODEC.fieldOf("provider").forGetter(FocusCrystalFilamentObserver::getProvider),
            SetCodec.of(BlockPos.CODEC).fieldOf("positions").forGetter(FocusCrystalFilamentObserver::getPositions)
    ).apply(builder, (provider, positions) -> new FocusCrystalFilamentObserver(provider).addPositions(positions)));

    @Override
    public MapCodec<FocusCrystalFilamentObserver> codec() {
        return OBSERVER_CODEC;
    }

    @Nonnull
    @Override
    public FocusCrystalFilamentObserver newObserver() {
        return new FocusCrystalFilamentObserver(this);
    }
}
