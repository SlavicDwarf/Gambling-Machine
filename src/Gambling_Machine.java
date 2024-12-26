/**
 *
 *  @author Grochowski Franciszek S31116
 *
 */

//package zad1;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Gambling_Machine extends JPanel {
    private double blockY1 = (int) ((Math.random() * -(400 +200)) -400);
    private double blockY2 = (int) ((Math.random() * -(400 +200)) -400);
    private double blockY3 = (int) ((Math.random() * -(400 +200)) -400);
    private double TARGET_BLOCK_Y = 540;
    private double velocity = -20;
    private final double DAMPING = 0.7;
    private final double SPRING = 0.15;
    private boolean CzyAnimacja = true;
    private Timer timer;
    private BufferedImage tlo;
    private BufferedImage[] obrazki;
    private BufferedImage blok1, blok2, blok3;

    private ExecutorService executorService = Executors.newFixedThreadPool(3); //to od wątków
    private List<Future<String>> listaZadan = new ArrayList<>();

    private DefaultListModel<String> TaskModel = new DefaultListModel<>();
    private JList<String> TaskLista;
    private JButton Start, Anuluj, Wynik;
    private JScrollPane lista;

    public Gambling_Machine() {
        setLayout(null);
        ustObrazkow();
        UI();
    }

    private void ustObrazkow() {
        try {
            tlo = ImageIO.read(new File("src/img/GamblerBG.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        obrazki = new BufferedImage[11];
        for (int i = 0; i <= 10; i++) {
            try {
                obrazki[i] = ImageIO.read(new File("src/img/" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//======================================================================================================================
    private void UI() {

        TaskLista = new JList<>(TaskModel);
        lista = new JScrollPane(TaskLista);
        lista.setBounds(700, 0, 200, getHeight());
        add(lista);


        Start = new JButton("Losuj");
        Start.setBounds(50, 50, 150, 30);
        add(Start);
        Start.addActionListener(e -> Losowanie());

        Anuluj = new JButton("Anuluj");
        Anuluj.setBounds(50, 100, 150, 30);
        add(Anuluj);
        Anuluj.addActionListener(e -> Anuluowanie());

        Wynik = new JButton("Pokaż wynik");
        Wynik.setBounds(50, 150, 150, 30);
        add(Wynik);
        Wynik.addActionListener(e -> Wyniki());


        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                lista.setBounds(getWidth() - 200, 0, 200, getHeight());
            }
        });
    }
//======================================================================================================================
    private void Losowanie() {
        ResetAnim();
        Callable<String> task = () -> {
            int value1 = (int) (Math.random() * 11);
            int value2 = (int) (Math.random() * 11);
            int value3 = (int) (Math.random() * 11);
            blok1 = obrazki[value1];
            blok2 = obrazki[value2];
            blok3 = obrazki[value3];
            AnimBlok();
            double average = (value1 + value2 + value3) / 3.0;
            return "Liczby: " + value1 + ", " + value2 + ", " + value3 + " | Średnia: " + average;
        };

        Future<String> futureTask = executorService.submit(task);
        listaZadan.add(futureTask);
        TaskModel.addElement("Losowanie " + listaZadan.size());
    }

    private void ResetAnim() {
        blockY1 = -360;
        blockY2 = -280;
        blockY3 = -200;
        CzyAnimacja = true;
        velocity = -20;
        repaint();
    }

    private void Anuluowanie() {
        int selectedIndex = TaskLista.getSelectedIndex();
        if (selectedIndex != -1) {
            Future<String> selectedTask = listaZadan.get(selectedIndex);
            if (!selectedTask.isDone() && !selectedTask.isCancelled()) {
                selectedTask.cancel(true);
                JOptionPane.showMessageDialog(this, "Zadanie anulowane.");
            } else {
                JOptionPane.showMessageDialog(this, "Zadanie zostało już zakończone lub anulowane.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz zadanie z listy.");
        }
    }

    private void Wyniki() {
        int selectedIndex = TaskLista.getSelectedIndex();
        if (selectedIndex != -1) {
            Future<String> selectedTask = listaZadan.get(selectedIndex);
            if (selectedTask.isDone() && !selectedTask.isCancelled()) {
                try {
                    String result = selectedTask.get();
                    JOptionPane.showMessageDialog(this, result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Zadanie jeszcze nie zakończone lub zostało anulowane.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz zadanie z listy.");
        }
    }

    private void AnimBlok() {
        timer = new Timer(1000 / 24, e -> {
            AnimBlok1();
            AnimBlok2();
            AnimBlok3();
            repaint();
        });
        timer.start();
    }

    private void AnimBlok1() {
        if (CzyAnimacja) {
            double force = SPRING * (TARGET_BLOCK_Y - blockY1);
            velocity += force;
            velocity *= DAMPING;
            blockY1 += velocity;
            if (Math.abs(velocity) < 0.1 && Math.abs(blockY1 - TARGET_BLOCK_Y) < 0.1) {
                blockY1 = TARGET_BLOCK_Y;
                CzyAnimacja = false;
            }
        }
    }

    private void AnimBlok2() {
        if (CzyAnimacja) {
            double force = SPRING * (TARGET_BLOCK_Y - blockY2);
            velocity += force;
            velocity *= DAMPING;
            blockY2 += velocity;
            if (Math.abs(velocity) < 0.1 && Math.abs(blockY2 - TARGET_BLOCK_Y) < 0.1) {
                blockY2 = TARGET_BLOCK_Y;
                CzyAnimacja = false;
            }
        }
    }

    private void AnimBlok3() {
        if (CzyAnimacja) {
            double force = SPRING * (TARGET_BLOCK_Y - blockY3);
            velocity += force;
            velocity *= DAMPING;
            blockY3 += velocity;
            if (Math.abs(velocity) < 0.1 && Math.abs(blockY3 - TARGET_BLOCK_Y) < 0.1) {
                blockY3 = TARGET_BLOCK_Y;
                CzyAnimacja = false;
            }
        }
    }
//======================================================================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(blok1, 280, (int) blockY1, 200, 200, this);
        g.drawImage(blok2, 580, (int) blockY2, 200, 200, this);
        g.drawImage(blok3, 880, (int) blockY3, 200, 200, this);
        g.drawImage(tlo, 0, 0, getWidth(), getHeight(), this);
    }

    public static void main(String[] args) {
        int ekranX = 900;
        int ekranY = 600;

        JFrame frame = new JFrame("Gambling Punktów Machina");
        Gambling_Machine panel = new Gambling_Machine();
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setSize(ekranX, ekranY);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
