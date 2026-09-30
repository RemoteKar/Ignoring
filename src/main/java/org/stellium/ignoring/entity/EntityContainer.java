package org.stellium.ignoring.entity;

import net.minecraft.world.entity.Entity;


public class EntityContainer {
   
   private Entity entity = null;
   private boolean enabled = true;

   public void setEntity( Entity entity) {
      if (this.isEnabled()) {
         this.entity = entity;
      }
   }

   
   public Entity getEntity() {
      return this.isEnabled() ? this.entity : null;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
