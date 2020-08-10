package main;

import java.awt.EventQueue;

import javax.swing.*;

import grid.Mode;
import grid.Grid;
import grid.Context;
import grid.Set;

import java.awt.Color;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;

import java.awt.Dimension;

import java.awt.Font;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.GroupLayout.Alignment;

/**
 * Main frame of the program.
 * @author Majid Feyzi
 * */
public class Main extends Context {
	
	private JFrame frame;
	private Grid grid;

	private JScrollPane buttonsScrollablePanel;
	private JScrollPane setsScrollablePanel;
	private JPanel buttonsPanel;
	private JPanel setsPanel;

	private ButtonGroup type;
	private JRadioButton line;
	private JRadioButton point;

	private JButton nextButton;
	private JButton helpButton;
	private JButton runButton;
	private JButton randomButton;
	private JButton clearButton;
	private JButton exitButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Main window = new Main();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		// Initialize frame
		frame = new JFrame("Set Cover");
		frame.setSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				grid.setSize(new Dimension(grid.getWidth(), frame.getHeight()));
				grid.resize();
				
				// Change focus to grid
				grid.requestFocus();
			}
		});
		
		// Initialize grid canvas and add it to frame
		grid = new Grid(this);
		grid.setBackground(Color.WHITE);
		grid.setColor("000000");
		grid.setPreferredSize(new Dimension(grid.getWidth(), Config.FRAME_HEIGHT));
		
		// Initialize bottom panel and add it to bottom of frame
		buttonsScrollablePanel = createScrollablePanel(Config.RIGHT_PANEL_WIDTH, Config.THEME_COLOR);
		buttonsPanel = (JPanel) buttonsScrollablePanel.getViewport().getView();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// Initialize sets panel to show sets with them color and pass it to canvas
		setsScrollablePanel = createScrollablePanel(Config.LEFT_PANEL_WIDTH, Color.WHITE);
		setsPanel = (JPanel) setsScrollablePanel.getViewport().getView();
		setsPanel.setLayout(new BoxLayout(setsPanel, BoxLayout.Y_AXIS));

		// Initialize clear canvas button
		clearButton = createButton("Reset");
		clearButton.addActionListener(e -> {
			grid.clear();
			point.doClick();

			// Remove sets panel objects
			setsPanel.removeAll();
			setsPanel.revalidate();
			setsPanel.repaint();

			// Change focus to grid
			grid.requestFocus();
		});

		nextButton = createButton("Next step");
		nextButton.addActionListener(e -> {
			grid.next();

			// Change focus to grid
			grid.requestFocus();
		});
		
		runButton = createButton("Run");
		runButton.addActionListener(e -> {
			grid.startAutoSolve();

			// Change focus to grid
			grid.requestFocus();
		});
		buttonsPanel.add(runButton);
		buttonsPanel.add(nextButton);
		
		randomButton = createButton("Random");
		randomButton.addActionListener(e -> {
			try {
				int count = Integer.parseInt(JOptionPane.showInputDialog("Please insert number of points"));

				int padding = 100;
				int width = grid.getWidth() - padding;
				int height = grid.getHeight() - padding;
				grid.generateRandomSets(count, padding, padding, width, height);

			} catch (Exception ignored) {}
		});
		buttonsPanel.add(randomButton);
		buttonsPanel.add(clearButton);

		helpButton = createButton("Help");
		helpButton.addActionListener(e -> {
			Help hframe = new Help();
			hframe.setVisible(true);
		});
		buttonsPanel.add(helpButton);

		exitButton = createButton("Exit");
		exitButton.addActionListener(e -> System.exit(0));
		buttonsPanel.add(exitButton);

		// Initialize radio buttons and add them to button group
		point = createRadioButton("Point");
		line = createRadioButton("Line");
		point.setSelected(true);
		buttonsPanel.add(point);
		buttonsPanel.add(line);
		type = new ButtonGroup();
		type.add(line);
		type.add(point);
		
		point.addChangeListener(e -> {
			if (point.isSelected()) {
				grid.setType(Mode.Point);
				grid.setColor("#000000");
			}

			// Change focus to grid
			grid.requestFocus();
		});
		line.addChangeListener(e -> {
			if (line.isSelected()) {
				grid.setType(Mode.Line);
				grid.refreshColor();
			}

			// Change focus to grid
			grid.requestFocus();
		});
		grid.setType(Mode.Point);
		
		// Change focus to grid and add views constraints
		grid.setFocusable(true);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(setsScrollablePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(grid, GroupLayout.DEFAULT_SIZE, 1472, Short.MAX_VALUE)
					.addComponent(buttonsScrollablePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(setsScrollablePanel, GroupLayout.PREFERRED_SIZE, 1001, Short.MAX_VALUE)
				.addComponent(grid, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
				.addComponent(buttonsScrollablePanel, GroupLayout.PREFERRED_SIZE, 1001, Short.MAX_VALUE)
		);
		frame.getContentPane().setLayout(groupLayout);
		grid.requestFocus();
	}

	@Override
	public void onSetsChange(List<Set> sets) {

		// Remove sets panel all objects
		setsPanel.removeAll();
		setsPanel.revalidate();
		setsPanel.repaint();

		for (Set set : sets) {

			// Create a color square to show color of set beside it's title
			JButton csquare = new JButton();
			csquare.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.black, 1),
					BorderFactory.createLineBorder(set.getColor(), 7)
			));
			csquare.setBackground(set.getColor());

			// Create title label of set
			JLabel title = new JLabel(set.getName());
			title.setFont(new Font(Config.FONT_NAME, Font.PLAIN, Config.FONT_SIZE));
			title.setBorder(new EmptyBorder(0, 5, 0, 5));

			// Create a panel to add color square and title label beside each other in a row
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.setBorder(new EmptyBorder(5, 5, 5, 5));
			p.setBackground(Color.white);
			p.setAlignmentX(0);
			p.add(csquare);
			p.add(title);

			// Add set row to sets panel
			setsPanel.add(p);
		}

		buttonsPanel.repaint();
	}

	@Override
	public void onSolveComplete(String result) {
		JLabel label = new JLabel("<html>" + result.replace("\n", "<br>") + "</html>");
		label.setFont(new Font(Config.FONT_NAME, Font.PLAIN, Config.FONT_SIZE));
		JOptionPane.showMessageDialog(null, label, "Result", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Create radio button method
	 * */
	private JRadioButton createRadioButton(String title) {
		JRadioButton radio = new JRadioButton(title);
		radio.setHorizontalAlignment(SwingConstants.CENTER);
		radio.setPreferredSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
		radio.setBackground(null);
		radio.setForeground(Color.WHITE);
		radio.setFont(new Font(Config.FONT_NAME, Font.BOLD, Config.FONT_SIZE));
		return radio;
	}

	/**
	 * Create left and right panels method
	 * */
	private JScrollPane createScrollablePanel(int width, Color bg) {
		JPanel panel = new JPanel();
		panel.setAutoscrolls(true);
		panel.setBackground(bg);
		panel.setPreferredSize(new Dimension(width, 0));
		JScrollPane panelScorllPane = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelScorllPane.setPreferredSize(new Dimension(width, 0));
		panelScorllPane.setBorder(BorderFactory.createDashedBorder(Color.lightGray));

		return panelScorllPane;
	}

	/**
	 * Create button method
	 * */
	private JButton createButton(String title) {
		JButton button = new JButton(title);
		button.setPreferredSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
		button.setFont(new Font(Config.FONT_NAME, Font.BOLD, Config.FONT_SIZE));
		button.setForeground(Color.WHITE);
		button.setBackground(null);
		button.setBorderPainted(false);

		return button;
	}
}
