package view;

import controller.ControlView;
import org.slf4j.*;
import javax.swing.*;
import java.awt.*;


public class AnswerButtonsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = LoggerFactory.getLogger(AnswerButtonsPanel.class);
	private JLabel queryLabel = new JLabel();
	
	public AnswerButtonsPanel(ControlView clientView){		
		JButton butYes = new JButton("Yes");
		JButton butNo = new JButton("No");
		butYes.setPreferredSize(new Dimension(100, 20));
		butNo.setPreferredSize(new Dimension(100, 20));
		butYes.addActionListener(clientView.new AnswerButtonsActionListener());
		butNo.addActionListener(clientView.new AnswerButtonsActionListener());
		
		setLayout(new FlowLayout());
		add(getQueryLabel());
		add(butNo);
		add(butYes);
	}
	public JLabel getQueryLabel() {
		return queryLabel;
	}
	public void setQueryLabel(JLabel queryLabel) {
		this.queryLabel = queryLabel;
	}
}
