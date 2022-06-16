package com.darksoldier1404.dss.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dss.SimpleShop;
import com.darksoldier1404.dss.functions.DSSFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@SuppressWarnings("all")
public class DSSEvent implements Listener {
    private final SimpleShop plugin = SimpleShop.getInstance();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (DSSFunction.currentInv.containsKey(p.getUniqueId())) {
            DInventory di = DSSFunction.currentInv.get(p.getUniqueId());
            if (!plugin.currentEditShop.containsKey(p.getUniqueId())) {
                plugin.currentItem.remove(p.getUniqueId());
                plugin.isBuying.remove(p.getUniqueId());
                DSSFunction.currentInv.remove(p.getUniqueId());
                return;
            }
            if (e.getView().getTitle().equals(plugin.currentEditShop.get(p.getUniqueId()))) {
                DSSFunction.saveShopShowCase(p, di);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (DSSFunction.currentInv.containsKey(p.getUniqueId())) {
            if (plugin.currentEditShop.containsKey(p.getUniqueId())) return;
            DInventory di = DSSFunction.currentInv.get(p.getUniqueId());
            if (di.isValidHandler(plugin)) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                        di.prevPage();
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                        di.nextPage();
                        return;
                    }
                    ClickType ct = e.getClick();
                    if (ct.isLeftClick()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "price")) {
                            if (plugin.currentItem.containsKey(p.getUniqueId())) {
                                if (plugin.isBuying.containsKey(p.getUniqueId())) {
                                    if (plugin.isBuying.get(p.getUniqueId())) {
                                        DSSFunction.buyMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"));
                                        if (!plugin.preventInvClose) {
                                            p.closeInventory();
                                            plugin.currentItem.remove(p.getUniqueId());
                                            plugin.isBuying.remove(p.getUniqueId());
                                        }
                                        return;
                                    } else {
                                        DSSFunction.sellMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"), false);
                                        if (!plugin.preventInvClose) {
                                            p.closeInventory();
                                            plugin.currentItem.remove(p.getUniqueId());
                                            plugin.isBuying.remove(p.getUniqueId());
                                        }
                                        return;
                                    }
                                }
                            }
                            DSSFunction.openBuyOption(p, e.getCurrentItem(), true);
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), true);
                        }
                        return;
                    }
                    if (ct.isCreativeAction()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "sellPrice")) {
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), false);
                            DSSFunction.sellMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"), true);
                            return;
                        }
                    }
                    if (ct.isRightClick()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "sellPrice")) {
                            if (plugin.currentItem.containsKey(p.getUniqueId())) return;
                            DSSFunction.openBuyOption(p, e.getCurrentItem(), false);
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), false);
                        }
                    }
                }
            }
        }
    }
}
