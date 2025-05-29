import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    int rows = 16;
    int columns = 16;
    int tileSize = 32;
    int boardHeight = tileSize * rows;
    int boardWidth = tileSize * columns;

    Image shipImg;
    Image alien;
    Image alienCyan;
    Image alienYellow;
    Image alienMagenta;
    ArrayList<Image> alienImgArray;

    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocityX = tileSize;
    int shipHeight = tileSize;
    int shipWidth = tileSize * 2;

    ArrayList<Block> alienArray;
    int alienHeight = tileSize;
    int alienWidth = tileSize * 2;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray = new ArrayList<>();
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    int score = 0;

    Block ship;
    Timer timer;

    public void draw(Graphics g) {
        g.drawImage(shipImg, ship.x, ship.y, ship.width, ship.height, null);

        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.white);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }


        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public void move() {
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                for (Block alien : alienArray) {
                    if (alien.alive && detectCollision(bullet, alien)) {
                        bullet.used = true;
                        alien.alive = false;
                        alienCount--;
                        score += 10;
                        break;
                    }
                }
            }
        }
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                bullet.y += bulletVelocityY;
                if (bullet.y + bullet.height < 0) {
                    bullet.used = true;
                }
            }
        }

        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocityX + ship.width <= boardWidth) {
            ship.x += shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(
                    ship.x + ship.width / 2 - bulletWidth / 2,
                    ship.y,
                    bulletWidth,
                    bulletHeight,
                    null
            );
            bulletArray.add(bullet);
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    class Block {
        int x, y, width, height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    SpaceInvaders() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        this.setBackground(Color.BLACK);


        shipImg = new ImageIcon(getClass().getResource("/ship.png")).getImage();
        alien = new ImageIcon(getClass().getResource("/alien.png")).getImage();
        alienMagenta = new ImageIcon(getClass().getResource("/alien-magenta.png")).getImage();
        alienCyan = new ImageIcon(getClass().getResource("/alien-cyan.png")).getImage();
        alienYellow = new ImageIcon(getClass().getResource("/alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alien);
        alienImgArray.add(alienMagenta);
        alienImgArray.add(alienYellow);
        alienImgArray.add(alienCyan);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);

        alienArray = new ArrayList<>();
        createAliens();

        timer = new Timer(1000 / 60, this);
        timer.start();
    }
}
