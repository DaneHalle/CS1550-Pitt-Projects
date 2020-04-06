import java.lang.Math;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
public class Simulation
{
	private int numFrames;
	private final int PAGE_SIZE=(int)Math.pow(2,12);
	private String trace;

	public Simulation(int inNumFrames, String inTrace)
	{
		numFrames=inNumFrames;
		trace=inTrace;
	}

	@SuppressWarnings("unchecked")
	public void optimal()
	{
		File file=new File(trace);
		entry[] ram=new entry[numFrames], table=genNewTable();
		//next will be used to pre-process the trace for opitmal algorithm
		LinkedList[] next=new LinkedList[table.length];
		Scanner scan;
		try{
			scan=new Scanner(file);
		}catch(FileNotFoundException e){
			System.out.println("File doesn't exist!");
			return;
		}
		int memAccess=0, faults=0, writes=0, curLoaded=0;
		//Pre-process of the memory addresses.
		int instructionNum=0;
		while(scan.hasNextLine()){
			//Parse the address of the instruction - "1 0x00000000" to "0x00000". Decodes"+"+ it into a long and then into an int
			int pageNum=(int)(Math.toIntExact((Long.decode(scan.nextLine().substring(2)))/PAGE_SIZE));
			if(next[pageNum]==null){ //This list of future addresses doesn't exist yet, so set it to a new object instance
				 next[pageNum]=new LinkedList(); 
			}
			next[pageNum].add(instructionNum); //Next instruction that will use this page
			instructionNum++;
		}
		//Reset the file scan for processing
		try{
			scan=new Scanner(file);
		}catch(FileNotFoundException e){
			System.out.println("File doesn't exist!");
			return;
		}
		//Optimal algorithm 
		while(scan.hasNextLine()){
			//Parse the memory address and operations ('l' - load, 's', store)
			//Parse the address of the instruction - "1 0x00000000" to "0x00000". Decodes"+"+ it into a long and then into an int
			String line=scan.nextLine();
			int pageNum=(int) Math.toIntExact((Long.decode(line.substring(2)))/PAGE_SIZE);
			char op=line.charAt(0);
			memAccess++; //Always increment memAccess whenever there is a line in the trace
			next[pageNum].remove(); //Remove this current address to find when this page is accessed next in the page replacement algorithm
			if(curLoaded<numFrames){ //Just insert or directly modify pages already in physical memory
				if(table[pageNum].getFrame()!=-1){ //If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
				}else{ //Frame number of the entry is not currently loaded an there is still an open spot in the table
					//The page is not loaded yet and the ram still has empty slots, so load the page into the next free frame
					ram[curLoaded]=table[pageNum];
					if(op=='s'){ //store operation sets dirty to true.
						ram[curLoaded].setDirty(true);
					}
					//Sets referenced and valid to true as well as frame to curLoaded. 
					ram[curLoaded].setReferenced(true);
					ram[curLoaded].setValid(true);
					ram[curLoaded].setFrame(curLoaded);
					//Increments curLoaded and since this was a fault, increments faults.
					curLoaded++;
					faults++;
				}
			}else{ //Either page is already loaded or evict using given algorithm to make room
				if(table[pageNum].getFrame()!=-1){ //If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
				}else{ //Frame number entry is not currently loaded and eviction needs to happen
					int toEvict=0; //Start at frame 0 and iterate over the frames to find the one that is accessed furthest in the future
					//The actual optimal algorithm at work
					for(int i=0; i<ram.length; i++){ //Search for page that is references furthest in the future; i=the frame number to check the entry's occurrances
						if(next[ram[i].getIndex()].peek()==null){ //Find the next time this page is referenced
							toEvict=i; //If it has a null linked list, there are no more references to it, so it's the optimal eviction
							break;
						}
						if((int)next[ram[i].getIndex()].peek()>(int)next[ram[toEvict].getIndex()].peek()){ //Set the new page that will be accessed furthest in the future
							toEvict=i; //Set new min to this frame's page index
						}
					}
					if(ram[toEvict].isDirty()){ //If the page is dirty, increment writes
						writes++; 
					}
					//Reset the toEvict's entry's properties
					ram[toEvict].setDirty(false);
					ram[toEvict].setReferenced(false);
					ram[toEvict].setValid(false);
					ram[toEvict].setFrame(-1);

					//Set the new entry's properties
					ram[toEvict]=table[pageNum]; //Replace toEvict page with new page
					if(op=='s'){ //store operation sets dirty to true.
						ram[toEvict].setDirty(true);
					}
					ram[toEvict].setReferenced(true);
					ram[toEvict].setValid(true);
					ram[toEvict].setFrame(toEvict);
					//Since this was a fault, increments faults
					faults++;
				}
			}
		}
		System.out.println("Algorithm: OPT\nNumber of frames: "+numFrames+"\nTotal memory accesses: "+memAccess);
		System.out.println("Total page faults: "+faults+"\nTotal writes to disk: "+writes);
	}

	@SuppressWarnings("unchecked")
	public void lru()
	{
		File file=new File(trace);
		entry[] ram=new entry[numFrames], table=genNewTable();
		Scanner scan;
		try{
			scan=new Scanner(file);
		} catch(FileNotFoundException e){
			System.out.println("File doesn't exist!");
			return;
		}
		int memAccess=0, faults=0, writes=0, curLoaded=0;
		//LRU Algorithm
		while(scan.hasNextLine()){
			//Parse the memory address and operations ('l' - load, 's', store)
			String line=scan.nextLine();
			int pageNum=(int) Math.toIntExact((Long.decode(line.substring(2)))/PAGE_SIZE);
			char op=line.charAt(0);
			memAccess++; //Always increment memAccess whenever there is a line in the trace
			if(curLoaded<numFrames){ //Just insert or directly modify pages already in physical memory
				if(table[pageNum].getFrame()!=-1){ //If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
					ram[table[pageNum].getFrame()].setLRU(memAccess); //updates/sets lruData 
				}else{ //Frame number of the entry is not currently loaded an there is still an open spot in the table
					ram[curLoaded]=table[pageNum];
					if(op=='s'){ //store operation sets dirty to true.
						ram[curLoaded].setDirty(true);
					}
					ram[curLoaded].setReferenced(true);
					ram[curLoaded].setValid(true);
					ram[curLoaded].setFrame(curLoaded);
					ram[curLoaded].setLRU(memAccess); //updates/sets lruData 
					//Increments curLoaded and since this was a fault, increments faults.
					curLoaded++;
					faults++;
				}
			}else{ //Either page is already loaded or evict using given algorithm to make room
				if(table[pageNum].getFrame()!=-1){ //If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
					ram[table[pageNum].getFrame()].setLRU(memAccess); //updates/sets lruData 
				}else{ //Frame number entry is not currently loaded and eviction needs to happen
					int toEvict=-1;
					//Actual LRU algorithm as stated in notes
					for(int i=0; i<ram.length; i++){
						if(toEvict==-1){ //Makes sure that toEvict is within the scope
							toEvict=i;
						}else if(ram[toEvict].getLRU()>ram[i].getLRU()){ //Finds the least used to evict
							toEvict=i;
						}
					}
					if(ram[toEvict].isDirty()){ //If the page is dirty, increment writes
						writes++;
					}
					//Reset the toEvict's properties
					ram[toEvict].setDirty(false);
					ram[toEvict].setReferenced(false);
					ram[toEvict].setValid(false);
					ram[toEvict].setLRU(0);
					ram[toEvict].setFrame(-1);

					//Set the new entry's properties
					ram[toEvict]=table[pageNum]; //Replace toEvict page with new page
					if(op=='s'){ //store operation sets dirty to true.
						ram[toEvict].setDirty(true);
					}
					ram[toEvict].setReferenced(true);
					ram[toEvict].setValid(true);
					ram[toEvict].setFrame(toEvict);
					ram[toEvict].setLRU(memAccess); //updates/sets lruData 
					//Since this was a fault, increments faults
					faults++;
				}
			}
		}
		System.out.println("Algorithm: LRU\nNumber of frames: "+numFrames+"\nTotal memory accesses: "+memAccess);
		System.out.println("Total page faults: "+faults+"\nTotal writes to disk: "+writes);
	}

	//I was unable to get this to work exactly as described and I am unsure why. 
	//I appear to have the same algorithm as described by the slides/notes. 
	//When I tried to follow exactly how second chance was described and not clock, I get the same results. 
	@SuppressWarnings("unchecked")
	public void sca()
	{
		File file=new File(trace);
		entry[] ram=new entry[numFrames], table=genNewTable();
		int clock=0; //Used for implementation of Second Chance Algorithm
		Scanner scan;
		try{
			scan=new Scanner(file);
		} catch(FileNotFoundException e){
			System.out.println("File doesn't exist!");
			return;
		}
		int memAccess=0, faults=0, writes=0, curLoaded=0;
		while(scan.hasNextLine()){
			//Parse the memory address and operations ('l' - load, 's', store)			
			//Parse the address of the instruction - "1 0x00000000" to "0x00000". Decodes"+"+ it into a long and then into an int
			String line=scan.nextLine();
			long address=Long.decode(line.substring(2));
			char op=line.charAt(0);
			memAccess++;
			int pageNum=(int) Math.toIntExact(address/PAGE_SIZE);
			if(curLoaded<numFrames){ //Just insert or directly modify pages already in physical memory
				if(table[pageNum].getFrame()!=-1){//If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
				}else{ //Frame number of the entry is not currently loaded an there is still an open spot in the table
					//If the page isn't already loaded, load it into the next slot in ram since it's not full yet
					ram[curLoaded]=table[pageNum];
					if(op=='s'){ //store operation sets dirty to true.
						ram[curLoaded].setDirty(true);
					}
					ram[curLoaded].setReferenced(true);
					ram[curLoaded].setValid(true);
					ram[curLoaded].setFrame(curLoaded);
					//Increments curLoaded and since this was a fault, increments faults.
					curLoaded++;
					faults++;
				}
			}else{ //Either page is already loaded or evict using given algorithm to make room
				if(table[pageNum].getFrame()!=-1){//If the frame number of the entry is not -1, then it is currently in ram
					if(op=='s'){ //store operation sets dirty to true.
						ram[table[pageNum].getFrame()].setDirty(true);
					}
				}else{ //Frame number entry is not currently loaded and eviction needs to happen
					int toEvict=-1; //Choose a random frame number to evict its page
					//Actual Second chance algorithm using clock algorithm for simplicity. 
					while(toEvict==-1){ //Haven't found a page to evict yet, will go all the way through ram and restart if needed
						if(ram[clock].isReferenced()){
							ram[clock].setReferenced(false); //Give the frame a second-chance and set its referenced bit to 0
							clock=(clock+1)%numFrames;
						}else{
							toEvict=clock; //If it's not referenced, evict it
							clock=(clock+1)%numFrames;
							break;
						}
					}
					if(ram[toEvict].isDirty()){ //If the page is dirty, increment writes
						writes++;
					}
					//Reset the toEvict's properties
					ram[toEvict].setDirty(false);
					ram[toEvict].setReferenced(false);
					ram[toEvict].setValid(false);
					ram[toEvict].setFrame(-1);

					//Set the new entry's properties
					ram[toEvict]=table[pageNum]; //Replace toEvict page with new page
					if(op=='s'){ //store operation sets dirty to true.
						ram[toEvict].setDirty(true);
					}
					ram[toEvict].setReferenced(true);
					ram[toEvict].setValid(true);
					ram[toEvict].setFrame(toEvict);
					//Since this was a fault, increments faults
					faults++;
				}
			}
		}
		// System.out.println(numFrames+" "+faults); Used for 2-100 test
		System.out.println("Algorithm: SECOND\nNumber of frames: "+numFrames+"\nTotal memory accesses: "+memAccess);
		System.out.println("Total page faults: "+faults+"\nTotal writes to disk: "+writes);
	}

	//Helper method
	private entry[] genNewTable()
	{
		int numberOfEntries=(int)(Math.pow(2, 32)/PAGE_SIZE); //Number of entries in the page table
		entry[] table=new entry[numberOfEntries];

		for(int i=0; i<table.length; i++){
			entry newEntry=new entry();
			newEntry.setIndex(i);
			table[i]=newEntry;
		}

		return table;
	}
}