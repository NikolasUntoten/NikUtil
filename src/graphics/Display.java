/**
 * Written by Nikolas Gaub, 2016
 */
package graphics;

import javax.swing.*;
import java.util.*;

/*  Easy to use™ Free™ utility for displaying and changing panels in a JFrame
 *  Contains only one main panel, and overlays other panels on top of main panel.
 *  This limit allows for simpler use.
 *  
 *  The Display has a main panel, at the bottom, with as many overlays as are added that layer on top.
 *  Display also has the functionality to switch to a menu
 *  TODO add functionality for stacking menus.
 *  
 *  MUST USE start() METHOD TO SHOW PANEL
 */

public class Display {
	/*
	 * main frame component used throughout display
	 */
	private JFrame frame;
	
	/*
	 * panel that is displayed by default, at the bottom layer. 
	 * used for the main game world and things like that.
	 */
	private JPanel mainPanel;
	
	/*
	 * overlay panels keeps track of all stacked panels on screen for easy
	 * adding and removal
	 */
	private Stack<JPanel> overlayPanels;
	
	/*
	 * contains all stacked panels and displays as one pane
	 */
	private JLayeredPane overlay;
	
	/*
	 * menu panel for menu mode functionality.
	 */
	private JPanel menu;
	
	/*
	 * boolean to keep track of menu mode state
	 */
	private boolean inMenuMode;
	
	/*
	 * boolean to allow the stopping of refresh
	 */
	private boolean refresh;
	
	/*
	 * width and height of panel, does not include border.
	 */
	private int width = 1000;
	private int height = 1000;
	
	/*
	 * refresh rate is the times per second the frame will refresh.
	 */
	private static final double REFRESH_RATE = 60;
	
	/*
	 * The time between each refresh of the frame.
	 */
	private static final long SLEEP_MILLIS = (long) (1000 / REFRESH_RATE);
	
	
	public Display(int width, int height, String name, JPanel mainPanel) {
		this.width = width;
		this.height = height;
		
		createFrame(name);
		createOverlay();
		
		this.mainPanel = mainPanel;
		overlay.add(mainPanel, 0);
		inMap = overlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actMap = overlay.getActionMap();
		
		overlayPanels = new Stack<JPanel>();
		inMenuMode = false;
	}
	
	/*
	 * Helper method to initiate frame
	 */
	private void createFrame(String name) {
		frame = new JFrame(name);
		frame.setBounds(0, 0, width + 16, height + 39);
		//frame.getContentPane().setSize(1000, 1000);
		//frame.setUndecorated(true);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * creates a JLayeredPane that is used to show multiple overlays, 
	 * with mainPanel being the bottom.
	 */
	private void createOverlay() {
		overlay = new JLayeredPane();
		overlay.setVisible(true);
		overlay.setLayout(null);
		overlay.setSize(width, height);
		frame.getContentPane().add(overlay);
	}
	
	/*
	 * Starts the refresh and makes frame visible.
	 */
	public void start() {
		refresh = true;
		createRefresh();
		frame.setVisible(true);
	}
	
	/*
	 * Creates a thread that repeats until the window is closed.
	 * Repaints and revalidates frame, then sleeps for a given time.
	 */
	private void createRefresh() {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (refresh) {
					frame.getContentPane().revalidate();
					frame.getContentPane().repaint();
					try {
						sleep(SLEEP_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}
	
	/*
	 * Stops refresh and makes panel invisible
	 */
	public void stop() {
		refresh = false;
		frame.setVisible(false);	
	}
	
	/*
	 * stops all things to do with Display, hopefully
	 */
	public void kill() {
		refresh = false;
		frame.setVisible(false);
		frame.setEnabled(false);
	}
	
	/*
	 * toggles maximization
	 */
	public void toggleMaximize() {
		if (isMaximized()) {
			minimize();
		} else {
			maximize();
		}
	}
	
	/*
	 * Maximizes frame
	 */
	public void maximize() {
		if (!isMaximized()) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}
	
	/*
	 * minimizes frame
	 */
	public void minimize() {
		if (isMaximized()) {
			frame.setExtendedState(JFrame.NORMAL);
			frame.setBounds(0, 0, width + 16, height + 39);
		}
	}
	
	/*
	 * checks if the frame is maximized
	 */
	private boolean isMaximized() {
		return frame.getExtendedState() != JFrame.MAXIMIZED_BOTH;
	}
	
	/*
	 * adds a panel to the top of the overlay
	 */
	public void addOverlay(JPanel panel) {
		overlayPanels.push(panel);
		overlay.add(panel, overlayPanels.size());
		overlay.moveToFront(panel);
	}
	
	/*
	 * removes a panel from the overlay.
	 */
	public void removeTopOverlay() {
		if (overlayPanels.size() > 1) {
			JPanel trash = overlayPanels.pop();
			overlay.remove(trash);
		} else {
			System.out.println("No More overlays left in display.");
		}
	}
	
	/*
	 * replaces current main panel with new panel.
	 * This process is unstable, since it relies on restack()
	 * to make the new panel become the bottom panel.
	 * This should not effect normal usage however.
	 */
	public void replaceMainPanel(JPanel panel) {
		int index = overlay.getIndexOf(mainPanel);
		overlay.remove(index);
		overlay.add(panel);
		mainPanel = panel;
		restackPanels();
	}
	
	/*
	 * Switches to main panel from menu mode.
	 */
	public void switchToMainPanel() {
		if (inMenuMode) {
			inMenuMode = false;
			overlay.setVisible(true);
		}
	}
	/*
	 * switches to menu from main panel
	 */
	public void switchToMenu(JPanel initialMenu) {
		menu = initialMenu;
		if (!inMenuMode) {
			inMenuMode = true;
			overlay.setVisible(false);
		}
		frame.getContentPane().add(menu);
	}
	
	/*
	 * Switches to a different menu panel.
	 */
	public void changeMenu(JPanel nextMenu) {
		if (inMenuMode) {
			frame.getContentPane().remove(menu);
			frame.getContentPane().add(nextMenu);
		}
		menu = nextMenu;
	}
	
	/*
	 * Restacks the panels so that main panel is on the bottom, keeps overlay order.
	 */
	private void restackPanels() {
		for (JPanel p : overlayPanels) {
			overlay.remove(p);
		}
		overlay.remove(mainPanel);
		
		for (JPanel p : overlayPanels) {
			overlay.add(p);
		}
		overlay.add(mainPanel);
	}
	
	/*
	 * Allows the adding of actions to the frame.
	 */
	private InputMap inMap;
	private ActionMap actMap;
	public void addAction(Action action, int key, boolean onRelease) {
		String tag = "tag: " + key + onRelease;
		inMap.put(KeyStroke.getKeyStroke(key, 0, onRelease), tag);
		actMap.put(tag, action);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return width + "wide, " + height + "high : " + "in menu mode: " + inMenuMode + " : Frame info: " + frame.toString();
	}
}
