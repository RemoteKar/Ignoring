package org.stellium.ignoring.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import org.stellium.ignoring.config.IgnoringConfig;

public class IgnoringCommands {
   public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:togglerender").executes(IgnoringCommands::toggleRender));
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:togglechat").executes(IgnoringCommands::toggleChat));
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:toggletablist").executes(IgnoringCommands::toggleTablist));
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:toggleinteraction").executes(IgnoringCommands::toggleInteraction));
      dispatcher.register(
         (LiteralArgumentBuilder)ClientCommands.literal("!ignoring:addignore")
            .then(ClientCommands.argument("player", StringArgumentType.string()).executes(IgnoringCommands::addIgnore))
      );
      dispatcher.register(
         (LiteralArgumentBuilder)ClientCommands.literal("!ignoring:removeignore")
            .then(ClientCommands.argument("player", StringArgumentType.string()).executes(IgnoringCommands::removeIgnore))
      );
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:listignore").executes(IgnoringCommands::listIgnore));
      dispatcher.register(
         (LiteralArgumentBuilder)ClientCommands.literal("!ignoring:transparency")
            .then(ClientCommands.argument("value", IntegerArgumentType.integer(0, 255)).executes(IgnoringCommands::setTransparency))
      );
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:reload").executes(IgnoringCommands::reload));
      dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal("!ignoring:help").executes(IgnoringCommands::help));
   }

   private static int toggleRender(CommandContext<FabricClientCommandSource> context) {
      IgnoringConfig config = IgnoringConfig.get();
      config.ignoreRender = !config.ignoreRender;
      saveConfig();
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.translatable("text.ignoring.toggle.ignoreRender")
               .append(Component.literal(": "))
               .append(
                  Component.translatable(config.ignoreRender ? "text.ignoring.status.enabled" : "text.ignoring.status.disabled")
                     .withStyle(config.ignoreRender ? ChatFormatting.GREEN : ChatFormatting.RED)
               )
         );
      return 1;
   }

   private static int toggleChat(CommandContext<FabricClientCommandSource> context) {
      IgnoringConfig config = IgnoringConfig.get();
      config.ignoreChat = !config.ignoreChat;
      saveConfig();
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.translatable("text.ignoring.toggle.ignoreChat")
               .append(Component.literal(": "))
               .append(
                  Component.translatable(config.ignoreChat ? "text.ignoring.status.enabled" : "text.ignoring.status.disabled")
                     .withStyle(config.ignoreChat ? ChatFormatting.GREEN : ChatFormatting.RED)
               )
         );
      return 1;
   }

   private static int toggleTablist(CommandContext<FabricClientCommandSource> context) {
      IgnoringConfig config = IgnoringConfig.get();
      config.ignoreTablist = !config.ignoreTablist;
      saveConfig();
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.translatable("text.ignoring.toggle.ignoreTablist")
               .append(Component.literal(": "))
               .append(
                  Component.translatable(config.ignoreTablist ? "text.ignoring.status.enabled" : "text.ignoring.status.disabled")
                     .withStyle(config.ignoreTablist ? ChatFormatting.GREEN : ChatFormatting.RED)
               )
         );
      return 1;
   }

   private static int toggleInteraction(CommandContext<FabricClientCommandSource> context) {
      IgnoringConfig config = IgnoringConfig.get();
      config.interactionThroughIgnoredPlayer = !config.interactionThroughIgnoredPlayer;
      saveConfig();
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.translatable("text.ignoring.toggle.interactionThroughIgnoredPlayer")
               .append(Component.literal(": "))
               .append(
                  Component.translatable(config.interactionThroughIgnoredPlayer ? "text.ignoring.status.enabled" : "text.ignoring.status.disabled")
                     .withStyle(config.interactionThroughIgnoredPlayer ? ChatFormatting.GREEN : ChatFormatting.RED)
               )
         );
      return 1;
   }

   private static int addIgnore(CommandContext<FabricClientCommandSource> context) {
      String playerName = StringArgumentType.getString(context, "player");
      IgnoringConfig config = IgnoringConfig.get();
      if (config.ignoredPlayerList.contains(playerName)) {
         ((FabricClientCommandSource)context.getSource())
            .sendError(Component.translatable("text.ignoring.command.addignore.duplicate", new Object[]{playerName}));
         return 0;
      } else {
         config.ignoredPlayerList.remove("Insert name");
         config.ignoredPlayerList.add(playerName);
         saveConfig();
         ((FabricClientCommandSource)context.getSource())
            .sendFeedback(Component.translatable("text.ignoring.command.addignore.success", new Object[]{playerName}).withStyle(ChatFormatting.GREEN));
         return 1;
      }
   }

   private static int removeIgnore(CommandContext<FabricClientCommandSource> context) {
      String playerName = StringArgumentType.getString(context, "player");
      IgnoringConfig config = IgnoringConfig.get();
      if (!config.ignoredPlayerList.contains(playerName)) {
         ((FabricClientCommandSource)context.getSource())
            .sendError(Component.translatable("text.ignoring.command.removeignore.notfound", new Object[]{playerName}));
         return 0;
      } else {
         config.ignoredPlayerList.remove(playerName);
         saveConfig();
         ((FabricClientCommandSource)context.getSource())
            .sendFeedback(Component.translatable("text.ignoring.command.removeignore.success", new Object[]{playerName}).withStyle(ChatFormatting.GREEN));
         return 1;
      }
   }

   private static int listIgnore(CommandContext<FabricClientCommandSource> context) {
      IgnoringConfig config = IgnoringConfig.get();
      if (!config.ignoredPlayerList.isEmpty() && (config.ignoredPlayerList.size() != 1 || !config.ignoredPlayerList.contains("Insert name"))) {
         ((FabricClientCommandSource)context.getSource())
            .sendFeedback(
               Component.translatable("text.ignoring.command.listignore.header").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
            );

         for (String player : config.ignoredPlayerList) {
            if (!player.equals("Insert name")) {
               ((FabricClientCommandSource)context.getSource())
                  .sendFeedback(Component.literal("  - ").withStyle(ChatFormatting.GRAY).append(Component.literal(player).withStyle(ChatFormatting.WHITE)));
            }
         }

         return 1;
      } else {
         ((FabricClientCommandSource)context.getSource())
            .sendFeedback(Component.translatable("text.ignoring.command.listignore.empty").withStyle(ChatFormatting.YELLOW));
         return 1;
      }
   }

   private static int setTransparency(CommandContext<FabricClientCommandSource> context) {
      int value = IntegerArgumentType.getInteger(context, "value");
      IgnoringConfig config = IgnoringConfig.get();
      config.transparency = value;
      saveConfig();
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(Component.translatable("text.ignoring.command.transparency.success", new Object[]{value}).withStyle(ChatFormatting.GREEN));
      return 1;
   }

   private static int reload(CommandContext<FabricClientCommandSource> context) {
      try {
         AutoConfig.getConfigHolder(IgnoringConfig.class).load();
         ((FabricClientCommandSource)context.getSource())
            .sendFeedback(Component.translatable("text.ignoring.command.reload.success").withStyle(ChatFormatting.GREEN));
         return 1;
      } catch (Exception e) {
         ((FabricClientCommandSource)context.getSource())
            .sendError(Component.translatable("text.ignoring.command.reload.failed", new Object[]{e.getMessage()}));
         return 0;
      }
   }

   private static int help(CommandContext<FabricClientCommandSource> context) {
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(Component.translatable("text.ignoring.command.help.header").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}));
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:togglerender")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.togglerender").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:togglechat")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.togglechat").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:toggletablist")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.toggletablist").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:toggleinteraction")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.toggleinteraction").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:addignore <player>")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.addignore").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:removeignore <player>")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.removeignore").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:listignore")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.listignore").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:transparency <0-255>")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.transparency").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:reload")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.reload").withStyle(ChatFormatting.GRAY))
         );
      ((FabricClientCommandSource)context.getSource())
         .sendFeedback(
            Component.literal("!ignoring:help")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" - "))
               .append(Component.translatable("text.ignoring.command.help.help").withStyle(ChatFormatting.GRAY))
         );
      return 1;
   }

   private static void saveConfig() {
      AutoConfig.getConfigHolder(IgnoringConfig.class).save();
   }
}
