import java.util.Scanner;
import java.util.HashMap;
import java.util.Set;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Assignment1 {
	public static void main(String[] args) {
	
		Scanner getInput = new Scanner(System.in);
		HashMap<String, symbol> symTab = new HashMap<String, symbol>();
		System.out.println("Please enter the input directly, and then hit enter!");
		int moduleNum = getInput.nextInt(); // gets number of modules first
		int[] mods = new int[moduleNum]; // array of length modules, each index will be the starting address
		mods[0] = 0;
		int[] mods2 = new int[moduleNum];
		
        int curChunk=0; //current index of chunk
		
		HashMap<Integer, symbol> memMap = new HashMap<Integer, symbol>();
		ArrayList<String> types = new ArrayList<String>();
		ArrayList<String> adrsUseListError = new ArrayList<String>();
		//key = index (0) value = address
		// 0 ... 1004
		//1 ... 5678
		//when edit address just replace address
		//value of type symbol -> you can store multiple characteristics 
		
		//Set<Integer> memIn = memMapH.keySet();
		int totalChunks = 0;

		//FIRST PASS
		int numChunksMax = 0; // maximum number of program chunks in a single module
		int symcount = 0;
		for (int mod = 0; mod<moduleNum; mod++) { //loop through modules
			int numDef = getInput.nextInt(); // gets the number of definitions

			for (int s = 0; s<numDef; s++){ // if there are definitions
				String sym = getInput.next();
                int symAdrs = getInput.nextInt();
                
    //might need to update in case error of a symbol being defined like twice
                if (symTab.containsKey(sym)){ //if already contains
                	symTab.get(sym).warning ="Error, This variable is multiply defined; first value used.";
                }else {
                	symbol value = new symbol(symAdrs,mod);
                	//symbol value = new symbol(symAdrs+mods[mod],mod); // value with base address + starting point of current module as location, module number
                	symTab.put(sym,value); // adds symbol and its base address to hashmap
                }
			}	
			// after the definitions line is where the symbol is used line
			int symuses = getInput.nextInt();
			for (int n = 0; n < symuses; n++){ // loop through the uses line
				String symb = getInput.next();
				int reladrs = getInput.nextInt();
				if (symTab.containsKey(symb)){
					symbol RELADRS = new symbol(symTab.get(symb).loc, true, symTab.get(symb).warning,symTab.get(symb).module);
					symTab.put(symb,RELADRS);
				} 
				while (reladrs != -1){
					reladrs = getInput.nextInt();
				}
			} // done looping through uses line				
			//after reads symbols and their addresses, next integer should be the number of program texts

			int numChunks = getInput.nextInt(); 
			mods2[mod] = numChunks;
			totalChunks = numChunks + mods[mod]; // adds another base address to array of base addresses 
			if (mod != moduleNum-1){ // fills in array with 0, numChunks, plus previous etc
				//	totalChunks = numChunks + mods[mod]; // adds another base address to array of base addresses 
				mods[mod+1] = totalChunks;
			}// of modules

			for (int chunk = 0; chunk < numChunks; chunk++){
				String type = getInput.next();
                types.add(type); //adds letter type to arraylist types
				int adrs = getInput.nextInt();
                symbol chunkAdrs = new symbol(adrs,false,mod);
                memMap.put(curChunk,chunkAdrs); // index of current chunk is key, value is the stuff
                curChunk++; //update current chunk index
                //curChunk is just an integer for index
			}
		}
		
		Set symSet2 = symTab.entrySet();
		Iterator i4 = symSet2.iterator();		
		//If an address appearing in a definition exceeds the size of the module, print an error message and treat the address
		//given as 0 (relative).
		while (i4.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<String, symbol> me = (Map.Entry<String, symbol>)i4.next();
		//	System.out.println(me.getKey()+" "+ mods[me.getValue().module]);
			if (me.getValue().loc >= mods2[me.getValue().module]){
				me.getValue().loc = mods[me.getValue().module];
				me.getValue().warning = "Error: Definition exceeds module size; first word in module used.";
			} else {
				me.getValue().loc = me.getValue().loc + mods[me.getValue().module];
			//	symbol value = new symbol(symAdrs+mods[mod],mod);
			}
		}

		System.out.println("Please enter the same input a second time and afterwards hit the enter key");

		//SECOND PASS		
		//as of now we have: hashmap symTab-just symbols, used or not, and the base address
        //arraylist types - just the chunk letters in order from 0 to whatever ex: R, I, E...
        //Hashmap memMap - key is index of the chunk as a whole/total, value is the address associated, is used is true,

		//START OF PASS 2

		int moduleNum2 = getInput.nextInt();

		int chunkctr = 0;
		int rEdit = 0;

		for (int mod = 0; mod<moduleNum; mod++) { //loop through modules
			int defNum2 = getInput.nextInt(); // gets the number of definitions				
			for (int def = 0; def<defNum2; def++){ // if there are definitions 
				String sym2 = getInput.next();
				int baseadrs2 = getInput.nextInt();
			}	//dont need to do anytihng with definitoins
			// after the definitions line is where the symbol is used line
			int symuses2 = getInput.nextInt(); //2 before z
//			int symuseswarning = 0;
			for (int s = 0; s < symuses2; s++){ // loop through the uses line			
				String symb2 = getInput.next();	
				int baseAdrs = 0;
				if (symTab.containsKey(symb2) == false){ //symTab should already have all the symbols
					//after first pass
				//	int baseAdrs = 0;
				//	symbol NOTDEFINED = new symbol(0,true,("Error: " +symb2+ " is not defined; zero used."), mod);
			//		symTab.put(symb2,NOTDEFINED);
				} // adds to symbol table which we dont want?
				else{				
					baseAdrs = symTab.get(symb2).loc; //assigning
				}
				if (symTab.containsKey(symb2)){
					symbol RELADRS = new symbol(symTab.get(symb2).loc, true, symTab.get(symb2).warning, symTab.get(symb2).module);
					symTab.put(symb2,RELADRS);
				}
				int chunkadrs = getInput.nextInt();						
				while (chunkadrs != -1){
					if (chunkadrs > mods2[mod]){ // if 4 > 3
						adrsUseListError.add(("Error: Use of "+symb2+" in module "+(mods[mod]+1)+" exceeds module size; use ignored.") );
				//		System.out.println(chunkadrs+"this one");
					}
					else{
						chunkadrs +=mods[mod];
						if(memMap.get(chunkadrs).isUsed){//if true aka been updated
							memMap.get(chunkadrs).warning = "Error: Multiple variables used in instruction; all but first ignored.";
						}else{ // if isUsed is still false					
							//chunkadrs += mods[mod];
							int changeAdrs = memMap.get(chunkadrs).loc;
							changeAdrs = changeAdrs/1000;
							changeAdrs = changeAdrs*1000;
							changeAdrs += baseAdrs; // now updated
							memMap.get(chunkadrs).loc = changeAdrs;//should update the loc which is adress of the chunks
							memMap.get(chunkadrs).isUsed = true;
						}
						if (baseAdrs == 0){
							memMap.get(chunkadrs).warning =("Error: " +symb2+ " is not defined; zero used.");
						}
					}
					chunkadrs = getInput.nextInt();
				}												
			} //done with uses line, next is chunk line

			//now need to create lists of chunks and their addresses 
			//String[] CHUNKS = new String[totalChunks]; 	int[] CHUNKSadrs = new int[totalChunks];
			int numChunks2 = getInput.nextInt(); 
			for (int t=0; t<numChunks2;t++){
				String type2 = getInput.next();
				int adrs2 = getInput.nextInt();
				if (type2 == "R"){
					int update = memMap.get(rEdit).loc; //rEdit is just a counter
					update = update + mods[mod];
					memMap.get(rEdit).loc = update;
				}
				rEdit++;
			}
		} // DONE WITH SECOND PASS
	
		
		String[] newArr = new String[totalChunks];
		for (int h =0; h<totalChunks; h++){
			newArr[h] = (String)types.get(h);
		}
		
		//• If a relative address exceeds the size of the module, print an error message and use the value zero (absolute).
		for (int h =0; h<totalChunks; h++){
			if (newArr[h].compareTo("R") == 0){
				int base = memMap.get(h).loc;
				base += mods[memMap.get(h).module];
				if (base%1000 > totalChunks){
					memMap.get(h).warning = "Error: Relative address exceeds module size; zero used.";
					base = base/1000;
					base = base*1000;
				}
				memMap.get(h).loc= base;
			}
		}
		
		Set symSet = symTab.entrySet();
		
		//Warnings part!
		
		//• If a symbol is multiply defined, print an error message and use the value given in the first definition.
		//done
		
		//• If a symbol is used but not defined, print an error message and use the value zero.
		//done
				
		//• If a symbol is defined but not used, print a warning message and continue.
		Iterator i2 = symSet.iterator();
		while (i2.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<String, symbol> me = (Map.Entry<String, symbol>)i2.next();
			if (me.getValue().isUsed == false){
				System.out.println("Warning: "+me.getKey()+" was defined in module "+(me.getValue().module+1)+" but never used");
			}
		}
		//• If multiple symbols are listed as used in the same instruction, print an 
		//error message and ignore all but the first usage given.
		//done
		
		//• If an address appearing in a definition exceeds the size of the module, 
	//	print an error message and treat the address given as 0 (relative).
		//i will treat this as ex: 1 z 3 (def), 0(uses), 2 I 3456 I 3123, since that module is size 2 but def is 3
		//which is greater than size of module, -> error
		//done
		
	//	• If an address appearing in a use list exceeds the size of the module,
	//	print an error message and ignore this particular use.
		//i will treat this as ex: 1 z 5 -1, next line is only 1 R 1000, no 5th address exists in that module so error
		for (int g=0; g<adrsUseListError.size();g++){
			System.out.println(adrsUseListError.get(g));
		}
	//	System.out.println("done");
		
		//• If an absolute address exceeds the size of the machine, print an error message and use the value zero.
		//assuming machine size is 200
		for (int h =0; h<totalChunks; h++){
			if (newArr[h].compareTo("A") == 0){
				int adrs = memMap.get(h).loc;
				int remainder = adrs%1000;
				if (remainder >200){
					memMap.get(h).warning = "Error: Absolute address exceeds machine size; zero used.";
					adrs = adrs/1000;
					adrs = adrs*1000;
					memMap.get(h).loc = adrs;
				}
			}
		}
		
	//	• If a relative address exceeds the size of the module, 
		//print an error message and use the value zero (absolute).
		//done

		Iterator i = symSet.iterator();
		System.out.println("Symbol Table");
		while (i.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<String, symbol> me = (Map.Entry<String, symbol>)i.next();
			System.out.print(me.getKey()+":");
			if (me.getValue().warning != null){
				System.out.print(me.getValue().loc+" ");
				System.out.println(me.getValue().warning);
			}else {
				System.out.println(me.getValue().loc);	
			}
		}
		
		System.out.println("Memory Map");
		for (int u = 0; u <totalChunks; u++){
			System.out.print(u+":");
			System.out.print(types.get(u)+" ");
			if(memMap.get(u).warning!= null){
				System.out.print(memMap.get(u).loc+" ");
				System.out.println(memMap.get(u).warning);
			}else{
				System.out.println(memMap.get(u).loc);
			}
		}
		getInput.close();
	}
}