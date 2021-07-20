package com.mcsunnyside.qsareashop;


import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.events.notify.UnrentedRegionEvent;
import me.wiefferink.areashop.regions.RentRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.event.ShopCreateEvent;
import org.maxgamer.quickshop.event.ShopPreCreateEvent;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class QSRRAreaShopAddon extends JavaPlugin implements Listener {
	QuickShop qs = null;
	AreaShop areaShop = null;
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		qs = (QuickShop) Bukkit.getPluginManager().getPlugin("QuickShop");
		areaShop = (AreaShop) Bukkit.getPluginManager().getPlugin("AreaShop");
		//Verify this is reremake or reremake's forks
		try {
			QuickShop.getVersion();
		}catch (Exception e) {
			getLogger().severe("QSRR AreaShop Addon only can support under QuickShop-Reremake by Ghost_chu, we can't promise it can working perfactly under other forks");
		}
		saveDefaultConfig();
		getLogger().info("Successfully loaded QSRR's AreaShop addon.");
		
	}
	@Override
	public void onDisable() {
	}
	@EventHandler
	public void createShop(ShopPreCreateEvent e) {
			UUID playerUUID = e.getPlayer().getUniqueId();
			List<RentRegion> regions = me.wiefferink.areashop.tools.Utils.getImportantRentRegions(e.getLocation());	
			boolean passTheRegionCheck = false;	
			for (RentRegion rentRegion : regions) {	
				if(rentRegion.getRenter()!=null&&rentRegion.getRenter().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
				if(rentRegion.getOwner()!=null&&rentRegion.getOwner().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
				if(rentRegion.getLandlord()!=null&&rentRegion.getLandlord().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
			}	
			if(!passTheRegionCheck && !e.getPlayer().hasPermission("quickshop.addon.areashop.bypass")) {	
				e.setCancelled(true);
			}	
	}
	@EventHandler
	public void createShop(ShopCreateEvent e) {
			UUID playerUUID = e.getCreator();
			Player player = Bukkit.getPlayer(e.getCreator());
			List<RentRegion> regions = me.wiefferink.areashop.tools.Utils.getImportantRentRegions(e.getShop().getLocation());	
			boolean passTheRegionCheck = false;	
			for (RentRegion rentRegion : regions) {	
				if(rentRegion.getRenter()!=null&&rentRegion.getRenter().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
				if(rentRegion.getOwner()!=null&&rentRegion.getOwner().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
				if(rentRegion.getLandlord()!=null&&rentRegion.getLandlord().toString().equals(playerUUID.toString())) {
					passTheRegionCheck=true;	
					break;	
				}	
			}	
			if(!passTheRegionCheck && !player.hasPermission("quickshop.addon.areashop.bypass")) {
				e.setCancelled(true);
			}	
	}
	@EventHandler
	public void unRentedArea(UnrentedRegionEvent e) {
		Vector areaMaxVector;
		Vector areaMinVector;
		int minX;
		int maxX;
		int minY;
		int maxY;
		int minZ;
		int maxZ;
		try {
		areaMaxVector = e.getRegion().getMaximumPoint();
		areaMinVector = e.getRegion().getMinimumPoint();
		minX = areaMinVector.getBlockX();
		maxX = areaMaxVector.getBlockX();
		minY = areaMinVector.getBlockY();
		maxY = areaMaxVector.getBlockY();
		minZ = areaMinVector.getBlockZ();
		maxZ = areaMaxVector.getBlockZ();
		}catch (Exception ex) {
			getLogger().warning("You are using not incompatible AreaShop, this feature will cannot working!!!");
			getLogger().warning("Please use 2.5.0#271 or higher build!");
			getLogger().warning("You can download our recommend AreaShop build at there: https://github.com/Ghost-chu/QuickShop-Reremake/raw/master/lib/AreaShop.jar");
			return;
		}
		Iterator<Shop> shops = qs.getShopManager().getShopIterator();
		while (shops.hasNext()) {
			Shop shop = shops.next();
			java.util.List<Shop> waitingRemove = new ArrayList<>();
			if(shop.getLocation().getWorld().getName().equals(e.getRegion().getWorld().getName())) {
				int bX = shop.getLocation().getBlockX();
				int bY = shop.getLocation().getBlockY();
				int bZ = shop.getLocation().getBlockZ();
				if(bX>=minX && bX<=maxX) {
					if(bY>=minY && bY<=maxY) {
						if(bZ>=minZ && bZ<=maxZ) {
							//In region, we need remove that shop.
							waitingRemove.add(shop);
						}
					}
				}
			}
			for (Shop removeShop : waitingRemove) {
				removeShop.delete();
				try {
					qs.getDatabaseHelper().removeShop(shop.getLocation().getWorld().getName(), shop.getLocation().getBlockX(), shop.getLocation().getBlockY(), shop.getLocation().getBlockZ());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
				
			
		}
	}
}
