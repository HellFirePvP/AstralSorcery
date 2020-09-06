/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityClientReplacement
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:13
 */
public class EntityClientReplacement extends AbstractClientPlayerEntity {

    public EntityClientReplacement() {
        super(Minecraft.getInstance().world, Minecraft.getInstance().player.getGameProfile());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isWearing(PlayerModelPart part) {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.isWearing(part);
    }
}
