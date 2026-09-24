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
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageStructure;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageStructure(ObserverProvider<?> structureObserver, int structureIndex) implements TomePage {

    public static final MapCodec<TomePageStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryProviders.getRegistry().byNameCodec().fieldOf("structureObserver").forGetter(TomePageStructure::structureObserver),
            Codec.INT.fieldOf("structureIndex").forGetter(TomePageStructure::structureIndex)
    ).apply(inst, TomePageStructure::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageStructure> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegistryProviders.REGISTRY_KEY),
            TomePageStructure::structureObserver,
            ByteBufCodecs.INT,
            TomePageStructure::structureIndex,
            TomePageStructure::new);

    public static TomePageStructure of(ObserverRegistryObject structureObject) {
        return of(structureObject, 0);
    }

    public static TomePageStructure of(ObserverRegistryObject structureObject, int structureIndex) {
        return new TomePageStructure(structureObject.observer().get(), structureIndex);
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.STRUCTURE_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        List<StructureBlockArray> structures = ObserverRegistryObject.extractRequiredStructures(this.structureObserver());
        if (structures.isEmpty()) return TomePageEmpty.getInstance().createPage(node, page);
        if (this.structureIndex() < 0 || this.structureIndex() >= structures.size()) return TomePageEmpty.getInstance().createPage(node, page);
        return Optional.ofNullable(structures.get(this.structureIndex()))
                .map(structure -> (RenderPage) new RenderPageStructure(node, page, structure))
                .orElse(TomePageEmpty.getInstance().createPage(node, page));
    }
}
