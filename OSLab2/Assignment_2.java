package scheduler;
import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Assignment_2 {
	public static int randCtr = 0;	
	static boolean verb = false;
	public static int time = 0;
	public static int randomOS(int u) throws FileNotFoundException {
	/*	URL url = Assignment_2.class.getResource("random-numbers");*/
		File f = new File("random-numbers");
		Scanner input = new Scanner(f);
		int x=0;
		int ctr = randCtr;
		while (ctr >=0){
			x = input.nextInt();	
			ctr--;
		}
		input.close();
		int y = 1+(x%u);
		//	randCtr++;
		return y;
	}
	public static void main(String[] args) throws FileNotFoundException {		
		/*	if (args[0].equals("hello")) { do the arg test thing
			System.out.println("hi");
		}*/
		System.out.println("Please enter filename");
		/*	Scanner userIn = new Scanner(java.lang.System.in);
		String fileName = userIn.next();*/

		if (args[0].equals("--verbose")){
			verb = true;
		}	
		String fileName = args[args.length-1];

		/*URL url = Assignment_2.class.getResource(fileName);*/
		File f = new File(fileName);
		Scanner input = new Scanner(f);
		int numProc = input.nextInt();
		//int start; //A
		//int cpuBurst; //B -> randomOS(B) to get a random burst time in between the range
		//int cpuTotal; //C -> how many times CPU burst needs to be done
		//int ioBurst; //M -> you only do the io burst in between CPU bursts
		ArrayList<Proc> pros = new ArrayList<Proc>();

		//	Proc process = new Proc();
		for (int i = 0; i<numProc; i++){ // loop through each process
			pros.add(new Proc());
			pros.get(i).start = input.nextInt();
			pros.get(i).cpuBurstBase = input.nextInt();
			//pros.get(i).cpuBurstBase =cpuB;
			pros.get(i).cpuTotal = input.nextInt();
			pros.get(i).ioBurstBase = input.nextInt();
			//pros.add(process);
		}
		String det = args[args.length-2];
		int randCtr =0;
		int time =0;
		int[] totals = new int[pros.size()];
		int[] waitingTimes = new int[pros.size()];
		ArrayList<Proc> ready = new ArrayList<Proc>();
		ArrayList<Proc> running = new ArrayList<Proc>();
		ArrayList<Proc> blocked = new ArrayList<Proc>(pros.size());

		ArrayList<Proc> done = new ArrayList<Proc>();
		ArrayList<Proc> unstarted = new ArrayList<Proc>();

		float ioCtr=0;
		float cpuCtr = 0;
		int[] ioTimes = new int[pros.size()];
		int[] finishingTimes =new int[pros.size()];
		int starter =0;
		float avgTurn = 0;
		float waitTotal =0;
		float timefloat = time;

		boolean skip= false;
		boolean[] extra = new boolean[pros.size()];
		int skip2 =0;
		
		if (det.equals("F")){
			System.out.print("The original input was: "+pros.size()+"  ");
			for (int i =0;i < pros.size();i++){
				System.out.print(pros.get(i).start+" "+pros.get(i).cpuBurstBase+" "+pros.get(i).cpuTotal+" "+pros.get(i).ioBurstBase+"   ");
			}
			System.out.println("");
			//first find the process that starts first
			//	ArrayList<Proc> sortedProc = new ArrayList<Proc>();
			//Sorts!!!!!!!!!!!
			Proc[] sorted = pros.toArray(new Proc[0]);
			for (int k=1;k<sorted.length;k++){	//sort by index so its in order by that, still only gets out 						
				for (int j=k; j>0; j--){ //if the iocounter goes to 0
					if (sorted[j].index < sorted[j-1].index){
						Proc temp = sorted[j];
						sorted[j] = sorted[j-1];
						sorted[j-1] = temp;		
					}
				}
			}
			
			
			//now its sorted
			System.out.print("The Sorted input was: ");
			for (int i =0;i < pros.size();i++){
				System.out.print(sorted[i].start+" "+sorted[i].cpuBurstBase+" "+sorted[i].cpuTotal+" "+sorted[i].ioBurstBase+"   ");
			}
			System.out.println("");
			while(!pros.isEmpty()){
				pros.remove(0);
			}
			for (int i =0;i < sorted.length;i++){
				pros.add(sorted[i]);
			}
			

			//	int[] totals = new int[pros.size()];
			//	int[] waitingTimes = new int[pros.size()];
			for (int g =0; g<pros.size();g++){ //initialize wait times to be 0
				totals[g] = pros.get(g).cpuTotal;
				waitingTimes[g]=0;
			}
			for (int i =0; i<pros.size();i++){
				pros.get(i).index = i;
			}
			if (verb){
				System.out.print("Before cycle "+time+": ");
				for (int m=0;m<pros.size();m++){
					System.out.printf("\t%10s ",pros.get(m).status);//+pros.get(m).cpuBurstRand+"    ");
					System.out.print("0");
				} // print unstarted line
				System.out.println();
			}
			time++;
			//	Proc[] arr = new Proc[pros.size()];



			for (int y=0; y<pros.size();y++){
				unstarted.add(pros.get(y));
			}

			if (pros.size()==1){ //IF JUST ONE
				//time++;
				//	System.out.println(pros.get(0).start);
				//	System.out.println("time"+time);
				while (pros.get(0).cpuTotal>0){ //IF JUST ONE				
					if (pros.get(0).start == time-1){
						if (!unstarted.isEmpty()){ //add to running 

							unstarted.get(0).status = "running";
							running.add(unstarted.remove(0));


							running.get(0).cpuBurstRand = randomOS(running.get(0).cpuBurstBase);				
						} 
					}else{
						if (pros.get(0).cpuTotal ==0){
							Proc temp =running.remove(0);
							temp.status = "terminated";
						}else{				
							if (!running.isEmpty()){//if was running
								running.get(0).cpuTotal--;
								cpuCtr++;
								if (running.get(0).cpuTotal ==0){
									Proc temp =running.remove(0);
									temp.status = "terminated";
									break;
								}else{
									running.get(0).cpuBurstRand--;							
									if (running.get(0).cpuBurstRand ==0){
										running.get(0).status ="blocked";
										running.get(0).ioBurstRand=randomOS(running.get(0).ioBurstBase);
										blocked.add(running.remove(0));
										//		blocked.add(running.remove(0));
										ioCtr++;
										ioTimes[0]++;
									}
								}					
							} else {
								blocked.get(0).ioBurstRand--;//	blocked.get(0).ioBurstRand--;

								if (blocked.get(0).ioBurstRand ==0){
									blocked.get(0).status ="running";
									blocked.get(0).cpuBurstRand=randomOS(blocked.get(0).cpuBurstBase);

									running.add(blocked.remove(0));
								}
							}
						}
					}
					if (verb){
						System.out.print("Before cycle "+time+": " +"\t");
						for (int m=0;m<pros.size();m++){
							System.out.printf("%10s ",pros.get(m).status);
							if (pros.get(m).status == "running"){
								System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
							} else if (pros.get(m).status == "blocked"){
								System.out.print(pros.get(m).ioBurstRand);
							}
							System.out.print("    ");
						} // print
						System.out.println();
					}
					time++;
					finishingTimes[0]++;
				}
				//time--;
			}else{ // EVERYTHING BELOW IS IF GREATER THAN 1	
				//int starter =0;
				//randCtr is determining which random numebr to use
				//	int temptempsize =0;

				while (true){			
					for (int k=0;k<pros.size();k++){ //INITIALIZE OUTSIDE THE WHILE LOOP TO OPTIMIZE AKA SKIP THE CHECK 
						if (pros.get(k).status == "unstarted" && starter ==0 && pros.get(k).start== time-1){// beginning set up/initialize
							running.add(pros.get(k));
							running.get(0).status = "running";
							pros.get(k).cpuBurstRand = randomOS(pros.get(k).cpuBurstBase);
							randCtr++;
							starter++; 
							unstarted.remove(0);
							skip = true;
						}
						if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 

							pros.get(k).status="ready";
							waitingTimes[k] = waitingTimes[k] +1;
							if (time > 1){
								extra[k] = true;
							}
							skip2++;	
							//unstarted.remove(0);
						}
					}
					if (skip){ // will only print this once
						skip = false;
						if (verb){
							System.out.print("Before cycle "+time+": ");

							for (int m=0;m<pros.size();m++){
								System.out.printf("\t%10s ",pros.get(m).status);
								if (pros.get(m).status == "running"){
									System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
								} else if (pros.get(m).status == "blocked"){
									System.out.print(pros.get(m).ioBurstRand);
								} else{
									System.out.print(waitingTimes[m]);
								}

							}
						}
						time ++;			
						System.out.println();
						//continue;
					}	
					for (int k=0;k<pros.size();k++){
						if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 

							pros.get(k).status="ready";
							waitingTimes[k] = waitingTimes[k] +1;
							if (time > 1){
								extra[k] = true;
							}
							skip2++;	
							//unstarted.remove(0);
						}
					}
					//blocked is now an arrayLIst
					// if theres something in the blocked list,		
					if (!blocked.isEmpty()){ //decrement the blocked waiting time
						ioCtr++;
						Proc[] block = blocked.toArray(new Proc[0]); //now block is a copy array of the arraylist

						for (int i=0; i<block.length; i++){
							block[i].ioBurstRand--; //decrement each of the io burst counter
						}//		blocked.get(0).ioBurstRand--;

						for (int j=0;j<pros.size(); j++){ // incrementing the iotimes counter for each process
							for (int k=0; k<block.length;k++){
								if (block[k].index == pros.get(j).index){//if indexes are same
									ioTimes[j]++;
								}
							}
						}
						for (int k=1;k<block.length;k++){							
							for (int j=k; j>0; j--){
								if (block[j].index < block[j-1].index){
									Proc temp = block[j];
									block[j] = block[j-1];
									block[j-1] = temp;		
								}
							}
						} //sorts solely by indexes, now block list is sorted based on indexes
						for (int i =0; i <block.length;i++){
							if (block[i].ioBurstRand == 0){
								block[i].status = "ready";
								ready.add(block[i]);								
								blocked.remove(block[i]);
							}//adds to ready queue, removes from blocked arraylist
						} //otherwise its still in blocked list		
					} //DONE MANAGING BLOCK LIST KINDA
					while (skip2>0){
						ready.add(unstarted.remove(0));
						skip2--;
					}
					if (!running.isEmpty()){ // if not empty
						cpuCtr++;
						if (running.get(0).cpuTotal <= 1){ // if the cputotal of current running = 1 then its done?
							Proc dun = running.remove(0);
							dun.status = "terminated";
							for (int i =0; i<pros.size(); i++){
								if (dun.index == pros.get(i).index){
									finishingTimes[i] = (time-1);
								}
							}
							//	running.get(0).cpuTotal--;
							done.add(dun); //remove from running
							if (!ready.isEmpty()){
								ready.get(0).status = "running"; // add from ready list
								Proc temp = ready.remove(0);
								/*	if (temp.cpuTotal ==0){ // but only if the temp isnt also done, otherwise ur also done
										temp.status ="terminated";

									}else{ //if from ready list its not done yet, add to running
								 */	temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
								 randCtr++;
								 running.add(temp);
							}

							//}
						}else {	//if running is not empty		
							running.get(0).cpuBurstRand--;
							running.get(0).cpuTotal--;

							if (running.get(0).cpuBurstRand ==0){ //if running goes down to 0
								running.get(0).status = "blocked";
								running.get(0).ioBurstRand = randomOS(running.get(0).ioBurstBase);
								//	System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);
								randCtr++;
								blocked.add(running.remove(0));
								//	blocked.add(running.remove(0));
								if (!ready.isEmpty()){
									running.add(ready.remove(0));
									running.get(0).status ="running";
									running.get(0).cpuBurstRand = randomOS(running.get(0).cpuBurstBase);
									//	System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);
									randCtr++;
								}
							}
						}
					}else {
						if (!ready.isEmpty()){
							Proc temp =ready.remove(0);
							temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
							randCtr++;
							temp.status ="running";
							running.add(temp);
						}else if (!blocked.isEmpty()) {
							//	break;
						}
					}
					if (blocked.isEmpty() && ready.isEmpty() && running.isEmpty() && unstarted.isEmpty()){
						break;
					}
					for (int k = 0;k<pros.size();k++){
						if (pros.get(k).status == "ready"){
							waitingTimes[k] = waitingTimes[k] +1;
							if (extra[k]){
								waitingTimes[k]--;
								extra[k] = false;
							}
						}
					}
					if (verb){
						System.out.print("Before cycle "+time+": ");
						for (int m=0;m<pros.size();m++){
							System.out.printf("\t%10s ",pros.get(m).status);
							if (pros.get(m).status == "running"){
								System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
							} else if (pros.get(m).status == "blocked"){
								System.out.print(pros.get(m).ioBurstRand);
							} else{
								System.out.print("0");
							}
							//	System.out.print("    ");

						} // print
					}
					time ++;
					System.out.println();
				}
			}
			time--;
			System.out.println();
			System.out.println("The scheduling algorithm used was First Come First Served");


			for (int j =0; j<pros.size(); j++){
				System.out.println("Process "+j+":");
				System.out.println("(A,B,C,IO) = "+pros.get(j).start+","+pros.get(j).cpuBurstBase+","+totals[j]+","+pros.get(j).ioBurstBase);
				System.out.println("Finishing time: "+finishingTimes[j]);
				System.out.println("Turnaround time: "+(finishingTimes[j]-pros.get(j).start));

				avgTurn +=(finishingTimes[j]-pros.get(j).start);
				System.out.println("I/O time: "+ioTimes[j]);
				System.out.println("Waiting time: "+waitingTimes[j]);
				System.out.println("");
			}
			//	float waitTotal =0;
			for (int q = 0; q<waitingTimes.length;q++){
				waitTotal += waitingTimes[q];
			}
			System.out.println();
			System.out.println("Summary Data:");
			System.out.println("Finishing time: "+time);
			System.out.println("CPU Utilization: "+(cpuCtr/time));
			System.out.println("I/O Utilization: "+(ioCtr/time)); //50 = blocked (w/o overlap)/94 = .53

			timefloat /= 100;
			System.out.println("Throughput: "+pros.size()/timefloat+" processes per hundred cycles");	
			System.out.println("Average turnaround time: "+avgTurn/pros.size());	
			System.out.println("Average waiting time: "+waitTotal/pros.size()); 
		}

		else if (det.equals("R")){
			//randCtr = 0;
			//	time =0;
			int quantum = 2;
			System.out.print("The original input was: "+pros.size()+"  ");
			for (int i =0;i < pros.size();i++){
				System.out.print(pros.get(i).start+" "+pros.get(i).cpuBurstBase+" "+pros.get(i).cpuTotal+" "+pros.get(i).ioBurstBase+"   ");
			}
			System.out.println("");
			//first find the process that starts first
			//	ArrayList<Proc> sortedProc = new ArrayList<Proc>();
			//Sorts!!!!!!!!!!!
			Proc[] sorted = pros.toArray(new Proc[0]);
			for (int k=1;k<sorted.length;k++){	//sort by index so its in order by that, still only gets out 						
				for (int j=k; j>0; j--){ //if the iocounter goes to 0
					if (sorted[j].index < sorted[j-1].index){
						Proc temp = sorted[j];
						sorted[j] = sorted[j-1];
						sorted[j-1] = temp;		
					}
				}
			}
			
			
			//now its sorted
			System.out.print("The Sorted input was: ");
			for (int i =0;i < pros.size();i++){
				System.out.print(sorted[i].start+" "+sorted[i].cpuBurstBase+" "+sorted[i].cpuTotal+" "+sorted[i].ioBurstBase+"   ");
			}
			while(!pros.isEmpty()){
				pros.remove(0);
			}
			for (int i =0;i < sorted.length;i++){
				pros.add(sorted[i]);
			}
			

			System.out.println("");
			//		int[] totals = new int[pros.size()];
			//		int[] waitingTimes = new int[pros.size()];
			for (int g =0; g<pros.size();g++){ //initialize wait times to be 0
				totals[g] = pros.get(g).cpuTotal;
				waitingTimes[g]=0;
			}
			for (int i =0; i<pros.size();i++){
				pros.get(i).index = i;
			}

			if(verb){
				System.out.print("Before cycle "+time+": ");
				for (int m=0;m<pros.size();m++){
					System.out.printf("\t%10s ",pros.get(m).status);//+pros.get(m).cpuBurstRand+"    ");
					System.out.print("0");
				} // print unstarted line
				System.out.println();
			}
			time++;
			Proc[] arr = new Proc[pros.size()];

			/*ArrayList<Proc> ready = new ArrayList<Proc>();
				ArrayList<Proc> running = new ArrayList<Proc>();
				ArrayList<Proc> blocked = new ArrayList<Proc>(pros.size());

				ArrayList<Proc> done = new ArrayList<Proc>();
				ArrayList<Proc> unstarted = new ArrayList<Proc>();*/

			for (int y=0; y<pros.size();y++){
				unstarted.add(pros.get(y));
			}
			/*
				float ioCtr=0;
				float cpuCtr = 0;
				int[] ioTimes = new int[pros.size()];
				int[] finishingTimes =new int[pros.size()];
				int starter =0;*/
			if (pros.size()==1){ //IF JUST ONE
				//time++;

				while (pros.get(0).cpuTotal>0){ //IF JUST ONE				
					if (pros.get(0).start == time-1){
						if (!unstarted.isEmpty()){ //add to running 
							unstarted.get(0).status = "running";
							running.add(unstarted.remove(0));
							running.get(0).cpuBurstRand = randomOS(running.get(0).cpuBurstBase);				
						} 
					}else{
						if (pros.get(0).cpuTotal ==0){
							Proc temp =running.remove(0);
							temp.status = "terminated";
						}else{				
							if (!running.isEmpty()){//if was running
								running.get(0).cpuTotal--;
								cpuCtr++;
								if (running.get(0).cpuTotal ==0){
									Proc temp =running.remove(0);
									temp.status = "terminated";
									break;
								}else{
									running.get(0).cpuBurstRand--;							
									if (running.get(0).cpuBurstRand ==0 ){
										running.get(0).status ="blocked";
										running.get(0).ioBurstRand=randomOS(running.get(0).ioBurstBase);
										blocked.add(running.remove(0));
										//		blocked.add(running.remove(0));
										ioCtr++;
										ioTimes[0]++;
									}
								}					
							} else {
								blocked.get(0).ioBurstRand--;//	blocked.get(0).ioBurstRand--;

								if (blocked.get(0).ioBurstRand ==0){
									blocked.get(0).status ="running";
									blocked.get(0).cpuBurstRand=randomOS(blocked.get(0).cpuBurstBase);

									running.add(blocked.remove(0));
								}
							}
						}
					}
					if (verb){
						System.out.print("Before cycle "+time+": " +"\t");
						for (int m=0;m<pros.size();m++){
							System.out.printf("%10s ",pros.get(m).status);
							if (pros.get(m).status == "running"){
								System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
							} else if (pros.get(m).status == "blocked"){
								System.out.print(pros.get(m).ioBurstRand);
							}
							System.out.print("    ");
						} // print
						System.out.println();
					}
					time++;
					finishingTimes[0]++;	
				}
				//time--;
			}else{ // EVERYTHING BELOW IS IF GREATER THAN 1	
				quantum = 2;
				//make new arraylist for ready, add to ready whenever its supposed to add to ready, then at the end just add to actual ready list
				//	ArrayList<Proc> readyTemp = new ArrayList<Proc>();
				//int starter =0;
				//randCtr is determining which random numebr to use

				while (true){		
					for (int k=0;k<pros.size();k++){ 
						pros.get(k).newready = false;
					}
					for (int k=0;k<pros.size();k++){  
						if (pros.get(k).status == "unstarted" && starter ==0 && pros.get(k).start== time-1){// beginning set up/initialize
							running.add(pros.get(k));
							running.get(0).status = "running";
							pros.get(k).cpuBurstRand = randomOS(pros.get(k).cpuBurstBase);
							randCtr++;
							pros.get(k).quantum = Math.min(2, pros.get(k).cpuBurstRand);
							starter++; 
							unstarted.remove(0);
							skip = true;
						}
						if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 

							pros.get(k).status="ready";
							waitingTimes[k] = waitingTimes[k] +1;
							if (time > 1){
								extra[k] = true;
							}
							skip2++;	
							//unstarted.remove(0);
						}
					}
					if (skip){ // will only print this once
						skip = false;
						if(verb){
							System.out.print("Before cycle "+time+": ");
							for (int m=0;m<pros.size();m++){
								System.out.printf("\t%10s ",pros.get(m).status);
								if (pros.get(m).status == "running"){
									System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
								} else if (pros.get(m).status == "blocked"){
									System.out.print(pros.get(m).ioBurstRand);
								} else{
									System.out.print("0");
								}
							} // print
						}
						time ++;			
						System.out.println();
					}	
					for (int k=0;k<pros.size();k++){
						if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 	
							pros.get(k).status="ready";
							waitingTimes[k] = waitingTimes[k] +1;
							if (time > 1){
								extra[k] = true;
							}
							skip2++;	
							//unstarted.remove(0);
						}
					} // have it twice, to catch in case doesnt catch the first time? unsure
					//blocked is now an arrayLIst
					// if theres something in the blocked list,	
					//^above shit deals with unstarted stuff
					if (!blocked.isEmpty()){ //decrement the blocked waiting time
						ioCtr++;
						Proc[] block = blocked.toArray(new Proc[0]); //now block is a copy array of the arraylist

						for (int i=0; i<block.length; i++){
							block[i].ioBurstRand--; //decrement each of the io burst counter
						}//		blocked.get(0).ioBurstRand--;

						for (int j=0;j<pros.size(); j++){ // incrementing the iotimes counter for each process
							for (int k=0; k<block.length;k++){
								if (block[k].index == pros.get(j).index){//if indexes are same
									ioTimes[j]++;
								}
							}
						}
						for (int k=1;k<block.length;k++){	//sort by index so its in order by that, still only gets out 						
							for (int j=k; j>0; j--){ //if the iocounter goes to 0
								if (block[j].index < block[j-1].index){
									Proc temp = block[j];
									block[j] = block[j-1];
									block[j-1] = temp;		
								}
							}
						} //sorts solely by indexes, now block list is sorted based on indexes
						for (int i =0; i <block.length;i++){
							if (block[i].ioBurstRand == 0){
								block[i].status = "ready";
								//	readyTemp.add(block[i]);
								block[i].newready = true;
								ready.add(block[i]);

								blocked.remove(block[i]);
							}//adds to ready queue, removes from blocked arraylist
						} //otherwise its still in blocked list		
					} //DONE MANAGING BLOCK LIST KINDA
					while (skip2>0){
						//	readyTemp.add(unstarted.remove(0));
						Proc temp = unstarted.remove(0);
						temp.newready = true;
						ready.add(temp);
						skip2--;
					}
					if (!running.isEmpty()){ // if not empty
						cpuCtr++;
						if (running.get(0).cpuTotal <= 1){ // if the cputotal of current running = 1 then its done?
							Proc dun = running.remove(0);
							dun.status = "terminated";
							for (int i =0; i<pros.size(); i++){
								if (dun.index == pros.get(i).index){
									finishingTimes[i] = (time-1);
								}
							}
							//	running.get(0).cpuTotal--;
							done.add(dun); //remove from running
							if (!ready.isEmpty()){
								ready.get(0).status = "running"; // add from ready list
								Proc temp = ready.remove(0);
								temp.newready = false;
								if (temp.cpuBurstRand >0){ // but only if the temp isnt also done, otherwise ur also done
									temp.quantum = Math.min(2,temp.cpuBurstRand);

								}else{ //if from ready list its not done yet, add to running
									temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
									randCtr++;
									temp.quantum = Math.min(2,temp.cpuBurstRand);
								}
								running.add(temp);
							}

							//}
						}else {	//if running is not ready to be terminated (and also running is not empty)		
							running.get(0).cpuBurstRand--;
							running.get(0).cpuTotal--;
							running.get(0).quantum--;
							//check the burst first, bc if burst reaches 0 thats like higher priority than if quantum reaches 0
							if (running.get(0).cpuBurstRand <=0){ //if running goes down to 0
								running.get(0).status = "blocked";
								running.get(0).ioBurstRand = randomOS(running.get(0).ioBurstBase);
								//	System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);
								randCtr++;
								blocked.add(running.remove(0));
								//	blocked.add(running.remove(0));
								if (!ready.isEmpty()){ // if ready is not empty
									running.add(ready.remove(0));
									running.get(0).status ="running";
									running.get(0).newready = false;
									if (running.get(0).cpuBurstRand <=0){ // if in ready list it had reached 0 last time
										running.get(0).cpuBurstRand = randomOS(running.get(0).cpuBurstBase);
										randCtr++;
									} 
									running.get(0).quantum = Math.min(2,running.get(0).cpuBurstRand);
									//	System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);

								}
							} else // else if preempted basically 
								if (running.get(0).quantum <= 0){ // if quantum goes down to 0 when preempted basically
									running.get(0).status = "ready";
									running.get(0).newready = true;
									ready.add(running.remove(0));	
									if (!ready.isEmpty()){ //basically just check the ready list
										running.add(ready.remove(0));
										running.get(0).status ="running";
										running.get(0).newready = false;
										if (running.get(0).cpuBurstRand <=0){// || running.get(0).cpuBurstRand==-10){ // if its also done running overall
											running.get(0).cpuBurstRand = randomOS(running.get(0).cpuBurstBase);
											randCtr++;
											//	System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);									
										}
										running.get(0).quantum = Math.min(2,running.get(0).cpuBurstRand); //the min thing
									}
								}						
						}
					}else {//if running is empty, must add to it
						if (!ready.isEmpty()){
							Proc temp =ready.remove(0);
							temp.newready = false;
							if (temp.cpuBurstRand >0){//if was preempted last time
								temp.quantum = Math.min(2,temp.cpuBurstRand);
								temp.status ="running";
							} else{//was done and like was blocked and now in ready
								temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
								randCtr++;
								temp.status ="running";
								temp.quantum = Math.min(2,temp.cpuBurstRand);
							}
							running.add(temp);
						}else if (!blocked.isEmpty()) {
							//	break;
						}
					}
					if (blocked.isEmpty() && ready.isEmpty() && running.isEmpty() && unstarted.isEmpty()){
						break;
					}
					for (int k = 0;k<pros.size();k++){
						if (pros.get(k).status == "ready"){
							waitingTimes[k] = waitingTimes[k] +1;
							if (extra[k]){
								waitingTimes[k]--;
								extra[k] = false;
							}
						}
					}
					//do sort of ready list here
					//Proc[] block = blocked.toArray(new Proc[0]);
					ArrayList<Proc> readyTemp = new ArrayList<Proc>();
					//	Proc[] readyTemp = ready.toArray(new Proc[]0);
					int w = ready.size();
					for (int i = 0; i < w; i++){
						Proc temp = ready.remove(0);
						if (temp.newready){ // if it was set to true
							readyTemp.add(temp);
						} else{
							ready.add(temp);
						}
					}
					Proc[] readyTemp2 = readyTemp.toArray(new Proc[0]);
					//sort here

					for (int k=1;k<readyTemp2.length;k++){	//sort by index so its in order by that, still only gets out 						
						for (int j=k; j>0; j--){ //if the iocounter goes to 0
							if (readyTemp2[j].index < readyTemp2[j-1].index){
								Proc temp = readyTemp2[j];
								readyTemp2[j] = readyTemp2[j-1];
								readyTemp2[j-1] = temp;		
							}
						}
					}
					for (int k=0;k<readyTemp2.length;k++){
						ready.add(readyTemp2[k]);
					}
					//	System.out.println("Time: "+time+" first in ready list: "+ready.get(0).index);
					if (verb){
						System.out.print("Before cycle "+time+": ");
						for (int m=0;m<pros.size();m++){
							System.out.printf("\t%10s ",pros.get(m).status);
							if (pros.get(m).status == "running"){
								System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
							} else if (pros.get(m).status == "blocked"){
								System.out.print(pros.get(m).ioBurstRand);
							} else{
								System.out.print(pros.get(m).cpuBurstRand);
							}
							//	System.out.print("    ");

						} // print
					}
					time ++;
					System.out.println();
					for (int k=0;k<pros.size();k++){ 
						pros.get(k).newready = false;
					}

				}
			}
			time--;
			System.out.println();
			System.out.println("The scheduling algorithm used was Round Robin");


			for (int j =0; j<pros.size(); j++){
				System.out.println("Process "+j+":");
				System.out.println("(A,B,C,IO) = "+pros.get(j).start+","+pros.get(j).cpuBurstBase+","+totals[j]+","+pros.get(j).ioBurstBase);
				System.out.println("Finishing time: "+finishingTimes[j]);
				System.out.println("Turnaround time: "+(finishingTimes[j]-pros.get(j).start));

				avgTurn +=(finishingTimes[j]-pros.get(j).start);
				System.out.println("I/O time: "+ioTimes[j]);
				System.out.println("Waiting time: "+waitingTimes[j]);
				System.out.println("");
			}

			for (int w = 0; w<waitingTimes.length;w++){
				waitTotal += waitingTimes[w];
			}
			System.out.println();
			System.out.println("Summary Data:");
			System.out.println("Finishing time: "+time);
			System.out.println("CPU Utilization: "+(cpuCtr/time));
			System.out.println("I/O Utilization: "+(ioCtr/time)); //50 = blocked (w/o overlap)/94 = .53

			timefloat /= 100;
			System.out.println("Throughput: "+pros.size()/timefloat+" processes per hundred cycles");	
			System.out.println("Average turnaround time: "+avgTurn/pros.size());	
			System.out.println("Average waiting time: "+waitTotal/pros.size());
		}
		else if (det.equals("L")){


			System.out.print("The original input was: "+pros.size()+"  ");
			for (int i =0;i < pros.size();i++){
				System.out.print(pros.get(i).start+" "+pros.get(i).cpuBurstBase+" "+pros.get(i).cpuTotal+" "+pros.get(i).ioBurstBase+"   ");
			}
			System.out.println("");
			
			Proc[] sorted = pros.toArray(new Proc[0]);
			for (int k=1;k<sorted.length;k++){	//sort by index so its in order by that, still only gets out 						
				for (int j=k; j>0; j--){ //if the iocounter goes to 0
					if (sorted[j].index < sorted[j-1].index){
						Proc temp = sorted[j];
						sorted[j] = sorted[j-1];
						sorted[j-1] = temp;		
					}
				}
			}
			
			
			//now its sorted
			System.out.print("The Sorted input was: ");
			for (int i =0;i < pros.size();i++){
				System.out.print(sorted[i].start+" "+sorted[i].cpuBurstBase+" "+sorted[i].cpuTotal+" "+sorted[i].ioBurstBase+"   ");
			}
			System.out.println("");
			while(!pros.isEmpty()){
				pros.remove(0);
			}
			for (int i =0;i < sorted.length;i++){
				pros.add(sorted[i]);
			}
			
			System.out.println("");
			for (int g =0; g<pros.size();g++){ //initialize wait times to be 0
				totals[g] = pros.get(g).cpuTotal;
				waitingTimes[g]=0;
			}
			for (int i =0; i<pros.size();i++){
				pros.get(i).index = i;
			}
			if(verb){
				System.out.print("Before cycle "+time+": ");
				for (int m=0;m<pros.size();m++){
					System.out.printf("\t%10s ",pros.get(m).status);//+pros.get(m).cpuBurstRand+"    ");
					System.out.print("0");
				}  //prints unstarted line
				System.out.println();
			}
			time++;


			ArrayList<Proc> auxReady = new ArrayList<Proc>();


			for (int y=0; y<pros.size();y++){
				unstarted.add(pros.get(y));
			}



			while (true) {

				for (int k=0;k<pros.size();k++){ //FROM UNSTARTED TO RUNNING/READY
					if (pros.get(k).status == "unstarted" && starter ==0 && pros.get(k).start== time-1){// beginning set up/initialize
						auxReady.add(pros.get(k));
						auxReady.get(0).status = "ready";
						//	pros.get(k).cpuBurstRand = randomOS(pros.get(k).cpuBurstBase);
						//	randCtr++;
						starter++; 
						unstarted.remove(0);
						skip = true;
					}
					if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 
						unstarted.remove(0);
						pros.get(k).status="ready";
						auxReady.add(pros.get(k));
						waitingTimes[k] = waitingTimes[k] +1;
						if (time >= 1){
							extra[k] = true;
						}
						//	System.out.print("WAIT TIME "+waitingTimes[3]);
						skip2++;	
					}
				} // done with unstarted for now, only stuff will be in ready list

				if (!running.isEmpty()){ //decrement
					running.get(0).cpuBurstRand--;
					running.get(0).cpuTotal--;
					cpuCtr++;
				}
				if (!blocked.isEmpty()) {
					ioCtr++;
					//	ArrayList<Proc> addReady = new ArrayList<Proc>(); 
					Proc[] block = blocked.toArray(new Proc[0]);
					for (int k = 0;k<pros.size();k++){
						if (pros.get(k).status == "blocked"){
							ioTimes[k] = ioTimes[k] +1;
						}
					}
					for (int k=1;k<block.length;k++){							
						for (int j=k; j>0; j--){
							if (block[j].index > block[j-1].index){
								Proc temp = block[j];
								block[j] = block[j-1];
								block[j-1] = temp;		
							}
						}
					}
					for (int i = 0; i < block.length; i++){
						block[i].ioBurstRand--;
						if (block[i].ioBurstRand == 0){
							block[i].status = "ready";
							//auxReady.add(block[i]);
							auxReady.add(block[i]);
							blocked.remove(block[i]);
						}
					}
				}
				//now sort auxready
				Proc[] readyAux = auxReady.toArray(new Proc[0]);
				for (int k=1;k<readyAux.length;k++){							
					for (int j=k; j>0; j--){
						if (readyAux[j].index < readyAux[j-1].index){
							Proc temp = readyAux[j];
							readyAux[j] = readyAux[j-1];
							readyAux[j-1] = temp;		
						}
					}
				} // now sorted regularly by index
				for (int i = readyAux.length-1; i >=0; i--){
					ready.add(readyAux[i]);
				}
				auxReady.clear();

				//maybe decrement first thing, only if theres something in running list, end wiht something in running list too
				//now that decremented, want to check if should remove from running
				//aka either to ready or blocked 
				//check blocked first

				// NEED OT GET STUFF FROM READY LIST HERE
				//SORT HEREEEE
				if (running.isEmpty() && !ready.isEmpty()){ // if there's nothing in running list yet, aka first time
					//System.out.println("gets hereeeee");
					Proc temp = ready.remove(ready.size()-1);

					temp.status = "running";
					temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
					//	System.out.print("current burst0: "+temp.cpuBurstRand);
					//this one seems fine
					randCtr++;
					running.add(temp);
					//	System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);

				}//so before this u should decrement if stuff is in 

				if (!running.isEmpty() && !ready.isEmpty()){ // from running to something ->
					//System.out.println("Gets here");
					Proc temp = running.get(0);
					if (temp.cpuTotal <= 0){
						running.get(0).status = "terminated";
						for (int i =0; i<pros.size(); i++){
							if (running.get(0).index == pros.get(i).index){
								finishingTimes[i] = (time-1);
							}
						}
						done.add(running.remove(0));
						ready.get(ready.size()-1).status = "running"; //check if was preempted last time
						ready.get(ready.size()-1).cpuBurstRand = randomOS(ready.get(ready.size()-1).cpuBurstBase);
						randCtr++;
						//	System.out.println("Find burst when choosing ready process to run "+ready.get(0).cpuBurstRand);
						running.add(ready.remove(ready.size()-1));
					}
					else if (temp.cpuBurstRand == 0){ //possibly <=0, if done with burst
						//remove from running
						//HERE ITS == INSTEAD OF <=
						running.get(0).status = "blocked";
						//System.out.println("Gets here");
						running.get(0).ioBurstRand = randomOS(running.get(0).ioBurstBase);
						randCtr++;
						//	System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);
						blocked.add(running.remove(0));
						//running -> blocked

						//Proc temp2 = ready.remove(0);
						ready.get(ready.size()-1).status = "running"; //check if was preempted last time
						ready.get(ready.size()-1).cpuBurstRand = randomOS(ready.get(ready.size()-1).cpuBurstBase);
						//		System.out.print("current burst2: "+ready.get(0).cpuBurstRand);
						randCtr++;
						//		System.out.println("Find burst when choosing ready process to run "+ready.get(0).cpuBurstRand);
						running.add(ready.remove(ready.size()-1));
						// ready -> running	
					} 
					// else do nothing basically
				} else if ( ( !running.isEmpty() && ready.isEmpty() ) || ( ( !blocked.isEmpty() && ready.isEmpty() ) ) ){//so dealing with just one process
					if (!running.isEmpty()){
						Proc temp = running.get(0);

						if (temp.cpuTotal <= 0){
							running.get(0).status = "terminated";
							for (int i =0; i<pros.size(); i++){
								if (running.get(0).index == pros.get(i).index){
									finishingTimes[i] = (time-1);
								}
							}
						}
						else if (temp.cpuBurstRand <= 0){
							temp.status = "blocked";
							temp.ioBurstRand = randomOS(temp.ioBurstBase);
							randCtr++;
							//		System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);
							blocked.add(temp);
							running.remove(0);
							//	System.out.print("Why not");
						}
					} else if (!blocked.isEmpty()){
						Proc temp = blocked.get(0);
						if (temp.ioBurstRand == 0){
							temp.status = "running";
							temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
							//	System.out.print("current burst3: "+ready.get(0).cpuBurstRand);
							randCtr++;
							running.add(temp);
							//			System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);
							blocked.remove(0);
						}
					}
				}					
				boolean breakCheck = true;
				for (int m=0;m<pros.size();m++){
					if (pros.get(m).cpuTotal > 0){
						breakCheck = false;
					}
				}
				if (breakCheck){
					//	finishingTimes[done] = (time-1);
					//	finishingTimes[pros.size()-1] = (time-1);
					//	System.out.println(running.get(0).index);
					finishingTimes[running.get(0).index] = (time-1);
					break;
				}
				for (int k = 0;k<pros.size();k++){
					if (pros.get(k).status == "ready"){
						//System.out.println("SIZE "+pros.size());
						waitingTimes[k] = waitingTimes[k] +1;
					}
					if (extra[k]){
						waitingTimes[k]--;
						extra[k] = false;
					}
				}
				if (verb){
					System.out.print("Before cycle "+time+": ");
					for (int m=0;m<pros.size();m++){
						System.out.printf("\t%10s ",pros.get(m).status);
						if (pros.get(m).status == "running"){
							System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
						} else if (pros.get(m).status == "blocked"){
							System.out.print(pros.get(m).ioBurstRand);
						} else{
							System.out.print(pros.get(m).cpuTotal);
						}
					} // print
				}
				time ++;
				System.out.println();
			}
			time--;
			System.out.println();
			System.out.println("The scheduling algorithm used was Last Come First Serve");


			for (int j =0; j<pros.size(); j++){
				System.out.println("Process "+j+":");
				System.out.println("(A,B,C,IO) = "+pros.get(j).start+","+pros.get(j).cpuBurstBase+","+totals[j]+","+pros.get(j).ioBurstBase);
				System.out.println("Finishing time: "+finishingTimes[j]);
				System.out.println("Turnaround time: "+(finishingTimes[j]-pros.get(j).start));

				avgTurn +=(finishingTimes[j]-pros.get(j).start);
				System.out.println("I/O time: "+ioTimes[j]);
				System.out.println("Waiting time: "+waitingTimes[j]);
				System.out.println("");
			}

			for (int e = 0; e<waitingTimes.length;e++){
				waitTotal += waitingTimes[e];
			}
			System.out.println();
			System.out.println("Summary Data:");
			System.out.println("Finishing time: "+time);
			System.out.println("CPU Utilization: "+(cpuCtr/time));
			System.out.println("I/O Utilization: "+(ioCtr/time)); //50 = blocked (w/o overlap)/94 = .53

			timefloat /= 100;
			System.out.println("Throughput: "+pros.size()/timefloat+" processes per hundred cycles");	
			System.out.println("Average turnaround time: "+avgTurn/pros.size());	
			System.out.println("Average waiting time: "+waitTotal/pros.size());
		}
		else if (det.equals("P")){
			randCtr=0;

			System.out.print("The original input was: "+pros.size()+"  ");
			for (int i =0;i < pros.size();i++){
				System.out.print(pros.get(i).start+" "+pros.get(i).cpuBurstBase+" "+pros.get(i).cpuTotal+" "+pros.get(i).ioBurstBase+"   ");
			}
			System.out.println("");
			Proc[] sorted = pros.toArray(new Proc[0]);
			for (int k=1;k<sorted.length;k++){	//sort by index so its in order by that, still only gets out 						
				for (int j=k; j>0; j--){ //if the iocounter goes to 0
					if (sorted[j].index < sorted[j-1].index){
						Proc temp = sorted[j];
						sorted[j] = sorted[j-1];
						sorted[j-1] = temp;		
					}
				}
			}
			
			
			//now its sorted
			System.out.print("The Sorted input was: ");
			for (int i =0;i < pros.size();i++){
				System.out.print(sorted[i].start+" "+sorted[i].cpuBurstBase+" "+sorted[i].cpuTotal+" "+sorted[i].ioBurstBase+"   ");
			}
			System.out.println("");

			while(!pros.isEmpty()){
				pros.remove(0);
			}
			for (int i =0;i < sorted.length;i++){
				pros.add(sorted[i]);
			}
			
			
			
			System.out.println("");
			for (int g =0; g<pros.size();g++){ //initialize wait times to be 0
				totals[g] = pros.get(g).cpuTotal;
				waitingTimes[g]=0;
			}
			for (int i =0; i<pros.size();i++){
				pros.get(i).index = i;
			}
			System.out.print("Before cycle "+time+": ");
			for (int m=0;m<pros.size();m++){
				System.out.printf("\t%10s ",pros.get(m).status);//+pros.get(m).cpuBurstRand+"    ");
				System.out.print("0");
			}  //prints unstarted line
			System.out.println();
			time++;

			for (int y=0; y<pros.size();y++){
				unstarted.add(pros.get(y));
			}

			while (true) {

				for (int k=0;k<pros.size();k++){ //FROM UNSTARTED TO RUNNING/READY
					if (pros.get(k).status == "unstarted" && starter ==0 && pros.get(k).start== time-1){// beginning set up/initialize
						ready.add(pros.get(k));
						ready.get(0).status = "ready";
						//	pros.get(k).cpuBurstRand = randomOS(pros.get(k).cpuBurstBase);
						//	randCtr++;
						starter++; 
						unstarted.remove(0);
						skip = true;
					}
					if (pros.get(k).status == "unstarted" && starter !=0 && pros.get(k).start == time-1){ //otherwise ready 
						unstarted.remove(0);
						pros.get(k).status="ready";
						ready.add(pros.get(k));
						waitingTimes[k] = waitingTimes[k] +1;
						if (time >= 1){
							extra[k] = true;
						}
						//	System.out.print("WAIT TIME "+waitingTimes[3]);
						skip2++;	
					}
				} // done with unstarted for now, only stuff will be in ready list

				if (!running.isEmpty()){ //decrement
					running.get(0).cpuBurstRand--;
					running.get(0).cpuTotal--;
					cpuCtr++;
				}
				if (!blocked.isEmpty()) {
					ioCtr++;
					//	ArrayList<Proc> addReady = new ArrayList<Proc>(); 
					Proc[] block = blocked.toArray(new Proc[0]);
					for (int k = 0;k<pros.size();k++){
						if (pros.get(k).status == "blocked"){
							ioTimes[k] = ioTimes[k] +1;
						}
					}
					for (int i = 0; i < block.length; i++){
						block[i].ioBurstRand--;

						if (block[i].ioBurstRand == 0){
							block[i].status = "ready";
							//auxReady.add(block[i]);
							ready.add(block[i]);
							blocked.remove(block[i]);
						}
					}
				}

				//maybe decrement first thing, only if theres something in running list, end wiht something in running list too
				//now that decremented, want to check if should remove from running
				//aka either to ready or blocked 
				//check blocked first
				int w = ready.size();
				Proc[] readyAux3 = ready.toArray(new Proc[0]);
				for (int k=1;k<readyAux3.length;k++){							
					for (int j=k; j>0; j--){
						if (readyAux3[j].cpuTotal < readyAux3[j-1].cpuTotal){
							Proc temp = readyAux3[j];
							readyAux3[j] = readyAux3[j-1];
							readyAux3[j-1] = temp;		
						}else if (readyAux3[j].cpuTotal == readyAux3[j-1].cpuTotal){//if equal
							if (readyAux3[j].index < readyAux3[j-1].index){
								Proc temp = readyAux3[j];
								readyAux3[j] = readyAux3[j-1];
								readyAux3[j-1] = temp;
							}
						}
					} 
				}
				while (!ready.isEmpty()){
					ready.remove(0);
				}
				for (int i = 0; i < w; i++){
					ready.add(readyAux3[i]);
				}
				if (running.isEmpty() && !ready.isEmpty()){ // if there's nothing in running list yet, aka first time
					//System.out.println("gets hereeeee");
					Proc temp = ready.remove(0);

					temp.status = "running";
					temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
					randCtr++;
					running.add(temp);
					//	System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);

				}//so before this u should decrement if stuff is in 
				w = ready.size();
				Proc[] readyAux = ready.toArray(new Proc[0]);

				for (int k=1;k<readyAux.length;k++){							
					for (int j=k; j>0; j--){
						if (readyAux[j].cpuTotal < readyAux[j-1].cpuTotal){
							Proc temp = readyAux[j];
							readyAux[j] = readyAux[j-1];
							readyAux[j-1] = temp;		
						}else if (readyAux[j].cpuTotal == readyAux[j-1].cpuTotal){//if equal
							if (readyAux[j].index < readyAux[j-1].index){
								Proc temp = readyAux[j];
								readyAux[j] = readyAux[j-1];
								readyAux[j-1] = temp;
							}
						}
					} 
				}
				while (!ready.isEmpty()){
					ready.remove(0);
				}
				for (int i = 0; i < w; i++){
					ready.add(readyAux[i]);
				}
				if (!running.isEmpty() && !ready.isEmpty()){ // from running to something ->
					//System.out.println("Gets here");
					Proc temp = running.get(0);
					Proc temp2 = ready.get(0);

					if (temp.cpuTotal <= 0){
						running.get(0).status = "terminated";
						for (int i =0; i<pros.size(); i++){
							if (running.get(0).index == pros.get(i).index){
								finishingTimes[i] = (time-1);
							}
						}
						done.add(running.remove(0));
						ready.get(0).status = "running"; //check if was preempted last time
						if (ready.get(0).cpuBurstRand <=0){
							ready.get(0).cpuBurstRand = randomOS(ready.get(0).cpuBurstBase);
							randCtr++;
							//	System.out.println("Find burst when choosing ready process to run "+ready.get(0).cpuBurstRand);

						}
						running.add(ready.remove(0));
						//System.out.print(ready.size()); // PRINTS SIZE THING HERE
					}
					else if (temp.cpuBurstRand <= 0){ //possibly <=0, if done with burst
						//remove from running
						running.get(0).status = "blocked";
						//System.out.println("Gets here");
						running.get(0).ioBurstRand = randomOS(running.get(0).ioBurstBase);
						randCtr++;
						//	System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);

						blocked.add(running.remove(0));
						//running -> blocked

						//Proc temp2 = ready.remove(0);
						ready.get(0).status = "running"; //check if was preempted last time
						if (ready.get(0).cpuBurstRand <=0){
							ready.get(0).cpuBurstRand = randomOS(ready.get(0).cpuBurstBase);
							randCtr++;
							//		System.out.println("Find burst when choosing ready process to run "+ready.get(0).cpuBurstRand);

						}
						running.add(ready.remove(0));
						// ready -> running	
					} 
					else if (temp2.cpuTotal < temp.cpuTotal) {//if need to preempt
						//running.remove(0);
						running.get(0).status = "ready";
						ready.get(0).status = "running";
						if (ready.get(0).cpuBurstRand <=0){ //check if was preempted last time
							ready.get(0).cpuBurstRand = randomOS(ready.get(0).cpuBurstBase);
							randCtr++;
							//			System.out.println("Find burst when choosing ready process to run "+ready.get(0).cpuBurstRand);

						}
						ready.add(running.remove(0));
						running.add(ready.remove(0));
					} // else do nothing basically
				} else if ( ( !running.isEmpty() && ready.isEmpty() ) || ( ( !blocked.isEmpty() && ready.isEmpty() ) ) ){//so dealing with just one process
					//	System.out.print("gets here");
					if (!running.isEmpty()){
						Proc temp = running.get(0);
						if (temp.cpuBurstRand <= 0){
							temp.status = "blocked";
							temp.ioBurstRand = randomOS(temp.ioBurstBase);
							randCtr++;
							//		System.out.println("Find I/O burst when blocking a process "+running.get(0).ioBurstRand);
							blocked.add(temp);
							running.remove(0);
							//	System.out.print("Why not");
						}
					} else if (!blocked.isEmpty()){
						Proc temp = blocked.get(0);
						if (temp.ioBurstRand == 0){
							temp.status = "running";
							temp.cpuBurstRand = randomOS(temp.cpuBurstBase);
							randCtr++;
							running.add(temp);
							//			System.out.println("Find burst when choosing ready process to run "+running.get(0).cpuBurstRand);

							blocked.remove(0);
						}
					}
				}					
				w = ready.size();			
				Proc[] readyAux2 = ready.toArray(new Proc[0]);
				for (int k=1;k<readyAux2.length;k++){							
					for (int j=k; j>0; j--){
						if (readyAux2[j].cpuTotal < readyAux2[j-1].cpuTotal){
							Proc temp = readyAux2[j];
							readyAux2[j] = readyAux2[j-1];
							readyAux2[j-1] = temp;		
						}else if (readyAux2[j].cpuTotal == readyAux2[j-1].cpuTotal){//if equal
							if (readyAux2[j].index < readyAux2[j-1].index){
								Proc temp = readyAux2[j];
								readyAux2[j] = readyAux2[j-1];
								readyAux2[j-1] = temp;
							}
							System.out.println("got here");
						}
					} 
				}
				while (!ready.isEmpty()){
					ready.remove(0);
				}
				for (int i = 0; i < w; i++){
					ready.add(readyAux2[i]);
				}
				boolean breakCheck = true;
				for (int m=0;m<pros.size();m++){
					if (pros.get(m).cpuTotal > 0){
						breakCheck = false;
					}
				}
				if (breakCheck){
					finishingTimes[pros.size()-1] = (time-1);
					break;
				}
				for (int k = 0;k<pros.size();k++){
					if (pros.get(k).status == "ready"){
						//System.out.println("SIZE "+pros.size());
						waitingTimes[k] = waitingTimes[k] +1;
					}
					if (extra[k]){
						waitingTimes[k]--;
						extra[k] = false;
					}
				}
				if(verb){
					System.out.print("Before cycle "+time+": ");
					for (int m=0;m<pros.size();m++){
						System.out.printf("\t%10s ",pros.get(m).status);
						if (pros.get(m).status == "running"){
							System.out.print(pros.get(m).cpuBurstRand);//+ " total :"+ pros.get(m).cpuTotal);
						} else if (pros.get(m).status == "blocked"){
							System.out.print(pros.get(m).ioBurstRand);
						} else{
							System.out.print(pros.get(m).cpuTotal);
						}
					} // print
				}
				time ++;
				System.out.println();
			}
			time--;
			System.out.println();
			System.out.println("The scheduling algorithm used was Preemptive Shortest Job First");


			for (int j =0; j<pros.size(); j++){
				System.out.println("Process "+j+":");
				System.out.println("(A,B,C,IO) = "+pros.get(j).start+","+pros.get(j).cpuBurstBase+","+totals[j]+","+pros.get(j).ioBurstBase);
				System.out.println("Finishing time: "+finishingTimes[j]);
				System.out.println("Turnaround time: "+(finishingTimes[j]-pros.get(j).start));

				avgTurn +=(finishingTimes[j]-pros.get(j).start);
				System.out.println("I/O time: "+ioTimes[j]);
				System.out.println("Waiting time: "+waitingTimes[j]);
				System.out.println("");
			}

			for (int r = 0; r<waitingTimes.length;r++){
				waitTotal += waitingTimes[r];
			}
			System.out.println();
			System.out.println("Summary Data:");
			System.out.println("Finishing time: "+time);
			System.out.println("CPU Utilization: "+(cpuCtr/time));
			System.out.println("I/O Utilization: "+(ioCtr/time)); //50 = blocked (w/o overlap)/94 = .53

			timefloat /= 100;
			System.out.println("Throughput: "+pros.size()/timefloat+" processes per hundred cycles");	
			System.out.println("Average turnaround time: "+avgTurn/pros.size());	
			System.out.println("Average waiting time: "+waitTotal/pros.size());
		}



	
	input.close();
}

}

