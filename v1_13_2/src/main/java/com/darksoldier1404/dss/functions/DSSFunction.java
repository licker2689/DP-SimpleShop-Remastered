package com.darksoldier1404.dss.functions;

import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dss.SimpleShop;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("all")
public class DSSFunction {
    private static final SimpleShop plugin = SimpleShop.getInstance();
    private static final String prefix = plugin.prefix;

    public static void openShop(Player p, String name) {
        if (!plugin.shops.containsKey(name)) {
            p.sendMessage(prefix + "해당 상점이 존재하지 않습니다.");
            return;
        }
        YamlConfiguration shop = plugin.shops.get(name);
        Inventory inv = new DInventory(null, ChatColor.translateAlternateColorCodes('&', shop.getString("Shop.Title") == null ? name : shop.getString("Shop.Title")), shop.getInt("Shop.Line") * 9, plugin);
        if (shop.getConfigurationSection("Shop.Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
                ItemStack item = shop.getItemStack("Shop.Items." + key);
                ItemMeta im = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im.hasLore()) {
                    lore = im.getLore();
                }
                if (shop.getString("Shop.Prices." + key + ".price") == null || shop.getInt("Shop.Prices." + key + ".price") == -1) {
                    item = NBT.setDoubleTag(item, "price", -1);
                    lore.add("§c구매 불가");
                } else {
                    double price = shop.getDouble("Shop.Prices." + key + ".price");
                    item = NBT.setDoubleTag(item, "price", price);
                    lore.add("§a좌클릭시 구매 | 가격 : " + price);
                }
                if (shop.getString("Shop.Prices." + key + ".sellPrice") == null || shop.getInt("Shop.Prices." + key + ".sellPrice") == -1) {
                    item = NBT.setDoubleTag(item, "sellPrice", -1);
                    lore.add("§c판매 불가");
                } else {
                    double price = shop.getDouble("Shop.Prices." + key + ".sellPrice");
                    item = NBT.setDoubleTag(item, "sellPrice", price);
                    lore.add("§a우클릭시 판매 | 가격 : " + price + " (휠 클릭시 모두 판매)");
                }
                ItemStack r = item.clone();
                ItemMeta rm = r.getItemMeta();
                rm.setLore(lore);
                r.setItemMeta(rm);
                inv.setItem(Integer.parseInt(key), r);
            }
        }
        p.openInventory(inv);
    }

    public static void openShop(CommandSender p, String name, String username) {
        if (!p.hasPermission("dss.admin")) {
            p.sendMessage(prefix + "권한이 없습니다.");
            return;
        }
        Player target = plugin.getServer().getPlayer(username);
        if (target == null) {
            p.sendMessage(prefix + "해당 유저는 존재하지 않습니다.");
            return;
        }
        openShop(target, name);
    }

    public static void createShop(CommandSender p, String name, String line) {
        int l;
        try {
            l = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + "잘못된 숫자입니다.");
            return;
        }
        if (l < 1 || l > 6) {
            p.sendMessage(prefix + "상점의 줄은 1~6줄 사이여야 합니다.");
            return;
        }
        createShop(name, l);
        p.sendMessage(prefix + "상점을 생성했습니다.");
    }

    public static void setTitle(CommandSender p, String name, String[] args) {
        if (plugin.shops.containsKey(name)) {
            YamlConfiguration shop = plugin.shops.get(name);
            shop.set("Shop.Title", getColoredText(args, 2));
            saveData(name, "shops", shop);
            p.sendMessage(prefix + "상점의 타이틀을 설정했습니다.");
        } else {
            p.sendMessage(prefix + "해당 상점이 존재하지 않습니다.");
        }
    }

    public static String getColoredText(String[] args, int line) {
        StringBuilder s = new StringBuilder();
        args = Arrays.copyOfRange(args, line, args.length);
        Iterator<String> i = Arrays.stream(args).iterator();
        while (i.hasNext()) {
            s.append(i.next()).append(" ");
        }
        // delete last space
        if (s.charAt(s.length() - 1) == ' ') {
            s.deleteCharAt(s.length() - 1);
        }
        return ChatColor.translateAlternateColorCodes('&', s.toString());
    }

    public static void removeShop(CommandSender p, String name) {
        if (plugin.shops.containsKey(name)) {
            plugin.shops.remove(name);
            File file = new File(plugin.getDataFolder() + "/shops/" + name + ".yml");
            file.delete();
            p.sendMessage(prefix + "상점을 삭제했습니다.");
        } else {
            p.sendMessage(prefix + "해당 상점이 존재하지 않습니다.");
        }
    }

    public static void removeAllShop(CommandSender p) {
        for(String name : plugin.shops.keySet()) {
            File file = new File(plugin.getDataFolder() + "/shops/" + name + ".yml");
            file.delete();
        }
        p.sendMessage(prefix + "모든 상점을 삭제하였습니다.");
    }

    public static void clearShop(CommandSender p, String name) {
        if (plugin.shops.containsKey(name)) {
            YamlConfiguration shop = plugin.shops.get(name);
            shop.set("Shop.Items", null);
            shop.set("Shop.Prices", null);
            saveData(name, "shops", shop);
            p.sendMessage(prefix + "상점을 초기화했습니다.");
        } else {
            p.sendMessage(prefix + "해당 상점이 존재하지 않습니다.");
        }
    }

    public static List<YamlConfiguration> getData(String path) {
        File file = new File(plugin.getDataFolder() + "/" + path);
        if (!file.exists()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        List<YamlConfiguration> list = Lists.newArrayList();
        for (File f : files) {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder() + "/" + path, f.getName()));
            list.add(data);
        }
        return list;
    }

    public static void loadAllShops() {
        plugin.shops.clear();
        if (getData("shops") != null) {
            getData("shops").forEach((shop) -> plugin.shops.put(shop.getString("Shop.Name"), shop));
        }
    }

    public static boolean createShop(String name, int line) {
        if (plugin.shops.containsKey(name)) {
            return false;
        }
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("Shop.Name", name);
        shop.set("Shop.Line", line);
        saveData(name, "shops", shop);
        plugin.shops.put(name, shop);
        return true;
    }

    public static void loadAllShop() {
        plugin.shops.clear();
        if (getData("shops") != null) {
            getData("shops").forEach((shop) -> {
                plugin.shops.put(shop.getString("Shop.Name"), shop);
            });
        }
    }

    public static void saveAllShop() {
        plugin.shops.forEach((name, shop) -> {
            saveData(name, "shops", shop);
        });
    }

    public static void saveData(String key, String path, YamlConfiguration data) { // for shop
        try {
            data.save(new File(plugin.getDataFolder() + "/" + path, key + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openShopShowCase(Player p, String name) {
        plugin.currentEditShop.put(p.getUniqueId(), name);
        YamlConfiguration shop = plugin.shops.get(name);
        Inventory inv = new DInventory(null, name, shop.getInt("Shop.Line") * 9, plugin);
        if (shop.getConfigurationSection("Shop.Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), shop.getItemStack("Shop.Items." + key));
            }
        }
        p.openInventory(inv);
    }

    public static void saveShopShowCase(Player p, Inventory inv) {
        String name = plugin.currentEditShop.get(p.getUniqueId());
        YamlConfiguration shop = plugin.shops.get(name);
        for (int i = 0; i < inv.getSize(); i++) {
            shop.set("Shop.Items." + i, inv.getItem(i));
        }
        p.sendMessage(prefix + name + " 상점을 저장했습니다.");
        saveData(name, "shops", shop);
        plugin.currentEditShop.remove(p.getUniqueId());
    }

    public static void setShopPrice(CommandSender p, int slot, double price, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".price", price);
            saveData(name, "shops", shop);
            p.sendMessage(prefix + "구매 가격을 " + price + "원으로 설정했습니다.");
        } else {
            p.sendMessage(prefix + "아이템이 없습니다.");
        }
    }

    public static void setShopSellPrice(CommandSender p, int slot, double price, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".sellPrice", price);
            saveData(name, "shops", shop);
            p.sendMessage(prefix + "판매 가격을 " + price + "원으로 설정했습니다.");
        } else {
            p.sendMessage(prefix + "아이템이 없습니다.");
        }
    }

    public static void buyMultiple(Player p, ItemStack item, int amount) {
        if (item == null) return;
        if (NBT.getDoubleTag(item, "price") == 0 || NBT.getDoubleTag(item, "price") == -1) {
            p.sendMessage(prefix + "구매할 수 없는 아이템입니다.");
            return;
        }
        double price = NBT.getDoubleTag(item, "price") * amount;
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(prefix + "인벤토리가 꽉 찼습니다.");
            return;
        }
        if (!MoneyAPI.hasEnoughMoney(p, price)) {
            p.sendMessage(prefix + "돈이 부족합니다.");
            return;
        }
        ItemStack r = item.clone();
        r = NBT.c(r, "price");
        r = NBT.c(r, "sellPrice");
        ItemMeta im = r.getItemMeta();
        List<String> lore = im.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        im.setLore(lore);
        r.setItemMeta(im);
        r.setAmount(1);
        ItemStack[] items = p.getInventory().getStorageContents();
        Inventory inv = Bukkit.createInventory(null, 27, "logic");
        inv.setContents(items);
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            leftover.putAll(inv.addItem(r));
        }
        if (leftover.isEmpty()) {
            for (int i = 0; i < amount; i++) {
                p.getInventory().addItem(r);
            }
            MoneyAPI.takeMoney(p, price);
            p.sendMessage(prefix + "아이템을 구매하였습니다.");
        } else {
            p.sendMessage(prefix + "인벤토리 공간이 부족합니다.");
        }
    }

    public static void sellMultiple(Player p, ItemStack item, int amount, boolean isSellAll) {
        if (item == null) return;
        if (NBT.getDoubleTag(item, "sellPrice") == 0 || NBT.getDoubleTag(item, "sellPrice") == -1) {
            p.sendMessage(prefix + "판매할 수 없는 아이템입니다.");
            return;
        }
        double price = NBT.getDoubleTag(item, "sellPrice") * amount;
        ItemStack r = item.clone();
        r = NBT.c(r, "sellPrice");
        r = NBT.c(r, "price");
        ItemMeta im = r.getItemMeta();
        List<String> lore = im.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        im.setLore(lore);
        r.setItemMeta(im);
        r.setAmount(1);
        if (isSellAll) {
            amount = getAllItemCount(p.getInventory().getStorageContents(), r);
            if(amount == 0) {
                p.sendMessage(prefix + "판매할 아이템이 충분하지 않습니다.");
                return;
            }
            price = NBT.getDoubleTag(item, "sellPrice") * amount;
        }
        if (hasEnoughItem(p.getInventory().getStorageContents(), r, amount)) {
            int count = amount;
            ItemStack[] items = p.getInventory().getStorageContents();
            for (ItemStack pi : items) {
                if (pi == null) continue;
                if (count == 0) break;
                ItemStack rpi = pi.clone();
                rpi.setAmount(1);
                if (r.equals(rpi)) {
                    if (pi.getAmount() >= count) {
                        pi.setAmount(pi.getAmount() - count);
                        break;
                    } else {
                        count -= pi.getAmount();
                        pi.setAmount(0);
                    }
                }
            }
            p.getInventory().setStorageContents(items);
        } else {
            p.sendMessage(prefix + "판매할 아이템이 충분하지 않습니다.");
            return;
        }
        MoneyAPI.addMoney(p, price);
        p.sendMessage(prefix + "아이템을 " + price + "원에 판매하였습니다.");
    }

    public static boolean hasEnoughItem(ItemStack[] items, ItemStack item, int amount) {
        int count = 0;
        for (ItemStack i : items) {
            if (i == null) continue;
            ItemStack r = i.clone();
            r.setAmount(1);
            if (r.equals(item)) {
                count += i.getAmount();
            }
        }
        return count >= amount;
    }

    public static int getAllItemCount(ItemStack[] items, ItemStack item) {
        int count = 0;
        for (ItemStack i : items) {
            if (i == null) continue;
            ItemStack r = i.clone();
            r.setAmount(1);
            if (r.equals(item)) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public static ItemStack fixPrice(ItemStack item, double price, double sellPrice) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        if (price <= 0) {
            lore.add("§c구매 불가");
        } else {
            lore.add("§a구매 가격 : " + price);
        }
        if (sellPrice <= 0) {
            lore.add("§c판매 불가");
        } else {
            lore.add("§a판매 가격 : " + sellPrice);
        }
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    public static void openBuyOption(Player p, ItemStack item, boolean isBuying) {
        DInventory inv = new DInventory(null, isBuying ? "§1구매 옵션" : "§1판매 옵션", 54, true, plugin);
        inv.setPages(1);
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack prev = NBT.setStringTag(new ItemStack(Material.PINK_DYE), "prev", "true");
        ItemMeta im = prev.getItemMeta();
        im.setDisplayName("§c이전 페이지");
        prev.setItemMeta(im);
        ItemStack next = NBT.setStringTag(new ItemStack(Material.LIME_DYE), "next", "true");
        im = next.getItemMeta();
        im.setDisplayName("§a다음 페이지");
        next.setItemMeta(im);
        inv.setPageTools(new ItemStack[]{pane, pane, prev, pane, pane, pane, next, pane, pane});
        double price = NBT.getDoubleTag(item, "price");
        double sellPrice = NBT.getDoubleTag(item, "sellPrice");
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, pane);
        }
        inv.setItem(20, NBT.setIntTag(item, "amount", 1));
        item.setAmount(8);
        fixPrice(item, price * item.getAmount(), sellPrice * item.getAmount());
        inv.setItem(21, NBT.setIntTag(item, "amount", 8));
        item.setAmount(16);
        fixPrice(item, price * item.getAmount(), sellPrice * item.getAmount());
        inv.setItem(22, NBT.setIntTag(item, "amount", 16));
        item.setAmount(32);
        fixPrice(item, price * item.getAmount(), sellPrice * item.getAmount());
        inv.setItem(23, NBT.setIntTag(item, "amount", 32));
        item.setAmount(64);
        fixPrice(item, price * item.getAmount(), sellPrice * item.getAmount());
        inv.setItem(24, NBT.setIntTag(item, "amount", 64));
        ItemStack info = new ItemStack(Material.PAPER);
        im = info.getItemMeta();
        im.setDisplayName("§1단일 " + (isBuying ? "구매" : "판매"));
        info.setItemMeta(im);
        inv.setItem(4, info);
        inv.setPageContent(0, inv.getContents());
        int i = 10;
        item.setAmount(1);
        for (int l = 0; l < 3; l++) {
            for (int a = 0; a < 7; a++) {
                fixPrice(item, 64 * item.getAmount() * price, sellPrice * item.getAmount());
                inv.setItem(i, NBT.setIntTag(item, "amount", 64 * item.getAmount()));
                item.setAmount(item.getAmount() + 1);
                i++;
            }
            i = i + 2;
        }
        im = info.getItemMeta();
        im.setDisplayName("§1세트 " + (isBuying ? "구매" : "판매"));
        info.setItemMeta(im);
        inv.setItem(4, info);
        inv.setPageContent(1, inv.getContents());
        inv.update();
        p.openInventory(inv);
    }
}
