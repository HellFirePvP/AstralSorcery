package hellfirepvp.astralsorcery.common.integration.jade.provider;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public enum LumenDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = AstralSorcery.key("lumen");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        if (!iPluginConfig.get(UID)) return;
        if (accessor.getServerData().contains("lumenData")) {
            var list = accessor.getServerData().getList("lumenData", Tag.TAG_COMPOUND);
            var helper = IElementHelper.get();
            for (int i = 0; i < list.size(); i++) {
                var tag = list.getCompound(i);
                int capacity = tag.getInt("capacity");
                if (capacity <= 0) continue;

                LumenStack.CODEC.parse(accessor.nbtOps(), tag).result().ifPresent(stack -> {
                    int amount = stack.getAmount();
                    var lumen = stack.getLumen();
                    int color = lumen.getColor(ClientProxy.getClientTick()).getColor();
                    var style = helper.progressStyle().color(color);
                    var text = lumen.getHoverName().plainCopy().append(": " + amount + " / " + capacity).withStyle(ChatFormatting.WHITE);

                    tooltip.add(helper.progress((float) amount / capacity, text, style, BoxStyle.getNestedBox(), true));
                });
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        ILumenHandler handler = accessor.getLevel().getCapability(ILumenHandler.BLOCK, accessor.getPosition(), null);
        if (handler == null) return;

        var lumenData = new ListTag();
        handler.getContainedLumen().forEach(stack -> {
            LumenStack.CODEC.encodeStart(accessor.nbtOps(), stack).result().ifPresent(tag -> {
                if (tag instanceof CompoundTag compoundTag) {
                    compoundTag.putInt("capacity", handler.getCapacity(stack.getLumen()));
                    lumenData.add(tag);
                }
            });
        });
        data.put("lumenData", lumenData);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
