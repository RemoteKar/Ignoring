package org.stellium.ignoring.mixin.render;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.stellium.ignoring.render.IgnoringStateEntity;
// 렌더 상태에 엔티티를 직접 붙인다. 전역 맵에 넣으면 그려지지 않은 상태(1인칭 로컬 플레이어 등)가 영영 남는다.
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IgnoringStateEntity {
    @Unique private Entity ignoring$entity;
    public Entity ignoring$entity() { return ignoring$entity; }
    public void ignoring$setEntity(Entity entity) { ignoring$entity = entity; }
}
