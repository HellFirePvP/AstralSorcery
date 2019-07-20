/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktShootEntity
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:14
 */
public class PktShootEntity extends ASPacket<PktShootEntity> {

    private int entityId = -1;
    private Vector3 motionVector = null;

    private boolean hasEffect = false;
    private float effectLength = 0;

    public PktShootEntity() {}

    public PktShootEntity(int entityId, Vector3 motionVector) {
        this.entityId = entityId;
        this.motionVector = motionVector;
    }

    public PktShootEntity setEffectLength(float length) {
        this.hasEffect = true;
        this.effectLength = length;
        return this;
    }

    @Nonnull
    @Override
    public Encoder<PktShootEntity> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.entityId);
            ByteBufUtils.writeOptional(buffer, packet.motionVector, ByteBufUtils::writeVector);
            buffer.writeBoolean(packet.hasEffect);
            buffer.writeFloat(packet.effectLength);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktShootEntity> decoder() {
        return buffer -> {
            PktShootEntity shootEntity = new PktShootEntity(
                    buffer.readInt(),
                    ByteBufUtils.readOptional(buffer, ByteBufUtils::readVector));
            shootEntity.hasEffect = buffer.readBoolean();
            shootEntity.effectLength = buffer.readFloat();
            return shootEntity;
        };
    }

    @Nonnull
    @Override
    public Handler<PktShootEntity> handler() {
        return new Handler<PktShootEntity>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktShootEntity packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    World world = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT);
                    Entity entity = world.getEntityByID(packet.entityId);
                    if(entity != null) {
                        entity.setMotion(packet.motionVector.toVec3d());
                        if(packet.hasEffect) {
                            Vector3 origin = Vector3.atEntityCenter(entity)
                                    .setY(entity.posY + entity.getHeight());
                            Vector3 look = new Vector3(entity.getLookVec()).normalize().multiply(packet.effectLength * 18);
                            Vector3 motionReverse = look.clone().normalize().multiply(-0.4 * packet.effectLength);

                            Vector3 perp = look.clone().perpendicular();
                            for (int i = 0; i < 500 + rand.nextInt(80); i++) {
                                Vector3 at = look.clone().multiply(0.2 + rand.nextFloat() * 2.5).add(perp.clone().rotate(rand.nextFloat() * 360, look).multiply(rand.nextFloat() * 1.6)).add(origin);

                                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                        .spawn(at)
                                        .setGravityStrength(0.004F)
                                        .alpha(VFXAlphaFunction.FADE_OUT)
                                        .setAlphaMultiplier(1F)
                                        .setScaleMultiplier(0.35F + rand.nextFloat() * 0.2F)
                                        .setMaxAge(10 + rand.nextInt(10));

                                if(rand.nextBoolean()) {
                                    p.color(VFXColorFunction.WHITE)
                                            .setScaleMultiplier(0.1F + rand.nextFloat() * 0.05F);
                                } else {
                                    p.color(VFXColorFunction.constant(ColorsAS.CELESTIAL_CRYSTAL));
                                }
                                if (rand.nextInt(4) != 0) {
                                    p.setMotion(motionReverse);
                                } else {
                                    p.setMotion(new Vector3());
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void handle(PktShootEntity packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
