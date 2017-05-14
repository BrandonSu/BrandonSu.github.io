public class Frame {
	int process;
	int page;
	int lru;
	boolean used;
	
	public Frame(){
		this.process=-10;
		this.page=-10;
		this.lru=-10;
		this.used = false;
	}
	public Frame(int process, int page){
		this.process = process;
		this.page = page;
		this.lru=-10;
		this.used = false;
	}
}
