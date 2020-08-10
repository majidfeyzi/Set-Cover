package main;

import javax.swing.*;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Help of program.
 * Description about set cover problem and it's solution and how to interact with program.
 * @author Majid Feyzi
 * */
public class Help {

	private JFrame frame;

	private final String title = "Program Help";
	private final String subTitle = "Developed by: Majid Feyzi";
	private final String help = "<br />" +
			"<b>Set Cover Problem</b>:<br />" +
			"In Set Cover problem we have some points and some polygons on the plane, which each point placed inside at least one polygon and each polygon can contain zero or more points. each polygon with it's inside points is considered as a set.<br />" +
			"goal of this  problem is to select minimum number of sets to cover all points in the plane. <br />" +
			"In this program, approximation algorithm to sloving this problem, has been implemented that in it, you can draw sets manually and run algorithm step by step and see the results.<br />" +
			"<br />" +
			"<b>Drawing points and lines</b>:<br />" +
			"you can locate points on the plane and then clustering them. <br />" +
			"To add point you must select point mode from right panel of program and then click on the plane.<br />" +
			"Then for clustering points, you must change mode to line from the right panel of the progam and then click on the plane for drawing line. by drawing lines you can create a polygon by connecting last line to first line. to connect last line to first line you can click on first line or double click on the plane.<br />" +
			"The sequence of operatinos is not important. that is, you can draw polygons first and then add points inside of polygons to generate sets or vice versa.<br />" +
			"Name of created sets with it's number of points is showing in left panel of program.<br />" +
			"<br />" +
			"<b>Sets Modifying</b>:<br />" +
			"If you want change the location of points or polygons or lines you can undo your action and change it by Ctrl+Z .<br />" +
			"<br />" +
			"<b>Running the algorithm</b>:<br />" +
			"After specifying sets, you can run algorithm step by step, by clicking on Next step button.<br />" +
			"To run program automatically, Just click on Run button.<br />" +
			"After finish steps or complete the run, the selected sets will be shown as sorted in a dialog box.<br />" +
			"<br />" +
			"<b>Generate random sets</b>:<br />" +
			"If you want to generate random sets, you can click on the Random button, and it will be generate random sets.<br />" +
			"<br />" +
			"<b>Clear content</b>:<br />" +
			"To clear all content of the plane, you can click on the Reset button." +
			"<br />";

	/**
	 * Create the help frame.
	 */
	public Help() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		
		frame = new JFrame();
		frame.setSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(Config.THEME_COLOR);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		Component verticalStrut = Box.createVerticalStrut(10);
		frame.getContentPane().add(verticalStrut);
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(Config.FONT_NAME, Font.BOLD, Config.FONT_SIZE));
		titleLabel.setPreferredSize(new Dimension(Config.FRAME_WIDTH, 30));
		titleLabel.setForeground(Color.WHITE);
		frame.getContentPane().add(titleLabel);
		
		JLabel subTitleLabel = new JLabel(subTitle);
		subTitleLabel.setFont(new Font(Config.FONT_NAME, Font.PLAIN, Config.FONT_SIZE));
		subTitleLabel.setPreferredSize(new Dimension(Config.FRAME_WIDTH, 30));
		subTitleLabel.setForeground(Color.WHITE);
		frame.getContentPane().add(subTitleLabel);

		frame.getContentPane().add(verticalStrut);
		
		JTextPane helpTextPane = new JTextPane();
		helpTextPane.setFont(new Font(Config.FONT_NAME, Font.PLAIN, Config.FONT_SIZE));
		helpTextPane.setEditable(false);
		helpTextPane.setAlignmentY(Component.TOP_ALIGNMENT);
		helpTextPane.setContentType("text/html");
		helpTextPane.setText(help);
		helpTextPane.setBorder(padding);
		JScrollPane textAreaScroll = new JScrollPane (helpTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(textAreaScroll);
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

}
