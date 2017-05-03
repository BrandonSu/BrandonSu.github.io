
public class symbol {
	int loc;
	boolean isUsed;
	String warning;
	int module;
	
	public symbol(int loc, boolean isUsed, String warning, int module){
		this.loc = loc;
		this.isUsed = isUsed;
		this.warning = warning;
		this.module = module;
	}
	public symbol(int loc, int module){
		this.loc = loc;
		this.isUsed = false;
		this.warning = null;
		this.module = module;
	}
	public symbol(boolean isUsed, int module){
//		this.loc = this.loc;
		this.isUsed = isUsed;
		this.warning = null;
		this.module = module;
	}
	public symbol(int loc, boolean isUsed, int module){
		this.loc = loc;
		this.isUsed = isUsed;
		this.warning = null;
		this.module = module;
	}

}
