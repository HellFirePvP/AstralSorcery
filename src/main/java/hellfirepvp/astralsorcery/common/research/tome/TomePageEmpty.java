/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageEmpty;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageEmpty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class TomePageEmpty implements TomePage {

    public static final MapCodec<TomePageEmpty> CODEC = MapCodec.unit(TomePageEmpty::getInstance);
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageEmpty> STREAM_CODEC = StreamCodec.unit(TomePageEmpty.getInstance());

    private static final TomePageEmpty INSTANCE = new TomePageEmpty();

    private TomePageEmpty() {}

    public static TomePageEmpty getInstance() {
        return INSTANCE;
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.EMPTY_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        return new RenderPageEmpty(node, page);
    }
}
