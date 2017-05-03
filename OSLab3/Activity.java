

public class Activity {
	String type;
	int process;
	int delay;
	int resourceType;
	int resourceAmount;
	
	public Activity(String type){
		this.type=type;
		this.process = -10;
		this.delay = -10;
		this.resourceType = -10;
		this.resourceAmount = -10;
	}
	public Activity(){
		this.type=null;
		this.process = -10;
		this.delay = -10;
		this.resourceType = -10;
		this.resourceAmount = -10;
	}
	public Activity(String type, int delay, int resourceType, int resourceAmount){
		this.type=type;
		this.process = -10;
		this.delay = delay;
		this.resourceType = resourceType;
		this.resourceAmount = resourceAmount;
	}

}
