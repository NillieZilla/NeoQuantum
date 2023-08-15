package com.pixelmoncreate.neoquantum.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class UseItemTrigger extends SimpleCriterionTrigger<UseItemTrigger.Instance> {
    final ResourceLocation id;

    public UseItemTrigger(ResourceLocation location) {
        this.id = location;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    @Override
    public UseItemTrigger.Instance createInstance(@NotNull JsonObject json, @NotNull ContextAwarePredicate playerPred, DeserializationContext conditions) {
        return new UseItemTrigger.Instance(this.id ,playerPred, ItemPredicate.fromJson(json.get("items")), LocationPredicate.fromJson(json.get("location")));
    }

    public void trigger(ServerPlayer player, ItemStack stack, ServerLevel world, double x, double y, double z) {
        trigger(player, instance -> instance.test(stack, world, x, y, z));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final LocationPredicate location;

        public Instance(ResourceLocation location, ContextAwarePredicate playerPred, ItemPredicate count, LocationPredicate indexPos) {
            super(location,playerPred);
            this.item = count;
            this.location = indexPos;
        }

        boolean test(ItemStack stack, ServerLevel world, double x, double y, double z) {
            return this.item.matches(stack) && this.location.matches(world, x, y, z);
        }

        public ItemPredicate getItem() {
            return this.item;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject json = super.serializeToJson(serializationContext);
            if (item != ItemPredicate.ANY) {
                json.add("items", item.serializeToJson());
            }
            if (location != LocationPredicate.ANY) {
                json.add("location", location.serializeToJson());
            }
            return json;
        }

        public LocationPredicate getLocation() {
            return this.location;
        }
    }
}