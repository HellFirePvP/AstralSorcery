/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.network.play.PktRequestCancelLinkSession;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkSessionEffectHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinkSessionEffectHelper {

    private static final RandomSource rand = RandomSource.create();

    public static void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(LinkSessionEffectHelper::onMouseClick);
        eventBus.addListener(LinkSessionEffectHelper::onClientTick);
    }

    private static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;

        ClientLinkHelper.getActiveSession().ifPresent(session -> {
            PacketDistributor.sendToServer(PktRequestCancelLinkSession.cancelSession());
        });
    }

    private static void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().isPaused()) return;
        if (ClientProxy.getClientTick() % 30 != 0) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        ClientLinkHelper.getActiveSession().ifPresent(session -> {
            session.selected().resolveLocation(level).ifPresent(from -> {
                session.linked().forEach(eitherTarget -> {
                    eitherTarget.resolveLocation(level).ifPresent(target -> {
                        List<Vector3> pointPath = new ArrayList<>(VectorUtil.iteratePoints(from, target, 0.25F));

                        int targetPointsPerTick = 4;
                        int maxTickTime = 20;
                        int pointsPerTick = Math.max(targetPointsPerTick, pointPath.size() / maxTickTime);

                        for (int i = 0; i < pointPath.size(); i++) {
                            Vector3 pos = pointPath.get(i);
                            int tickDelay = i / pointsPerTick;

                            ClientProxy.scheduleEffectTask(tickDelay + 1, () -> {
                                for (int j = 0; j < 2; j++) {
                                    Vector3 effectPos = VectorUtil.withRandomOffset(pos.copy(), rand, 0.03F);
                                    Vector3 dir = Vector3.random(rand).normalize().multiply(0.004F + rand.nextFloat() * 0.005F);

                                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                            .spawn(effectPos)
                                            .alpha(FXAlphaFunction.FADE_OUT)
                                            .color((fx, pTicks) -> ColorWrapper.ofHSB((ClientProxy.getClientTick() % 100) / 100F, 1F, 1F))
                                            .setScale(0.25F + rand.nextFloat() * 0.15F)
                                            .setMotion(dir)
                                            .setGravity(Vector3.y(0.001F))
                                            .setMaxAge(20 + rand.nextInt(10));
                                }
                            });
                        }
                    });
                });
            });
        });
    }
}
