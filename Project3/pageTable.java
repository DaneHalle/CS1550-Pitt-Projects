public class pageTable
{
	private entry[] Table;

	public pageTable()
	{
		int numberOfEntries=(int)Math.pow(2, 32)/(int)Math.pow(2,12); //Number of entries in the page table
		Table=new entry[numberOfEntries];
		for(int i=0; i<Table.length; i++){
			entry newEntry=new entry();
			Table[i]=newEntry;
		}
	}

	public entry get(int index)
	{
		return Table[index];
	}

	public int size()
	{
		return Table.length;
	}
}