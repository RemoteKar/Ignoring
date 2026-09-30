package org.stellium.ignoring.render;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.entity.Entity;


public final class RenderCommandEntityBridge {
   private static final ThreadLocal<Map<Object, Entity>> ITEM_COMMAND_ENTITIES = ThreadLocal.withInitial(IdentityHashMap::new);

   private RenderCommandEntityBridge() {
   }

   public static void registerItemCommand(Object command, Entity entity) {
      ITEM_COMMAND_ENTITIES.get().put(command, entity);
   }

   
   public static Entity getItemCommandEntity(Object command) {
      return ITEM_COMMAND_ENTITIES.get().get(command);
   }

   public static void clearItemCommands() {
      ITEM_COMMAND_ENTITIES.get().clear();
   }
}
