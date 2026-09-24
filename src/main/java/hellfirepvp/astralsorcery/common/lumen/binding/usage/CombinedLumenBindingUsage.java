/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.usage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingUsageTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CombinedLumenBindingUsage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CombinedLumenBindingUsage extends LumenBindingUsage {

    public static final MapCodec<CombinedLumenBindingUsage> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            LumenBindingUsage.CODEC.listOf().fieldOf("usages").forGetter(CombinedLumenBindingUsage::getUsages)
    ).apply(inst, CombinedLumenBindingUsage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CombinedLumenBindingUsage> STREAM_CODEC = StreamCodec.composite(
            LumenBindingUsage.STREAM_CODEC.apply(ByteBufCodecs.list()),
            CombinedLumenBindingUsage::getUsages,
            CombinedLumenBindingUsage::new);

    private final List<LumenBindingUsage> usages = new ArrayList<>();

    private CombinedLumenBindingUsage(List<LumenBindingUsage> usages) {
        super(0, 0F);
        this.usages.addAll(usages);
    }

    public List<LumenBindingUsage> getUsages() {
        return Collections.unmodifiableList(this.usages);
    }

    public static CombinedLumenBindingUsage of(LumenBindingUsage... effects) {
        return new CombinedLumenBindingUsage(List.of(effects));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingUsageTypesAS.COMBINED;
    }
}
