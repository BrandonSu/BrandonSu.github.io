import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
public class Assignment4 {
	public static int randCtr = 0;		
	public static int randomOS() throws FileNotFoundException {
		URL url = Assignment4.class.getResource("random-numbers");
		File f = new File(url.getPath());
		Scanner input = new Scanner(f);
		int x=0;
		int ctr = randCtr;
		randCtr++;
		while (ctr >=0){
			x = input.nextInt();	
			ctr--;
		}
		input.close();
		return x;
	}
	public static void main(String[] args) throws FileNotFoundException {
		/*
		 * Eclipse testing
		System.out.println("Please enter filename");
		Scanner userIn = new Scanner(java.lang.System.in);
	//	String fileName = userIn.next();
		
		String fileName = "input14";
		
		URL url = Assignment4.class.getResource(fileName);
		File f = new File(url.getPath());
		Scanner input = new Scanner(f);
		System.out.println(fileName);
		*/		
		/*	M, the machine size in words.
		P, the page size in words.
		S, the size of a process, i.e., the references are to virtual addresses 0..S-1. 
		J, the ‘‘job mix’’, which determines A, B, and C, as described below.
		N, the number of references for each process.
		R, the replacement algorithm, FIFO, RANDOM, or LRU. */	
		int m = Integer.parseInt(args[0]);
		int p = Integer.parseInt(args[1]);
		int s = Integer.parseInt(args[2]);
		int J = Integer.parseInt(args[3]);
		int n = Integer.parseInt(args[4]);
		String algo = args[5];
		int debug = Integer.parseInt(args[6]);
		/*
		 * For eclipse testing
		int m = input.nextInt();
		int p = input.nextInt();
		int s = input.nextInt(); // the like range for word number
		int J = input.nextInt(); //refers to how many processes therell be 
		int n = input.nextInt(); // this number times ^ # of processes = the time
		String algo = input.next();
		int debug = input.nextInt(); //optional debugging thing
		*/
		int numSlots = m/p; //number of possible frames to put things in	
		System.out.println("The machine size is " + m + ".");
        System.out.println("The page size is " + p + ".");
        System.out.println("The job size is " + s + ".");
        System.out.println("The job mix number is " + J + ".");
        System.out.println("The number of references per process is " + n + ".");
        System.out.println("The replacement algorithm is " + algo + ".");
        System.out.println("The level of debugging output is " + debug + ".\n");
        int numProc=0;
    //    double [][] probability;
        ArrayList<double[]> probability = new ArrayList<double[]>();       
		if (J == 1){
			numProc = 1;
			double[] prob = {1,0,0,0};
			probability.add(prob);			
			//probabiliyy.get(0) refers to the first set, probability.get(0)[0] = the 1 thats for A 			
		} else if (J==2){
			numProc =4;
			//all A = 100%
			double[] prob = {1,0,0,0};
			probability.add(prob);	
			probability.add(prob);	
			probability.add(prob);	
			probability.add(prob);							
		} else if (J==3){
			numProc =4;
			//all random = 100%
			double[] prob = {0,0,0,1};
			probability.add(prob);	
			probability.add(prob);	
			probability.add(prob);	
			probability.add(prob);
		} else if (J==4){
			numProc =4;
			//the weird one
			double[] prob1 = {0.75,0.25,0,0};
			double[] prob2 = {0.75,0,0.25,0};
			double[] prob3 = {0.75,0.125,0.125,0};
			double[] prob4 = {0.5,0.125,0.125,0.25};
			probability.add(prob1);	
			probability.add(prob2);	
			probability.add(prob3);	
			probability.add(prob4);		
		} else{
			System.out.println("J error");
		}	
	//	System.out.println(probability.get(0)[0]); is the probabilities
		// to get page number
		//take word number divide by page size (p)	
		Frame[] frames = new Frame[numSlots]; //array of frame object, with field process and page
		for (int i=0; i < frames.length; i++){
			frames[i] = new Frame();
		}
		Process[] processes = new Process[numProc];
		for (int i = 0; i<processes.length; i++){
			processes[i] = new Process(i+1);
			processes[i].word = (111 * (i + 1)) % s;
			processes[i].resStart = new int[s/p];
		}		
		int[] word = new int[numProc]; //process size refers to word number
        int[] counter = new int[numProc];
        int ctr = 1;
        for (int i = 0; i < counter.length;i++){
        	counter[i]=n;
        }
        double y = 0;
        double A = 0;
        double B = 0;
        double C = 0;
        boolean match = false;
        int frameNum = -10;
        int[] faults = new int[numProc];
        double[] resTimeSum = new double[numProc]; // sum 
        double[] resTime = new double[numProc]; // 
        double[] evictions = new double[numProc];
        
        int evictCtr=0;
        int fifoCtr = frames.length-1;
        
        while (counter[0] >0){//deceremnt counter to 0 to replicate hte 10 references each
	        for (int i = 0; i <numProc; i++){//for each process
	        	for (int j = 0; j<3; j++){
	        		match = false;
	        		frameNum = -10;
	        		
	        		int pageNum = processes[i].word/p;//word[i]/p;
	        		if (debug==1){
	        			System.out.printf("%d references word %d (page %d) at time %d: ", processes[i].id, processes[i].word, pageNum, ctr);
	        		}
	        		for (int g = 0; g<frames.length; g++){//go thru frame list
	        		//	System.out.println(frames[0].page+": "+pageNum+", "+ frames[0].process+": "+(i+1));
	        			if (frames[g].page == pageNum && frames[g].process ==i){
	        				match = true;
	        				frameNum = g;
	        				frames[g].lru = ctr;
	        				//System.out.println("True");
	        			}
	        		}
	        		if (match){
	        			if (debug==1){
	        				System.out.printf("Hit in frame %d", frameNum);
	        			}
	        			//resTime[i]++;        			
	        		} else{ //fault
	        			faults[i]++;
	        			int frameUse = 0;		
	        			//^change this part
	        			if (evictCtr<frames.length){ //initial times for each frame
	        				frameUse = frames.length-1;
	        				while (frames[frameUse].used){
	        					frameUse--;
	        				}	        				        				
	        				frames[frameUse].lru=ctr; //update lru to be the time of use
	        				if (debug==1){
	        					System.out.printf("Fault, using free frame %d",frameUse);
	        				}
	        				//basically, upon initialization, for that processes resStart array which refers to all available pages per process
	        				//at the index= page number(bc they start at 0), the load time = ctr
	        				//then upon eviction, subtract current time - the load time stored in array
	        				//upon re entering, rewrite load time
	        				
	        				processes[i].resStart[pageNum] = ctr;
	        				//initialize load time
	        				
	        				evictCtr++;
	        				frames[frameUse].used = true;
	        				//resTime[i]=ctr;
	        			} else{
	        				// not the first time
	        				//if there was a hit in the frame, that one is now like most recently used
	        				if (algo.equals("lru")){
		        				//want frameUse to be the smallest time for LRU
		        				frameUse=0;
		        				for (int t=0;t<frames.length;t++){
		        					if (frames[t].lru < frames[frameUse].lru){ // if 
		        						frameUse = t;	        						
		        					}
		        				}
		        				if (debug==1){
		        					System.out.printf("Fault, evicting page %d of %d from frame %d",frames[frameUse].page, frames[frameUse].process+1, frameUse);
		        				}//+1 is bc i save hte index not ID
		        				
		        				processes[frames[frameUse].process].residenceSum += (ctr- processes[frames[frameUse].process].resStart[frames[frameUse].page]);
		        				//not +1 is bc its index not ID		        				
		        				//initialize the new ones
		        				processes[i].resStart[pageNum] = ctr;
		        				//basically, upon initialization, for that processes resStart array which refers to all available pages per process
		        				//at the index= page number(bc they start at 0), the load time = ctr
		        				//then upon eviction, subtract current time - the load time stored in array
		        				//upon re entering, rewrite load time
		        				processes[frames[frameUse].process].evict++;
	        				} else if (algo.equals("random")) {
	        					//bascially, choosing which frame to evict is based on random
	        					//random
	        					frameUse=0;
	        					frameUse = randomOS() % frames.length;		        				
	        					if (debug==1){
	        						System.out.printf("Fault, evicting page %d of %d from frame %d",frames[frameUse].page, frames[frameUse].process+1, frameUse);
	        					}
		        				//+1 is bc i save hte index not ID
		        				
		        				processes[frames[frameUse].process].residenceSum += (ctr- processes[frames[frameUse].process].resStart[frames[frameUse].page]);
		        				//not +1 is bc its index not ID
		        				
		        				//initialize the new ones
		        				processes[i].resStart[pageNum] = ctr;
		        				//basically, upon initialization, for that processes resStart array which refers to all available pages per process
		        				//at the index= page number(bc they start at 0), the load time = ctr
		        				//then upon eviction, subtract current time - the load time stored in array
		        				//upon re entering, rewrite load time	
		        				processes[frames[frameUse].process].evict++;   					
	        				} else if (algo.equals("fifo")){
	        					//want frameUse to be the smallest time for LRU		        			
		        				frameUse = fifoCtr;
		        				//^ just have a ctr or something
		        				if (debug==1){
		        					System.out.printf("Fault, evicting page %d of %d from frame %d",frames[frameUse].page, frames[frameUse].process+1, frameUse);
		        				}
		        				//+1 is bc i save hte index not ID	        				
		        				processes[frames[frameUse].process].residenceSum += (ctr- processes[frames[frameUse].process].resStart[frames[frameUse].page]);
		        				//not +1 is bc its index not ID
		        				
		        				//initialize the new ones
		        				processes[i].resStart[pageNum] = ctr;
		        				//basically, upon initialization, for that processes resStart array which refers to all available pages per process
		        				//at the index= page number(bc they start at 0), the load time = ctr
		        				//then upon eviction, subtract current time - the load time stored in array
		        				//upon re entering, rewrite load time
		        				processes[frames[frameUse].process].evict++;
		        				fifoCtr = (fifoCtr -1 +frames.length)%frames.length;
	        				}		
	        				else{
	        					if (debug==1){
	        						System.out.println("BREAKKKKKKKK");
	        					}	        					
	        				}
	        			}
	        			frames[frameUse].page = pageNum;
	        			frames[frameUse].process = i;
	        			frames[frameUse].lru = ctr;
	        		//	System.out.println(frames[0].page+" "+ frames[0].process);	        			
	        		}	        		   		
	        		if (debug==1){
	        			System.out.println();
	        		}
	        		int x = randomOS();
	        		y = x/ (Integer.MAX_VALUE + 1d);
	        //		System.out.println("uses random number: "+x);
	            	A = probability.get(i)[0];
	                B = probability.get(i)[1];
	                C = probability.get(i)[2];
	                if (y < A){
	                	processes[i].word = (processes[i].word+1)%s;
	                } else if (y < A+B){
	                	processes[i].word = (processes[i].word-5+s)%s;
	                }else if (y < A+B+C){
	                	processes[i].word = (processes[i].word+4)%s;
	                }else {
	                	processes[i].word =randomOS()%s;
	                }
	        		counter[i]--;
	        		ctr++;
	        		if (counter[i] ==0){
	        			break;
	        		}
	        	}
	        }
        }
        System.out.println();
        
        //after while loop
        for (int i = 0; i<numProc; i++){
        	if (processes[i].evict ==0){
        		System.out.printf("Process %d had %d faults and no evictions  ", i+1, faults[i]);
        	}
        	else{
        		System.out.printf("Process %d had %d faults and %f average residency", i+1, faults[i], (float)processes[i].residenceSum/(float)processes[i].evict); // /evictions[i]);
        	}
        	System.out.println();
        }
        int faultSum=0;
        for (int i =0; i<faults.length; i++){
        	faultSum += faults[i];
        }
        float resSumSum = 0;
        float evictSumSum = 0;
        for (int i = 0;i < processes.length;i++){
        	resSumSum+=processes[i].residenceSum;
        }
        for (int i = 0;i < processes.length;i++){
        	evictSumSum+=processes[i].evict;
        }
        System.out.println();
        if (evictSumSum>0){
        	float avgavgRes = resSumSum/evictSumSum;
        	System.out.printf("The total number of faults is %d and the overall average residency is %f", faultSum,avgavgRes);
            
        } else{
        	System.out.printf("The total number of faults is %d and the overall average residency is undefined bc no evictions", faultSum);
            
        }
        System.out.println();
               
        /*
        double y = 0;
        double A = 0;
        double B = 0;
        double C = 0;
        for (int i = 0; i < n*numProc; i++){
        	System.out.println(word[0]);
        	y = randomOS() / (Integer.MAX_VALUE + 1d);
        	A = probability.get(0)[0];
            B = probability.get(0)[1];
            C = probability.get(0)[2];
            if (y < A){
            	word[0] = (word[0]+1)%s;
            } else if (y < A+B){
            	word[0] = (word[0]-5+s)%s;
            }else if (y < A+B+C){
            	word[0] = (word[0]+4)%s;
            }else {
            	word[0] =randomOS()%s;
            }
        	
        }*/
        
		/*
		M, the machine size in words.
		P, the page size in words.
		S, the size of a process, i.e., the references are to virtual addresses 0..S-1.  = word number
		J, the ‘‘job mix’’, which determines A, B, and C, as described below.
		N, the number of references for each process.
		R, the replacement algorithm, FIFO, RANDOM, or LRU.

		N- number of memory references 
		round robin, quantum 3  - groups of 3, per frame, 
		so if theres 2 frames, theres 6 like 
		
		
		fraction A - address one higher than current
		fraction B - nearby lower address
		fraction C - nearby higher address (but not the one directly higher?)
		remaining fraction 1-(A+B+C) = D: - are to random addresses 
		
		current word address w
		prob A = w+1 (mod S, S is total size, so it cant go out of bounds)
		prob B = (w-5+S)% S
		prob C = w+4 mod S
		prob D = random value between 0 and S
		
		values for J : 4 possible sets of processes
		J=1 : 1 process with A = 100% (w+1)
		J=2 : 4 processes, each with A=100% (w+1)
		J=3 : 4 processes, all random (prob D)
		J=4 : 1 process, A=.75(w+1), B=.25(w-5), C=0, random=0
			  1 process, A=.75(w+1), B=0, C=.25(w+4), random=0
			  1 process, A=.75(w+1), B=.125(w-5), C=.125(w+4), random=0
			  1 process, A=.5(w+1), B=.125(w-5), C=.125(w+4), random=.25(random)
		
		if a fault occurs (page isnt in the table yet) put it into the table
		-if no free frames, a resident page(in the table) is evicted depending on algorithm
		victim can be any frame not just ones used by the faulting process 
		just implement a frame table (not page tables)
		
		begins all frames empty, first reference for each process will 100% be a page fault
		begins by referencing word 111*k mod S
		
		for each process, print # of page faults and avg residency time
		avg residency time = time page evicted - time it was loaded 
		at eviction, add current residency time to a sum, at end divide by number of evictions
		
		choose the highest numbered free frame for first placement
		
		*/ 
	}
}