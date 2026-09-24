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
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
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
 * Class: TomePage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TomePage {

    public static final Codec<TomePage> CODEC = RegistriesAS.REGISTRY_TOME_PAGE_TYPES.byNameCodec()
            .dispatch(TomePage::getType, TomePage.TomePageType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePage> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_TOME_PAGE_TYPES)
            .dispatch(TomePage::getType, TomePage.TomePageType::streamCodec);

    int DEFAULT_WIDTH = 174;
    int DEFAULT_HEIGHT = 220;

    TomePageType<?> getType();

    @OnlyIn(Dist.CLIENT)
    RenderPage createPage(@Nullable ResearchNode node, int page);

    record TomePageType<T extends TomePage>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}
}
