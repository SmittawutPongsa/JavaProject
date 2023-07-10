package Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class JLabelInJListExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JLabel in JList Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a list of labels
        String[] labels = {"Label 1", "Label 2", "Label 3"};

        // Create a JList
        JList<String> jList = new JList<>(labels);

        // Set a custom cell renderer
        jList.setCellRenderer(new LabelListCellRenderer());

        // Add the JList to the frame
        frame.getContentPane().add(new JScrollPane(jList));

        frame.setSize(300, 200);
        frame.setVisible(true);
    }

    // Custom cell renderer
    private static class LabelListCellRenderer extends JLabel implements ListCellRenderer<String> {
        public LabelListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setForeground(Color.BLACK);
            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);

            return this;
        }
    }
}
