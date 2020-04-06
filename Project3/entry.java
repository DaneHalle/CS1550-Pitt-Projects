public class entry
{
	private int index, frame, lruData;
	private boolean dirty, valid, referenced;

	public entry()
	{
		index=-1;
		frame=-1;
		dirty=false;
		valid=false;
		referenced=false;
		lruData=0;
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public boolean isReferenced()
	{
		return referenced;
	}

	public boolean isValid()
	{
		return valid;
	}

	public int getIndex()
	{
		return index;
	}

	public int getFrame()
	{
		return frame;
	}

	public int getLRU()
	{
		return lruData;
	}

	public void setDirty(boolean val)
	{
		dirty=val;
	}

	public void setReferenced(boolean val)
	{
		referenced=val;
	}

	public void setValid(boolean val)
	{
		valid=val;
	}

	public void setIndex(int val)
	{
		index=val;
	}

	public void setFrame(int val)
	{
		frame=val;
	}

	public void setLRU(int val)
	{
		lruData=val;;
	}
}