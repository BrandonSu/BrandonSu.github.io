

import java.io.File;
import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

class CompareId implements Comparator<Task> {
	// compares tasks based on their id
	public int compare(Task t1, Task t2) {
		if (t1.id > t2.id) {
			return 1;
		} else { //error check, tasks shouldnt have same id
			return -1;
		}
	}
}

public class Assignment3 {
	public static boolean verbose = true;
	public static void main(String[] args) throws FileNotFoundException {
		/*
		  String input = args[args.length-1];
		if (args[0].equals("--verbose")) {
			verbose = true;
		} else {
			verbose = false;
		}
		FileReader file = new FileReader(input);
		Scanner in = new Scanner(file);
		 */


		//for eclipse testing
/*
		System.out.println("Please enter filename");
		Scanner userIn = new Scanner(java.lang.System.in);
		String fileName = userIn.next();
		//String fileName = "input13";
		URL url = Assignment3.class.getResource(fileName);
		File f = new File(url.getPath());
		Scanner input = new Scanner(f);
*/
		
		//for mauler testing if you want verbose flag
		String filename = args[args.length-1];
		if (args[0].equals("--verbose")) {
			verbose = true;
		} else {
			verbose = false;
		}
		FileReader file = new FileReader(filename);
		Scanner input = new Scanner(file);

		int numTask = input.nextInt(); // how many tasks
		int numRes = input.nextInt(); //how many resource types
		/*	ArrayList<Task> ok = new ArrayList<Task>();
		for (int i = 0; i<numTask; i++){ //initialize tasks with ids
			ok.add(new Task(i)); 
		}*/
		//ArrayList<Task> blocked = new ArrayList<Task>();



		int [] resources = new int[numRes];//the number of each type of resource
		int [] resources2 = new int[numRes];
		for (int i = 0; i < numRes; i++){
			int x = input.nextInt();
			resources[i] = x;
			resources2[i] = x;
		}
		//resources[0] = 4
		ArrayList<Task> tasks = new ArrayList<Task>(); //arraylist of tasks
		ArrayList<Task> tasks2 = new ArrayList<Task>();
		for (int i = 0; i < numTask; i++){
			tasks.add(new Task(i+1)); //give it id starting with 0
			tasks2.add(new Task(i+1));
		}


		//		ArrayList<Activity> acts = new ArrayList<Activity>(); // arraylist of each like activity line
		while (input.hasNext()){
			Activity act = new Activity();
			//initiate  1 0 1 3
			act.type = input.next(); //initiate
			int id = input.nextInt(); //1
			act.delay = input.nextInt(); //0
			act.resourceType = input.nextInt(); //1
			act.resourceAmount = input.nextInt(); //3
			tasks.get(id-1).activities.add(act); //id - 1
			tasks2.get(id-1).activities.add(act); //id - 1
		}

		// now lets make the allocation chart 2D array thing
		int[][] chart = new int[numTask][numRes];
		int[][] chart2 = new int[numTask][numRes];
	//	int[][] claimChart = new int[numTask][numRes];
		//how much is requested
		/*for (int i = 0; i <numProc; i++){
			for (int j = 0; j<numRes; j++){

			}
		}*/

		FIFO(chart, tasks, resources);//, ok, blocked); //activities is an arraylist of arraylists of activities for each process
		//so if there are 2 processes, activities.get(0) will be the arraylist of all the commands for the first process
		//and activities.get(1) will be for second process

		bankers(chart2, tasks2, resources2);
		/*
		for(int i = 0; i < chart2.length; i++){
			for (int j = 0; j < chart2[i].length; j++){
				//	System.out.println(chart2[i][j]);
			}
		}*/
		input.close();
		//userIn.close();
	}
	public static void FIFO(int[][] chart, ArrayList<Task> tasks, int[] resources){//ArrayList<ArrayList<Activity>> activities, int[] resources, ArrayList<Task> ok, ArrayList<Task> blocked){
		int time = 0;

		//check claim table, thats what were measuring against
		//basically check currently if there is at least as much as the tasks initial calim before satisfying request

		//tasks is an arraylist of tasks
		//each task has an arraylist of activities which is like for it

		//	int[][] release = new int[activities.size()][resources.length];
		boolean completed = false;

		//	boolean abort = true;
		boolean fin = false;
		CompareId compareId = new CompareId();

		int numTask = tasks.size();
		int numRes = resources.length;

		int[][] requests = new int[tasks.size()][resources.length]; // requests that werent able to complete
		int[] released = new int[resources.length];
		int[] resAvail = resources; //make a copy of resources
		ArrayList<Task> aux = new ArrayList<Task>();
		ArrayList<Task> blocked = new ArrayList<Task>();
		ArrayList<Task> done = new ArrayList<Task>();
		ArrayList<Task> aborted = new ArrayList<Task>();

		int[][] claimChart = new int[numTask][numRes]; // so if we have 4 tasks, 1 resource
		//claimchart[0][0] refers to the claim of task 1
		/*	
		for (int i = 0; i < numTask; i++){ //initialize claim chart
			if (tasks.get(i).activities.get(0).type.equals("initiate")){
				int resType = tasks.get(i).activities.get(0).resourceType;
				int resAmount = tasks.get(i).activities.get(0).resourceAmount;
				claimChart[i][resType-1] = resAmount;
			}
		}
		for (int i = 0; i < numTask; i++){ //intialize claim field too not sure which one to use yet
			if (tasks.get(i).activities.get(0).type.equals("initiate")){
				tasks.get(i).claim = tasks.get(i).activities.get(0).resourceAmount;
			}
		}*/

		// have arraylists of ok and blocked, will have to remove things occasionally
		while (true){ // asdfads
			Collections.sort(tasks, compareId); // sort tasks by id
			
			boolean abort = true; //true state = need to abort, anytime a request/release/whatever is granted, set to false
			
			//printing
			if (verbose){
				System.out.printf("During %d-%d ", time, time + 1);
				System.out.print("(");
				for (int i = 0; i < resAvail.length; i++) {
					if (i != resAvail.length - 1) {
						System.out.print(resAvail[i] + ", ");
					} else {
						System.out.print(resAvail[i] + " ");
					}
					System.out.print("units available)\n");
				}
			}
			//	System.out.println("task size: "+tasks.size());
			//		System.out.println("blocked size: "+blocked.size());

			if (!blocked.isEmpty()){
				if (verbose){
					System.out.println("\tFirst check blocked requests:");
				}
				for (int i = 0; i < blocked.size(); i++) { //increment waiting times
					blocked.get(i).waitTime++;
				}
				for (int i = 0; i < blocked.size(); i++){//basically check if can do the blocked list ones

					int it = blocked.get(i).iterator;
					int id= blocked.get(i).id;
					if (blocked.get(i).activities.get(it).type.equals("request")){
						int resType = blocked.get(i).activities.get(it).resourceType; //type of resource (will have to -1)
						int resAmount = blocked.get(i).activities.get(it).resourceAmount;
						if ( resAmount  > resAvail[resType-1]){//if claiming more than available
							//do nothing basically
							if (verbose){
								System.out.printf("\t\tTask %d still unable to complete its request\n", blocked.get(i).id);
							}
						} else{// grant the request and add to aux arraylist
							resAvail[resType-1] -= resAmount;
							//	claimChart[id-1][resType-1] -= resAmount; //update claim ttable too
							blocked.get(i).iterator++;
							if (verbose){	
								System.out.printf("\t\tTask %d completes its request\n", blocked.get(i).id);
							}
							aux.add(blocked.remove(i));
							i--;
							abort = false;
						}
					}
				}
			}

			//check if request + current > claim

			for (int i = 0; i < tasks.size(); i++){
				int it = tasks.get(i).iterator;
				int id = tasks.get(i).id;

				//	int delay = tasks.get(i).activities.get(it).delay;
				if (it < tasks.get(i).activities.size()){ //make sure you dont iterate past the activities list size
					int delay = tasks.get(i).activities.get(it).delay;
					int resType = tasks.get(i).activities.get(it).resourceType; //type of resource (will have to -1)
					int resAmount = tasks.get(i).activities.get(it).resourceAmount; //amoutn of that resource for this activity line
					if (delay >0){
						tasks.get(i).activities.get(it).delay--;
						if (verbose){
							System.out.printf("\tTask %d is delayed %d\n", tasks.get(i).id, delay);
						}
						abort = false;
					} else{
						if (tasks.get(i).activities.get(it).type.equals("initiate")){ //chekc if initate
							//HAVE TO CHECK FOR ABORTION IMMEDIATELY LIKE WHEN IT CLAIMS 5
							if (verbose){
								System.out.printf("\tTask %d completed initiate\n", tasks.get(i).id);
							}
							//	claimChart[id-1][resType-1] = resAmount;
							tasks.get(i).iterator++;
							abort = false;
							//	resAvail[resType-1] -= resAmount;  //dont forget that the type is 1 but index is 0
							//}
						} //else if its not itinitiate
						else if (tasks.get(i).activities.get(it).type.equals("request")){
							//	System.out.println("hello");
							//if request, check claim table to see if it can process request

							//check if request + current > original claim
							//or the way mine is set up, if request amount is greater than however much it still needs after
							

							if (resAvail[resType-1] >= resAmount){ // if theres enough to satisfy request for the claim aka no deadlock
								resAvail[resType-1] -= resAmount;

								claimChart[id-1][resType-1] += resAmount; //add to claim ttable
								//maybe have claim char start at 0 , add to it every time, release 
								tasks.get(i).iterator++;
								if (verbose){
									System.out.printf("\tTask %d completes its request\n", tasks.get(i).id);
								}
								abort = false;
							} else{//put into block list
								if (verbose){
									System.out.printf("\tTask %d request cannot be granted\n", tasks.get(i).id);
								}
								blocked.add(tasks.remove(i));
								i--; //have to decrement i once so that it doesnt skip the next task since we removied it
							}

						} else if (tasks.get(i).activities.get(it).type.equals("release")){ // release at the end
							abort = false;
							if (verbose){
								System.out.printf("\tTask %d releases %d unit(s)", tasks.get(i).id, resAmount);
							}
							released[resType-1] += resAmount;
			
							claimChart[id-1][resType-1] -=resAmount; //re update claim table
							tasks.get(i).iterator++;
							it = tasks.get(i).iterator; //update it for the terminate check
							delay = tasks.get(i).activities.get(it).delay; //update delay too
							if (tasks.get(i).activities.get(it).type.equals("terminate")){
								if (delay >0){
									tasks.get(i).activities.get(it).delay--;
									if (verbose){
										System.out.printf("\tTask %d is delayed %d\n", tasks.get(i).id, delay);
									}
								} else{
									if (verbose){
										System.out.printf(" and terminates at %d\n", time+1);
									}
									tasks.get(i).finishTime = time+1;
									done.add(tasks.remove(i)); //remove from tasks and add to done list
									i--; //have to decrement i once so that it doesnt skip the next task since we removied it
								}
							}else{
								if (verbose){
									System.out.println("");
								}
							}

						} else if (tasks.get(i).activities.get(it).type.equals("terminate")){
							abort = false;
							if (verbose){
								System.out.printf(" and terminates at %d\n", time+1);
							}
							tasks.get(i).finishTime = time+1;
							done.add(tasks.remove(i)); //remove from tasks and add to done list
							i--; //have to decrement i once so that it doesnt skip the next task since we removied it

						}
					}
				}
			}
			//add the released back into available
			for (int i =0; i<released.length; i++){
				resAvail[i]+=released[i];
				released[i]=0; //reset back to 0
				//THIS IS THE KEY ERROR IN MY FIFO TOO
			}
			//aborted.addAll(c)
			//add back from aux to tasks
			while (!aux.isEmpty()){
				tasks.add(aux.remove(0));
			}
			//check here to abort
			if (tasks.isEmpty() && !blocked.isEmpty() && abort){ //no more tasks, all in block or done or whatever
				// find the min id for the tasks bc u have to abort from teh start,
				//loop aborting if the abort didnt release any resources
				//otherwise move onto next cycle
				int removed = 0;
				while (removed ==0){
					Task min = new Task(numTask+1);
					for (int i =0; i< blocked.size(); i++){
						int tempID = blocked.get(i).id;
						if (tempID < min.id){
							min = blocked.get(i);
						}
					}
					//now that we've found the liek first task basically (task 1)
					int id = min.id;
					int it= min.iterator;
					int resType = min.activities.get(it).resourceType;

					//	aborted.add(blocked.remove(min));
					blocked.remove(min);
					if (verbose){
						System.out.printf("Task %d was ABORTED \n", min.id);
					}
					//System.out.println("");

					aborted.add(min);
					//remove from claim chart
					removed = claimChart[id-1][resType-1];
					resAvail[resType-1]+= removed;
				}



			}
			abort = true;




			if (tasks.isEmpty() && blocked.isEmpty()){
				break;
			}

			if (time > 20){
				break;
			}

			time++;
		} //after while loop

		//print, for each task, the time taken, the waiting time, and the percentage of time spent waiting.
		//		 total time for all tasks, the total waiting time, and the overall percentage of time spent waiting.
		Collections.sort(done, compareId);
		Collections.sort(aborted, compareId);
		int abortedCtr = 0;
		int doneCtr = 0;
		int totalTime =0;
		int totalWait = 0;
		//int avgPer = 0;
		System.out.println("\t FIFO");
		for (int i = 0; i < numTask; i++){
			if (done.get(doneCtr).id == i+1){
				//		System.out.printf("\tTask %d releases %d unit(s)", tasks.get(i).id, resAmount);

				System.out.printf("Task %d", done.get(doneCtr).id);
				totalWait +=done.get(doneCtr).waitTime;
				totalTime += done.get(doneCtr).finishTime;
				float waitTime = done.get(doneCtr).waitTime;
				float finishTime = done.get(doneCtr).finishTime;
				float percentage = waitTime/finishTime;
				percentage = percentage*100;
				int percent = Math.round(percentage);
				//	avgPer +=percent;
				System.out.printf("\t %3d %3d %3d%%", done.get(doneCtr).finishTime, done.get(doneCtr).waitTime, percent );
				System.out.println("");
				doneCtr++;
			} else if (aborted.get(abortedCtr).id ==i+1 ){
				System.out.printf("Task %d \t aborted ", aborted.get(abortedCtr).id);
				abortedCtr++;
				System.out.println("");

			}
		}
		float totTime = totalTime;
		float totWait = totalWait;
		float totPer = totWait/totTime;
		totPer*=100;		
		int avgPer = Math.round(totPer);
		System.out.printf("total \t %3d %3d %3d%% ", totalTime, totalWait, avgPer);
		System.out.println("");
	}

	public static void bankers(int[][] chart, ArrayList<Task> tasks, int[] resources){
		int time = 0;

		//check claim table, thats what were measuring against
		//basically check currently if there is at least as much as the tasks initial calim before satisfying request

		//tasks is an arraylist of tasks
		//each task has an arraylist of activities which is like for it

		//	int[][] release = new int[activities.size()][resources.length];
		boolean completed = false;

		//	boolean abort = true;
		boolean fin = false;
		CompareId compareId = new CompareId();

		int numTask = tasks.size();
		int numRes = resources.length;

		int[][] requests = new int[tasks.size()][resources.length]; // requests that werent able to complete
		int[] released = new int[resources.length];
		int[] resAvail = resources; //make a copy of resources
		ArrayList<Task> aux = new ArrayList<Task>();
		ArrayList<Task> blocked = new ArrayList<Task>();
		ArrayList<Task> done = new ArrayList<Task>();
		ArrayList<Task> aborted = new ArrayList<Task>();

		int[][] claimChart = new int[numTask][numRes]; // so if we have 4 tasks, 1 resource
		//claimchart[0][0] refers to the claim of task 1
		
		for (int i = 0; i < numTask; i++){ //initialize claim chart
			if (tasks.get(i).activities.get(0).type.equals("initiate")){
				int resType = tasks.get(i).activities.get(0).resourceType;
				int resAmount = tasks.get(i).activities.get(0).resourceAmount;
				claimChart[i][resType-1] = resAmount;
			}
		}
		for (int i = 0; i < numTask; i++){ //intialize claim field too not sure which one to use yet
			if (tasks.get(i).activities.get(0).type.equals("initiate")){
				tasks.get(i).claim = tasks.get(i).activities.get(0).resourceAmount;
			}
		}

		// have arraylists of ok and blocked, will have to remove things occasionally
		while (true){ // asdfads
			Collections.sort(tasks, compareId); // sort tasks by id
			//printing
			if (verbose){
				System.out.printf("During %d-%d ", time, time + 1);
				System.out.print("(");
				for (int i = 0; i < resAvail.length; i++) {
					if (i != resAvail.length - 1) {
						System.out.print(resAvail[i] + ", ");
					} else {
						System.out.print(resAvail[i] + " ");
					}
					System.out.print("units available)\n");
				}
			}
			//	System.out.println("task size: "+tasks.size());
			//		System.out.println("blocked size: "+blocked.size());

			if (!blocked.isEmpty()){
				if (verbose){
					System.out.println("\tFirst check blocked requests:");
				}
				for (int i = 0; i < blocked.size(); i++) { //increment waiting times
					blocked.get(i).waitTime++;
				}
				for (int i = 0; i < blocked.size(); i++){//basically check if can do the blocked list ones

					int it = blocked.get(i).iterator;
					int id= blocked.get(i).id;
					if (blocked.get(i).activities.get(it).type.equals("request")){
						int resType = blocked.get(i).activities.get(it).resourceType; //type of resource (will have to -1)
						int resAmount = blocked.get(i).activities.get(it).resourceAmount;
						boolean safe = true;
						for (int j = 0; j<resAvail.length; j++){
							if (resAvail[j]< claimChart[id-1][j]){
								safe = false;
							}
						}
						if (safe){
							resAvail[resType-1] -= resAmount;
							claimChart[id-1][resType-1] -= resAmount; //update claim ttable too
							blocked.get(i).iterator++;
							if (verbose){
								System.out.printf("\t\tTask %d completes its request\n", blocked.get(i).id);
							}
							aux.add(blocked.remove(i));
							i--;
						}
						else {
							if (verbose){
								System.out.printf("\t\tTask %d still unable to complete its request\n", blocked.get(i).id);
							}
						}
					}
				}
			}
			//	System.out.println("");
			
			//check if request + current > claim

			for (int i = 0; i < tasks.size(); i++){
				int it = tasks.get(i).iterator;
				int id = tasks.get(i).id;

				//	int delay = tasks.get(i).activities.get(it).delay;
				if (it < tasks.get(i).activities.size()){ //make sure you dont iterate past the activities list size
					int delay = tasks.get(i).activities.get(it).delay;
					int resType = tasks.get(i).activities.get(it).resourceType; //type of resource (will have to -1)
					int resAmount = tasks.get(i).activities.get(it).resourceAmount; //amoutn of that resource for this activity line
					if (delay >0){
						tasks.get(i).activities.get(it).delay--;
						if (verbose){
							System.out.printf("\tTask %d is delayed %d\n", tasks.get(i).id, delay);
						}
					} else{
						if (tasks.get(i).activities.get(it).type.equals("initiate")){ //chekc if initate
							//HAVE TO CHECK FOR ABORTION IMMEDIATELY LIKE WHEN IT CLAIMS 5
							if (claimChart[id-1][resType-1] > resAvail[resType-1]){//if claim is higher than total amount
								if (verbose){
									System.out.printf("\tTask %d is aborted\n", tasks.get(i).id);
								}
								aborted.add(tasks.remove(i));

								i--;
							} else{
								if (verbose){
									System.out.printf("\tTask %d completed initiate\n", tasks.get(i).id);
								}
								claimChart[id-1][resType-1] = resAmount;
								tasks.get(i).iterator++;
								//	resAvail[resType-1] -= resAmount;  //dont forget that the type is 1 but index is 0
							}
						} //else if its not itinitiate
						else if (tasks.get(i).activities.get(it).type.equals("request")){
							//	System.out.println("hello");
							//if request, check claim table to see if it can process request

							//check if request + current > original claim
							//or the way mine is set up, if request amount is greater than however much it still needs after
							if (resAmount > claimChart[id-1][resType-1]){
								if (verbose){
									System.out.printf("\tTask %d aborted for requesting too much\n", tasks.get(i).id);
								}
								aborted.add(tasks.remove(i));
								//also have to release the resources
								released[resType-1] += resAmount;
								i--;
							}

							else{
								boolean safe = true;
								for (int j = 0; j<resAvail.length; j++){
									if (resAvail[j]< claimChart[id-1][j]){
										safe = false;
									}
								}
								if (safe){
									resAvail[resType-1] -= resAmount;
									claimChart[id-1][resType-1] -= resAmount; //update claim ttable too
									tasks.get(i).iterator++;
									if (verbose){
										System.out.printf("\tTask %d completes its request\n", tasks.get(i).id);
									}
								} else{
									if (verbose){
										System.out.printf("\tTask %d request cannot be granted\n", tasks.get(i).id);
									}
									blocked.add(tasks.remove(i));
									i--; //have to decrement i once so that it doesnt skip the next task since we removied it
								}
							}
						} else if (tasks.get(i).activities.get(it).type.equals("release")){ // release at the end
							if (verbose){
								System.out.printf("\tTask %d releases %d unit(s)", tasks.get(i).id, resAmount);
							}
							released[resType-1] += resAmount;
							//	System.out.println("pending to be released "+released[resType-1]);
							claimChart[id-1][resType-1] +=resAmount; //re update claim table
							tasks.get(i).iterator++;
							it = tasks.get(i).iterator; //update it for the terminate check
							delay = tasks.get(i).activities.get(it).delay; //update delay too
							if (tasks.get(i).activities.get(it).type.equals("terminate")){
								if (delay >0){
									tasks.get(i).activities.get(it).delay--;
									if (verbose){
										System.out.printf("\tTask %d is delayed %d\n", tasks.get(i).id, delay);
									}
								} else{
									if (verbose){
										System.out.printf(" and terminates at %d\n", time+1);
									}
									tasks.get(i).finishTime = time+1;
									done.add(tasks.remove(i)); //remove from tasks and add to done list
									i--; //have to decrement i once so that it doesnt skip the next task since we removied it

								}
							}else{
								System.out.println("");
							}
						} else if (tasks.get(i).activities.get(it).type.equals("terminate")){
							if (verbose){
								System.out.printf(" and terminates at %d\n", time+1);
							}
							tasks.get(i).finishTime = time+1;
							done.add(tasks.remove(i)); //remove from tasks and add to done list
							i--; //have to decrement i once so that it doesnt skip the next task since we removied it

						}
					}
				}
			}
		
			//add the released back into available
			for (int i =0; i<released.length; i++){
				resAvail[i]+=released[i];
				released[i]=0; //reset back to 0
				//THIS IS THE KEY ERROR IN MY FIFO TOO
			}

			//add back from aux to tasks
			while (!aux.isEmpty()){
				tasks.add(aux.remove(0));
			}
			if (tasks.isEmpty() && blocked.isEmpty()){
				break;
			}
			if (time > 10){
				break;
			}

			time++;
		} //after while loop

		//print, for each task, the time taken, the waiting time, and the percentage of time spent waiting.
		//		 total time for all tasks, the total waiting time, and the overall percentage of time spent waiting.
		Collections.sort(done, compareId);
		Collections.sort(aborted, compareId);
		int abortedCtr = 0;
		int doneCtr = 0;
		int totalTime =0;
		int totalWait = 0;
		//int avgPer = 0;
		System.out.println("\t Banker");
		for (int i = 0; i < numTask; i++){
			if (done.get(doneCtr).id == i+1){
				//		System.out.printf("\tTask %d releases %d unit(s)", tasks.get(i).id, resAmount);

				System.out.printf("Task %d", done.get(doneCtr).id);
				totalWait +=done.get(doneCtr).waitTime;
				totalTime += done.get(doneCtr).finishTime;
				float waitTime = done.get(doneCtr).waitTime;
				float finishTime = done.get(doneCtr).finishTime;
				float percentage = waitTime/finishTime;
				percentage = percentage*100;
				int percent = Math.round(percentage);
				//	avgPer +=percent;
				System.out.printf("\t %3d %3d %3d%%", done.get(doneCtr).finishTime, done.get(doneCtr).waitTime, percent );
				System.out.println("");
				doneCtr++;
			} else if (aborted.get(abortedCtr).id ==i+1 ){
				System.out.printf("Task %d \t aborted ", aborted.get(abortedCtr).id);
				abortedCtr++;
				System.out.println("");

			}
		}
		float totTime = totalTime;
		float totWait = totalWait;
		float totPer = totWait/totTime;
		totPer*=100;		
		int avgPer = Math.round(totPer);
		System.out.printf("total \t %3d %3d %3d%% ", totalTime, totalWait, avgPer);

	}
}
