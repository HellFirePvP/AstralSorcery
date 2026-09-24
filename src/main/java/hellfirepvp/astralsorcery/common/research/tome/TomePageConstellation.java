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
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageConstellation;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageConstellationDetail;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageText;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
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
 * Class: TomePageConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageConstellation(BaseConstellation constellation) implements TomePage {

    public static final MapCodec<TomePageConstellation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("structureObserver").forGetter(TomePageConstellation::constellation)
    ).apply(inst, TomePageConstellation::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageConstellation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS),
            TomePageConstellation::constellation,
            TomePageConstellation::new);

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.CONSTELLATION_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        if (page % 2 != 0) {
            return new RenderPageConstellationDetail(node, page, this.constellation);
        }
        return new RenderPageConstellation(node, page, this.constellation);
    }
}
