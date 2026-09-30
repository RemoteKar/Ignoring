package org.stellium.ignoring.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Config(name = "ignoring")
public class IgnoringConfig implements ConfigData {
   private static boolean initialized;
   @Tooltip
   @TransitiveObject
   public boolean ignoreChat = false;
   @Tooltip
   @TransitiveObject
   public boolean ignoreRender = false;
   @Tooltip
   @TransitiveObject
   public boolean ignoreTablist = false;
   @Tooltip(count = 2)
   @TransitiveObject
   public boolean interactionThroughIgnoredPlayer = false;
   @Tooltip
   @TransitiveObject
   public boolean ignoreEveryone = false;
   @Tooltip
   @TransitiveObject
   public boolean ignoreSpecialCharacter = false;
   @Tooltip
   @BoundedDiscrete(min = 0L, max = 255L)
   public int transparency = 255;
   @Tooltip
   public List<String> ignoredPlayerList = new ArrayList<>();

   public IgnoringConfig() {
      if (this.ignoredPlayerList.isEmpty()) {
         this.ignoredPlayerList.add("Insert name");
      }
   }

   static void validate(IgnoringConfig config) {
      if (config.ignoredPlayerList != null) {
         config.ignoredPlayerList.removeIf(name -> name == null || name.isBlank());
      }

      if (config.transparency < 0) {
         config.transparency = 0;
      }

      if (config.transparency > 255) {
         config.transparency = 255;
      }
   }

   public static void init() {
      if (!initialized) { AutoConfig.register(IgnoringConfig.class, GsonConfigSerializer::new); initialized = true; }
   }

   public boolean shouldIgnorePlayer(Entity entity) {
      if (entity instanceof Player player) {
         return this.ignoreEveryone
            ? !this.isLocalPlayer(player)
            : this.isListedName(player.getScoreboardName())
               || this.isListedName(player.getName().getString())
               || this.isListedName(player.getGameProfile().name());
      } else {
         return false;
      }
   }

   public boolean isPlayerIgnored(String playerName) {
      if (playerName == null || playerName.isBlank()) {
         return false;
      } else {
         return this.ignoreEveryone ? !this.isLocalPlayerName(playerName) : this.ignoredPlayerList.contains(playerName);
      }
   }

   private boolean isListedName(String value) {
      return value != null && this.ignoredPlayerList.contains(value);
   }

   private boolean isLocalPlayer(Player player) {
      Minecraft client = Minecraft.getInstance();
      return client != null && client.player != null && client.player.getUUID().equals(player.getUUID());
   }

   private boolean isLocalPlayerName(String playerName) {
      Minecraft client = Minecraft.getInstance();
      if (client == null || client.player == null) {
         return false;
      } else if (playerName.equals(client.player.getScoreboardName())) {
         return true;
      } else {
         return playerName.equals(client.player.getGameProfile().name()) ? true : playerName.equals(client.player.getName().getString());
      }
   }

   public static IgnoringConfig get() {
      IgnoringConfig config = (IgnoringConfig)AutoConfig.getConfigHolder(IgnoringConfig.class).getConfig();
      validate(config);
      return config;
   }

   public void validatePostLoad() throws ValidationException {
      validate(this);
   }
}
