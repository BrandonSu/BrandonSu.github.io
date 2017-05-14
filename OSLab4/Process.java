public class Process {
	int id;
	int quantum;
	int word;
	int page;
	int references;
	int faults;
	int[] resStart;
	int residenceSum;
	int evict;
	boolean done;
	
	public Process() {
		this.id = -1;
		this.quantum = 3;
		this.word = -1;
		this.page = -1;
		this.references = -1;
		this.faults = 0;
		this.resStart = null;
		this.residenceSum =0;
		int evict=0;
		this.done = false;
	}
	
	public Process(int id) {
		this.id = id;
		this.quantum = 3;
		this.word = -1;
		this.page = -1;
		this.references = -1;
		this.faults = 0;
		this.resStart = null;
		this.residenceSum =0;
		int evict=0;
		this.done = false;
	}
}