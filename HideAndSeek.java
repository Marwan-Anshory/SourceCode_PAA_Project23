/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hideandseek;

/**
 *
 * @author ASUS
 */
import java.awt.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.*;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class HideAndSeek extends JFrame {
    private int[][] map;
    private final int numRows = 15;
    private final int numCols = 15;
    private final int cellSize = 40;
    private final JPanel mapPanel;
    private final JButton generateButton;
    private final JButton redButton;
    private final JButton greenButton;
    private final JButton shuffleRedButton;
    private final JButton shuffleGreenButton;
    private final JButton startButton;
    private final JButton pauseButton;
    private final JButton pandanganRedButton;
    private final JButton offPandanganRedButton;
    private final JButton pandanganGreenButton;
    private final JButton offPandanganGreenButton;
    private final JSlider pandanganGreenSlider;
    private boolean droidMerahBersentuhan;
    private int jarakPandangDroidHijau;
    private boolean pandanganDroidMerah;
    private boolean pandanganDroidHijau;
    private Timer timer;
    private final ArrayList<Point> redDroids = new ArrayList<>(); // variabel untuk menyimpan droid merah
    private Point greenDroid; // variabel untuk menyimpan droid hijau
    private final Random rand;
    private ScheduledExecutorService executor;

    public HideAndSeek() {
        setTitle("PAA Project-2101020067_MarwanAnshory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // membuat panel untuk peta
        mapPanel = new JPanel();
        mapPanel.setPreferredSize(new Dimension(numCols * cellSize, numRows * cellSize));
        mapPanel.setLayout(new GridLayout(numRows, numCols));
        getContentPane().add(mapPanel, BorderLayout.CENTER);

        //membuat panel untuk tombol
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(12, 1));
        getContentPane().add(buttonPanel, BorderLayout.WEST);

        // membuat tombol untuk peta acak
        generateButton = new JButton("GENERATE / ACAK MAP");
        generateButton.addActionListener(e -> generateMap());
        buttonPanel.add(generateButton);
        generateButton.setBackground(Color.WHITE);
        generateButton.setForeground(Color.BLACK);

        // membuat tombol untuk menambahkan droid merah
        redButton = new JButton("TAMBAH DROID MERAH");
        redButton.addActionListener(e -> tambahDroidMerah());
        buttonPanel.add(redButton);
        redButton.setBackground(Color.WHITE);
        redButton.setForeground(Color.RED);
        
        shuffleRedButton = new JButton("ACAK DROID MERAH");
        shuffleRedButton.addActionListener(e -> acakDroidMerah());
        buttonPanel.add(shuffleRedButton);
        shuffleRedButton.setBackground(Color.WHITE);
        shuffleRedButton.setForeground(Color.RED);
        
        // membuat tombol untuk menambahkan droid hijau
        greenButton = new JButton("LETAK DROID HIJAU");
        greenButton.addActionListener(e -> letakDroidHijau());
        buttonPanel.add(greenButton);
        greenButton.setBackground(Color.WHITE);
        greenButton.setForeground(new Color(0, 128, 0));
        
        shuffleGreenButton = new JButton("ACAK DROID HIJAU");
        shuffleGreenButton.addActionListener(e -> acakDroidHijau());
        buttonPanel.add(shuffleGreenButton);
        shuffleGreenButton.setBackground(Color.WHITE);
        shuffleGreenButton.setForeground(new Color(0, 128, 0));
        
        pandanganRedButton = new JButton("PANDANGAN DROID MERAH");
        pandanganRedButton.addActionListener(e -> pandanganDroidMerah());
        buttonPanel.add(pandanganRedButton);
        pandanganRedButton.setBackground(Color.WHITE);
        pandanganRedButton.setForeground(Color.RED);
        
        offPandanganRedButton = new JButton("OFF PANDANGAN DROID MERAH");
        offPandanganRedButton.addActionListener(e -> offPandanganDroidMerah());
        buttonPanel.add(offPandanganRedButton);
        offPandanganRedButton.setBackground(Color.WHITE);
        offPandanganRedButton.setForeground(Color.RED);
        
        pandanganGreenButton = new JButton("PANDANGAN DROID HIJAU");
        pandanganGreenButton.addActionListener(e -> pandanganDroidHijau());
        pandanganGreenSlider = new JSlider( 0, 15, 1);
        pandanganGreenSlider.setMajorTickSpacing(1);
        pandanganGreenSlider.setMinorTickSpacing(1);
        pandanganGreenSlider.setPaintTicks(true);
        pandanganGreenSlider.setPaintLabels(true);
        pandanganGreenSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            int newValue = source.getValue();
            jarakPandangDroidHijau = newValue;
        });
        jarakPandangDroidHijau = 15;
        buttonPanel.add(pandanganGreenSlider);
        buttonPanel.add(pandanganGreenButton);
        pandanganGreenSlider.setBackground(Color.WHITE);
        pandanganGreenButton.setBackground(Color.WHITE);
        pandanganGreenButton.setForeground(new Color(0, 128, 0));
        
        offPandanganGreenButton = new JButton("OFF PANDANGAN DROID HIJAU");
        offPandanganGreenButton.addActionListener(e -> offPandanganDroidHijau());
        buttonPanel.add(offPandanganGreenButton);
        offPandanganGreenButton.setBackground(Color.WHITE);
        offPandanganGreenButton.setForeground(new Color(0, 128, 0));
        
        startButton = new JButton("MULAI");
        startButton.addActionListener(e -> mulaiPergerakan());
        buttonPanel.add(startButton);
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);
        
        pauseButton = new JButton("PAUSE");
        pauseButton.addActionListener(e -> hentikanPergerakan());
        buttonPanel.add(pauseButton);
        pauseButton.setBackground(Color.WHITE);
        pauseButton.setForeground(Color.BLACK);

        // border untuk map
        mapPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Menambahkan margin
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black, 10));
        
        //border untuk button
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Menambahkan margin
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.black, 10));
        
        //mengatur font button
        Font font = new Font("Poppins", Font.PLAIN, 14);
        generateButton.setFont(font);
        redButton.setFont(font);
        greenButton.setFont(font);
        shuffleRedButton.setFont(font);
        shuffleGreenButton.setFont(font);
        pandanganRedButton.setFont(font);
        pandanganGreenButton.setFont(font);
        offPandanganRedButton.setFont(font);
        offPandanganGreenButton.setFont(font);
        startButton.setFont(font);
        pauseButton.setFont(font);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        rand = new Random();
    }

    private void generateMap() {
        // Menghapus droid merah dan hijau yang telah ada
        for (Point droid : redDroids) {
            JPanel cell = (JPanel) mapPanel.getComponent(droid.x * numCols + droid.y);
            cell.removeAll();
        }
        redDroids.clear();
        if (greenDroid != null) {
            JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
            cell.removeAll();
            greenDroid = null;
        }
        map = new int[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            Arrays.fill(map[i], 1);
        }
        int startX = 0;
        int startY = 0;

        recursiveBacktracking(startX, startY);
        // Menggambar peta
        mapPanel.removeAll();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                JPanel cell = new JPanel();
                cell.setOpaque(true);
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Menambahkan border
                // Menambahkan garis pada setiap sel jalan
                if (map[i][j] == 0) {
                    cell.setBackground(Color.WHITE);
                } else {
                    cell.setBackground(Color.GRAY);
                }
                mapPanel.add(cell);
            }
        }
        mapPanel.revalidate();
        mapPanel.repaint();
    }

    // algortima pembuatan jalan
    private void recursiveBacktracking(int x, int y) {
        map[x][y] = 0; // Tandai sel ini sebagai jalan

        // Membuat daftar arah yang mungkin, dengan tetangga berjarak 2 cell
        ArrayList<Point> directions = new ArrayList<>();
        directions.add(new Point(x - 2, y)); // kiri
        directions.add(new Point(x + 2, y)); // kanan
        directions.add(new Point(x, y - 2)); // bawah
        directions.add(new Point(x, y + 2)); // atas

        // Mengacak urutan arah
        long seed = System.nanoTime();
        Collections.shuffle(directions, new Random(seed));

        for (Point direction : directions) {
            int nextX = direction.x;
            int nextY = direction.y;

            // Memeriksa apakah tetangga yang mungkin berada di dalam batas peta
            if (nextX >= 0 && nextX < numRows && nextY >= 0 && nextY < numCols && map[nextX][nextY] == 1) {
                // Membuat jalan menuju tetangga
                int wallX = (x + nextX) / 2;
                int wallY = (y + nextY) / 2;
                map[wallX][wallY] = 0;

                if (Math.random() > 0.40) {
                    recursiveBacktracking(nextX, nextY);
                } else {
                    ArrayList<Point> otherDirections = new ArrayList<>(directions);
                    otherDirections.remove(direction);
                    Collections.shuffle(otherDirections, new Random(seed));

                    boolean pathFound = false;
                    for (Point otherDirection : otherDirections) {
                        int otherX = x + (otherDirection.x - x) / 2;
                        int otherY = y + (otherDirection.y - y) / 2;
                        if (otherX >= 0 && otherX < numRows && otherY >= 0 && otherY < numCols && map[otherX][otherY] == 1) {
                            int otherMidX = x + (otherDirection.x - x) / 2;
                            int otherMidY = y + (otherDirection.y - y) / 2;
                            map[otherMidX][otherMidY] = 0;

                            recursiveBacktracking(nextX, nextY);
                            pathFound = true;
                            break;
                        }
                    }
                    if (!pathFound) {
                        map[nextX][nextY] = 1;
                    }
                }
            }
        }
    }

    private void tambahDroidMerah() {
        // mencari sebuah sel yang merupakan jalan
        int x, y;
            do {
                x = rand.nextInt(numRows);
                y = rand.nextInt(numCols);
            } while (map[x][y] != 0);

        // menempatkan droid pada sel tersebut
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.setLayout(new BorderLayout());

        // Membuat gambar droid merah
        JLabel droid = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.RED);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        // Mengatur ukuran droid merah
        droid.setPreferredSize(new Dimension(cellSize, cellSize));
        cell.add(droid, BorderLayout.CENTER);
        redDroids.add(new Point(x, y)); // menambahkan posisi droid merah ke variabel
        mapPanel.revalidate();
        mapPanel.repaint();
    }

    private void letakDroidHijau() {
        if (greenDroid != null) {
            return; // sudah ada droid hijau pada peta
        }
        // mencari sebuah sel yang merupakan jalan
        int x, y;
        do {
            x = rand.nextInt(numRows);
            y = rand.nextInt(numCols);
        } while (map[x][y] != 0);

        // menempatkan droid hijau pada sel tersebut
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.setLayout(new BorderLayout());

        // Membuat gambar droid hijau
        JLabel droid = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.GREEN);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        // Mengatur ukuran droid hijau
        droid.setPreferredSize(new Dimension(cellSize, cellSize));
        cell.add(droid, BorderLayout.CENTER);
        greenDroid = new Point(x, y); // menyimpan posisi droid hijau ke variabel
        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    private void acakDroidMerah() {
        // Menghapus semua droid merah dari panel peta
        for (Point droid : redDroids) {
            JPanel cell = (JPanel) mapPanel.getComponent(droid.x * numCols + droid.y);
            cell.removeAll();
        }

        // Menempatkan droid merah pada posisi yang sudah diacak
        for (Point droid : redDroids) {
            int x, y;
            do {
                x = rand.nextInt(numRows);
                y = rand.nextInt(numCols);
            } while (map[x][y] != 0);

            JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
            cell.setLayout(new BorderLayout());
            JLabel redDroid = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(Color.RED);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };

            redDroid.setPreferredSize(new Dimension(cellSize, cellSize));
            cell.add(redDroid, BorderLayout.CENTER);
            redDroids.set(redDroids.indexOf(droid), new Point(x, y));
        }

        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    private void acakDroidHijau() {
        // Menghapus droid hijau yang telah ada
        if (greenDroid != null) {
            JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
            cell.removeAll();
            greenDroid = null;
        }
        
        // Mengacak posisi droid hijau
        int x, y;
        do {
            x = rand.nextInt(numRows);
            y = rand.nextInt(numCols);
        } while (map[x][y] != 0);

        // Menempatkan droid hijau pada posisi yang sudah diacak
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.setLayout(new BorderLayout());

        JLabel greenDroid = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.GREEN);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        greenDroid.setPreferredSize(new Dimension(cellSize, cellSize));
        cell.add(greenDroid, BorderLayout.CENTER);
        this.greenDroid = new Point(x, y); // menyimpan posisi droid hijau ke variabel
        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    public void mulaiPergerakan() {
        executor = Executors.newScheduledThreadPool(redDroids.size());
        for (int i = 0; i < redDroids.size(); i++) {
            Point redDroidPos = redDroids.get(i);
            executor.scheduleAtFixedRate(() -> gerakDroidMerah(redDroidPos), 0, 250, TimeUnit.MILLISECONDS);
        }
        executor.scheduleAtFixedRate(() -> gerakDroidHijau(), 0, 250, TimeUnit.MILLISECONDS);
    }

    private void hentikanPergerakan() {
        executor.shutdownNow();
        // Optionally, you can await termination of the executor
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            droidMerahBersentuhan = false;
        }
    }
    
    private List<Point> getValidNeighbors(Point currentPos, int pandangan) {
        List<Point> tetangga = new ArrayList<>();
        int x = currentPos.x;
        int y = currentPos.y;

        // Mengecek ke empat arah tetangga (atas, kanan, bawah, kiri)
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            // Memeriksa batasan peta dan apakah sel tetangga merupakan jalan
            if (nx >= 0 && nx < numRows && ny >= 0 && ny < numCols) {
                // Memeriksa apakah tetangga berada dalam jarak pandang Droid Hijau
                if (Math.abs(nx - currentPos.x) <= pandangan && Math.abs(ny - currentPos.y) <= pandangan) {
                    if (map[nx][ny] == 0) {
                        tetangga.add(new Point(nx, ny));
                    }
                }
            }
        }
        return tetangga;
    }
    
    private void gerakDroidMerah(Point redDroidPos) {
        // Mengambil posisi droid merah saat ini
        Point currentPos = new Point(redDroidPos);

        // Mendapatkan posisi droid hijau
        int greenX = greenDroid.x; // mengambil koordinat x
        int greenY = greenDroid.y;
        
        // Cek apakah droid merah bersentuhan dengan droid hijau
        if (redDroidPos.x == greenX && redDroidPos.y == greenY) {
            // Menampilkan notifikasi hanya sekali jika belum bersentuhan sebelumnya
            if (!droidMerahBersentuhan) {
                droidMerahBersentuhan = true;
                String message = "DROID HIJAU TERTANGKAP!";
                JOptionPane.showMessageDialog(this, message, "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
                hentikanPergerakan(); // Menghentikan pergerakan droid merah
                offPandanganDroidHijau();
                offPandanganDroidMerah();
            }
            return;
        }
        // Menggunakan algoritma BFS untuk mencari jalur terpendek dari droid merah ke droid hijau
        Queue<Point> queue = new LinkedList<>(); // menympan titik yang akan dikunjungi
        queue.offer(currentPos); // menambahkan curent pos sebagai posisi awal dalam antrian

        boolean[][] visited = new boolean[numRows][numCols];
        visited[currentPos.x][currentPos.y] = true; // tandai sudah dikunjungi

        int[][] parentX = new int[numRows][numCols]; // menyimpan kordinat x dari kordinat sebelumnya dalam jalur terpendek
        int[][] parentY = new int[numRows][numCols];

        boolean foundPath = false;

        while (!queue.isEmpty()) { // *
            Point current = queue.poll(); // mengambil antrian pertama dan dimasukkan di current
            if (current.x == greenX && current.y == greenY) {
                foundPath = true;
                break;
            }
            // Cek semua tetangga yang belum dikunjungi
            List<Point> neighbors = getValidNeighbors(current, 15); // mengembalikan daftar tetangga yang valid dari dari suatu koordinat
            for (Point tetangga : neighbors) { // perulangan setiap tetangga yang valid dari koordinat saat ini untuk mengecek sudah dikunjungi atau belum
                if (!visited[tetangga.x][tetangga.y]) {
                    queue.offer(tetangga); // menambah ke antrian untk dikunjungi slnjutnya
                    visited[tetangga.x][tetangga.y] = true;
                    parentX[tetangga.x][tetangga.y] = current.x; // menyimpan kordinat x dari current sebagai koordinat sebelumnya
                    parentY[tetangga.x][tetangga.y] = current.y;
                }
            }
        }
        // Jika jalur ditemukan, mengikuti parent untuk mencari langkah terdekat
        if (foundPath) {
            List<Point> path = new ArrayList<>(); // utk menyimpan jalur terpendek dari merah ke hijau
            Point current = new Point(greenX, greenY); // posisi droid hijau
            while (current.x != currentPos.x || current.y != currentPos.y) {
                path.add(current);
                int parentXPos = parentX[current.x][current.y]; // memperbarui koordinat sebelumnya
                int parentYPos = parentY[current.x][current.y];
                current = new Point(parentXPos, parentYPos);
            }
            // Menghapus droid merah dari posisi saat ini
            JPanel currentCell = (JPanel) mapPanel.getComponent(currentPos.x * numCols + currentPos.y);
            currentCell.removeAll();

            // Menempatkan droid merah pada posisi berikutnya dalam jalur terpendek
            Point nextPos = path.get(path.size() - 1); // ambil posisi berikutnya dari path
            JPanel newCell = (JPanel) mapPanel.getComponent(nextPos.x * numCols + nextPos.y);
            newCell.setLayout(new BorderLayout());
            JLabel droid = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(Color.RED);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            droid.setPreferredSize(new Dimension(cellSize, cellSize));
            newCell.add(droid, BorderLayout.CENTER);

            // Memperbarui posisi droid merah pada variabel
            redDroidPos.setLocation(nextPos);

            // Memperbarui tampilan peta
            mapPanel.revalidate();
            mapPanel.repaint();
        }
    }
    
    private void gerakDroidHijau() {
        SwingUtilities.invokeLater(() -> {
            // Mendapatkan posisi droid merah
            List<Point> redDroidPositions = new ArrayList<>(redDroids); // buat salinan dari posisi droid merah
            
            if (jarakPandangDroidHijau >= 3) {
                // Menggunakan algoritma A* untuk mencari jalur terpendek dari droid hijau ke titik terjauh dari droid merah
                Point titikTerjauh = cariTitikTerjauhDariDroidMerah();
                List<Point> path = findShortestPathTo(greenDroid, titikTerjauh, redDroidPositions);
                
                if (path.size() >= 2) { // untuk mengatasai indexOfBondException(saatini dan berikutnya)
                    // Menghapus droid hijau dari posisi saat ini
                    if (greenDroid != null) {
                        JPanel currentCell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
                        currentCell.removeAll();
                    }
                    // Menempatkan droid hijau pada posisi berikutnya dalam jalur terpendek
                    Point nextPos = path.get(1);
                    JPanel newCell = (JPanel) mapPanel.getComponent(nextPos.x * numCols + nextPos.y);
                    newCell.setLayout(new BorderLayout());
                    JLabel droid = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setColor(Color.GREEN);
                            g2d.fillOval(0, 0, getWidth(), getHeight());
                            g2d.dispose();
                        }
                    };
                    droid.setPreferredSize(new Dimension(cellSize, cellSize));
                    newCell.add(droid, BorderLayout.CENTER);

                    // Memperbarui posisi droid hijau pada variabel
                    greenDroid = nextPos;

                    // Mengecek pandangan Droid Merah
                    if (pandanganDroidMerah == true){
                        droidMerahVisibility();
                        offPandanganDroidHijau();
                    }

                    // Memperbarui tampilan peta
                    mapPanel.revalidate();
                    mapPanel.repaint();
                }
            } else {
                // Mendapatkan posisi droid hijau saat ini
                Point currentPos = new Point(greenDroid.x, greenDroid.y);

                // Mendapatkan jarak antara droid hijau dan droid merah
                double minDistance = Double.MAX_VALUE;
                Point nearestRedDroidPos = null;

                for (Point redDroidPos : redDroidPositions) {
                    double distance = Math.sqrt(Math.pow(greenDroid.x - redDroidPos.x, 2) + Math.pow(greenDroid.y - redDroidPos.y, 2)); // rumus jarak euclidean (x1-x2)^2 + (y1-y2)^2, kemudian di akarkan
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestRedDroidPos = redDroidPos;
                    }
                }
                if (nearestRedDroidPos == null) {
                    // Tidak ada droid merah yang ditemukan, tidak ada pergerakan yang mungkin
                    return;
                }

                // Mendapatkan arah pergerakan droid hijau untuk menjauhi droid merah
                int dx = (int) Math.round((currentPos.x - nearestRedDroidPos.x) / minDistance);
                int dy = (int) Math.round((currentPos.y - nearestRedDroidPos.y) / minDistance);

                // Mendapatkan posisi tujuan pergerakan droid hijau
                int targetX = currentPos.x + dx;
                int targetY = currentPos.y + dy;

                // Memeriksa apakah target berada di luar peta atau merupakan tembok
                if (targetX < 0 || targetX >= numRows || targetY < 0 || targetY >= numCols || map[targetX][targetY] == 1) {
                    // Jika target di luar peta atau merupakan tembok, pilih arah pergerakan menjauh dari droid merah

                    // Mendapatkan semua tetangga yang valid
                    List<Point> validNeighbors = getValidNeighbors(currentPos, jarakPandangDroidHijau);

                    // Mendapatkan tetangga dengan jarak terjauh dari droid merah
                    double farthestDistance = Double.MIN_VALUE;
                    Point farthestNeighbor = null;

                    for (Point neighbor : validNeighbors) {
                        double neighborDistance = Math.sqrt(Math.pow(neighbor.x - nearestRedDroidPos.x, 2) + Math.pow(neighbor.y - nearestRedDroidPos.y, 2));
                        if (neighborDistance > farthestDistance) {
                            farthestDistance = neighborDistance;
                            farthestNeighbor = neighbor;
                        }
                    }
                    // Memilih tetangga terjauh sebagai target pergerakan
                    if (farthestNeighbor != null) {
                        targetX = farthestNeighbor.x;
                        targetY = farthestNeighbor.y;
                    } else {
                        // Tidak ada tetangga yang valid, tidak ada pergerakan yang mungkin
                        return;
                    }
                }
                // Menghapus droid hijau dari posisi saat ini
                JPanel currentCell = (JPanel) mapPanel.getComponent(currentPos.x * numCols + currentPos.y);
                currentCell.removeAll();

                // Menempatkan droid hijau pada posisi tujuan
                JPanel newCell = (JPanel) mapPanel.getComponent(targetX * numCols + targetY);
                newCell.setLayout(new BorderLayout());
                JLabel droid = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setColor(Color.GREEN);
                        g2d.fillOval(0, 0, getWidth(), getHeight());
                        g2d.dispose();
                    }
                };
                droid.setPreferredSize(new Dimension(cellSize, cellSize));
                newCell.add(droid, BorderLayout.CENTER);

                // Memperbarui posisi droid hijau pada variabel
                greenDroid = new Point(targetX, targetY);

                // Memperbarui tampilan peta
                mapPanel.revalidate();
                mapPanel.repaint();
            }
        });
    }

    private Point cariTitikTerjauhDariDroidMerah() {
        double maxDistance = Double.MIN_VALUE;
        Point titikTerjauh = null;
        for (int x = 0; x < numRows; x++) { // iterasi variabel x dari 0 hingga jumlah baris di peta (memeriksa setiap sel peta)
            for (int y = 0; y < numCols; y++) {
                if (map[x][y] == 0) {
                    Point currentPoint = new Point(x, y);
                    double distance = 0.0; //Inisialisasi distance dengan nilai 0.0, yang akan digunakan untuk melacak jarak saat ini antara sel saat ini dan droid merah.
                    for (Point redDroidPos : redDroids) {
                        double currentDistance = Math.sqrt(Math.pow(redDroidPos.x - currentPoint.x, 2) + Math.pow(redDroidPos.y - currentPoint.y, 2));
                        if (currentDistance > distance) {
                            distance = currentDistance;
                        }
                    }
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        titikTerjauh = currentPoint;
                    }
                }
            }
        }
        return titikTerjauh;
    }

    private List<Point> findShortestPathTo(Point start, Point destination, List<Point> redDroidPositions) {
        Queue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.fScore));
        queue.add(new Node(start, 0, null));
        
        Map<Point, Integer> gScore = new HashMap<>(); // GScore digunakan untuk menyimpan jarak terpendek dari start ke suatu titik
        gScore.put(start, 0);
        Map<Point, Point> cameFrom = new HashMap<>(); // CameFrom digunakan untuk melacak jalur yang diambil untuk mencapai suatu titik
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.position == null) {
                continue; // Skip jika current.position null
            }
            if (current.position.equals(destination)) {
                // Jalur ditemukan, mengikuti cameFrom untuk membangun jalur terpendek
                List<Point> path = new ArrayList<>();
                Point pathNode = current.position;
                while (pathNode != null) {
                    path.add(0, pathNode);
                    pathNode = cameFrom.get(pathNode);
                }
                return path;
            }
            // Mendapatkan tetangga-tetangga yang valid dari posisi saat ini
            List<Point> neighbors = getValidNeighbors(current.position, jarakPandangDroidHijau);
            for (Point neighbor : neighbors) {
                int tentativeGScore = gScore.getOrDefault(current.position, 0) + 1;

                // Mengecek apakah tetangga berada di antara droid merah
                boolean isNeighborBetweenRedDroids = false;
                for (Point redDroidPos : redDroidPositions) {
                    if (isPointBetween(current.position, redDroidPos, neighbor)) {
                        isNeighborBetweenRedDroids = true;
                        break;
                    }
                }
                if (isNeighborBetweenRedDroids) {
                    // Menghindari tetangga yang berada di antara droid merah
                    continue;
                }
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    // Jalur baru yang lebih baik ditemukan
                    cameFrom.put(neighbor, current.position);
                    gScore.put(neighbor, tentativeGScore);
                    int fScore = tentativeGScore + heuristicCostEstimate(neighbor, destination);
                    queue.add(new Node(neighbor, fScore, current.position));
                }
            }
        }

        return Collections.emptyList(); // Tidak ada jalur yang ditemukan
    }

    private boolean isPointBetween(Point start, Point middle, Point end) {
        // Memeriksa apakah middle berada di antara start dan end dalam satu garis lurus
        return (start.x <= middle.x && middle.x <= end.x || start.x >= middle.x && middle.x >= end.x) &&
               (start.y <= middle.y && middle.y <= end.y || start.y >= middle.y && middle.y >= end.y);
    }

    private int heuristicCostEstimate(Point start, Point destination) {
        // Menggunakan jarak Manhattan sebagai estimasi heuristik
        return Math.abs(start.x - destination.x) + Math.abs(start.y - destination.y);
    }

    private static class Node implements Comparable<Node> {
        private Point position;
        private final int fScore;

        public Node(Point position, int fScore, Point cameFrom) {
            this.position = position;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fScore, other.fScore);
        }
    }
 
    private void pandanganDroidMerah() {
        pandanganDroidMerah = true;
        timer = new Timer(0, e -> droidMerahVisibility());
        timer.start();
    }

    
    private void offPandanganDroidMerah() {
        pandanganDroidMerah = false;
        droidMerahVisibility();
        timer.stop();
    }
    
    private void droidMerahVisibility() {
        offPandanganDroidHijau();

        // Mendapatkan posisi droid hijau
        int greenX = greenDroid.x;
        int greenY = greenDroid.y;

        // Mengecek apakah ada droid merah yang sejajar dengan droid hijau dan tidak ada tembok di antara keduanya
        for (Point redDroidPos : redDroids) {
            // Menghitung jarak antara droid merah dan droid hijau
            int distance = Math.abs(redDroidPos.x - greenX) + Math.abs(redDroidPos.y - greenY);

            // Mengecek apakah droid merah sejajar dengan droid hijau dan tidak ada tembok di antara keduanya
            boolean sejajar = (redDroidPos.x == greenX) || (redDroidPos.y == greenY);
            boolean tanpaTembok = true;

            // Mengecek keberadaan tembok di antara droid merah dan droid hijau
            if (redDroidPos.x == greenX) {
                int minY = Math.min(redDroidPos.y, greenY);
                int maxY = Math.max(redDroidPos.y, greenY);

                for (int y = minY + 1; y < maxY; y++) {
                    if (map[redDroidPos.x][y] == 1) {
                        tanpaTembok = false;
                        break;
                    }
                }
            } else if (redDroidPos.y == greenY) {
                int minX = Math.min(redDroidPos.x, greenX);
                int maxX = Math.max(redDroidPos.x, greenX);

                for (int x = minX + 1; x < maxX; x++) {
                    if (map[x][redDroidPos.y] == 1) {
                        tanpaTembok = false;
                        break;
                    }
                }
            }
            //matikan droid merah
            if (pandanganDroidMerah == false) {
                // Memunculkan kembali droid hijau pada posisi yang sama
                JPanel cell = (JPanel) mapPanel.getComponent(greenX * numCols + greenY);
                cell.setLayout(new BorderLayout());

                // Membuat gambar droid hijau
                JLabel droid = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setColor(Color.GREEN);
                        g2d.fillOval(0, 0, getWidth(), getHeight());
                        g2d.dispose();
                    }
                };
                // Mengatur ukuran droid hijau
                droid.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.add(droid, BorderLayout.CENTER);

                mapPanel.revalidate();
                mapPanel.repaint();
            } else if (pandanganDroidMerah && sejajar && tanpaTembok) { // jika pandangan droid merah true, dan sejajar dengan droid hijau tanpa halangan tembok
                // Memunculkan kembali droid hijau pada posisi yang sama
                JPanel cell = (JPanel) mapPanel.getComponent(greenX * numCols + greenY);
                cell.setLayout(new BorderLayout());

                // Membuat gambar droid hijau
                JLabel droid = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setColor(Color.GREEN);
                        g2d.fillOval(0, 0, getWidth(), getHeight());
                        g2d.dispose();
                    }
                };
                // Mengatur ukuran droid hijau
                droid.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.add(droid, BorderLayout.CENTER);

                mapPanel.revalidate();
                mapPanel.repaint();
            } else {
                // Menghilangkan gambar droid hijau jika jarak lebih dari 1
                if (distance > 1) {
                    JPanel cell = (JPanel) mapPanel.getComponent(greenX * numCols + greenY);
                    Component[] components = cell.getComponents();
                    for (Component component : components) {
                        if (component instanceof JLabel) {
                            cell.remove(component);
                            break;
                        }
                    }
                    mapPanel.revalidate();
                    mapPanel.repaint();
                }
            }
        }
    }

    private void pandanganDroidHijau() {
        pandanganDroidHijau = true;
        jarakPandangDroidHijau = pandanganGreenSlider.getValue();
        pandanganDroidMerah = false;
        timer = new Timer(0, e -> droidHijauVisibility());
        timer.start();
    }
    
    private void offPandanganDroidHijau() {
        jarakPandangDroidHijau = 15;
        droidHijauVisibility();
        timer.stop();
    }

    private void droidHijauVisibility() {
        if (pandanganDroidHijau == true) {
            Point greenDroidPos = new Point(greenDroid.x, greenDroid.y);
            int minX = Math.max(0, greenDroidPos.x - jarakPandangDroidHijau);
            int maxX = Math.min(numRows - 1, greenDroidPos.x + jarakPandangDroidHijau);
            int minY = Math.max(0, greenDroidPos.y - jarakPandangDroidHijau);
            int maxY = Math.min(numCols - 1, greenDroidPos.y + jarakPandangDroidHijau);

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    JPanel cell = (JPanel) mapPanel.getComponent(i * numCols + j);
                    if (i >= minX && i <= maxX && j >= minY && j <= maxY) {
                        if (map[i][j] == 0) {
                            cell.setBackground(new Color(255, 0, 255, 0));
                        } else {
                            cell.setBackground(Color.GRAY);
                        }
                    } else {
                        cell.setBackground(Color.BLACK);
                    }
                }
            }
        }

        mapPanel.revalidate();
        mapPanel.repaint();
    }

    public static void main(String[] args) {
        new HideAndSeek();
    }
}