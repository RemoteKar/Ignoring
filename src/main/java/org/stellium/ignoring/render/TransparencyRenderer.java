package org.stellium.ignoring.render;

import net.minecraft.world.entity.Entity;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.entity.EntityCaptures;

public class TransparencyRenderer {
   public static void handleEntityRendering(Entity entity, Runnable renderCall) {
      IgnoringConfig cfg = IgnoringConfig.get();
      if (!cfg.ignoreRender) {
         renderCall.run();
      } else if (!cfg.shouldIgnorePlayer(entity)) {
         renderCall.run();
      } else {
         EntityCaptures.MAIN.setEntity(entity);
         try { if (cfg.transparency > 0) renderCall.run(); }
         finally { EntityCaptures.MAIN.clearEntity(); }
      }
   }
}
