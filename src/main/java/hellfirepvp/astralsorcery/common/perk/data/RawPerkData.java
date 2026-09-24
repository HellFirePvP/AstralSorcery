/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgressFullAccess;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;

import java.util.Collections;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RawPerkData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record RawPerkData(AbstractPerk<?> perk, Set<ResourceLocation> connections) {

    public static final Codec<RawPerkData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AbstractPerk.DATAPACK_CODEC.fieldOf("perk").forGetter(RawPerkData::perk),
            SetCodec.of(ResourceLocation.CODEC).fieldOf("connections").forGetter(RawPerkData::connections)
    ).apply(inst, RawPerkData::new));

    @Override
    public Set<ResourceLocation> connections() {
        return Collections.unmodifiableSet(this.connections);
    }
}
