public class vmsim
{
	public static void main(String[] args)
	{
		if(args.length!=5){
			System.out.println("Error");
			return;
		}
		int numFrame=-1;
		String alg="";
		String traceFile="";
		if(!args[0].equals("-n")){
			System.out.println("Error");
			return;
		}else{
			numFrame=Integer.parseInt(args[1]);
		}
		if(!args[2].equals("-a")){
			System.out.println("Error");
			return;
		}else{
			alg=args[3];
		}
		traceFile=args[4];

		Simulation sim=new Simulation(numFrame, traceFile);
		if(alg.equals("opt")){
			sim.optimal();
		}else if(alg.equals("lru")){
			sim.lru();
		}else if(alg.equals("second")){
			sim.sca();
		}else{
			System.out.println("Error");
			return;
		}
	}
}