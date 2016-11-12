package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectAra;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectFertilitas;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectFornax;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectHorologium;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectOrion;
import hellfirepvp.astralsorcery.common.entities.EntityItemStardust;
import hellfirepvp.astralsorcery.common.item.tool.ItemWand;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktParticleEvent
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:15
 */
public class PktParticleEvent implements IMessage, IMessageHandler<PktParticleEvent, IMessage> {

    private int typeOrdinal;
    private double xCoord, yCoord, zCoord;

    public PktParticleEvent() {}

    public PktParticleEvent(ParticleEventType type, Vec3i vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, Vector3 vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, double x, double y, double z) {
        this.typeOrdinal = type.ordinal();
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readInt();
        this.xCoord = buf.readDouble();
        this.yCoord = buf.readDouble();
        this.zCoord = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.typeOrdinal);
        buf.writeDouble(this.xCoord);
        buf.writeDouble(this.yCoord);
        buf.writeDouble(this.zCoord);
    }

    @Override
    public IMessage onMessage(PktParticleEvent message, MessageContext ctx) {
        try {
            ParticleEventType type = ParticleEventType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if(trigger != null) {
                trigger.trigger(message);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("Error executing ParticleEventType " + message.typeOrdinal + " at " + xCoord + ", " + yCoord + ", " + zCoord);
        }
        return null;
    }

    public Vector3 getVec() {
        return new Vector3(xCoord, yCoord, zCoord);
    }

    public static enum ParticleEventType {

        //DEFINE EVENT TRIGGER IN THE FCKING HUGE SWITCH STATEMENT DOWN TEHRE.
        COLLECTOR_BURST,
        CELESTIAL_CRYSTAL_BURST,
        CELESTIAL_CRYSTAL_FORM,
        CRAFT_FINISH_BURST,
        STARMETAL_ORE_CHARGE,
        WELL_CATALYST_BREAK,
        WAND_CRYSTAL_HIGHLIGHT,

        CE_CROP_INTERACT,
        CE_MELT_BLOCK,
        CE_ACCEL_TILE,
        CE_DMG_ENTITY,
        CE_WATER_FISH,
        CE_TREE_VORTEX,

        DEBUG;

        //GOD I HATE THIS PART
        //But i can't do this in the ctor because server-client stuffs.
        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(ParticleEventType type) {
            switch (type) {
                case COLLECTOR_BURST:
                    return TileCollectorCrystal::breakParticles;
                case CELESTIAL_CRYSTAL_BURST:
                    return TileCelestialCrystals::breakParticles;
                case CELESTIAL_CRYSTAL_FORM:
                    return EntityItemStardust::spawnFormationParticles;
                case CRAFT_FINISH_BURST:
                    return TileAltar::finishBurst;
                case STARMETAL_ORE_CHARGE:
                    return BlockCustomOre::playStarmetalOreEffects;
                case WELL_CATALYST_BREAK:
                    return TileWell::catalystBurst;
                case WAND_CRYSTAL_HIGHLIGHT:
                    return ItemWand::highlightEffects;
                case CE_CROP_INTERACT:
                    return CEffectFertilitas::playParticles;
                case CE_MELT_BLOCK:
                    return CEffectFornax::playParticles;
                case CE_ACCEL_TILE:
                    return CEffectHorologium::playParticles;
                case CE_DMG_ENTITY:
                    return CEffectOrion::playParticles;
                case CE_WATER_FISH:
                    return CEffectOctans::playParticles;
                case CE_TREE_VORTEX:
                    return CEffectAra::playParticles;
                case DEBUG:
                    return RaytraceAssist::playDebug;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if(!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktParticleEvent event);

    }

}
