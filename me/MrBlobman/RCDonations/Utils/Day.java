package me.MrBlobman.RCDonations.Utils;

public enum Day {
	
	SUNDAY("Sunday", 0), MONDAY("Monday", 1), TUESDAY("Tuesday", 2), WEDNESDAY("Wednesday", 3), THRUSDAY("Thursday", 4), FRIDAY("Friday", 5), SATURDAY("Saturday", 6);
	
	private String name;
	private int id;
	
	Day(String name, int id){
		this.name = name;
		this.id = id;
	}
	
	public int id(){
		return this.id;
	}
	
	public static Day fromId(int id){
		switch (id) {
		case 0:
			return Day.SUNDAY;
		case 1:
			return Day.MONDAY;
		case 2:
			return Day.TUESDAY;
		case 3:
			return Day.WEDNESDAY;
		case 4:
			return Day.THRUSDAY;
		case 5:
			return Day.FRIDAY;
		case 6:
			return Day.SATURDAY;
		default:
			return Day.SUNDAY;
		}
	}
	
	@Override
	public String toString(){
		return name;
	}
}
