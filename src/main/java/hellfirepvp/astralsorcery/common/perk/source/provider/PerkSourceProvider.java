/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider;

import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkSourceProvider
 * Created by HellFirePvP
 * Date: 01.04.2020 / 19:32
 */
public class PerkSourceProvider extends ModifierSourceProvider<AbstractPerk> {

    public PerkSourceProvider() {
        super(ModifierManager.PERK_PROVIDER_KEY);
    }

    @Override
    protected void update(ServerPlayerEntity playerEntity) {}

    @Override
    public void serialize(AbstractPerk source, PacketBuffer buf) {
        ByteBufUtils.writeRegistryEntry(buf, source);
    }

    @Override
    public AbstractPerk deserialize(PacketBuffer buf) {
        return ByteBufUtils.readRegistryEntry(buf);
    }
}
