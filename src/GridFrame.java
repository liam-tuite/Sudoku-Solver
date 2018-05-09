package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class GridFrame extends JFrame {

	private JPanel contentPane;
	private JPanel cellPanel;
	
	private Cell[] cells;
	
	private SudokuSolver parent;
	
	private final Integer[] GREY_CELL_INDICES = {3, 4, 5, 12, 13, 14, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43,
			44, 45, 46, 47, 51, 52, 53, 57, 58, 59, 66, 67, 68, 75, 76, 77};
	
	/**
	 * Create the frame.
	 */
	public GridFrame(SudokuSolver parent){
		setResizable(false);
	
		this.parent = parent;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		cellPanel = new JPanel();
		contentPane.add(cellPanel);
		cellPanel.setLayout(new GridLayout(9, 9, 0, 0));
		
		JButton solve_button = new JButton("Solve");
		solve_button.setAlignmentX(Component.CENTER_ALIGNMENT);
		solve_button.addActionListener(new ButtonClickListener());
		contentPane.add(solve_button);
		
		cells = new Cell[81];
		for(int i = 0; i < cells.length; i++){
			
			cells[i] = new Cell();
			cellPanel.add(cells[i]);
			cells[i].setHorizontalAlignment(SwingConstants.CENTER);
			cells[i].setFont(new Font("Arial", Font.BOLD, 20));
			cells[i].setColumns(5);
			
			if(Arrays.asList(GREY_CELL_INDICES).contains(i))
				cells[i].setBackground(Color.LIGHT_GRAY);
		}
	}
	
	private class ButtonClickListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e){
			
			for(Cell cell : cells){
				cell.initValue();
			}
			parent.setCells(cells);
			parent.solve();
			repaint();
		}
	}
}