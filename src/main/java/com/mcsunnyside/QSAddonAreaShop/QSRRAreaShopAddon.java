package com.mcsunnyside.QSAddonAreaShop;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Database.DatabaseHelper;
import org.maxgamer.quickshop.Shop.Shop;
import org.maxgamer.quickshop.Util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.events.notify.UnrentedRegionEvent;

public class QSRRAreaShopAddon extends JavaPlugin implements Listener {
	boolean fail2load = false;
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
			getLogger().severe("QSRR AreaShop Addon only can running under QuickShop-Reremake 1.3.3+ by Ghost_chu.");
			fail2load=true;
			Bukkit.getPluginManager().disablePlugin(this);
		}finally {
			if(fail2load)
				return;
		}
		if(fail2load)
			return;
		saveDefaultConfig();
		getLogger().info("Successfully loaded QSRR's AreaShop addon.");
		
	}
	@Override
	public void onDisable() {
		if(fail2load)
			return;
	}
	@EventHandler
	public void unRentedArea(UnrentedRegionEvent e) {
		Vector areaMaxVector = null;
		Vector areaMinVector = null;
		int minX = 0;
		int maxX= 0;
		int minY = 0;
		int maxY = 0;
		int minZ = 0;
		int maxZ = 0;
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
			this.getLogger().warning("You are using not incompatible AreaShop, this feature will cannot working!!!");
			this.getLogger().warning("Please use 2.5.0#271 or higher build!");
			this.getLogger().warning("You can download our recommend AreaShop build at there: https://github.com/Ghost-chu/QuickShop-Reremake/raw/master/lib/AreaShop.jar");
			return;
		}
		Iterator<Shop> shops = qs.getShopManager().getShopIterator();
		while (shops.hasNext()) {
			Shop shop = shops.next();
			java.util.List<Shop> waitingRemove = new ArrayList<Shop>();
			if(shop.getLocation().getWorld().getName()==e.getRegion().getWorld().getName()) {
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
				Util.debugLog("Removed shop at:["+shop.getLocation().toString()+"] cause AreaShop region unrented!");
				removeShop.delete();
				try {
					DatabaseHelper.removeShop(qs.getDB(), shop.getLocation().getBlockX(), shop.getLocation().getBlockY(), shop.getLocation().getBlockZ(), shop.getLocation().getWorld().getName());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
				
			
		}
	}
}