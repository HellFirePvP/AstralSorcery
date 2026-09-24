/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.attunement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.effect.function.offset.RenderOffsetNoisePlane;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.camera.CameraManager;
import hellfirepvp.astralsorcery.client.util.camera.CameraTransformerPlayerFocus;
import hellfirepvp.astralsorcery.client.util.camera.RevertableCameraTransformer;
import hellfirepvp.astralsorcery.client.util.camera.path.CameraPathBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.property.AttunePlayerProperty;
import hellfirepvp.astralsorcery.common.event.helper.InvulnerabilityHelper;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerAttunementRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerAttunementRecipe extends AttunementRecipe<PlayerAttunementRecipe.Active> {

    public static final ResourceLocation ID = AstralSorcery.key("player_attunement");
    public static final PlayerAttunementRecipe INSTANCE = new PlayerAttunementRecipe();

    private PlayerAttunementRecipe() {
        super(ID);
    }

    @Override
    public boolean canStartCrafting(TileAttunementAltar altar) {
        return DayTimeHelper.isNight(altar.getLevel()) && findEligiblePlayer(altar).isPresent();
    }

    @Override
    public Active createActiveRecipe(TileAttunementAltar altar) {
        return altar.getTileData().getActiveConstellation().flatMap(cst -> {
            return findEligiblePlayer(altar).map(player -> {
                return new Active(cst, player.getUUID());
            });
        }).orElse(null);
    }

    @Override
    public MapCodec<Active> codec() {
        return Active.CODEC;
    }

    public static Optional<ServerPlayer> findEligiblePlayer(TileAttunementAltar altar) {
        BaseConstellation cst = altar.getTileData().getActiveConstellation().orElse(null);
        if (cst == null) return Optional.empty();
        return AttunePlayerProperty.getRootPerk(cst, LogicalSide.SERVER)
                .map(root -> {
                    Vector3 tileVec = Vector3.atCenter(altar).addY(0.5);
                    Player player = altar.getLevel().getNearestPlayer(tileVec.getX(), tileVec.getY(), tileVec.getZ(), 1, false);
                    if (player instanceof ServerPlayer sPlayer) {
                        if (isEligiblePlayer(sPlayer, cst)) {
                            return sPlayer;
                        }
                    }
                    return null;
                });
    }

    public static boolean isEligiblePlayer(ServerPlayer player, BaseConstellation attuneTo) {
        if (player != null && player.isAlive() && !MiscUtil.isPlayerFake(player) && !player.isShiftKeyDown()) {
            PlayerProgress prog = ResearchManager.getProgress(player, LogicalSide.SERVER);

            return prog.isValid() &&
                    attuneTo instanceof BaseConstellation &&
                    !prog.isAttuned() &&
                    prog.getTierReached().isThisLaterOrEqual(ResearchTier.ILLUMINATION) &&
                    prog.hasDiscoveredConstellation(attuneTo);
        }
        return false;
    }

    public static class Active extends AttunementRecipe.Active<PlayerAttunementRecipe, Active> {

        private static final int DURATION_PLAYER_ATTUNEMENT = 800; //Duration of the player's camera flight
        public static final MapCodec<Active> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("tick").forGetter(AttunementRecipe.Active::getTick),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("constellation").forGetter(AttunementRecipe.Active::getConstellation),
                CodecUtil.uuidCodec().fieldOf("player_uuid").forGetter(Active::getPlayerUUID)
        ).apply(inst, Active::new));

        private final UUID playerUUID;

        private final ClientObject<CameraTransformerPlayerFocus> cameraFlight = new ClientObject<>();
        private final ClientObject<PlayableSoundInstance> attunementSound = new ClientObject<>();
        private final List<ClientObject<RenderOffsetNoisePlane>> playerNoisePlanes = new ArrayList<>();

        protected Active(BaseConstellation constellation, UUID playerUUID) {
            super(INSTANCE, constellation);
            this.playerUUID = playerUUID;
        }

        protected Active(int tick, BaseConstellation constellation, UUID playerUUID) {
            super(INSTANCE, tick, constellation);
            this.playerUUID = playerUUID;
        }

        @Override
        public boolean matches(TileAttunementAltar altar) {
            if (!super.matches(altar)) {
                return false;
            }
            Player player = altar.getLevel().getPlayerByUUID(this.playerUUID);
            return player != null && player.isAlive();
        }

        @Override
        public void startCrafting(TileAttunementAltar altar) {
            Player player = altar.getLevel().getPlayerByUUID(this.playerUUID);
            if (player == null) return;

            Vector3 offset = Vector3.atCenter(altar.getBlockPos()).addY(0.7);
            player.absMoveTo(offset.getX(), offset.getY(), offset.getZ(), 0F, 0F);
            player.absMoveTo(offset.getX(), offset.getY(), offset.getZ(), 0F, 0F);
        }

        @Override
        public void stopCrafting(TileAttunementAltar altar) {

        }

        @Override
        public void finishRecipe(TileAttunementAltar altar) {
            Player player = altar.getLevel().getPlayerByUUID(this.playerUUID);
            if (player instanceof ServerPlayer sPlayer) {
                ResearchHelper.attuneConstellation(sPlayer, this.getConstellation());
            }
        }

        @Override
        public void doTick(LogicalSide side, TileAttunementAltar altar) {
            if (side.isServer()) {
                Player player = altar.getLevel().getPlayerByUUID(this.playerUUID);
                if (player != null) {
                    InvulnerabilityHelper.setInvulnerable(player);
                }
            } else {
                altar.getTileData().getActiveConstellation().ifPresent(cst -> {
                    this.ensureNoisePlanes();
                    this.ensureCameraFlight(altar.getBlockPos());
                    this.spawnActiveEffects(altar, cst);
                    this.tickPlaySound(altar);
                });
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void ensureNoisePlanes() {
            if (this.playerNoisePlanes.isEmpty()) {
                this.playerNoisePlanes.add(new ClientObject<>(new RenderOffsetNoisePlane(1.0F)));
                this.playerNoisePlanes.add(new ClientObject<>(new RenderOffsetNoisePlane(1.4F)));
                this.playerNoisePlanes.add(new ClientObject<>(new RenderOffsetNoisePlane(1.8F)));
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void ensureCameraFlight(BlockPos altarPos) {
            if (this.cameraFlight.isNull() &&
                    Minecraft.getInstance().player != null &&
                    Minecraft.getInstance().player.getUUID().equals(this.playerUUID)) {
                this.cameraFlight.set(CameraFlight.createCameraFlight(altarPos));
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void spawnActiveEffects(TileAttunementAltar altar, BaseConstellation cst) {
            Vector3 playerTarget = Vector3.atCenter(altar).addY(2);
            TileAttunementAltar.AttunementConstellationFinder finder = new TileAttunementAltar.AttunementConstellationFinder(Minecraft.getInstance().level, altar.getBlockPos());
            FXColorFunction<?> beamColor = FXColorFunction.constant(ColorsAS.ATTUNEMENT_ALTAR_BEAM);
            int tick = this.getTick();

            if (tick % 40 == 0) {
                finder.getConstellationPositions(cst).forEach(relayPos -> {
                    Vector3 from = Vector3.atBottomCenter(relayPos);
                    from = VectorUtil.withRandomOffset(from, rand, 0.15F);

                    EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                            .spawn(from)
                            .setup(from.copy().addY(6), 1.2F, 1.2F)
                            .color(beamColor)
                            .setAlpha(0.8F)
                            .setMaxAge(60);
                });
            }

            float scale = 7.0F;
            float edgeScale = (scale * 2 + 1);
            for (int i = 0; i < 7; i++) {
                Vector3 offset = new Vector3(altar).add(-scale, 0.1F, -scale);
                if (rand.nextBoolean()) {
                    offset.add(edgeScale * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * edgeScale);
                } else {
                    offset.add(rand.nextFloat() * edgeScale, 0, edgeScale * (rand.nextBoolean() ? 1 : 0));
                }

                EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .color(FXColorFunction.WHITE)
                        .setScale(0.3F + rand.nextFloat() * 0.15F)
                        .setGravity(Vector3.y(0.0002F + rand.nextFloat() * 0.0001F))
                        .setMaxAge(40 + rand.nextInt(10));
                if (rand.nextBoolean()) {
                    fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                }
            }

            for (int i = 0; i < 5; i++) {
                Set<BlockPos> offsets = finder.getConstellationPositions(cst);
                BlockPos pos = MiscUtil.getRandomEntry(offsets, rand).orElseThrow();

                if (tick <= 380) {
                    Vector3 offset = Vector3.atBottomCenter(pos)
                            .add(Vector3.random(rand).setY(0).multiply(0.6F));

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(offset)
                            .color(FXColorFunction.WHITE)
                            .setAlpha(0.6F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setScale(0.3F + rand.nextFloat() * 0.15F)
                            .setMotion(Vector3.random(rand).addY(4).normalize().multiply(0.015F + rand.nextFloat() * 0.01F))
                            .setGravity(Vector3.y(0.0006F + rand.nextFloat() * 0.003F))
                            .setMaxAge(60 + rand.nextInt(20));
                } else {
                    Vector3 offset = Vector3.atBottomCenter(pos)
                            .add(Vector3.random(rand).setY(0).multiply(0.5F));

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(offset)
                            .color(FXColorFunction.WHITE)
                            .alpha(FXAlphaFunction.proximity(playerTarget::copy, 3F))
                            .setAlpha(0.6F)
                            .setScale(0.2F + rand.nextFloat() * 0.1F)
                            .setMotion(Vector3.y(0.2F + rand.nextFloat() * 0.15F))
                            .motion(FXMotionFunction.target(playerTarget::copy, 0.08F))
                            .setMaxAge(60 + rand.nextInt(20));

                    offset = Vector3.atBottomCenter(pos)
                            .add(Vector3.random(rand).setY(0).multiply(0.6F));

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(offset)
                            .color(FXColorFunction.WHITE)
                            .setAlpha(0.8F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setScale(0.3F + rand.nextFloat() * 0.15F)
                            .setMotion(Vector3.random(rand).addY(4).normalize().multiply(0.02F + rand.nextFloat() * 0.01F))
                            .setGravity(Vector3.y(0.0007F + rand.nextFloat() * 0.005F))
                            .setMaxAge(40 + rand.nextInt(10));
                }
            }

            if (tick >= 220) {
                Vector3 offset = Vector3.atBottomCenter(altar)
                        .add(Vector3.random(rand).setY(0));

                EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .color(FXColorFunction.WHITE)
                        .setAlpha(1F)
                        .alpha(FXAlphaFunction.proximity(playerTarget::copy, 3F))
                        .setScale(0.2F + rand.nextFloat() * 0.1F)
                        .setMotion(Vector3.positiveYRandom(rand).setY(1).normalize().multiply(0.5F + rand.nextFloat() * 0.1F))
                        .motion(FXMotionFunction.target(playerTarget::copy, 0.1F))
                        .setMaxAge(60 + rand.nextInt(20));

                if (rand.nextBoolean()) {
                    fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                }

                for (int i = 0; i < 3; i++) {
                    Vector3 at = Vector3.atBottomCenter(altar)
                            .addX(rand.nextFloat() * 7F * (rand.nextBoolean() ? 1 : -1))
                            .addZ(rand.nextFloat() * 7F * (rand.nextBoolean() ? 1 : -1));

                    fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(at)
                            .setAlpha(0.75F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .color(FXColorFunction.WHITE)
                            .setScale(0.3F + rand.nextFloat() * 0.1F)
                            .setGravity(Vector3.y(0.001F + rand.nextInt() * 0.0005F))
                            .setMaxAge(20 + rand.nextInt(10));

                    if (rand.nextBoolean()) {
                        fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                    }
                    if (tick >= 500) {
                        fx.setScale(0.3F + rand.nextFloat() * 0.15F);
                    }
                }
            }

            if (tick >= 400) {
                int amount = tick >= 500 ? 4 : 1;
                for (int i = 0; i < amount; i++) {
                    RenderOffsetNoisePlane plane = MiscUtil.getRandomEntry(this.playerNoisePlanes, rand)
                            .map(ClientObject::get).orElseThrow();

                    EntityVisualFX fx = plane.createParticle(playerTarget.copy())
                            .setAlpha(0.6F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setScale(0.2F + rand.nextFloat() * 0.15F)
                            .setMotion(Vector3.random(rand).setY(0).multiply(rand.nextFloat() * 0.01F))
                            .setMaxAge(50 + rand.nextInt(20));

                    if (rand.nextInt(3) == 0) {
                        fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                    }
                }
            }

            if (tick >= 600) {
                if (tick % 10 == 0) {
                    Vector3 from = Vector3.atBottomCenter(altar);
                    from = VectorUtil.withRandomOffset(from, rand, 0.25F);

                    EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                            .spawn(from)
                            .setup(from.copy().addY(8), 2.4F, 1.8F)
                            .setAlpha(0.8F)
                            .setMaxAge(40 + rand.nextInt(20));
                }
            }

            if (tick >= (DURATION_PLAYER_ATTUNEMENT - 5)) {
                for (int i = 0; i < 60; i++) {
                    Vector3 at = Vector3.atBottomCenter(altar)
                            .addY(rand.nextFloat() * 15);

                    EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(at)
                            .color(FXColorFunction.WHITE)
                            .setAlpha(0.7F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setScale(0.3F + rand.nextFloat() * 0.15F)
                            .setMotion(Vector3.random(rand).setY(0).normalize().multiply(0.03F + rand.nextFloat() * 0.01F))
                            .setMaxAge(140 + rand.nextInt(60));

                    if (rand.nextBoolean()) {
                        fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                    }
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void tickPlaySound(TileAttunementAltar altar) {
            if (this.attunementSound.isNull()) {
                PlayableSoundInstance attuneSound = PlayableSoundInstance.of(SoundsAS.ATTUNEMENT_ALTAR_PLAYER_ATTUNE)
                        .pos(Vector3.atCenter(altar))
                        .volume(0.5F)
                        .loop(true)
                        .stopFunction(sound -> {
                            return !altar.canPlayConstellationEffects(altar.getLevel()) ||
                                    altar.getTileData().getActiveRecipe().isEmpty() ||
                                    altar.getTileData().getActiveRecipe().get().getRecipe() != PlayerAttunementRecipe.INSTANCE;
                        })
                        .fadeInTicks(20)
                        .fadeOutTicks(80)
                        .play();
                this.attunementSound.set(attuneSound);
            }
        }

        @Override
        public boolean isFinished(TileAttunementAltar altar) {
            return this.getTick() >= DURATION_PLAYER_ATTUNEMENT;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void stopEffects(TileAttunementAltar altar) {
            this.cameraFlight.ifPresent(transformer -> {
                CameraManager.getInstance().removeTransformer(transformer);
            });
        }

        public UUID getPlayerUUID() {
            return this.playerUUID;
        }

        @Override
        public void copyEffectDataTo(Active other) {
            super.copyEffectDataTo(other);

            other.cameraFlight.set(this.cameraFlight.get());
            other.attunementSound.set(this.attunementSound.get());
            other.playerNoisePlanes.clear();
            other.playerNoisePlanes.addAll(this.playerNoisePlanes);
        }

        private static class CameraFlight {

            @OnlyIn(Dist.CLIENT)
            private static CameraTransformerPlayerFocus createCameraFlight(BlockPos altarPos) {
                Vector3 altar = Vector3.atCenter(altarPos);
                Vector3 cameraOffset = altar.copy().addY(5);
                Vector3 cameraStart = cameraOffset.copy().add(5, 0, 5);
                CameraPathBuilder builder = CameraPathBuilder.builder(cameraStart, altar);
                builder.addCircularPoints(cameraOffset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease(5, 0.025), 200, 2);
                builder.addCircularPoints(cameraOffset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease(10, -0.01), 200, 2);
                builder.setTickDelegate(createTick(Vector3.atCenter(altarPos).addY(1)));

                return builder.finishAndStart();
            }

            @OnlyIn(Dist.CLIENT)
            private static Runnable createTick(Vector3 offset) {
                return () -> {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) return;

                    float floatTick = (ClientProxy.getClientTick() % 40) / 40F;
                    float sin = Mth.sin((float) (floatTick * 2 * Math.PI)) / 2F + 0.5F;
                    player.absMoveTo(offset.getX(), offset.getY() + sin * 0.2D, offset.getZ(), 0F, 0F);
                    player.absMoveTo(offset.getX(), offset.getY() + sin * 0.2D, offset.getZ(), 0F, 0F);
                    player.setOldPosAndRot();
                };
            }
        }
    }
}
