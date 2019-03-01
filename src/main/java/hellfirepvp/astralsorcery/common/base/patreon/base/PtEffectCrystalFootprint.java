/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.base;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystal;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.EnumSet;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectCrystalFootprint
 * Created by HellFirePvP
 * Date: 01.03.2019 / 17:37
 */
public class PtEffectCrystalFootprint extends PatreonEffectHelper.PatreonEffect implements ITickHandler {

    private final UUID playerUUID;
    private Color color;

    public PtEffectCrystalFootprint(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor, UUID playerUUID, Color footprintColor) {
        super(uniqueId, chosenColor);
        this.playerUUID = playerUUID;
        this.color = footprintColor;
    }

    @Override
    public void initialize() {
        super.initialize();

        TickManager.getInstance().register(this);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer player = (EntityPlayer) context[0];
        Side side = (Side) context[1];
        if (side == Side.CLIENT &&
                player != null &&
                player.getUniqueID().equals(playerUUID) &&
                player.onGround) {

            //spawnDust(player);

            if (player.ticksExisted % 1 == 0 &&
                    rand.nextBoolean()) {
                spawnFootprint(player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnDust(EntityPlayer player) {
        Vector3 pos = Vector3.atEntityCorner(player)
                .subtract(player.width / 2,
                        0,
                        player.width / 2)
                .add(player.width * rand.nextFloat(),
                        0.01,
                        player.width * rand.nextFloat());

        float scale = rand.nextFloat() * 0.3F + 0.25F;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos);
        p.setColor(this.color).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.scale(scale);
        p.gravity(0.004);
        p.setMaxAge(45 + rand.nextInt(30));
    }

    @SideOnly(Side.CLIENT)
    private void spawnFootprint(EntityPlayer player) {
        Vector3 pos = Vector3.atEntityCorner(player)
                .subtract(player.width / 2,
                        0.1,
                        player.width / 2)
                .add(player.width * rand.nextFloat(),
                        0,
                        player.width * rand.nextFloat());

        EntityFXCrystal crystal = new EntityFXCrystal(pos.getX(), pos.getY(), pos.getZ());
        crystal.rotation(
                rand.nextFloat() * 25F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 25F * (rand.nextBoolean() ? 1 : -1),
                180 + rand.nextFloat() * 25F * (rand.nextBoolean() ? 1 : -1));
        crystal.setColor(this.color);
        crystal.setMaxAge(70 + rand.nextInt(40));
        crystal.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        crystal.scale(0.025F + rand.nextFloat() * 0.03F);
        EffectHandler.getInstance().registerFX(crystal);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon - Crystal footprints " + playerUUID.toString();
    }
}
