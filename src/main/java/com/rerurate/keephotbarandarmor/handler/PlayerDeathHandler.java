package com.rerurate.keephotbarandarmor.handler;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathHandler {

    private static final int HOTBAR_SIZE = 9;

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND
    };

    private static class SavedItems {
        List<ItemStack> hotbar = new ArrayList<>();
        Map<EquipmentSlot, ItemStack> armor = new HashMap<>();
    }

    private final Map<UUID, SavedItems> savedData = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        SavedItems saved = new SavedItems();

        for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            saved.hotbar.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            saved.armor.put(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        savedData.put(player.getUUID(), saved);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        SavedItems saved = savedData.get(player.getUUID());
        if (saved == null) {
            return;
        }

        List<ItemStack> protectedItems = new ArrayList<>(saved.hotbar);
        protectedItems.addAll(saved.armor.values());

        for (ItemStack protect : protectedItems) {
            if (protect.isEmpty()) continue;

            event.getDrops().removeIf(drop -> {
                ItemStack dropStack = drop.getItem();
                return dropStack.getItem() == protect.getItem()
                        && dropStack.getCount() == protect.getCount()
                        && ItemStack.isSameItemSameTags(dropStack, protect);
            });
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();

        SavedItems saved = savedData.remove(uuid);
        if (saved == null) {
            return;
        }

        for (int slot = 0; slot < HOTBAR_SIZE && slot < saved.hotbar.size(); slot++) {
            ItemStack stack = saved.hotbar.get(slot);
            if (!stack.isEmpty()) {
                player.getInventory().setItem(slot, stack.copy());
            }
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = saved.armor.getOrDefault(slot, ItemStack.EMPTY);
            if (!stack.isEmpty()) {
                player.setItemSlot(slot, stack.copy());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();

        SavedItems saved = savedData.remove(uuid);
        if (saved == null) {
            return;
        }

        for (int slot = 0; slot < HOTBAR_SIZE && slot < saved.hotbar.size(); slot++) {
            ItemStack stack = saved.hotbar.get(slot);
            if (!stack.isEmpty()) {
                player.getInventory().setItem(slot, stack.copy());
            }
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = saved.armor.getOrDefault(slot, ItemStack.EMPTY);
            if (!stack.isEmpty()) {
                player.setItemSlot(slot, stack.copy());
            }
        }
    }
}
