package com.darksoldier1404.dss.commands;

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "/상점 생성 <이름> <줄> - 상점을 생성합니다.");
                sender.sendMessage(prefix + "/상점 타이틀 <이름> <타이틀> - 상점의 타이틀을 설정합니다.");
                sender.sendMessage(prefix + "/상점 진열 <이름> - 상점의 물품을 진열합니다.");
                sender.sendMessage(prefix + "/상점 가격 <이름> <슬롯> <B/S> <가격> - 해당 상점의 해당 슬롯의 구매/판매 가격을 설정합니다.");
                sender.sendMessage(prefix + "/상점 목록 - 상점 목록을 확인합니다.");
                sender.sendMessage(prefix + "/상점 초기화 <이름> - 상점의 물품을 초기화합니다.");
                sender.sendMessage(prefix + "/상점 삭제 <이름> - 상점을 삭제합니다.");
                sender.sendMessage(prefix + "/상점 전체삭제 - 모든 상점을 삭제합니다.");
            }
            if (sender.hasPermission("dss.list")) {
                sender.sendMessage(prefix + "/상점 오픈 <이름> <닉네임> - 해당 상점을 해당 유저에게 오픈합니다.");
            }
            sender.sendMessage(prefix + "/상점 오픈 <이름> - 해당 상점을 오픈합니다.");
            return false;
        }
        if (args[0].equals("오픈")) {
            if (args.length == 1) {
                sender.sendMessage(prefix + "오픈할 상점의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + "콘솔에서는 사용 불가합니다.");
                    return false;
                }
                DSSFunction.openShop((Player) sender, args[1]);
                return false;
            }
            if (args.length == 3) {
                if (!sender.hasPermission("dss.admin")) {
                    sender.sendMessage(prefix + "권한이 없습니다.");
                    return false;
                }
                DSSFunction.openShop(sender, args[1], args[2]);
                return false;
            }
        }
        if (args[0].equals("생성")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "생성할 상점의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + "생성할 상점의 줄을 입력해주세요.");
                return false;
            }
            if (args.length == 3) {
                DSSFunction.createShop(sender, args[1], args[2]);
                return false;
            }
        }
        if (args[0].equals("진열")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "진열할 상점의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + "콘솔에서는 사용 불가합니다.");
                    return false;
                }
                DSSFunction.openShopShowCase((Player) sender, args[1]);
                return false;
            }
        }
        if (args[0].equals("가격")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "상점의 이름을 입력해주세요");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + "슬롯을 입력해주세요.");
                return false;
            }
            if (args.length == 3) {
                sender.sendMessage(prefix + "B 또는 S를 입력하여 가격 옵션을 선택해주세요.");
                return false;
            }
            if (args.length == 4) {
                sender.sendMessage(prefix + "가격을 입력해주세요.");
                return false;
            }
            int slot;
            double price;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + "옳바르지 않은 슬롯입니다.");
                return false;
            }
            boolean isBuying;
            if (args[3].equalsIgnoreCase("b")) {
                isBuying = true;
            } else if (args[3].equalsIgnoreCase("s")) {
                isBuying = false;
            } else {
                sender.sendMessage(prefix + "가격 옵션이 옳바르지 않습니다.");
                return false;
            }
            try {
                price = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + "옳바르지 않은 가격입니다.");
                return false;
            }
            if (isBuying) {
                DSSFunction.setShopPrice(sender, slot, price, args[1]);
            } else {
                DSSFunction.setShopSellPrice(sender, slot, price, args[1]);
            }
            return false;
        }
        if (args[0].equals("타이틀")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "설정할 상점의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + "설정할 상점의 타이틀을 입력해주세요.");
                return false;
            }
            DSSFunction.setTitle(sender, args[1], args);
            return false;
        }
        if (args[0].equals("초기화")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "초기화할 상점의 이름을 입력해주세요.");
                return false;
            }
            DSSFunction.clearShop(sender, args[1]);
            return false;
        }
        if (args[0].equals("삭제")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "삭제할 상점의 이름을 입력해주세요.");
                return false;
            }
            DSSFunction.removeShop(sender, args[1]);
            return false;
        }
        if (args[0].equals("전체삭제")) {
            if (!sender.hasPermission("dss.admin")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            DSSFunction.removeAllShop(sender);
            return false;
        }
        if (args[0].equals("목록")) {
            if (!sender.hasPermission("dss.list")) {
                sender.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            sender.sendMessage(prefix + "<<< 상점 목록 >>>");
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
