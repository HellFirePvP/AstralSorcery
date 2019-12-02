package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;

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
}
