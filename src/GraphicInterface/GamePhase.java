package GraphicInterface;

import Exceptions.EdgeNotFoundException;
import Exceptions.VertexNotFoundException;
import HauntedHouse.HauntedHouseGraph;
import LinkedList.ArrayUnorderedList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePhase extends JLabel {

    private JFrame frame;
    private JLabel mainPanel;
    private ArrayUnorderedList<JButton> portas;
    private boolean sound;
    private HauntedHouseGraph mapGraph;
    private JLabel ghost;
    private JLabel previous;
    private Clip backgroundSound;
    private Clip deadSound;
    private boolean checkGhost;

    public GamePhase(JFrame frame, JLabel mainPanel, boolean sound, HauntedHouseGraph mapGraph, JLabel previous, Clip backgroundSound, boolean checkGhost) {
        this.frame = frame;
        this.mainPanel = mainPanel;
        this.sound = sound;
        this.mapGraph = mapGraph;
        this.previous = previous;
        this.ghost = new JLabel();
        this.portas = new ArrayUnorderedList();
        this.backgroundSound = backgroundSound;
        this.checkGhost = checkGhost;

        this.setLayout(new BorderLayout());

        try {
            roomScreen();
        } catch (VertexNotFoundException e) {
        }
    }

    public void roomScreen() throws VertexNotFoundException {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel top = new JPanel(new GridBagLayout());
        JPanel center = new JPanel();
        JPanel bottom = new JPanel(new GridBagLayout());
        JButton giveUp = new JButton();
        top.setPreferredSize(new Dimension(700, 100));
        //top.setOpaque(false);
        top.setBackground(Color.red);
        center.setPreferredSize(new Dimension(700, 200));
        //center.setOpaque(false);
        center.setBackground(Color.green);
        bottom.setPreferredSize(new Dimension(700, 400));
        //bottom.setOpaque(false);
        bottom.setBackground(Color.blue);

        //////TOP
        //Health Bar
        //GiveUp
        giveUp.setText("GIVE UP");
        giveUp.setPreferredSize(new Dimension(100, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 60, 10);
        top.add(giveUp, gbc);

        //////CENTER
        if (this.checkGhost) {
            this.setGhost();
        }
        center.add(this.ghost, JLabel.CENTER);

        //////BOTTOM
        this.setPortas(bottom);

        this.add(top, BorderLayout.PAGE_START);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.PAGE_END);

        //UPDATE
        this.frame.remove(this.previous);
        this.frame.add(this);
        SwingUtilities.updateComponentTreeUI(this.frame);
        this.frame.setVisible(true);

        giveUp.addActionListener((ActionEvent event) -> {
            giveUpScreen();
        });
    }

    public void giveUpScreen() {
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel giveUpLabel = new JLabel();
        giveUpLabel.setLayout(new GridBagLayout());
        JButton backButton = new JButton();

        backButton.setText("BACK");
        backButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        giveUpLabel.add(backButton, gbc);

        //UPDATE
        this.frame.remove(this);
        this.frame.add(giveUpLabel);
        SwingUtilities.updateComponentTreeUI(this.frame);
        this.frame.setVisible(true);
        backButton.addActionListener((ActionEvent event) -> {
            //UPDATE
            if (this.sound) {
                backgroundSound.stop();
                backgroundSound.flush();
                backgroundSound.close();
            }
            this.frame.remove(giveUpLabel);
            this.frame.add(this.mainPanel);
            SwingUtilities.updateComponentTreeUI(this.frame);
            this.frame.setVisible(true);
        });
    }

    public void winScreen() {
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel winLabel = new JLabel();
        winLabel.setLayout(new GridBagLayout());
        winLabel.setIcon(new ImageIcon("resources/win.gif"));
        JButton backButton = new JButton();

        JLabel text = new JLabel();
        text.setPreferredSize(new Dimension(200,100));
        text.setText("Congratulations " + this.mapGraph.getPlayerName() + "<br>"
                + "you didn't died</html>");
        text.setForeground(Color.white);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(550, 0, 0, 0);
        winLabel.add(text, gbc);

        backButton.setText("BACK");
        backButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 1;
        gbc.insets = new Insets(300, 0, 0, 0);
        winLabel.add(backButton, gbc);

        //UPDATE
        if (this.sound) {
            backgroundSound.stop();
            backgroundSound.flush();
            backgroundSound.close();
        }
        this.frame.remove(this);
        this.frame.add(winLabel);
        SwingUtilities.updateComponentTreeUI(this.frame);
        this.frame.setVisible(true);

        backButton.addActionListener((ActionEvent event) -> {
            //UPDATE
            this.frame.remove(winLabel);
            this.frame.add(this.mainPanel);
            SwingUtilities.updateComponentTreeUI(this.frame);
            this.frame.setVisible(true);
        });
    }

    public void deadScreen() {
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel deadLabel = new JLabel();
        deadLabel.setLayout(new GridBagLayout());
        JLabel wastedImg = new JLabel();
        JButton backButton = new JButton();

        wastedImg.setIcon(new ImageIcon("resources/wasted.png"));
        wastedImg.setPreferredSize(new Dimension(400, 400));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 100, 0);
        deadLabel.add(wastedImg, gbc);

        backButton.setText("BACK");
        backButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 50, 0);
        deadLabel.add(backButton, gbc);

        if (this.sound) {
            this.backgroundSound.stop();
            this.backgroundSound.flush();
            this.backgroundSound.close();
            try {
                this.deadSound = AudioSystem.getClip();
                this.deadSound.open(this.deadSound());
                this.deadSound.start();
            } catch (LineUnavailableException ex) {
            } catch (IOException ex) {
            }
        }
        //UPDATE
        this.frame.remove(this);
        this.frame.add(deadLabel);
        SwingUtilities.updateComponentTreeUI(this.frame);
        this.frame.setVisible(true);

        backButton.addActionListener((ActionEvent event) -> {
            //UPDATE
            if (this.sound) {
                this.deadSound.stop();
                this.deadSound.flush();
                this.deadSound.close();
            }
            this.frame.remove(deadLabel);
            this.frame.add(this.mainPanel);
            SwingUtilities.updateComponentTreeUI(this.frame);
            this.frame.setVisible(true);
        });
    }

    public AudioInputStream deadSound() {
        AudioInputStream audioinputstream = null;
        try {
            File soundFile = new File("resources/wasted.wav");
            audioinputstream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException | IOException e) {
        }
        return audioinputstream;
    }

    public ArrayUnorderedList<JButton> getPortas() {
        return portas;
    }

    public void setPortas(JPanel label) throws VertexNotFoundException {
        GridBagConstraints gbc = new GridBagConstraints();
        Iterator iterator = this.mapGraph.getAvailableDoors(this.mapGraph.getCurrentPosition()).iterator();
        JButton porta;
        int i = 0;
        while (iterator.hasNext()) {
            String stringPorta = (String) iterator.next();
            System.out.println(stringPorta);

            porta = new JButton();
            porta.setText(stringPorta);
            this.portas.addToRear(porta);

            porta.setPreferredSize(new Dimension(100, 200));
            gbc.gridx = i;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 5, 0, 5);
            label.add(porta, gbc);
            i++;

            porta.addActionListener((ActionEvent event) -> {
                try {
                    GamePhase game;
                    boolean checkG = false;
                    if (this.mapGraph.changePosition(stringPorta)) {
                        checkG = true;
                    }
                    if (this.mapGraph.isComplete()) {
                        this.winScreen();
                    } else if (!this.mapGraph.isAlive()) {
                        this.deadScreen();
                    } else {
                        System.out.println("atual: " + this.mapGraph.getCurrentPosition());
                        game = new GamePhase(this.frame, this.mainPanel, this.sound, this.mapGraph, this, this.backgroundSound, checkG);
                    }
                } catch (VertexNotFoundException | EdgeNotFoundException ex) {
                }
            });
        }
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public HauntedHouseGraph getMapGraph() {
        return mapGraph;
    }

    public void setMapGraph(HauntedHouseGraph mapGraph) {
        this.mapGraph = mapGraph;
    }

    public JLabel getGhost() {
        return ghost;
    }

    public void setGhost() {
        if (this.mapGraph.getLevel() == 1) {
            this.ghost.setIcon(new ImageIcon("resources/gengar.gif"));
        }
        if (this.mapGraph.getLevel() == 2) {
            this.ghost.setIcon(new ImageIcon("resources/darkrai.gif"));
        }
        if (this.mapGraph.getLevel() == 3) {
            this.ghost.setIcon(new ImageIcon("resources/giratina.gif"));
        }
    }
}
