package WebViewer_;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

public class WebViewer_ {

	private JFrame frame,frame2;
	private JTextField torPathTextField, torProfilePathTextField, webLinkTextField;
	private WebDriver driver;
	private JSpinner viewCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5000, 1));
	private JTextArea log;
	private JButton startButton,stopButton;
	private boolean start=false;
	private FirefoxOptions firefoxOptions;
	
	/**
	 * @author Kristijan
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					_WebViewer_ window = new _WebViewer_();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public _WebViewer_() {
		initialize();
	}
	
	private void initialize() {
		JPanel panel = new JPanel();
		frame = new JFrame("WebViewer_");
		frame.setResizable(false);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 645, 420);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		panel.setBackground(new Color(130,130,130));
		panel.setLayout(null);
		
		JButton helpButton = new JButton("Help");
		helpButton.setBounds(517, 11, 92, 21);
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InfoPane();
			}
		});
		frame.getContentPane().add(helpButton);
		
		JLabel titleLabel = new JLabel("WebViewer_",SwingConstants.CENTER);
		titleLabel.setBounds(0, 11, 623, 59);
		titleLabel.setForeground(new Color(0,255,0));
		titleLabel.setFont(new Font("System",Font.BOLD, 45));
		frame.getContentPane().add(titleLabel);
		
		JLabel torPathLabel = new JLabel("Tor Path:");
		torPathLabel.setBounds(10, 74, 239, 47);
		torPathLabel.setFont(new Font("System",Font.PLAIN, 30));
		frame.getContentPane().add(torPathLabel);
		
		JLabel torProfileLabel = new JLabel("Tor Profile Path:");
		torProfileLabel.setBounds(10, 119, 239, 47);
		torProfileLabel.setFont(new Font("System",Font.PLAIN, 30));
		frame.getContentPane().add(torProfileLabel);
		
		JLabel webLinkLabel = new JLabel("Web Link:");
		webLinkLabel.setBounds(10, 162, 239, 47);
		webLinkLabel.setFont(new Font("System",Font.PLAIN, 30));
		frame.getContentPane().add(webLinkLabel);
		
		JLabel viewCountLabel = new JLabel("View Count:");
		viewCountLabel.setBounds(10, 208, 239, 47);
		viewCountLabel.setFont(new Font("System",Font.PLAIN, 30));
		frame.getContentPane().add(viewCountLabel);
		
		log = new JTextArea();
		log.append("- - - WebViewer_ - - -\n");
		log.setEditable(false);
		log.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(log,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(10, 266, 599, 99);
		frame.getContentPane().add(scroll);
		
		viewCountSpinner.setBounds(259, 208, 131, 47);
		frame.getContentPane().add(viewCountSpinner);
		
		torPathTextField = new JTextField();
		torPathTextField.setBounds(259, 81, 350, 33);
		frame.getContentPane().add(torPathTextField);
		torPathTextField.setColumns(10);
		
		torProfilePathTextField = new JTextField();
		torProfilePathTextField.setBounds(259, 125, 350, 33);
		torProfilePathTextField.setColumns(10);
		frame.getContentPane().add(torProfilePathTextField);
		
		webLinkTextField = new JTextField();
		webLinkTextField.setBounds(259, 170, 350, 33);
		webLinkTextField.setColumns(10);
		frame.getContentPane().add(webLinkTextField);
		
		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Check();
			}
		});
		startButton.setBounds(440, 216, 131, 33);
		frame.getContentPane().add(startButton);
		stopButton = new JButton("Stop");
		stopButton.setVisible(false);
		stopButton.setBounds(440, 216, 131, 33);
		frame.getContentPane().add(stopButton);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Stop();
			}
		});
		FirstCheck();
		InfoSetup();
	}
	
	private void FirstCheck() {
		try {
		File save = new File((System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/save.properties");
		if(!save.exists()) {
			save.createNewFile();
		}
		Properties prop = new Properties();
		prop.load(new FileInputStream((System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/save.properties"));
		torPathTextField.setText(prop.getProperty("torPath"));
		torProfilePathTextField.setText(prop.getProperty("torProfilePath"));
		}catch(Exception e) {
			log.append("Error!\nRun this application as administrator\n");
			e.printStackTrace();
		}
		
		try {
		File bat = new File((System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/newIdentity.bat");
		bat.createNewFile();
		FileWriter fw = new FileWriter(bat);
		fw.write("@echo off\n");
		fw.write("echo AUTHENTICATE \"WebViewer_\">> commands.txt\n");
		fw.write("echo SIGNAL NEWNYM>> commands.txt\n");
		fw.write("echo QUIT>> commands.txt\n");
		fw.write((System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/nc64 localhost 9051 < commands.txt\n");
		fw.write("del /Q commands.txt\n");
		fw.close();
		}catch(Exception e1) {
			log.append("Error!\nRun this application as administrator\n");
			e1.printStackTrace();
		}
	}
	
	private void InfoSetup() {
		frame2 = new JFrame("WebViewer_Info");
		frame2.setVisible(false);
		frame2.setBounds(100, 100, 450, 530);
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.getContentPane().setLayout(null);
		frame2.setResizable(false);
		frame2.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(130,130,130));
		panel.setBounds(0, 0, 434, 491);
		frame2.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel torPathLabel = new JLabel("Tor Path:");
		torPathLabel.setBounds(10, 11, 325, 40);
		torPathLabel.setFont(new Font("System",Font.BOLD,23));
		torPathLabel.setForeground(Color.GREEN);
		panel.add(torPathLabel);
		
		JLabel torProfilePath = new JLabel("Tor Profile Path:");
		torProfilePath.setBounds(10, 136, 325, 40);
		torProfilePath.setFont(new Font("System",Font.BOLD,23));
		torProfilePath.setForeground(Color.GREEN);
		panel.add(torProfilePath);
		
		JTextArea text1 = new JTextArea("Tor browser must be running in the background and must be connected to the network!");
		text1.setBounds(10, 286, 414, 88);
		text1.setWrapStyleWord(true);
		text1.setLineWrap(true);
		text1.setEditable(false);
		text1.setBackground(new Color(130,130,130));
		text1.setFont(new Font("System",Font.PLAIN,22));
		text1.setForeground(Color.YELLOW);
		panel.add(text1);
		
		JTextArea text2 = new JTextArea("Note that you must have the Tor browser on your device!");
		text2.setBounds(10, 380, 414, 88);
		text2.setWrapStyleWord(true);
		text2.setLineWrap(true);
		text2.setEditable(false);
		text2.setBackground(new Color(130,130,130));
		text2.setFont(new Font("System",Font.PLAIN,22));
		text2.setForeground(Color.RED);
		panel.add(text2);
		
		JTextArea torProfilePathSection = new JTextArea("You must enter the Tor profile folder\nExample:  PathToTor/Tor Browser/Browser/TorBrowser/Data/Browser/profile.default");
		torProfilePathSection.setBounds(10, 187, 414, 88);
		torProfilePathSection.setWrapStyleWord(true);
		torProfilePathSection.setEditable(false);
		torProfilePathSection.setBackground(new Color(130,130,130));
		torProfilePathSection.setFont(new Font("System",Font.PLAIN,16));
		torProfilePathSection.setLineWrap(true);
		panel.add(torProfilePathSection);
		
		JTextArea torPathSection = new JTextArea("You must enter thr Tor executable file in this field\nExample:\nPathToTor/Tor Browser/Browser/firefox.exe");
		torPathSection.setBounds(10, 49, 414, 88);
		torPathSection.setFont(new Font("System",Font.PLAIN,16));
		torPathSection.setEditable(false);
		torPathSection.setBackground(new Color(130,130,130));
		torPathSection.setWrapStyleWord(true);
		torPathSection.setLineWrap(true);
		panel.add(torPathSection);
	}
	
	private void InfoPane() {
		if(!frame2.isVisible()) {
			frame2.setVisible(true);
		}else {
			frame2.setVisible(false);
		}
	}
	
	private void Check() {
			try {
			if(torPathTextField.getText()==null||(torPathTextField.getText().equals(""))||!Files.isExecutable(Paths.get(torPathTextField.getText()))) {
				log.setText(log.getText()+"Invalid Tor path!\n");
				return;
			}
			log.append("Tor path might be valid!\n");
			if(torProfilePathTextField.getText()==null||(torProfilePathTextField.getText().equals(""))||!Files.isDirectory(Paths.get(torProfilePathTextField.getText()))) {
				log.setText(log.getText()+"Invalid Tor profile path!\n");
				return;
			}
			log.append("Tor profile path might be valid!\n");
			log.setCaretPosition(log.getText().length());
			if(webLinkTextField.getText().equals("")) {
				log.append("Web Link is empty!\n");
				log.setCaretPosition(log.getText().length());
				return;
			}
			Properties prop = new Properties();
			prop.setProperty("torPath", torPathTextField.getText());
			prop.setProperty("torProfilePath", torProfilePathTextField.getText());
			prop.store(new FileOutputStream((System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/save.properties"), "#WebViewer_");
			File file = new File(torPathTextField.getText());
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd /min "+file.getParent().replace("\\", "/")+"/TorBrowser/Tor/tor.exe --hash-password \"WebViewer_\" | more");
			log.append("Starting...\n");
			log.setCaretPosition(log.getText().length());
			viewCountSpinner.setEnabled(false);
			webLinkTextField.setEnabled(false);
			torProfilePathTextField.setEnabled(false);
			torPathTextField.setEnabled(false);
			new Thread(()->{
			Search();
			}).start();
			}catch(Exception e) {
				log.append("Error!\n");
				e.printStackTrace();
			}
	}
	
	private void Setup() {
		try {
			System.setProperty("webdriver.gecko.driver", (System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/geckodriver.exe");
			
			File torProfile = new File(torProfilePathTextField.getText());
			FirefoxProfile profile = new FirefoxProfile(torProfile);
			profile.setPreference("network.proxy.type", 1);
			profile.setPreference("network.proxy.socks", "127.0.0.1");
			profile.setPreference("network.proxy.socks_port", 9150);
			profile.setPreference("webdriver.load.strategy","unstable");
			
			File binaryPath = new File(torPathTextField.getText());
			FirefoxBinary binary = new FirefoxBinary(binaryPath);
			firefoxOptions = new FirefoxOptions();
			firefoxOptions.setProfile(profile);
			firefoxOptions.setBinary(binary);
			firefoxOptions.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
			firefoxOptions.setHeadless(true);
			
			driver = new FirefoxDriver(firefoxOptions);
			driver.switchTo().defaultContent();
		}catch(Exception e) {
			log.append("Error!\n");
			e.printStackTrace();
		}
	}
	
	private void Search() {
		startButton.setVisible(false);
		stopButton.setVisible(true);
		start=true;
		
		for(int i=0;i<(Integer)viewCountSpinner.getValue();i++) {
			if(start) {
			try {
			Setup();
			driver.navigate().to(webLinkTextField.getText());
			log.append((i+1)+": connected!\n");
			}catch(Exception e) {
			log.append((i+1)+": failed to connect!\nMake sure Tor works in the background\n");
			e.printStackTrace();
			}
			log.setCaretPosition(log.getText().length());
			try {
			Timer timer = new Timer();
			Thread thread = new Thread(()->{
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					driver.quit();
					NewIdentity();
				}
			};
			timer.schedule(task, 5000);
			});
			thread.start();
			thread.join();
			}catch(Exception e) {
				log.append("Error!\n");
				e.printStackTrace();
			}
			}else {
				break;
			}
			}
		startButton.setVisible(true);
		stopButton.setVisible(false);
		viewCountSpinner.setEnabled(true);
		webLinkTextField.setEnabled(true);
		torProfilePathTextField.setEnabled(true);
		torPathTextField.setEnabled(true);
		log.append("Finished!\n");
		log.setCaretPosition(log.getText().length());
			
	}
	
	private void Stop() {
		start=false;
		log.append("Stopping...\n");
		log.setCaretPosition(log.getText().length());
	}
	
	private void NewIdentity() {
		try {
			Process process = Runtime.getRuntime().exec("cmd /min start "+(System.getProperty("user.dir").replace("\\", "/"))+"/app/Other/newIdentity.bat");
			log.append("Created new identity!\n");
			process.destroy();
		} catch (IOException e) {
			log.append("Unable to create new identity!\n");
			e.printStackTrace();
		}
	}
	
}
