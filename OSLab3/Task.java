

import java.util.ArrayList;

public class Task {
	ArrayList<Activity> activities;
	String status;
	int id;
	int iterator; // to iterate through activities
	int claim; //initial claim
	int finishTime;
	int waitTime;
	double percentage;
	boolean aborted;
	
	
	public Task(){
		this.activities = new ArrayList<Activity>();
		this.status = "ok";
		this.iterator = 0;
		this.id = -10;
		this.claim = -10;
		this.finishTime = -10;
		this.waitTime = 0;
		this.aborted = false;
	}
	
	public Task(String status){
		this.status = status;
		this.activities = new ArrayList<Activity>();
		this.iterator = 0;
		this.id = -10;
		this.claim = -10;
		this.finishTime = -10;
		this.waitTime = 0;
		this.aborted = false;
		
	}
	public Task(int id){
		this.status = "ok";
		this.activities = new ArrayList<Activity>();
		this.iterator = 0;
		this.id = id;
		this.claim = -10;
		this.finishTime = -10;
		this.waitTime = 0;
		this.aborted = false;
		
	}
	public Task(int id, int claim){
		this.status = "ok";
		this.activities = new ArrayList<Activity>();
		this.iterator = 0;
		this.id = id;
		this.claim = claim;
		this.finishTime = -10;
		this.waitTime = 0;
		this.aborted = false;
		
	}
	
}
