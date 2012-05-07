/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package danceDoll;

/**
 *
 * @author master
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;


/**
 * Testing an UncaughtExceptionHandler implementation with StandardGame.
 *
 * No liscense attached, free to use or modify in absolutely any form with or without
 * credit given.
 *
 * @author Andrew Carter

 * **/

 public class ExceptionHandler implements UncaughtExceptionHandler {

      private DanceDoll app = null;

      public ExceptionHandler(DanceDoll app) {
         this.app = app;
      }

      public void uncaughtException(Thread t, final Throwable e) {

         try {
            SwingUtilities.invokeLater(new Runnable() {

               public void run() {

                  ExceptionDialog dialog = new ExceptionDialog("<Your game>", e);
                  center(dialog);
                  dialog.setVisible(true);

                  // Dispose of the dialog to prevent the swing thread
                  // from keeping the application alive.
                  dialog.dispose();
               }
            });
         }
         catch (Exception ex) {
            ex.printStackTrace();
         }

         // After getting swing started, be sure to shutdown standard game
         //app.shutdown();
         app.destroy();
      }


   /**
    * This dialog will present the details of a throwable exception to a user.
    */
   public static class ExceptionDialog extends JDialog {

      private static final long serialVersionUID = 1L;

      public ExceptionDialog(String appName, final Throwable e) {

         setTitle(appName + " Error");
         setModal(true);
         setResizable(false);
         setPreferredSize(new Dimension(500, 150));

         final JPanel contentPane = new JPanel();
         contentPane.setLayout(new BorderLayout());
         contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

         setContentPane(contentPane);

         JPanel messagePanel = new JPanel();
         messagePanel.setLayout(new BorderLayout());
         contentPane.add(messagePanel, BorderLayout.PAGE_START);

         messagePanel.add(new JLabel(appName + " has been terminated due to an unrecoverable error."), BorderLayout.PAGE_START);

         JLabel errorType = new JLabel(e.getClass().getSimpleName());
         errorType.setFont(errorType.getFont().deriveFont(Font.PLAIN));
         messagePanel.add(errorType, BorderLayout.CENTER);

         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

         JButton close = new JButton("Close");
         close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

               setVisible(false);
            }
         });

         buttonPanel.add(close);

         final JButton details = new JButton("Details");
         details.addActionListener(new ActionListener() {

            /**
             * When the user clicks the details button, add the stack trace
             * to the dialog and disable the details button.
             */
            public void actionPerformed(ActionEvent evt) {

               JPanel detailsPanel = new JPanel();
               detailsPanel.setLayout(new BorderLayout());
               detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

               contentPane.add(detailsPanel, BorderLayout.CENTER);

               JLabel detailsLabel = new JLabel("Error Details");
               detailsPanel.add(detailsLabel, BorderLayout.PAGE_START);

               /*
                * This special construction of a JTextPane prevents line
                * wrapping, allowing the scrollpane to do its job
                * horizontally.
                */
               final JTextPane detailsText = new JTextPane() {

                  private static final long serialVersionUID = 1L;

                  public boolean getScrollableTracksViewportWidth() {
                     return (getSize().width < getParent().getSize().width);
                  }

                  public void setSize(Dimension d) {
                     if(d.width < getParent().getSize().width) {
                        d.width = getParent().getSize().width;
                     }
                     super.setSize(d);
                  }
               };

               detailsText.setEditable(false);
               /* Uncomment this line if you don't like the white background of the text pane */
               //detailsText.setBackground(contentPane.getBackground());

               // Print the stack trace to a string...
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw, true);
               e.printStackTrace(pw);
               pw.flush();
               sw.flush();
               detailsText.setText(sw.toString());

               JScrollPane scrollPane = new JScrollPane(detailsText);

               detailsPanel.add(scrollPane, BorderLayout.CENTER);

               details.setEnabled(false);

               // expand the dialog size to make room for the details
               setPreferredSize(new Dimension(500, 300));

               pack();
            }
         });

         buttonPanel.add(details);
         contentPane.add(buttonPanel, BorderLayout.PAGE_END);

         pack();
      }
   }

   /**
    * Utility method for centering swing components onscreen.
    */
   public static void center(Component component) {

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = component.getSize();

      if(frameSize.height > screenSize.height) {
         frameSize.height = screenSize.height;
      }
      if(frameSize.width > screenSize.width) {
         frameSize.width = screenSize.width;
      }

      component.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
   }
}