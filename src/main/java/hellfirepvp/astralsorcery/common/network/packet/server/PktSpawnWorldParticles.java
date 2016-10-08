package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSpawnWorldParticles
 * Created by HellFirePvP
 * Date: 07.10.2016 / 13:50
 */
public class PktSpawnWorldParticles implements IMessage, IMessageHandler<PktSpawnWorldParticles, IMessage> {

    private static final Random rand = new Random();

    public int particleId;
    public double posX, posY, posZ;
    public float vX, vY, vZ;
    public int[] args;

    public PktSpawnWorldParticles() {}

    public PktSpawnWorldParticles(int particleId, double posX, double posY, double posZ, float vX, float vY, float vZ, int... args) {
        this.particleId = particleId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.vX = vX;
        this.vY = vY;
        this.vZ = vZ;
        this.args = args;
    }

    public static PktSpawnWorldParticles getWorldParticle(EnumParticleTypes type, double posX, double posY, double posZ, float vX, float vY, float vZ, int... args) {
        return new PktSpawnWorldParticles(type.getParticleID(), posX, posY, posZ, vX, vY, vZ, args);
    }

    public static PktSpawnWorldParticles getRockCrystalParticles(double dstr, BlockPos pos) {
        Color c = BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor;
        return new PktSpawnWorldParticles(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(),
                pos.getX() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4),
                pos.getY() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4),
                pos.getZ() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4),
                rand.nextFloat() * 0.1F,
                rand.nextFloat(),
                rand.nextFloat() * 0.1F,
                c.getRed(), c.getGreen(), c.getBlue(), (int) (150 * dstr), 10);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        particleId = buf.readInt();
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        vX = buf.readFloat();
        vY = buf.readFloat();
        vZ = buf.readFloat();
        int t = buf.readInt();
        args = new int[t];
        for (int i = 0; i < t; i++) {
            args[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleId);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeFloat(vX);
        buf.writeFloat(vY);
        buf.writeFloat(vZ);
        buf.writeInt(args.length);
        for (int arg : args) {
            buf.writeInt(arg);
        }
    }

    @Override
    public IMessage onMessage(PktSpawnWorldParticles message, MessageContext ctx) {
        try {
            spawnParticles(message);
        } catch (Exception exc) {
            AstralSorcery.log.warn("Coudn't process worldparticle packet! pid=" + message.particleId + ", px=" + message.posX + ", py=" + message.posY + ", pz=" + message.posZ);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles(PktSpawnWorldParticles message) {
        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
        EnumParticleTypes type = EnumParticleTypes.getParticleFromId(message.particleId);
        List<Particle> particles = new LinkedList<>();
        int[] prop = message.args;
        int run = 1;
        if(prop.length >= 5) {
            run = Math.max(1, prop[4]);
        }
        for (int i = 0; i < run; i++) {
            float velX = message.vX;
            float velY = message.vY;
            float velZ = message.vZ;
            if(i > 0) {
                velX *= (0.2 + 0.8 * rand.nextFloat());
                velY *= (0.2 + 0.8 * rand.nextFloat());
                velZ *= (0.2 + 0.8 * rand.nextFloat());
            }
            Particle p = pm.spawnEffectParticle(message.particleId, message.posX, message.posY, message.posZ, velX, velY, velZ, prop);
            if(p != null) {
                p.field_190017_n = false;
                particles.add(p);
            }
        }
        if(particles.isEmpty()) return;
        if(type != null && type.getArgumentCount() > 0) return;

        if(prop.length >= 3) {
            float r = ((float) prop[0]) / 255F;
            float g = ((float) prop[1]) / 255F;
            float b = ((float) prop[2]) / 255F;
            for (Particle p : particles) {
                p.setRBGColorF(r, g, b);
            }
        }
        if(prop.length >= 4) {
            float a = ((float) prop[3]) / 255F;
            for (Particle p : particles) {
                p.setAlphaF(a);
            }
        }
    }

}
