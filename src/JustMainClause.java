import javax.swing.SwingUtilities;

public class JustMainClause{
	
/**
 * Launches a NastyPrimes object, which contains two SwingWorker threads.
 * @param args
 */
public static void main(String[] args) 
{
	SwingUtilities.invokeLater(new Runnable() 
	{
		public void run() 
		{
			new NastyPrimes();
		}
	});
}
}