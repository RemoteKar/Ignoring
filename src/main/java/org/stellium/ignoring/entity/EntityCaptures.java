package org.stellium.ignoring.entity;

import net.minecraft.world.entity.Entity;


public class EntityCaptures {
   public static final EntityCaptures MAIN = new EntityCaptures();
   private final ThreadLocal<EntityContainer> container = ThreadLocal.withInitial(EntityContainer::new);

   public void setEntity(Entity entity) {
      this.container.get().setEntity(entity);
   }

   
   public Entity getEntity() {
      return this.container.get().getEntity();
   }

   public void setEnabled(boolean enabled) {
      this.container.get().setEnabled(enabled);
   }

   public void clearEntity() {
      this.container.get().setEntity(null);
   }
}
