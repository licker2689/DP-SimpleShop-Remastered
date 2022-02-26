package com.darksoldier1404.dss.commands;

import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dss.SimpleShop;
import com.darksoldier1404.dss.functions.DSSFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("all")
public class DSSCommand implements CommandExecutor, TabCompleter {
    private final SimpleShop plugin = SimpleShop.getInstance();
    private final String prefix = plugin.prefix;
    private final DLang lang = plugin.lang;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_create"));
                sender.sendMessage(prefix + lang.get("shop_cmd_title"));
                sender.sendMessage(prefix + lang.get("shop_cmd_display"));
                sender.sendMessage(prefix + lang.get("shop_cmd_price"));
                sender.sendMessage(prefix + lang.get("shop_cmd_list"));
                sender.sendMessage(prefix + lang.get("shop_cmd_reset"));
                sender.sendMessage(prefix + lang.get("shop_cmd_delete"));
                sender.sendMessage(prefix + lang.get("shop_cmd_delete_all"));
            }
            if (sender.hasPermission("dss.list")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_open_other"));
            }
            sender.sendMessage(prefix + lang.get("shop_cmd_open"));
            return false;
        }
        if (args[0].equals("오픈") || args[0].equalsIgnoreCase("open")) {
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + lang.get("shop_cmd_cant_use_in_console"));
                    return false;
                }
                DSSFunction.openShop((Player) sender, args[1]);
                return false;
            }
            if (args.length == 3) {
                if (!sender.hasPermission("dss.admin")) {
                    sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                    return false;
                }
                DSSFunction.openShop(sender, args[1], args[2]);
                return false;
            }
        }
        if (args[0].equals("생성") || args[0].equalsIgnoreCase("create")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_line"));
                return false;
            }
            if (args.length == 3) {
                DSSFunction.createShop(sender, args[1], args[2]);
                return false;
            }
        }
        if (args[0].equals("진열") || args[0].equalsIgnoreCase("display")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + lang.get("shop_cmd_cant_use_in_console"));
                    return false;
                }
                DSSFunction.openShopShowCase((Player) sender, args[1]);
                return false;
            }
        }
        if (args[0].equals("가격") || args[0].equalsIgnoreCase("price")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_slot"));
                return false;
            }
            if (args.length == 3) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_buy_or_sell"));
                return false;
            }
            if (args.length == 4) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_price"));
                return false;
            }
            int slot;
            double price;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + lang.get("shop_cmd_slot_must_be_number"));
                return false;
            }
            boolean isBuying;
            if (args[3].equalsIgnoreCase("b")) {
                isBuying = true;
            } else if (args[3].equalsIgnoreCase("s")) {
                isBuying = false;
            } else {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_buy_or_sell_unvalid"));
                return false;
            }
            try {
                price = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + lang.get("shop_cmd_price_must_be_number"));
                return false;
            }
            if (isBuying) {
                DSSFunction.setShopPrice(sender, slot, price, args[1]);
            } else {
                DSSFunction.setShopSellPrice(sender, slot, price, args[1]);
            }
            return false;
        }
        if (args[0].equals("타이틀") || args[0].equalsIgnoreCase("title")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_title"));
                return false;
            }
            DSSFunction.setTitle(sender, args[1], args);
            return false;
        }
        if (args[0].equals("초기화") || args[0].equalsIgnoreCase("reset")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            DSSFunction.clearShop(sender, args[1]);
            return false;
        }
        if (args[0].equals("삭제") || args[0].equalsIgnoreCase("delete")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + lang.get("shop_cmd_require_name"));
                return false;
            }
            DSSFunction.removeShop(sender, args[1]);
            return false;
        }
        if (args[0].equals("전체삭제") || args[0].equalsIgnoreCase("deleteall")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            DSSFunction.removeAllShop(sender);
            return false;
        }
        if (args[0].equals("목록") || args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("dss.list")) {
                sender.sendMessage(prefix + lang.get("shop_cmd_permission_required"));
                return false;
            }
            sender.sendMessage(prefix + lang.get("shop_cmd_list_header"));
            plugin.shops.keySet().forEach(s -> sender.sendMessage(prefix + s));
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
