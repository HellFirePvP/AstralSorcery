/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.client.render.entity.layer.StarryLayerRenderer;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeStarryPlayerLayer
 * Created by HellFirePvP
 * Date: 09.01.2021 / 11:01
 */
public class TypeStarryPlayerLayer extends PatreonEffect {

    private final UUID playerUUID;

    public TypeStarryPlayerLayer(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
    }

    @Override
    public void initialize() {
        super.initialize();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            registerPlayerLayer();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void registerPlayerLayer() {
        StarryLayerRenderer.addRender((player, slot) -> this.playerUUID.equals(player.getUniqueID()));
    }
}
