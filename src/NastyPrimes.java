/********************************************************************
 * 
 *  Computes the primes in an incredibly inefficient way, by trial division.
 *  Uses SwingWorker to update a GUI in fairly concurrent style.
 * 
 *  After about 1 minute, the exponents get difficult - 
 *  so one actual use is for benchmarking different CPUs.
 * 
 *******************************************************************/

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.util.List;

public class NastyPrimes extends JFrame implements ActionListener 
{
	private JLabel titleLabel, numPrimesLabel, currentNumberLabel, timerLabel, estimateLabel;
	private JButton startButton, stopButton, resetButton, quitButton;
	private JTextField searchPointField, noFoundField, timerField, estimateField;
	private JTextArea areaForFoundPrimes;
	private JScrollPane scrollPane;
	private JPanel buttonPanel, eastPanel, scrollPanel;
	private PrimeFinder pf;
	//private ActiveBenchmarker ab;
	private String standardSpacing = String.format("%100s","");
	long startTime = 0;

	/****************************************************************************************************
	 *   GUI code.
	 ***************************************************************************************************/
	public NastyPrimes() 
	{
		buttonPanel = new JPanel();
		eastPanel = new JPanel(new FlowLayout());
		
		scrollPanel = new JPanel();
		add(buttonPanel, BorderLayout.NORTH);
		add(scrollPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		
		//  Gets rid of the ugly JFrame surrounding frame.
		this.setUndecorated(true);
		getContentPane().setBackground(Color.black);
		Border undecorBorder = new BevelBorder(0, Color.BLACK, Color.GRAY);
		((JComponent) this.getContentPane()).setBorder(undecorBorder);
		
		//  Centers GUI using two methods. 		
		setSize(800,600);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();	
		this.setLocation(getCentralWidth(dim), getCentralHeight(dim));	

		
		//  Add buttons to north...
		titleLabel = new JLabel("  A Nasty Prime Calculator  ");
		titleLabel.setFont(new Font("Courier", Font.BOLD, 18));
		buttonPanel.add(titleLabel);
		buttonPanel.setBorder(undecorBorder);
		startButton = makeButton("Start");
		stopButton = makeButton("Stop");
		stopButton.setEnabled(false);
		resetButton = makeButton("Reset");
		resetButton.setEnabled(false);
		JLabel quitFiller = new JLabel(standardSpacing);
		buttonPanel.add(quitFiller);
		quitButton = makeButton(" X ");	
		quitButton.addMouseListener(new MouseListener() 
		{	public void mouseReleased(MouseEvent e) {}
		    public void mousePressed(MouseEvent e) {}
		    public void mouseExited(MouseEvent e) {	quitButton.setBackground(Color.black);  quitButton.setForeground(Color.green);     }
		    public void mouseEntered(MouseEvent e) { quitButton.setBackground(Color.red); quitButton.setForeground(Color.white); }
		    public void mouseClicked(MouseEvent e) {}
	    });
		
		//  Format stats panel to the right.
		Dimension east = new Dimension(250,200);
		eastPanel.setPreferredSize(east);
		JLabel fillerLabel = new JLabel(standardSpacing);
		eastPanel.add(fillerLabel);
		eastPanel.setBorder(undecorBorder);
		
		// This will display the integer I'm currently testing. 
		currentNumberLabel = new JLabel("  CURRENTLY CHECKING THE NUMBER: ");
		eastPanel.add(currentNumberLabel);
		searchPointField = new JTextField(15);
		searchPointField.setText("0");
		searchPointField.setForeground(Color.BLACK);
		eastPanel.add(searchPointField);
		JLabel fillerLabel2 = new JLabel(standardSpacing);
		eastPanel.add(fillerLabel2);

		//  This will display the number of primes I've found.
		numPrimesLabel = new JLabel("  NO. PRIMES FOUND SO FAR:  ");
		eastPanel.add(numPrimesLabel);
		noFoundField = new JTextField(15);
		noFoundField.setForeground(Color.BLACK);
		noFoundField.setText("0");
		eastPanel.add(noFoundField);
		
		JLabel fillerLabel3 = new JLabel(standardSpacing);
		eastPanel.add(fillerLabel3);
		//  Creates a string of asterisks...
		String asterisks = new String(new char[40]).replace("\0", "*");
		JLabel asteriskLabel = new JLabel(String.format("%s", asterisks));
		eastPanel.add(asteriskLabel);
		JLabel fillerLabel4 = new JLabel(standardSpacing);
		eastPanel.add(fillerLabel4);
		
		//  Adds timer fields
		timerLabel = new JLabel("System has been running for:");
		eastPanel.add(timerLabel);
		timerField = new JTextField(15);
		timerField.setForeground(Color.BLACK);
		timerField.setText("0");
		eastPanel.add(timerField);
		
		JLabel fillerLabel5 = new JLabel(standardSpacing);
		eastPanel.add(fillerLabel5);
		
		//  Adds efficiency estimate fields.
		estimateLabel = new JLabel("Primes per second:");
		eastPanel.add(estimateLabel);
		estimateField = new JTextField(15);
		estimateField.setForeground(Color.BLACK);
		estimateField.setText("0");
		eastPanel.add(estimateField);
		
		
		//  A scrollable text area to show all the primes found so far.
		areaForFoundPrimes = new JTextArea(30,35);
		areaForFoundPrimes.setBackground(Color.black);	areaForFoundPrimes.setForeground(Color.GREEN);
		scrollPane = new JScrollPane(areaForFoundPrimes);
		scrollPanel.add(scrollPane);
		EmptyBorder eb = new EmptyBorder(new Insets(10, 30, 10, 10));        
		areaForFoundPrimes.setBorder(eb);
		Font font = new Font("Arial", Font.ITALIC, 12);
		areaForFoundPrimes.setFont(font);

		pack();
		setVisible(true);
	}

	
	//  Methods to center screen.
	private int getCentralWidth(Dimension dim) 
	{ return (dim.width/2 - this.getSize().width/2); }
	
	private int getCentralHeight(Dimension dim) 
	{ return (dim.height/2 - this.getSize().height/2); }
	
	
	//  Method to define and format a button, 
	// including a caption as actionPerformed trigger.
	private JButton makeButton(String caption) 
	{
		JButton b = new JButton(caption);
		b.setActionCommand(caption);
		b.addActionListener(this);
		b.setBackground(Color.black);
		b.setForeground(Color.green);
		buttonPanel.add(b);
		return b;
	}

	
	
/****************************************************************************************************
 *   Application code.
 ***************************************************************************************************/

	// Manage clicks - enabling and unenabling buttons as required
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand() == "Start") 
		{	// This line creates a PrimeFinder object by casting to void.
			(pf = new PrimeFinder()).execute();		
		
			stopButton.setEnabled(true);
			startButton.setEnabled(false);
			resetButton.setEnabled(false);
			startTimer();
		}
		
		else if (e.getActionCommand() == "Stop") 
		{ 	stopButton.setEnabled(false);
			resetButton.setEnabled(true);
			pf.cancel(true);
			stopTimer();
		}
		
		else if (e.getActionCommand() == "Reset") 
		{	searchPointField.setText("0");
			areaForFoundPrimes.setText("");
			noFoundField.setText("0");
			startButton.setEnabled(true);
			pf = null;
		}
		
		else if (e.getActionCommand() == " X ") 
		{	
			Object [] options = {"Leave", "More Bad Primes"};
			int choice = JOptionPane.showOptionDialog(null, "Are you sure?", "Stop being cool", JOptionPane.YES_NO_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
			if ( choice == 0 )
			{
				System.exit(0);
			}
		}
	}

	
	//  Methods to keep an accurate state of the program's time and efficiency.
	public void startTimer() 
	{
		startTime = System.currentTimeMillis();				// Logs program start time for the efficiency test.
	}
	
	public void stopTimer() 
	{
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000L;
		timerField.setText("   "+ elapsedTime +" seconds.");
	}

	
/****************************************************************************************************
 *  The worker thread that does the prime finding. 
****************************************************************************************************/
private class PrimeFinder extends SwingWorker<Void, PrimePair> 
{
	int index = 1;
	public Void doInBackground() 
	{	areaForFoundPrimes.append("These are the primes: \n\n");
		Integer maybeAPrime  = 2;

		while (!isCancelled())	
		{	if (checkInteger(maybeAPrime))
			{	publish(new PrimePair(index, maybeAPrime));
				index++;
			}
		maybeAPrime++;	
		}
		return null;
	}



	//  Displays the most recent current-number and total-found pair of numbers.
	public void process(List<PrimePair> visits)
	{	for (int i = visits.size(); i > 0; i--)
		{	PrimePair pair = visits.get(visits.size() - i);
			searchPointField.setText(""+ pair.aPrime);
			noFoundField.setText(""+ pair.ordinalPrime);	
			areaForFoundPrimes.append("The "+ pair.ordinalPrime +"th prime is "+ pair.aPrime +".\n");
			long elapsedTime = (System.currentTimeMillis() - startTime) / 1000L;
			timerField.setText(elapsedTime +" seconds.");
			
			//  Now set efficiency estimate.
			//  (Test prevents division by zero in the cast below.)
			if (elapsedTime > 0)
			{
				int primesPerSec = (int) (pair.ordinalPrime / elapsedTime);
				estimateField.setText(primesPerSec +" per second.");
			}
		}
	}


	//  Method to search if N is prime. 
	//  Loops i from 2 to N/2 and check if [N % i == 0] 
	//  (this asks: 'is the remainder after division equal to zero?'). 
	//  If this is so for any i, then N is not prime.
	public Boolean checkInteger(int numToCheck) 
	{	if (numToCheck<=1) 
		return false;	
	
	else 
	{	for (int i = 2; i <= numToCheck/2; i++) 
		{	if (numToCheck % i == 0) 
				return false;	
		}
	}
	return true;
	}
}
	
	
//  Object to store the pair of numbers:
//    1. The nth number successfully identified as a prime (ordinalPrime). 
//    2. The prime at position n in the series of all primes (aPrime).
private static class PrimePair 
{
	private final Integer ordinalPrime, aPrime;
	PrimePair(Integer checked, Integer aPrime) 
	{
		this.ordinalPrime = checked;
		this.aPrime = aPrime;
	}

}


/****************************************************************************************************
 *  A second worker thread to calculate and publish an estimate of primes per second. 
****************************************************************************************************/
//private class ActiveBenchmarker extends SwingWorker<Void, Integer> 
//{
//	int index = 1;
//	public Void doInBackground() 
//	{	areaForFoundPrimes.append("These are the primes: \n\n");
//		Integer maybeAPrime  = 2;
//
//		while (!isCancelled())	
//		{	if (checkInteger(maybeAPrime))
//			{	publish(new Integer(index));
//				index++;
//			}
//		maybeAPrime++;	
//		}
//		return null;
//	}
//
//	//  Displays the most recent primes/sec estimate.
//	public void process(List<Integer> visits)
//	{	for (int i = visits.size(); i > 0; i--)
//		{	//PrimePair pair = visits.get(visits.size() - i);
//			//timerField.setText("" + count);
//			//count++;     
//			noFoundField.setText(""+ index);	
//			areaForFoundPrimes.append("The "+ index);
//		}
//	}
//
//
//	//  Method to search if N is prime. 
//	//  Loops i from 2 to N/2 and check if [N % i == 0] 
//	//  (this asks: 'is the remainder after division equal to zero?'). 
//	//  If this is so for any i, then N is not prime.
//	public Boolean checkInteger(int numToCheck) 
//	{	if (numToCheck<=1) 
//		return false;	
//	
//	else 
//	{	for (int i = 2; i <= numToCheck/2; i++) 
//		{	if (numToCheck % i == 0) 
//				return false;	
//		}
//	}
//	return true;
//	}
//}

}

