package me.MrBlobman.RCDonations.Utils;

public class InventoryUtils {
	/**
	 * NOTE: values greater than 54 will be set to 54 as this is the maximum inventory size
	 * @param numberOfItems the amount of items your wish to put into the inventory
	 * @return the smallest number less than or equal to 54 that is divisible by 9
	 */
	public static int getInvSize(int numberOfItems) throws IllegalArgumentException{
		if (numberOfItems > 54){
			throw new IllegalArgumentException("Value given is larger than 54, the maximum size for an inventory.");
		}else if (numberOfItems <= 9){
			return 9;
		}
		return numberOfItems%9!=0 ? (9-numberOfItems%9) + numberOfItems : numberOfItems;
	}
}
