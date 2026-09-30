package org.stellium.ignoring.mixin.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stellium.ignoring.config.IgnoringConfig;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
   @Shadow
   @Mutable
   @Final
   private Set<PlayerInfo> listedPlayers;

   @Inject(method = "handleSystemChat(Lnet/minecraft/network/protocol/game/ClientboundSystemChatPacket;)V", at = @At("HEAD"), cancellable = true)
   private void onGameMessage(ClientboundSystemChatPacket packet, CallbackInfo ci) {
      IgnoringConfig config = IgnoringConfig.get();
      Component original = packet.content();
      String raw = original.getString();
      if (config.ignoreChat) {
         if (config.ignoreEveryone) {
            ci.cancel();
            return;
         }

         for (String playerName : config.ignoredPlayerList) {
            if (raw.contains(playerName)) {
               ci.cancel();
               return;
            }
         }
      }

      if (config.ignoreSpecialCharacter) {
         Component modified = trimTailOne(original);
         if (modified != null) {
            ci.cancel();
            Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(modified);
         }
      }
   }

   private static Component trimTailOne(Component original) {
      List<Style> styles = new ArrayList<>();
      List<String> strings = new ArrayList<>();
      original.visit((style, string) -> {
         styles.add(style);
         strings.add(string);
         return Optional.empty();
      }, Style.EMPTY);
      int pi = strings.size() - 1;
      int off = pi >= 0 && strings.get(pi) != null ? strings.get(pi).length() : 0;
      if (pi < 0) {
         return null;
      }

      int[] pos = prevNonWs(strings, pi, off);
      pi = pos[0];
      off = pos[1];
      if (pi < 0) {
         return null;
      }

      String s = strings.get(pi);
      int cp = s.codePointBefore(off);
      if (!isRemovable(cp)) {
         return null;
      }

      int start = s.offsetByCodePoints(off, -1);
      strings.set(pi, s.substring(0, start) + s.substring(off));
      MutableComponent out = Component.empty();

      for (int k = 0; k < strings.size(); k++) {
         String part = strings.get(k);
         if (part != null && !part.isEmpty()) {
            out.append(Component.literal(part).setStyle(styles.get(k)));
         }
      }

      return out;
   }

   private static int[] prevNonWs(List<String> strings, int pi, int off) {
      int p = pi;
      int o = off;

      while (p >= 0) {
         String s = strings.get(p);
         if (s != null && o > 0) {
            int cp = s.codePointBefore(o);
            if (!Character.isWhitespace(cp)) {
               return new int[]{p, o};
            }

            o = s.offsetByCodePoints(o, -1);
         } else {
            p--;
            o = p >= 0 && strings.get(p) != null ? strings.get(p).length() : 0;
         }
      }

      return new int[]{-1, 0};
   }

   private static boolean isRemovable(int cp) {
      if (cp >= 48 && cp <= 57) {
         return false;
      } else if (cp >= 65 && cp <= 90) {
         return false;
      } else if (cp >= 97 && cp <= 122) {
         return false;
      } else if (cp >= 44032 && cp <= 55203) {
         return false;
      } else if (cp == 91 || cp == 93 || cp == 40 || cp == 41 || cp == 123 || cp == 125 || cp == 60 || cp == 62) {
         return false;
      } else if (cp == 46 || cp == 44 || cp == 33 || cp == 63 || cp == 58 || cp == 59) {
         return false;
      } else if (cp != 39 && cp != 34 && cp != 45 && cp != 95 && cp != 126) {
         int type = Character.getType(cp);
         return type == 28 || type == 25 || type == 27;
      } else {
         return false;
      }
   }
}
