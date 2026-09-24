/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon.entity.client;

import hellfirepvp.astralsorcery.common.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonPartialClientEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonPartialClientEntity extends PatreonPartialEntity {

    public PatreonPartialClientEntity(UUID effectUUID, UUID ownerUUID) {
        super(effectUUID, ownerUUID);
    }

    public void tickEffects(Level level) {}

    @Override
    public boolean tick(Level level) {
        boolean changed = super.tick(level);

        float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null && this.getPosition().distanceSquared(camera) < renderDistance * renderDistance) {
            this.tickEffects(level);
        }

        return changed;
    }
}
