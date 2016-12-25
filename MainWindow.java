import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainWindow {

	private JFrame frmImageProcessing;
	public static boolean runThreads;
	public static JButton btnStart;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					System.out.println("Starting the Main Window...");
					window.frmImageProcessing.setVisible(true);
				} catch (Exception e) {
					System.out.println("ERROR: MainWindow " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		runThreads = true;
		frmImageProcessing = new JFrame();
		frmImageProcessing.setResizable(false);
		frmImageProcessing.setTitle("Image Processing");
		frmImageProcessing.setBounds(100, 100, 540, 320);
		frmImageProcessing.getContentPane().setLayout(null);

		frmImageProcessing.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				runThreads = false;
				// System.exit(0);
			}
		});

		frmImageProcessing.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		btnStart = new JButton("START");
		btnStart.setToolTipText("");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent args0) {
				btnStart.setText("Running...");
				btnStart.setEnabled(false);
				GrabImage g = new GrabImage();
				Thread t = new Thread(g);
				t.start();
			}
		});
		btnStart.setBounds(157, 11, 200, 105);
		frmImageProcessing.getContentPane().add(btnStart);

		JLabel lblCredits = new JLabel("Created by: Akshay");
		lblCredits.setBounds(217, 156, 112, 14);
		frmImageProcessing.getContentPane().add(lblCredits);

		try {
		}

		catch (Exception ex) {
		}
	}
}
