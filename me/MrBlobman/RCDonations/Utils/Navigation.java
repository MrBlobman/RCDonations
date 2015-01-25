package me.MrBlobman.RCDonations.Utils;

import java.util.Stack;

import me.MrBlobman.RCDonations.GUI.Menu;

public class Navigation {
	private Stack<Menu> nav = new Stack<Menu>();
	private Menu root;
	
	public Navigation(Menu root){ 
		this.root = root;
	}
	
	/**
	 * Adds the menu to the top of the nav
	 * @param menu the menu to push on the nav
	 */
	public void addTop(Menu menu){
		nav.push(menu);
	}
	
	/**
	 * Safe call, removes the top item in the nav
	 */
	public void removeTop(){
		if (!nav.isEmpty()){
			nav.pop();
		}
	}
	
	/**
	 * Pulls the top Menu from the nav and returns it to caller
	 * @return root if there is no top, or the top menu in the nav
	 */
	public Menu pullTop(){
		if (!nav.isEmpty()){
			return nav.pop();
		}
		return this.root;
	}
	
	/**
	 * Looks at the top Menu in the nav and returns it to caller
	 * @return root if there is no top, or the top menu in the nav
	 */
	public Menu getCurrent(){
		if (!nav.isEmpty()){
			return nav.peek();
		}
		return this.root;
	}
	
	/**
	 * Check if the nav is currently at the root level
	 * @return
	 */
	public boolean isInRoot(){
		if (nav.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
}
