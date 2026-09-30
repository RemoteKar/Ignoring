package org.stellium.ignoring.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class IgnoringConfigScreen {
   private IgnoringConfigScreen() {
   }

   public static Screen create(Screen parent) {
      IgnoringConfig working = copyOf(IgnoringConfig.get());
      ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("text.autoconfig.ignoring.title"));
      ConfigCategory category = builder.getOrCreateCategory(Component.translatable("text.autoconfig.ignoring.title"));
      ConfigEntryBuilder entries = builder.entryBuilder();
      category.addEntry(
         entries.startBooleanToggle(Component.translatable("text.autoconfig.ignoring.option.ignoreChat"), working.ignoreChat)
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoreChat.@Tooltip")})
            .setSaveConsumer(value -> working.ignoreChat = value)
            .build()
      );
      category.addEntry(
         entries.startBooleanToggle(Component.translatable("text.autoconfig.ignoring.option.ignoreRender"), working.ignoreRender)
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoreRender.@Tooltip")})
            .setSaveConsumer(value -> working.ignoreRender = value)
            .build()
      );
      category.addEntry(
         entries.startBooleanToggle(Component.translatable("text.autoconfig.ignoring.option.ignoreTablist"), working.ignoreTablist)
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoreTablist.@Tooltip")})
            .setSaveConsumer(value -> working.ignoreTablist = value)
            .build()
      );
      category.addEntry(
         entries.startBooleanToggle(
               Component.translatable("text.autoconfig.ignoring.option.interactionThroughIgnoredPlayer"), working.interactionThroughIgnoredPlayer
            )
            .setDefaultValue(false)
            .setTooltip(
               new Component[]{
                  Component.translatable("text.autoconfig.ignoring.option.interactionThroughIgnoredPlayer.@Tooltip[0]"),
                  Component.translatable("text.autoconfig.ignoring.option.interactionThroughIgnoredPlayer.@Tooltip[1]")
               }
            )
            .setSaveConsumer(value -> working.interactionThroughIgnoredPlayer = value)
            .build()
      );
      category.addEntry(
         entries.startBooleanToggle(Component.translatable("text.autoconfig.ignoring.option.ignoreEveryone"), working.ignoreEveryone)
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoreEveryone.@Tooltip")})
            .setSaveConsumer(value -> working.ignoreEveryone = value)
            .build()
      );
      category.addEntry(
         entries.startBooleanToggle(Component.translatable("text.autoconfig.ignoring.option.ignoreSpecialCharacter"), working.ignoreSpecialCharacter)
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoreSpecialCharacter.@Tooltip")})
            .setSaveConsumer(value -> working.ignoreSpecialCharacter = value)
            .build()
      );
      category.addEntry(
         entries.startIntSlider(Component.translatable("text.autoconfig.ignoring.option.transparency"), working.transparency, 0, 255)
            .setDefaultValue(255)
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.transparency.@Tooltip")})
            .setTextGetter(value -> Component.literal("Value: " + value))
            .setSaveConsumer(value -> working.transparency = value)
            .build()
      );
      category.addEntry(
         entries.startStrList(Component.translatable("text.autoconfig.ignoring.option.ignoredPlayerList"), new ArrayList<>(working.ignoredPlayerList))
            .setDefaultValue(new ArrayList<>(List.of("Insert name")))
            .setTooltip(new Component[]{Component.translatable("text.autoconfig.ignoring.option.ignoredPlayerList.@Tooltip")})
            .setCellErrorSupplier(name -> name != null && !name.isBlank() ? Optional.empty() : Optional.of(Component.literal("Player name cannot be empty.")))
            .setSaveConsumer(list -> working.ignoredPlayerList = new ArrayList<>(list))
            .build()
      );
      builder.setSavingRunnable(() -> {
         IgnoringConfig.validate(working);
         AutoConfig.getConfigHolder(IgnoringConfig.class).setConfig(working);
         AutoConfig.getConfigHolder(IgnoringConfig.class).save();
      });
      return builder.build();
   }

   private static IgnoringConfig copyOf(IgnoringConfig source) {
      IgnoringConfig copy = new IgnoringConfig();
      copy.ignoreChat = source.ignoreChat;
      copy.ignoreRender = source.ignoreRender;
      copy.ignoreTablist = source.ignoreTablist;
      copy.interactionThroughIgnoredPlayer = source.interactionThroughIgnoredPlayer;
      copy.ignoreEveryone = source.ignoreEveryone;
      copy.ignoreSpecialCharacter = source.ignoreSpecialCharacter;
      copy.transparency = source.transparency;
      copy.ignoredPlayerList = source.ignoredPlayerList == null ? new ArrayList<>() : new ArrayList<>(source.ignoredPlayerList);
      return copy;
   }
}
