/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider;

import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkSourceProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkSourceProvider extends ModifierSourceProvider<AbstractPerk<?>> {

    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractPerk<?>> STREAM_CODEC = StreamCodec.of(
            (buf, perk) -> buf.writeResourceLocation(perk.getKey()),
            buf -> PerkTree.getInstance().getPerk(LogicalSide.CLIENT, buf.readResourceLocation()).orElseThrow()
    );

    @Override
    protected void update(ServerPlayer playerEntity) {}

    @Override
    protected void removeModifiers(ServerPlayer playerEntity) {}

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractPerk<?>> getModifierSourceSyncCodec() {
        return STREAM_CODEC;
    }
}
