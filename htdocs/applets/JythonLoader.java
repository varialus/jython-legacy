import java.applet.*;
import java.awt.*;
import java.util.*;

public class JythonLoader extends Applet implements Runnable {
    public Label etime, l1, l1a;
    Thread timer;
    long initialTime;
    String waitFor = "HelloWorld";

    public void init() {
        setFont(new Font("Normal", Font.BOLD, 15));
        l1 = new Label("Jython Library is loading...");
        l1.setAlignment(Label.CENTER);
        l1a = new Label("Please wait");
        l1a.setAlignment(Label.CENTER);
        l1a.setForeground(Color.red);
        Label l2 = new Label("Elapsed time: ");
        l2.setAlignment(Label.RIGHT);
        etime = new Label("0:00.0");
        etime.setFont(new Font("Courier", Font.BOLD, 15));

        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        bag.setConstraints(l1, c);
        add(l1);
        bag.setConstraints(l1a, c);
        add(l1a);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weightx = 1.0;
        bag.setConstraints(l2, c);
        add(l2);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        bag.setConstraints(etime, c);
        add(etime);

        String n = getParameter("waitFor"); 
        if (n != null)
            waitFor = n;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        g.drawRect(1,1,d.width-3, d.height-3);
        g.drawRect(3,3,d.width-7, d.height-7);
    }

    public Insets getInsets() {
        return new Insets(7,7,7,7);
    }

    private int activeApplets() {
        try {
            Applet a = getAppletContext().getApplet(waitFor);
            if (a != null)
                return 2;
        } catch (Exception ex) {
            return 1;
        }
        return 1;
    }

    private void updateText() {
        long t0 = System.currentTimeMillis()-initialTime;
        String minutes = Integer.toString((int)(t0/1000)%60);
        if (minutes.length() < 2)
	    minutes = "0"+minutes;

        String ts = ""+(t0/60000)+":"+minutes+"."+((t0/100)%10);
        etime.setText(ts); //+", "+activeApplets());
    }

    private void done() {
        l1.setText("Jython Loading Complete");
        l1a.setText("");
    }

    public void run() {
        //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        //System.out.println("Running");
        while (true) {
            //System.out.println("Running");
            int n = activeApplets();
            if (n > 1)
		break;
            updateText();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        done();
    }

    public void start() {
        initialTime = System.currentTimeMillis();
        timer = new Thread(this);
        timer.start();
    }
}

