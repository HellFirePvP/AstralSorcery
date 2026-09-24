/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkDataType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.key.KeyPerkTreeConnector;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketPerk;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkDataTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkDataTypesAS {

    public static final DeferredRegister<PerkDataType<?>> PERK_DATA_TYPE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_DATA_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<PerkDataType<?>, PerkDataType<AbstractPerk.Data>> DEFAULT_DATA =
            PERK_DATA_TYPE_REGISTER.register("default", () ->
                    new PerkDataType<>(AbstractPerk.Data.CODEC, AbstractPerk.Data.SYNC_CODEC, AbstractPerk.Data::create));
    public static final DeferredHolder<PerkDataType<?>, PerkDataType<GemSocketPerk.Data>> GEM_SOCKET_DATA =
            PERK_DATA_TYPE_REGISTER.register("gem_socket", () ->
                    new PerkDataType<>(GemSocketPerk.Data.CODEC, GemSocketPerk.Data.SYNC_CODEC, GemSocketPerk.Data::create));
    public static final DeferredHolder<PerkDataType<?>, PerkDataType<KeyPerkTreeConnector.Data>> KEY_TREE_CONNECTOR_DATA =
            PERK_DATA_TYPE_REGISTER.register("key_tree_connector", () ->
                    new PerkDataType<>(KeyPerkTreeConnector.Data.CODEC, KeyPerkTreeConnector.Data.SYNC_CODEC, KeyPerkTreeConnector.Data::create));
}
