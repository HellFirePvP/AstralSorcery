/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageEmpty;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageText;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageText
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageText(String textKey) implements TomePage {

    public static final MapCodec<TomePageText> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("textKey").forGetter(TomePageText::textKey)
    ).apply(inst, TomePageText::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageText> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TomePageText::textKey,
            TomePageText::new);

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.TEXT_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        return new RenderPageText(node, page, this.textKey());
    }
}
