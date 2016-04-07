package cz.muni.fi.pv112.project;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Adam Jurcik <xjurc@fi.muni.cz>
 */
public class MainWindow extends javax.swing.JFrame {

    private GLJPanel panel;
    private Camera camera;
    private Scene scene;
    private FPSAnimator animator;
    private boolean fullscreen = false;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();

        setTitle("PV112 Project");

        GLProfile profile = GLProfile.get(GLProfile.GL3);
        panel = new GLJPanel(new GLCapabilities(profile));
        panel.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);

        add(panel, BorderLayout.CENTER);

        animator = new FPSAnimator(panel, 60, true);
        camera = new Camera();
        scene = new Scene(animator, camera);

        panel.addGLEventListener(scene);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                MainWindow.this.keyPressed(e);
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                camera.updateMousePosition(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO
                if (e.getButton() == MouseEvent.BUTTON1) {
                    camera.updateMouseButton(Camera.Button.LEFT, false, e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    camera.updateMouseButton(Camera.Button.RIGHT, false, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO
                if (e.getButton() == MouseEvent.BUTTON1) {
                    camera.updateMouseButton(Camera.Button.LEFT, true, e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    camera.updateMouseButton(Camera.Button.RIGHT, true, e.getX(), e.getY());
                }
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                camera.updateMousePosition(e.getX(), e.getY());
            }
        });
        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                camera.updateMouseButton(Camera.Button.MIDDLE, true, e.getX(), e.getY());
                camera.updateMousePosition(camera.getLastX(), camera.getLastY() + 20*e.getWheelRotation());
            }
        });

        animator.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(480, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;

            case KeyEvent.VK_A:
                toggleAnimation();
                break;

            case KeyEvent.VK_T:
                toggleFullScreen();
                break;

            case KeyEvent.VK_F:
                scene.toggleFill();
                break;

            case KeyEvent.VK_L:
                scene.toggleLines();
                break;
        }

        panel.display();
    }

    private void toggleAnimation() {
        if (animator.isAnimating()) {
            animator.stop();
        } else {
            animator.start();
        }
    }

    private void toggleFullScreen() {
        fullscreen = !fullscreen;

        if (animator.isAnimating()) {
            animator.stop();
        }

        dispose();
        setUndecorated(fullscreen);
        pack();

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();

        if (fullscreen) {
            device.setFullScreenWindow(this);
        } else {
            device.setFullScreenWindow(null);
        }
        setVisible(true);
        animator.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}