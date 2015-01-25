package me.MrBlobman.RCDonations.KitBuilding;

import me.MrBlobman.RCDonations.Items.KitItem;

public interface Step {
	
	public void start(Runnable callWhenDone);
	public KitItem getKitItem();
	public String getDataKey();
	
}
