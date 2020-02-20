/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffect
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:13
 */
public abstract class MantleEffect extends ForgeRegistryEntry<MantleEffect> implements ITickHandler {

    protected static final Random rand = new Random();

    private final IWeakConstellation constellation;

    public MantleEffect(IWeakConstellation constellation) {
        this.constellation = constellation;
        this.setRegistryName(this.constellation.getRegistryName());
    }

    public final IWeakConstellation getAssociatedConstellation() {
        return this.constellation;
    }

    protected void init() {}

    protected void attachEventListeners(IEventBus bus) {}

    protected void attachTickHandlers(Consumer<ITickHandler> registrar) {
        if (this.usesTickMethods()) {
            registrar.accept(this);
        }
    }

    protected void tickServer(PlayerEntity player, boolean hasMantle) {}

    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player, boolean hasMantle) {}

    protected boolean usesTickMethods() {
        return false;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity pl = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];
        boolean hasMantle = ItemMantle.getEffect(pl, this.getAssociatedConstellation()) != null;

        if (side.isServer()) {
            if (!(pl instanceof ServerPlayerEntity) || MiscUtils.isPlayerFakeMP((ServerPlayerEntity) pl)) {
                return;
            }
            this.tickServer(pl, hasMantle);
        } else {
            this.tickClient(pl, hasMantle);
        }
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
        return this.getClass().getName();
    }

    public static class Config extends ConfigEntry {

        private final boolean defaultEnabled = true;

        public ForgeConfigSpec.BooleanValue enabled;

        public Config(String constellationName) {
            super(String.format("constellation.mantle.%s", constellationName));
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.enabled = cfgBuilder
                    .comment("Set this to false to disable this mantle effect")
                    .translation(translationKey("enabled"))
                    .define("enabled", this.defaultEnabled);
        }
    }
}
