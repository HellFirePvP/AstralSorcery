/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColorReference
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface ColorReference {

    Map<ResourceLocation, MapCodec<? extends ColorReference>> REFERENCES = new HashMap<>() {
        {
            put(RGB.ID, RGB.CODEC);
            put(Dye.ID, Dye.CODEC);
            put(Chat.ID, Chat.CODEC);
        }
    };
    Map<ResourceLocation, StreamCodec<ByteBuf, ? extends ColorReference>> STREAM_REFERENCES = new HashMap<>() {
        {
            put(RGB.ID, RGB.STREAM_CODEC);
            put(Dye.ID, Dye.STREAM_CODEC);
            put(Chat.ID, Chat.STREAM_CODEC);
        }
    };
    Codec<ColorReference> CODEC = ResourceLocation.CODEC.dispatch(ColorReference::getId, REFERENCES::get);
    StreamCodec<ByteBuf, ColorReference> STREAM_CODEC = ResourceLocation.STREAM_CODEC.dispatch(ColorReference::getId, STREAM_REFERENCES::get);
    ColorReference WHITE = RGB.of(0xFFFFFF);

    int color();

    Optional<ChatFormatting> asChatFormatting();

    Optional<DyeColor> asDyeColor();

    ResourceLocation getId();

    class RGB implements ColorReference {

        private static final MapCodec<RGB> CODEC = Codec.INT.fieldOf("color").xmap(RGB::new, RGB::color);
        private static final StreamCodec<ByteBuf, RGB> STREAM_CODEC = ByteBufCodecs.INT.map(RGB::new, RGB::color);
        private static final ResourceLocation ID = AstralSorcery.key("rgb");

        private final int color;
        private ChatFormatting cachedClosestChatColor;
        private DyeColor cachedClosestDyeColor;

        private RGB(int color) {
            this.color = color;
        }

        public static RGB of(int color) {
            return new RGB(color);
        }

        @Override
        public int color() {
            return this.color;
        }

        @Override
        public Optional<ChatFormatting> asChatFormatting() {
            if (this.cachedClosestChatColor != null) return Optional.of(this.cachedClosestChatColor);

            ChatFormatting closest = null;
            double minDistance = Double.MAX_VALUE;

            for (ChatFormatting formatting : ChatFormatting.values()) {
                Integer formattingColor = formatting.getColor();
                if (formattingColor == null) continue;

                double distance = ColorUtil.colorDistance(this.color, formattingColor);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = formatting;
                }
            }

            this.cachedClosestChatColor = closest;
            return Optional.ofNullable(closest);
        }

        @Override
        public Optional<DyeColor> asDyeColor() {
            if (this.cachedClosestDyeColor != null) return Optional.of(this.cachedClosestDyeColor);

            DyeColor closest = DyeColor.WHITE;
            double minDistance = Double.MAX_VALUE;

            for (DyeColor formatting : DyeColor.values()) {
                double distance = ColorUtil.colorDistance(this.color, formatting.getTextColor());
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = formatting;
                }
            }

            this.cachedClosestDyeColor = closest;
            return Optional.of(closest);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            RGB rgb = (RGB) o;
            return color == rgb.color;
        }

        @Override
        public int hashCode() {
            return Objects.hash(color);
        }
    }

    class Dye implements ColorReference {

        private static final MapCodec<Dye> CODEC = CodecUtil.enumCodec(DyeColor.class).fieldOf("dyecolor").xmap(Dye::new, dye -> dye.dyeColor);
        private static final StreamCodec<ByteBuf, Dye> STREAM_CODEC = CodecUtil.enumStreamCodec(DyeColor.class).map(Dye::new, dye -> dye.dyeColor);
        private static final ResourceLocation ID = AstralSorcery.key("dyecolor");

        private final DyeColor dyeColor;
        private ChatFormatting cachedClosest;

        private Dye(DyeColor dyeColor) {
            this.dyeColor = dyeColor;
        }

        public static Dye of(DyeColor dyeColor) {
            return new Dye(dyeColor);
        }

        @Override
        public int color() {
            return this.dyeColor.getTextColor();
        }

        @Override
        public Optional<ChatFormatting> asChatFormatting() {
            if (this.cachedClosest != null) return Optional.of(this.cachedClosest);

            ChatFormatting closest = null;
            double minDistance = Double.MAX_VALUE;

            for (ChatFormatting formatting : ChatFormatting.values()) {
                Integer formattingColor = formatting.getColor();
                if (formattingColor == null) continue;

                double distance = ColorUtil.colorDistance(this.dyeColor.getTextColor(), formattingColor);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = formatting;
                }
            }

            this.cachedClosest = closest;
            return Optional.ofNullable(closest);
        }

        @Override
        public Optional<DyeColor> asDyeColor() {
            return Optional.of(this.dyeColor);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Dye dye = (Dye) o;
            return dyeColor == dye.dyeColor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dyeColor);
        }
    }

     class Chat implements ColorReference {

         private static final MapCodec<Chat> CODEC = CodecUtil.enumCodec(ChatFormatting.class).fieldOf("formatting").xmap(Chat::new, chat -> chat.formatting);
         private static final StreamCodec<ByteBuf, Chat> STREAM_CODEC = CodecUtil.enumStreamCodec(ChatFormatting.class).map(Chat::new, chat -> chat.formatting);
         private static final ResourceLocation ID = AstralSorcery.key("chatformatting");

         private final ChatFormatting formatting;
         private DyeColor cachedClosestDyeColor;

         private Chat(ChatFormatting formatting) {
             this.formatting = formatting;
         }

         private static Chat of(ChatFormatting formatting) {
             if (!formatting.isColor()) {
                 return new Chat(ChatFormatting.WHITE);
             }
             return new Chat(formatting);
         }

         @Override
         public int color() {
             return Optional.ofNullable(this.formatting.getColor()).orElse(0xFFFFFF);
         }

         @Override
         public Optional<ChatFormatting> asChatFormatting() {
             return Optional.of(this.formatting);
         }

         @Override
         public Optional<DyeColor> asDyeColor() {
             if (this.cachedClosestDyeColor != null) return Optional.of(this.cachedClosestDyeColor);
             Integer thisColor = this.formatting.getColor();
             if (thisColor == null) return Optional.of(DyeColor.WHITE);

             DyeColor closest = DyeColor.WHITE;
             double minDistance = Double.MAX_VALUE;

             for (DyeColor formatting : DyeColor.values()) {
                 double distance = ColorUtil.colorDistance(thisColor, formatting.getTextColor());
                 if (distance < minDistance) {
                     minDistance = distance;
                     closest = formatting;
                 }
             }

             this.cachedClosestDyeColor = closest;
             return Optional.of(closest);
         }

         @Override
         public ResourceLocation getId() {
             return ID;
         }

         @Override
         public boolean equals(Object o) {
             if (o == null || getClass() != o.getClass()) return false;
             Chat chat = (Chat) o;
             return formatting == chat.formatting;
         }

         @Override
         public int hashCode() {
             return Objects.hashCode(formatting);
         }
    }
}
