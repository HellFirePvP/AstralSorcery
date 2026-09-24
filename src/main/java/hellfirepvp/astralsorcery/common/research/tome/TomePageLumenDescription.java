/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageLumenDescription;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageLumenDescription
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageLumenDescription(Lumen lumen, List<LumenBindingType.SlotType> slots) implements TomePage {

    public static final MapCodec<TomePageLumenDescription> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistriesAS.REGISTRY_LUMEN.byNameCodec().fieldOf("lumen").forGetter(TomePageLumenDescription::lumen),
            StringRepresentable.fromEnum(LumenBindingType.SlotType::values).listOf().fieldOf("slots").forGetter(TomePageLumenDescription::slots)
    ).apply(inst, TomePageLumenDescription::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageLumenDescription> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN),
            TomePageLumenDescription::lumen,
            CodecUtil.enumStreamCodec(LumenBindingType.SlotType.class).apply(ByteBufCodecs.list()),
            TomePageLumenDescription::slots,
            TomePageLumenDescription::new);

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        return new RenderPageLumenDescription(node, page, this.lumen(), this.slots());
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.LUMEN_DESCRIPTION_PAGE.get();
    }
}
