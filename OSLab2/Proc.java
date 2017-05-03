package scheduler;

public class Proc {
	int start; //A
	int cpuBurstBase; //B
	int cpuTotal; //C
	int ioBurstBase; //M
	int cpuBurstRand; //the one after randomOS
	int ioBurstRand; //also after randomOS
	String status;
	int index;
	int quantum;
	boolean newready;
	
	public Proc(int A, int B, int C, int M, int rand, int rand2){ // same with this but well leave it here
		this.start = A;
		this.cpuBurstBase = B;
		this.cpuTotal = C;
		this.ioBurstBase = M;
		this.status = this.status;
		this.index = this.index;
		this.cpuBurstRand = rand;
		this.ioBurstRand = rand2;
		this.newready = false;
	}
	public Proc(){ //initialize as -10 to easily detect errors
		this.start = -10;
		this.cpuBurstBase = -10;
		this.cpuTotal = -10;
		this.ioBurstBase = -10;
		this.status = "unstarted";
		this.cpuBurstRand = -10;
		this.ioBurstRand = -10;
		this.index = -10;
		this.quantum =-10;
		this.newready = false;
	}
	public Proc(String x){ //not sure if this is ever used lol
		this.start = -10; 
		this.cpuBurstBase = -10;
		this.cpuTotal = -10;
		this.ioBurstBase = -10;
		this.status = x;
		this.cpuBurstRand = -10;
		this.ioBurstRand = -10;
		this.index = -10;
		this.newready = false;
	}
	/*public Proc(int A, int B, int M){
		this.start = A;
		this.cpuBurst = B;
		this.cpuTotal = this.cpuTotal;
		this.ioBurst = M;
	}
	public Proc(int A, int C, int M){
		this.start = A;
		this.cpuBurst = this.cpuBurst;
		this.cpuTotal = C;
		this.ioBurst = M;
	}*/

}
