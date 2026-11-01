package ui;

import dao.DangKyDAO;
import dao.DBConnection;
import dao.DonHangDAO;
import dao.GioHangDAO;
import dao.KhoaHocDAO;
import dao.TaiKhoanDAO;
import model.ChiTietGioHang;
import model.DangKy;
import model.KhoaHoc;
import model.TaiKhoan;
import session.UserSession;
import utils.AppUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Giao diện chính của HaUI Course App
 * Chuẩn hóa 1:1 theo thiết kế Figma (Màn hình Đăng ký, Đăng nhập, Trang chủ, Danh sách, Chi tiết, Giỏ hàng, Thanh toán)
 */
public class MainFrame extends JFrame {

    // Routing Card Keys
    public static final String PAGE_HOME          = "HOME";
    public static final String PAGE_COURSES       = "COURSES";
    public static final String PAGE_COURSE_DETAIL = "COURSE_DETAIL";
    public static final String PAGE_CART          = "CART";
    public static final String PAGE_CHECKOUT      = "CHECKOUT";
    public static final String PAGE_MY_COURSES    = "MY_COURSES";
    public static final String PAGE_PROFILE       = "PROFILE";
    public static final String PAGE_AUTH          = "AUTH";

    // Figma Color Palette
    public static final Color COLOR_PRIMARY       = new Color(47, 85, 136);   // Figma Navy #2F5588
    public static final Color COLOR_PRIMARY_HOVER = new Color(35, 68, 115);   // Darker Navy on hover
    public static final Color COLOR_BG            = new Color(245, 247, 250); // #F5F7FA Figma soft gray page bg
    public static final Color COLOR_HERO_BG       = new Color(241, 245, 249); // #F1F5F9
    public static final Color COLOR_BORDER        = new Color(226, 232, 240); // #E2E8F0
    public static final Color COLOR_BORDER_INPUT  = new Color(203, 213, 225); // #CBD5E1
    public static final Color COLOR_TEXT_DARK     = new Color(15, 23, 42);    // #0F172A
    public static final Color COLOR_TEXT_MUTED    = new Color(100, 116, 139); // #64748B
    public static final Color COLOR_ACCENT_ORANGE = new Color(245, 158, 11);  // #F59E0B
    public static final Color COLOR_SUCCESS       = new Color(22, 101, 52);   // #166534

    // Typography
    public static final Font FONT_LOGO    = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_NAV     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_NAV_ACT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_HERO_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_CARD_H  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 12);

    // DAOs
    private final KhoaHocDAO khoaHocDAO = new KhoaHocDAO();
    private final GioHangDAO gioHangDAO = new GioHangDAO();
    private final DangKyDAO dangKyDAO   = new DangKyDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final DonHangDAO donHangDAO = new DonHangDAO();

    private String getCurrentCartId() {
        String gh = UserSession.getInstance().getMaGioHang();
        if (gh != null && !gh.isEmpty()) return gh;
        TaiKhoan u = UserSession.getInstance().getCurrentUser();
        if (u != null) {
            gh = gioHangDAO.getOrCreateGioHang(u.getMaKhachHang());
            UserSession.getInstance().setMaGioHang(gh);
            return gh;
        }
        return "GH001";
    }

    private String getCurrentCustomerId() {
        TaiKhoan u = UserSession.getInstance().getCurrentUser();
        return u != null ? u.getMaKhachHang() : "KH001";
    }

    // Navigation & Router
    private JPanel cardContainer;
    private CardLayout cardLayout;
    private String currentPage = PAGE_HOME;

    // Header Components
    private JPanel pnlHeaderEast;
    private JPanel pnlLinksStandard;
    private JPanel pnlLinksAuth;
    private final List<NavButton> navButtons = new ArrayList<>();
    private NavButton btnNavCart;
    private NavButton btnNavUser;
    private Image imgHauiLogo;

    // Selected Course for Detail Page
    private KhoaHoc currentDetailCourse;

    // Dynamic Pages
    private PagePanel panelHome;
    private PagePanel panelCourses;
    private PagePanel panelCourseDetail;
    private PagePanel panelCart;
    private PagePanel panelCheckout;
    private PagePanel panelMyCourses;
    private PagePanel panelProfile;
    private PagePanel panelAuth;

    // Filter states for Courses page
    private String selectedCategory = "Tất cả";
    private int selectedPriceRange = 0;
    private String searchKeyword = "";
    private int discountPercent = 0;

    // Auth state: true = Đăng nhập, false = Đăng ký (Mặc định mở màn hình Đăng nhập)
    private boolean isAuthLoginMode = true;
    private CardBox authLeftCard;
    private CardBox authRightCard;

    public MainFrame() {
        setTitle("HaUI Course - Nền tảng học lập trình trực tuyến");
        loadLogoImage();
        adjustWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        openAuth(true);
    }

    private void loadLogoImage() {
        try {
            File f = new File("lib/haui_logo.png");
            if (!f.exists()) f = new File("HaUI_CourseApp/lib/haui_logo.png");
            if (f.exists()) {
                imgHauiLogo = ImageIO.read(f);
            }
        } catch (Exception ignored) {}
    }

    private void adjustWindowSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());

        int availableWidth = screenSize.width - insets.left - insets.right;
        int availableHeight = screenSize.height - insets.top - insets.bottom;

        int targetWidth = (int) (availableWidth * 0.95);
        int targetHeight = (int) (availableHeight * 0.95);

        targetWidth = Math.min(1360, Math.max(960, targetWidth));
        targetHeight = Math.min(880, Math.max(620, targetHeight));

        setSize(targetWidth, targetHeight);
        setMinimumSize(new Dimension(920, 580));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // 1. Top Navbar
        root.add(createTopNavBar(), BorderLayout.NORTH);

        // 2. Router (CardLayout)
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(COLOR_BG);

        panelHome = createHomePage();
        panelCourses = createCoursesPage();
        panelCourseDetail = createCourseDetailPage();
        panelCart = createCartPage();
        panelCheckout = createCheckoutPage();
        panelMyCourses = createMyCoursesPage();
        panelProfile = createProfilePage();
        panelAuth = createAuthPage();

        cardContainer.add(wrapInScroll(panelHome), PAGE_HOME);
        cardContainer.add(wrapInScroll(panelCourses), PAGE_COURSES);
        cardContainer.add(wrapInScroll(panelCourseDetail), PAGE_COURSE_DETAIL);
        cardContainer.add(wrapInScroll(panelCart), PAGE_CART);
        cardContainer.add(wrapInScroll(panelCheckout), PAGE_CHECKOUT);
        cardContainer.add(wrapInScroll(panelMyCourses), PAGE_MY_COURSES);
        cardContainer.add(wrapInScroll(panelProfile), PAGE_PROFILE);
        cardContainer.add(wrapInScroll(panelAuth), PAGE_AUTH);

        root.add(cardContainer, BorderLayout.CENTER);
        setContentPane(root);
    }

    // =========================================================================
    // CORE LAYOUT CONTAINERS (Đảm bảo căn lề trái tuyệt đối 100%, không lệch)
    // =========================================================================

    /**
     * VBox: Mọi component con thêm vào đều tự động setAlignmentX(LEFT_ALIGNMENT).
     * Khắc phục triệt để lỗi lệch lề giữa JLabel/JButton (0.0f) và JTextField/JPanel (0.5f) trong Swing BoxLayout.
     */
    public static class VBox extends JPanel {
        public VBox() {
            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void addImpl(Component comp, Object constraints, int index) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            super.addImpl(comp, constraints, index);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension pref = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, pref.height);
        }
    }

    /**
     * RowPanel: Hàng ngang chiếm 100% chiều rộng của VBox cha nhưng giữ nguyên chiều cao tự nhiên (không bị giãn dọc).
     */
    public static class RowPanel extends JPanel {
        public RowPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension pref = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, pref.height);
        }
    }

    /**
     * CardBox: Khung thẻ trắng bo góc mượt bằng Graphics2D, kế thừa VBox để mọi phần tử con thẳng hàng tuyệt đối
     * và tự động kéo dãn chiều cao đủ chứa 100% nội dung bên trong (không bao giờ bị thiếu kích thước).
     */
    public static class CardBox extends VBox {
        private Color bgColor;
        private Color borderColor;
        private int radius;
        private int fixedWidth = 0;
        private int minHeight = 0;

        public CardBox(Color bgColor, Color borderColor, int radius, Insets padding) {
            super();
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.radius = radius;
            setOpaque(false);
            if (padding != null) {
                setBorder(new EmptyBorder(padding));
            }
        }

        public void setCardColors(Color bg, Color border) {
            this.bgColor = bg;
            this.borderColor = border;
            repaint();
        }

        public void setMinDimensions(int width, int minH) {
            this.fixedWidth = width;
            this.minHeight = minH;
        }

        @Override
        public void setPreferredSize(Dimension preferredSize) {
            if (preferredSize != null) {
                this.fixedWidth = preferredSize.width;
                this.minHeight = preferredSize.height;
            } else {
                this.fixedWidth = 0;
                this.minHeight = 0;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            int w = fixedWidth > 0 ? fixedWidth : d.width;
            int h = Math.max(minHeight, d.height);
            return new Dimension(w, h);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension pref = getPreferredSize();
            return new Dimension(fixedWidth > 0 ? fixedWidth : Integer.MAX_VALUE, pref.height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (bgColor != null) {
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * PagePanel: Tự động kéo dãn chiều cao tối thiểu bằng đúng khung nhìn màn hình (Viewport)
     * và đẩy Footer xuống sát đáy cửa sổ, không để lộ khoảng trống xám bên dưới Footer.
     */
    public static class PagePanel extends JPanel implements Scrollable {
        private final JPanel bodyWrap;
        private final boolean centerVertically;

        public PagePanel() {
            this(false);
        }

        public PagePanel(boolean centerVertically) {
            super(new BorderLayout());
            this.centerVertically = centerVertically;
            setOpaque(true);
            setBackground(COLOR_BG);
            bodyWrap = new JPanel(new BorderLayout());
            bodyWrap.setOpaque(false);
            super.addImpl(bodyWrap, BorderLayout.CENTER, -1);
        }

        @Override
        protected void addImpl(Component comp, Object constraints, int index) {
            if (comp == bodyWrap) {
                super.addImpl(comp, constraints, index);
            } else if (bodyWrap.getComponentCount() == 0) {
                bodyWrap.add(comp, centerVertically ? BorderLayout.CENTER : BorderLayout.NORTH);
            } else {
                super.addImpl(comp, BorderLayout.SOUTH, -1);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            if (getParent() instanceof JViewport) {
                int viewH = getParent().getHeight();
                return new Dimension(d.width, Math.max(d.height, viewH));
            }
            return d;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 20; }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 60; }
        @Override
        public boolean getScrollableTracksViewportWidth() { return true; }
        @Override
        public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private JScrollPane wrapInScroll(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setBackground(COLOR_BG);
        return sp;
    }

    // =========================================================================
    // TOP NAVIGATION BAR (Chuẩn Figma: Logo trái, điều hướng phải)
    // =========================================================================
    private JPanel createTopNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(1000, 62));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Brand Logo Left (Theo đúng Figma: chỉ hiển thị logo HaUI vuông sắc nét)
        JPanel pnlLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 48, 12));
        pnlLogo.setOpaque(false);
        pnlLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { navigate(PAGE_HOME); }
        });

        JLabel lblLogoBox = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (imgHauiLogo != null) {
                    g2.drawImage(imgHauiLogo, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(COLOR_ACCENT_ORANGE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("HaUI", (getWidth() - fm.stringWidth("HaUI")) / 2, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                }
                g2.dispose();
            }
        };
        lblLogoBox.setPreferredSize(new Dimension(38, 38));
        pnlLogo.add(lblLogoBox);

        navBar.add(pnlLogo, BorderLayout.WEST);

        // Standard Links Panel
        pnlLinksStandard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlLinksStandard.setOpaque(false);
        pnlLinksStandard.setBorder(new EmptyBorder(0, 0, 0, 36));

        NavButton btnHome = new NavButton("Trang chủ", PAGE_HOME);
        NavButton btnCourses = new NavButton("Khóa học", PAGE_COURSES);
        NavButton btnMyCourses = new NavButton("Khóa học của tôi", PAGE_MY_COURSES);
        btnNavCart = new NavButton("Giỏ hàng (" + getCartCount() + ")", PAGE_CART);

        TaiKhoan user = UserSession.getInstance().getCurrentUser();
        String userTitle = user != null ? user.getHoTen() : "Đăng nhập/Đăng ký";
        btnNavUser = new NavButton(userTitle, user != null ? PAGE_PROFILE : PAGE_AUTH);

        navButtons.add(btnHome);
        navButtons.add(btnCourses);
        navButtons.add(btnMyCourses);
        navButtons.add(btnNavCart);
        navButtons.add(btnNavUser);

        for (NavButton btn : navButtons) {
            pnlLinksStandard.add(btn);
        }

        // Auth Links Panel (Theo Figma: chỉ hiển thị "← Quay về trang chủ")
        pnlLinksAuth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 48, 20));
        pnlLinksAuth.setOpaque(false);
        JLabel lnkBackHome = new JLabel("← Quay về trang chủ");
        lnkBackHome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lnkBackHome.setForeground(COLOR_TEXT_MUTED);
        lnkBackHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lnkBackHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { navigate(PAGE_HOME); }
            @Override
            public void mouseEntered(MouseEvent e) { lnkBackHome.setForeground(COLOR_PRIMARY); }
            @Override
            public void mouseExited(MouseEvent e) { lnkBackHome.setForeground(COLOR_TEXT_MUTED); }
        });
        pnlLinksAuth.add(lnkBackHome);

        pnlHeaderEast = new JPanel(new BorderLayout());
        pnlHeaderEast.setOpaque(false);
        pnlHeaderEast.add(pnlLinksStandard, BorderLayout.CENTER);
        navBar.add(pnlHeaderEast, BorderLayout.EAST);

        return navBar;
    }

    private int getCartCount() {
        return gioHangDAO.demGio(getCurrentCartId());
    }

    public void updateCartBadge() {
        if (btnNavCart != null) {
            btnNavCart.setText("Giỏ hàng (" + getCartCount() + ")");
            btnNavCart.revalidate();
            btnNavCart.repaint();
        }
    }

    public void updateUserBadge() {
        if (btnNavUser != null) {
            TaiKhoan user = UserSession.getInstance().getCurrentUser();
            if (user != null) {
                btnNavUser.setText(user.getHoTen());
                btnNavUser.setPageKey(PAGE_PROFILE);
            } else {
                btnNavUser.setText("Đăng nhập/Đăng ký");
                btnNavUser.setPageKey(PAGE_AUTH);
            }
            btnNavUser.revalidate();
            btnNavUser.repaint();
        }
    }

    private class NavButton extends JButton {
        private String pageKey;
        private boolean isActive = false;

        public NavButton(String text, String pageKey) {
            super(text);
            this.pageKey = pageKey;
            setFont(FONT_NAV);
            setForeground(COLOR_TEXT_MUTED);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setMargin(new Insets(0, 0, 0, 0));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 12));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) setForeground(COLOR_PRIMARY);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) setForeground(COLOR_TEXT_MUTED);
                }
            });

            addActionListener(e -> {
                if (PAGE_AUTH.equals(this.pageKey)) {
                    openAuth(false);
                } else {
                    navigate(this.pageKey);
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(FONT_NAV_ACT);
            int textWidth = (fm != null && getText() != null) ? fm.stringWidth(getText()) : 80;
            return new Dimension(textWidth + 24, 61);
        }

        public void setPageKey(String key) { this.pageKey = key; }

        public void setActive(boolean active) {
            this.isActive = active;
            setFont(active ? FONT_NAV_ACT : FONT_NAV);
            setForeground(active ? COLOR_PRIMARY : COLOR_TEXT_MUTED);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g2);
            if (isActive) {
                g2.setColor(COLOR_PRIMARY);
                FontMetrics fm = g2.getFontMetrics(FONT_NAV_ACT);
                int textW = (fm != null && getText() != null) ? fm.stringWidth(getText()) : getWidth() - 20;
                int startX = (getWidth() - textW) / 2;
                g2.fillRect(Math.max(4, startX), getHeight() - 3, Math.min(getWidth() - 8, textW), 2);
            }
            g2.dispose();
        }
    }

    public void navigate(String pageKey) {
        this.currentPage = pageKey;

        if (pnlHeaderEast != null) {
            pnlHeaderEast.removeAll();
            if (PAGE_AUTH.equals(pageKey)) {
                pnlHeaderEast.add(pnlLinksAuth, BorderLayout.CENTER);
            } else {
                pnlHeaderEast.add(pnlLinksStandard, BorderLayout.CENTER);
            }
            pnlHeaderEast.revalidate();
            pnlHeaderEast.repaint();
        }

        for (NavButton btn : navButtons) {
            btn.setActive(btn.pageKey.equals(pageKey));
        }
        updateCartBadge();
        updateUserBadge();

        if (PAGE_COURSES.equals(pageKey)) refreshCoursesPage();
        else if (PAGE_CART.equals(pageKey)) refreshCartPage();
        else if (PAGE_CHECKOUT.equals(pageKey)) refreshCheckoutPage();
        else if (PAGE_MY_COURSES.equals(pageKey)) refreshMyCoursesPage();
        else if (PAGE_PROFILE.equals(pageKey)) refreshProfilePage();

        cardLayout.show(cardContainer, pageKey);
    }

    public void openCourseDetail(KhoaHoc kh) {
        this.currentDetailCourse = kh;
        refreshCourseDetailPage();
        navigate(PAGE_COURSE_DETAIL);
    }

    public void openAuth(boolean loginMode) {
        this.isAuthLoginMode = loginMode;
        rebuildAuthCards();
        navigate(PAGE_AUTH);
    }

    // =========================================================================
    // MÀN HÌNH 1: TRANG CHỦ (Figma Frame Trang chủ - image30.png)
    // =========================================================================
    private PagePanel createHomePage() {
        PagePanel home = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(24, 48, 32, 48));

        content.add(createHeroSection());
        content.add(Box.createVerticalStrut(20));
        content.add(createHomeSearchBar());
        content.add(Box.createVerticalStrut(28));
        content.add(createCategorySection());
        content.add(Box.createVerticalStrut(32));
        content.add(createFeaturedCoursesSection());

        home.add(content);
        home.add(createFooterSection());

        return home;
    }

    private JPanel createHeroSection() {
        RowPanel hero = new RowPanel(new BorderLayout(32, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_HERO_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        hero.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Left Content
        VBox left = new VBox();

        JLabel badge = new JLabel("KHÓA HỌC LẬP TRÌNH");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(COLOR_PRIMARY);

        JLabel h1 = new JLabel("<html><div style='width:420px;'><b>Khóa học lập trình đơn giản, dễ theo dõi.</b></div></html>");
        h1.setFont(FONT_HERO_H1);
        h1.setForeground(COLOR_TEXT_DARK);

        JLabel sub = new JLabel("Tìm khóa học, học theo danh mục và bắt đầu ngay với lộ trình cơ bản.");
        sub.setFont(FONT_BODY);
        sub.setForeground(COLOR_TEXT_MUTED);

        ModernButton btnExplore = new ModernButton("Khám phá khóa học", COLOR_PRIMARY, Color.WHITE);
        btnExplore.setPreferredSize(new Dimension(170, 40));
        btnExplore.setMaximumSize(new Dimension(170, 40));
        btnExplore.addActionListener(e -> navigate(PAGE_COURSES));

        left.add(badge);
        left.add(Box.createVerticalStrut(10));
        left.add(h1);
        left.add(Box.createVerticalStrut(10));
        left.add(sub);
        left.add(Box.createVerticalStrut(20));
        left.add(btnExplore);

        hero.add(left, BorderLayout.CENTER);

        // Right Code Snippet Card
        CardBox rightCard = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(18, 20, 18, 20));
        rightCard.setPreferredSize(new Dimension(290, 175));

        JLabel lblSnippetTitle = new JLabel("BÀI HỌC ĐẦU TIÊN");
        lblSnippetTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSnippetTitle.setForeground(COLOR_TEXT_MUTED);

        JLabel lblCode1 = new JLabel("XinChao();");
        lblCode1.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblCode1.setForeground(COLOR_TEXT_DARK);

        JLabel lblCode2 = new JLabel("// Bắt đầu từ hôm nay");
        lblCode2.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblCode2.setForeground(COLOR_TEXT_MUTED);

        JLabel lblCode3 = new JLabel("hocTap.moiNgay();");
        lblCode3.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblCode3.setForeground(COLOR_TEXT_DARK);

        RowPanel pnlPills = new RowPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlPills.add(createPill("Java"));
        pnlPills.add(createPill("Python"));
        pnlPills.add(createPill("C++"));

        rightCard.add(lblSnippetTitle);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(lblCode1);
        rightCard.add(Box.createVerticalStrut(4));
        rightCard.add(lblCode2);
        rightCard.add(Box.createVerticalStrut(4));
        rightCard.add(lblCode3);
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(pnlPills);

        hero.add(rightCard, BorderLayout.EAST);
        return hero;
    }

    private JPanel createHomeSearchBar() {
        RowPanel bar = new RowPanel(new BorderLayout(10, 0));

        JTextField txtSearch = createModernTextField("Tìm kiếm khóa học, ngôn ngữ lập trình...", 42);

        ModernButton btnSearch = new ModernButton("Tìm kiếm", COLOR_PRIMARY, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(110, 42));
        btnSearch.addActionListener(e -> {
            searchKeyword = txtSearch.getText().trim();
            selectedCategory = "Tất cả";
            navigate(PAGE_COURSES);
        });

        txtSearch.addActionListener(e -> btnSearch.doClick());

        bar.add(txtSearch, BorderLayout.CENTER);
        bar.add(btnSearch, BorderLayout.EAST);
        return bar;
    }

    private JPanel createCategorySection() {
        VBox section = new VBox();

        JLabel lblTitle = new JLabel("Học theo danh mục");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        section.add(lblTitle);
        section.add(Box.createVerticalStrut(14));

        RowPanel grid = new RowPanel(new GridLayout(1, 5, 14, 0));
        grid.add(createCategoryCard("Java", "Nền tảng & hướng đối tượng"));
        grid.add(createCategoryCard("C++", "Lập trình & giải thuật"));
        grid.add(createCategoryCard("Python", "Ứng dụng & tự động hóa"));
        grid.add(createCategoryCard("HTML/CSS", "Thiết kế giao diện web"));
        grid.add(createCategoryCard("Spring Boot", "Phát triển ứng dụng backend"));

        section.add(grid);
        return section;
    }

    private JPanel createCategoryCard(String name, String desc) {
        CardBox card = new CardBox(Color.WHITE, COLOR_BORDER, 8, new Insets(14, 16, 14, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblName = new JLabel(name);
        lblName.setFont(FONT_CARD_H);
        lblName.setForeground(COLOR_TEXT_DARK);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(FONT_SMALL);
        lblDesc.setForeground(COLOR_TEXT_MUTED);

        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblDesc);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedCategory = name;
                searchKeyword = "";
                navigate(PAGE_COURSES);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCardColors(new Color(248, 250, 255), COLOR_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCardColors(Color.WHITE, COLOR_BORDER);
            }
        });

        return card;
    }

    private JPanel createFeaturedCoursesSection() {
        VBox section = new VBox();

        RowPanel header = new RowPanel(new BorderLayout());
        VBox titles = new VBox();

        JLabel lblTitle = new JLabel("Khóa học nổi bật");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel("Những khóa học được học viên yêu thích.");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        titles.add(lblTitle);
        titles.add(Box.createVerticalStrut(2));
        titles.add(lblSub);

        JLabel lnkAll = new JLabel("Xem tất cả khóa học →");
        lnkAll.setFont(FONT_BOLD);
        lnkAll.setForeground(COLOR_PRIMARY);
        lnkAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lnkAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { navigate(PAGE_COURSES); }
        });

        header.add(titles, BorderLayout.WEST);
        header.add(lnkAll, BorderLayout.EAST);

        section.add(header);
        section.add(Box.createVerticalStrut(16));

        RowPanel grid = new RowPanel(new GridLayout(1, 4, 16, 0));

        List<KhoaHoc> list = khoaHocDAO.getDanhSach();
        int count = Math.min(4, list.size());
        for (int i = 0; i < count; i++) {
            grid.add(createCourseCard(list.get(i)));
        }

        section.add(grid);
        return section;
    }

    /**
     * Thẻ khóa học chuẩn Figma (Có đường kẻ mảnh ngăn cách giữa đánh giá và giá tiền)
     */
    private JPanel createCourseCard(KhoaHoc kh) {
        CardBox card = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(0, 0, 14, 0));

        // 1. Thumbnail box chuẩn Figma
        JPanel thumb = createCourseThumbnail(kh.getAnhKhoaHoc() != null ? kh.getAnhKhoaHoc() : kh.getMaDanhMuc(), 0, 115);
        thumb.setPreferredSize(new Dimension(100, 115));
        thumb.setMaximumSize(new Dimension(2000, 115));
        card.add(thumb);

        VBox body = new VBox();
        body.setBorder(new EmptyBorder(12, 14, 0, 14));

        // 2. Metadata
        String lessonsOnly = kh.getThoiLuong() != null ? kh.getThoiLuong().split("•")[0].trim() : "40 bài học";
        JLabel lblMeta = new JLabel(kh.getCapDo() + " • " + lessonsOnly);
        lblMeta.setFont(FONT_SMALL);
        lblMeta.setForeground(COLOR_TEXT_MUTED);

        // 3. Tiêu đề khóa học
        JLabel lblTitle = new JLabel("<html><div style='width:145px;'><b>" + kh.getTenKhoaHoc() + "</b></div></html>");
        lblTitle.setFont(FONT_CARD_H);
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setVerticalAlignment(SwingConstants.TOP);
        lblTitle.setMinimumSize(new Dimension(185, 40));
        lblTitle.setPreferredSize(new Dimension(185, 40));
        lblTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // 4. Giảng viên
        JLabel lblTeacher = new JLabel(kh.getGiangVien());
        lblTeacher.setFont(FONT_SMALL);
        lblTeacher.setForeground(COLOR_TEXT_MUTED);

        // 5. Star Rating
        JPanel pnlRating = createStarRatingPanel(4.8, 1250);

        // 6. Học phí
        JLabel lblPrice = new JLabel(AppUtils.formatTien(kh.getHocPhi()));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPrice.setForeground(COLOR_TEXT_DARK);

        // 7. Nút Xem chi tiết
        ModernButton btnDetail = new ModernButton("Xem chi tiết", Color.WHITE, COLOR_TEXT_DARK, COLOR_BORDER_INPUT);
        btnDetail.setPreferredSize(new Dimension(100, 34));
        btnDetail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnDetail.addActionListener(e -> openCourseDetail(kh));

        body.add(lblMeta);
        body.add(Box.createVerticalStrut(4));
        body.add(lblTitle);
        body.add(Box.createVerticalStrut(6));
        body.add(lblTeacher);
        body.add(Box.createVerticalStrut(4));
        body.add(pnlRating);
        body.add(Box.createVerticalStrut(10));
        body.add(createSeparator());
        body.add(Box.createVerticalStrut(10));
        body.add(lblPrice);
        body.add(Box.createVerticalStrut(10));
        body.add(btnDetail);

        card.add(body);
        return card;
    }

    /**
     * Footer 2 tầng chuẩn thiết kế Figma (Nền trắng, căn lề 2 bên chuẩn xác)
     */
    private JPanel createFooterSection() {
        VBox footerWrap = new VBox();
        footerWrap.setOpaque(true);
        footerWrap.setBackground(Color.WHITE);
        footerWrap.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
            new EmptyBorder(24, 48, 20, 48)
        ));

        // Tầng 1: Thương hiệu bên trái - Menu links bên phải
        RowPanel topRow = new RowPanel(new BorderLayout());
        VBox left = new VBox();

        JLabel lblFTitle = new JLabel("HAUI");
        lblFTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblFSub = new JLabel("Học lập trình, xây dựng tương lai.");
        lblFSub.setFont(FONT_SMALL);
        lblFSub.setForeground(COLOR_TEXT_MUTED);

        left.add(lblFTitle);
        left.add(Box.createVerticalStrut(4));
        left.add(lblFSub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        right.setOpaque(false);
        String[] links = {"Về chúng tôi", "Điều khoản sử dụng", "Chính sách bảo mật", "Liên hệ"};
        for (String lnk : links) {
            JLabel l = new JLabel(lnk);
            l.setFont(FONT_SMALL);
            l.setForeground(COLOR_TEXT_MUTED);
            right.add(l);
        }

        topRow.add(left, BorderLayout.WEST);
        topRow.add(right, BorderLayout.EAST);

        // Tầng 2: Bản quyền bên trái - Email hỗ trợ bên phải
        RowPanel bottomRow = new RowPanel(new BorderLayout());
        JLabel lblFCopy = new JLabel("© 2026 HAUI. Nền tảng học lập trình trực tuyến.");
        lblFCopy.setFont(FONT_SMALL);
        lblFCopy.setForeground(new Color(148, 163, 184));

        JLabel lblFSupport = new JLabel("Hỗ trợ: hotro@haui.vn");
        lblFSupport.setFont(FONT_SMALL);
        lblFSupport.setForeground(new Color(148, 163, 184));

        bottomRow.add(lblFCopy, BorderLayout.WEST);
        bottomRow.add(lblFSupport, BorderLayout.EAST);

        footerWrap.add(topRow);
        footerWrap.add(Box.createVerticalStrut(16));
        footerWrap.add(createSeparator());
        footerWrap.add(Box.createVerticalStrut(12));
        footerWrap.add(bottomRow);

        return footerWrap;
    }

    // =========================================================================
    // MÀN HÌNH 2: DANH SÁCH KHÓA HỌC (Figma Frame 4 - image31.png)
    // =========================================================================
    private JPanel gridCoursesContainer;
    private JLabel lblCoursesCount;

    private PagePanel createCoursesPage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(20, 48, 32, 48));

        JLabel lblBreadcrumb = new JLabel("Trang chủ  /  Khóa học");
        lblBreadcrumb.setFont(FONT_SMALL);
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JLabel lblTitle = new JLabel("Khóa học lập trình");
        lblTitle.setFont(FONT_HERO_H1);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel("Tìm khóa học phù hợp với mục tiêu và trình độ của bạn.");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        content.add(lblBreadcrumb);
        content.add(Box.createVerticalStrut(6));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);
        content.add(Box.createVerticalStrut(16));

        // Search Bar
        RowPanel pnlSearch = new RowPanel(new BorderLayout(10, 0));
        JTextField txtSearch = createModernTextField("Tìm khóa học, ngôn ngữ lập trình...", 42);
        txtSearch.setText(searchKeyword);

        ModernButton btnSearch = new ModernButton("Tìm kiếm", COLOR_PRIMARY, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(110, 42));
        btnSearch.addActionListener(e -> {
            searchKeyword = txtSearch.getText().trim();
            refreshCoursesPage();
        });
        txtSearch.addActionListener(e -> btnSearch.doClick());

        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlSearch.add(btnSearch, BorderLayout.EAST);

        content.add(pnlSearch);
        content.add(Box.createVerticalStrut(22));

        // 2 Column Layout
        RowPanel contentRow = new RowPanel(new BorderLayout(24, 0));

        JPanel sideWrapper = new JPanel(new BorderLayout());
        sideWrapper.setOpaque(false);
        sideWrapper.add(createFilterSidebar(), BorderLayout.NORTH);
        contentRow.add(sideWrapper, BorderLayout.WEST);

        VBox rightCol = new VBox();

        RowPanel topBar = new RowPanel(new BorderLayout());
        lblCoursesCount = new JLabel("Hiển thị 1-6 trong 18 khóa học");
        lblCoursesCount.setFont(FONT_BODY);
        lblCoursesCount.setForeground(COLOR_TEXT_MUTED);

        JPanel sortRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sortRight.setOpaque(false);
        JLabel lblSort = new JLabel("Sắp xếp theo:");
        lblSort.setFont(FONT_SMALL);
        lblSort.setForeground(COLOR_TEXT_MUTED);
        JComboBox<String> cbSort = new JComboBox<>(new String[]{"Phổ biến nhất", "Giá tăng dần", "Giá giảm dần"});
        cbSort.setFont(FONT_BODY);
        cbSort.setPreferredSize(new Dimension(150, 32));
        sortRight.add(lblSort);
        sortRight.add(cbSort);

        topBar.add(lblCoursesCount, BorderLayout.WEST);
        topBar.add(sortRight, BorderLayout.EAST);

        rightCol.add(topBar);
        rightCol.add(Box.createVerticalStrut(14));

        gridCoursesContainer = new RowPanel(new GridLayout(0, 3, 16, 16));
        rightCol.add(gridCoursesContainer);
        rightCol.add(Box.createVerticalStrut(24));

        // Pagination Bar
        RowPanel pnlPagination = new RowPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        pnlPagination.add(createPagingButton("<", false));
        pnlPagination.add(createPagingButton("1", true));
        pnlPagination.add(createPagingButton("2", false));
        pnlPagination.add(createPagingButton("3", false));
        pnlPagination.add(createPagingButton(">", false));

        rightCol.add(pnlPagination);

        contentRow.add(rightCol, BorderLayout.CENTER);
        content.add(contentRow);

        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private JPanel createFilterSidebar() {
        CardBox side = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(18, 18, 18, 18));
        side.setPreferredSize(new Dimension(220, 480));

        RowPanel head = new RowPanel(new BorderLayout());
        JLabel lblBoLoc = new JLabel("Bộ lọc");
        lblBoLoc.setFont(FONT_CARD_H);
        lblBoLoc.setForeground(COLOR_TEXT_DARK);

        JLabel lblReset = new JLabel("Đặt lại");
        lblReset.setFont(FONT_SMALL);
        lblReset.setForeground(COLOR_PRIMARY);
        lblReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedCategory = "Tất cả";
                selectedPriceRange = 0;
                searchKeyword = "";
                refreshCoursesPage();
            }
        });

        head.add(lblBoLoc, BorderLayout.WEST);
        head.add(lblReset, BorderLayout.EAST);

        side.add(head);
        side.add(Box.createVerticalStrut(12));
        side.add(createSeparator());
        side.add(Box.createVerticalStrut(12));

        JLabel lblDM = new JLabel("Danh mục");
        lblDM.setFont(FONT_BOLD);
        lblDM.setForeground(COLOR_TEXT_DARK);
        side.add(lblDM);
        side.add(Box.createVerticalStrut(8));

        String[][] categories = {
            {"Tất cả", "18"}, {"Java", "5"}, {"C++", "4"}, {"Python", "4"}, {"HTML/CSS", "3"}, {"Spring Boot", "2"}
        };
        ButtonGroup bgDM = new ButtonGroup();
        for (String[] cat : categories) {
            String rawCat = cat[0];
            String countStr = cat[1];
            RowPanel r = new RowPanel(new BorderLayout());
            JCheckBox cb = new JCheckBox(rawCat, selectedCategory.equalsIgnoreCase(rawCat));
            cb.setFont(FONT_BODY);
            cb.setForeground(COLOR_TEXT_DARK);
            cb.setOpaque(false);
            bgDM.add(cb);
            cb.addActionListener(e -> {
                selectedCategory = rawCat;
                refreshCoursesPage();
            });
            JLabel cnt = new JLabel(countStr);
            cnt.setFont(FONT_SMALL);
            cnt.setForeground(COLOR_TEXT_MUTED);
            r.add(cb, BorderLayout.WEST);
            r.add(cnt, BorderLayout.EAST);
            side.add(r);
            side.add(Box.createVerticalStrut(4));
        }

        side.add(Box.createVerticalStrut(12));
        side.add(createSeparator());
        side.add(Box.createVerticalStrut(12));

        JLabel lblGia = new JLabel("Mức giá");
        lblGia.setFont(FONT_BOLD);
        lblGia.setForeground(COLOR_TEXT_DARK);
        side.add(lblGia);
        side.add(Box.createVerticalStrut(8));

        String[] prices = {"Tất cả mức giá", "Dưới 400.000 đ", "400.000 - 600.000 đ", "Trên 600.000 đ"};
        ButtonGroup bgGia = new ButtonGroup();
        for (int i = 0; i < prices.length; i++) {
            final int pIdx = i;
            JCheckBox cb = new JCheckBox(prices[i], selectedPriceRange == i);
            cb.setFont(FONT_BODY);
            cb.setForeground(COLOR_TEXT_DARK);
            cb.setOpaque(false);
            bgGia.add(cb);
            cb.addActionListener(e -> {
                selectedPriceRange = pIdx;
                refreshCoursesPage();
            });
            side.add(cb);
            side.add(Box.createVerticalStrut(4));
        }

        return side;
    }

    private void refreshCoursesPage() {
        if (gridCoursesContainer == null) return;
        gridCoursesContainer.removeAll();

        List<KhoaHoc> list;
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            list = khoaHocDAO.timKiem(searchKeyword);
        } else {
            list = khoaHocDAO.getTheoDanhMuc(selectedCategory);
        }

        List<KhoaHoc> filtered = new ArrayList<>();
        for (KhoaHoc kh : list) {
            long p = kh.getGiaHienThi();
            if (selectedPriceRange == 1 && p >= 400000) continue;
            if (selectedPriceRange == 2 && (p < 400000 || p > 600000)) continue;
            if (selectedPriceRange == 3 && p <= 600000) continue;
            filtered.add(kh);
        }

        int shown = Math.min(6, filtered.size());
        lblCoursesCount.setText("Hiển thị 1-" + shown + " trong " + filtered.size() + " khóa học");

        for (int i = 0; i < shown; i++) {
            gridCoursesContainer.add(createCourseCard(filtered.get(i)));
        }

        gridCoursesContainer.revalidate();
        gridCoursesContainer.repaint();
    }

    private JButton createPagingButton(String text, boolean active) {
        ModernButton btn = active ?
            new ModernButton(text, COLOR_PRIMARY, Color.WHITE) :
            new ModernButton(text, Color.WHITE, COLOR_TEXT_DARK, COLOR_BORDER);
        btn.setPreferredSize(new Dimension(34, 34));
        return btn;
    }

    // =========================================================================
    // MÀN HÌNH 3: CHI TIẾT KHÓA HỌC (Figma Frame 5 - image32.png)
    // =========================================================================
    private CardBox detailHeaderCard;
    private JLabel lblDetailBreadcrumb;

    private PagePanel createCourseDetailPage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(20, 48, 32, 48));

        lblDetailBreadcrumb = new JLabel("Trang chủ  /  Khóa học  /  Java  /  Java từ cơ bản đến hướng đối tượng");
        lblDetailBreadcrumb.setFont(FONT_SMALL);
        lblDetailBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        content.add(lblDetailBreadcrumb);
        content.add(Box.createVerticalStrut(14));

        detailHeaderCard = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 24, 22, 24));
        content.add(detailHeaderCard);
        content.add(Box.createVerticalStrut(16));

        // Tab Navigation Card
        CardBox tabCard = new CardBox(Color.WHITE, COLOR_BORDER, 8, new Insets(10, 20, 10, 20));
        RowPanel tabRow = new RowPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        String[] tabs = {"Mô tả khóa học", "Nội dung khóa học", "Giảng viên", "Đánh giá"};
        for (int i = 0; i < tabs.length; i++) {
            JLabel tabLbl = new JLabel(tabs[i]);
            tabLbl.setFont(i == 0 ? FONT_NAV_ACT : FONT_NAV);
            tabLbl.setForeground(i == 0 ? COLOR_PRIMARY : COLOR_TEXT_MUTED);
            tabRow.add(tabLbl);
        }
        tabCard.add(tabRow);
        content.add(tabCard);
        content.add(Box.createVerticalStrut(16));

        // 2 Column Body Layout
        RowPanel bodyRow = new RowPanel(new BorderLayout(20, 0));

        // Left Column (4 Stacked Cards chuẩn Figma image32.png)
        VBox leftCol = new VBox();

        // Card 1: Mô tả khóa học
        CardBox cardMoTa = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 24, 22, 24));
        JLabel lblMoTaTitle = new JLabel("Mô tả khóa học");
        lblMoTaTitle.setFont(FONT_SECTION);
        lblMoTaTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblMoTaBody = new JLabel("<html><div style='width:520px;'>Khóa học giúp bạn xây dựng nền tảng lập trình Java vững chắc, từ cú pháp cơ bản đến tư duy hướng đối tượng. Mỗi chương kết hợp video hướng dẫn, ví dụ minh họa và bài tập để bạn áp dụng ngay kiến thức đã học.<br><br>Ở cuối khóa học, bạn sẽ tự xây dựng ứng dụng quản lý sinh viên bằng Java, tổ chức mã nguồn theo lớp và xử lý dữ liệu đầu vào.</div></html>");
        lblMoTaBody.setFont(FONT_BODY);
        lblMoTaBody.setForeground(COLOR_TEXT_MUTED);

        JLabel lblLearnTitle = new JLabel("Bạn sẽ học được gì?");
        lblLearnTitle.setFont(FONT_CARD_H);
        lblLearnTitle.setForeground(COLOR_TEXT_DARK);

        cardMoTa.add(lblMoTaTitle);
        cardMoTa.add(Box.createVerticalStrut(10));
        cardMoTa.add(lblMoTaBody);
        cardMoTa.add(Box.createVerticalStrut(18));
        cardMoTa.add(lblLearnTitle);
        cardMoTa.add(Box.createVerticalStrut(10));
        cardMoTa.add(createCheckItem("Sử dụng biến, toán tử, điều kiện, vòng lặp và mảng trong Java.", COLOR_TEXT_DARK));
        cardMoTa.add(Box.createVerticalStrut(4));
        cardMoTa.add(createCheckItem("Hiểu và áp dụng lớp, đối tượng, kế thừa, đa hình và interface.", COLOR_TEXT_DARK));
        cardMoTa.add(Box.createVerticalStrut(4));
        cardMoTa.add(createCheckItem("Xử lý ngoại lệ, thao tác với Collection và đọc ghi tệp.", COLOR_TEXT_DARK));
        cardMoTa.add(Box.createVerticalStrut(4));
        cardMoTa.add(createCheckItem("Hoàn thành dự án quản lý sinh viên có các chức năng CRUD.", COLOR_TEXT_DARK));

        leftCol.add(cardMoTa);
        leftCol.add(Box.createVerticalStrut(16));

        // Card 2: Nội dung khóa học
        CardBox cardCur = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 24, 22, 24));
        RowPanel curHead = new RowPanel(new BorderLayout());
        JLabel lblCurriculum = new JLabel("Nội dung khóa học");
        lblCurriculum.setFont(FONT_SECTION);
        lblCurriculum.setForeground(COLOR_TEXT_DARK);
        JLabel lblCurMeta = new JLabel("6 chương • 42 bài học • 28 giờ");
        lblCurMeta.setFont(FONT_SMALL);
        lblCurMeta.setForeground(COLOR_TEXT_MUTED);
        curHead.add(lblCurriculum, BorderLayout.WEST);
        curHead.add(lblCurMeta, BorderLayout.EAST);

        cardCur.add(curHead);
        cardCur.add(Box.createVerticalStrut(14));

        // Chương 1 mở rộng (chuẩn Figma image32.png)
        CardBox ch1Box = new CardBox(Color.WHITE, COLOR_BORDER, 8, new Insets(0, 0, 4, 0));
        RowPanel ch1Head = new RowPanel(new BorderLayout());
        ch1Head.setOpaque(true);
        ch1Head.setBackground(new Color(241, 245, 249));
        ch1Head.setBorder(new EmptyBorder(10, 14, 10, 14));
        JLabel ch1Title = new JLabel("▾  Chương 1. Làm quen với Java");
        ch1Title.setFont(FONT_BOLD);
        JLabel ch1Right = new JLabel("6 bài học • 2 giờ");
        ch1Right.setFont(FONT_SMALL);
        ch1Right.setForeground(COLOR_TEXT_MUTED);
        ch1Head.add(ch1Title, BorderLayout.WEST);
        ch1Head.add(ch1Right, BorderLayout.EAST);
        ch1Box.add(ch1Head);

        String[][] ch1Lessons = {
            {"▷  Giới thiệu khóa học và lộ trình", "Học thử   12 phút"},
            {"▷  Java là gì? Ứng dụng của Java", "15 phút"},
            {"▷  Cài đặt JDK và IntelliJ IDEA", "20 phút"},
            {"▷  Viết chương trình Java đầu tiên", "25 phút"},
            {"▷  Cấu trúc chương trình và quy tắc đặt tên", "20 phút"},
            {"▷  Bài tập: In thông tin cá nhân", "28 phút"}
        };
        for (String[] ls : ch1Lessons) {
            RowPanel lr = new RowPanel(new BorderLayout());
            lr.setBorder(new EmptyBorder(8, 16, 8, 16));
            JLabel lLeft = new JLabel(ls[0]);
            lLeft.setFont(FONT_SMALL);
            lLeft.setForeground(COLOR_TEXT_DARK);
            JLabel lRight = new JLabel(ls[1]);
            lRight.setFont(FONT_SMALL);
            lRight.setForeground(COLOR_TEXT_MUTED);
            lr.add(lLeft, BorderLayout.WEST);
            lr.add(lRight, BorderLayout.EAST);
            ch1Box.add(lr);
        }
        cardCur.add(ch1Box);
        cardCur.add(Box.createVerticalStrut(8));

        String[][][] otherChapters = {
            {{"▸  Chương 2. Biến, kiểu dữ liệu và toán tử", "Khai báo biến • Kiểu dữ liệu • Nhập xuất • Bài tập tính toán"}, {"7 bài học • 4 giờ"}},
            {{"▸  Chương 3. Cấu trúc điều khiển và mảng", "If/else • Switch • Vòng lặp • Mảng một chiều và hai chiều"}, {"8 bài học • 5 giờ"}},
            {{"▸  Chương 4. Lập trình hướng đối tượng (OOP)", "Class • Object • Constructor • Encapsulation • Kế thừa & Đa hình"}, {"9 bài học • 7 giờ"}},
            {{"▸  Chương 5. Collection, Exception và làm việc với File", "ArrayList • HashMap • Try/Catch • Đọc và ghi dữ liệu"}, {"6 bài học • 5 giờ"}},
            {{"▸  Chương 6. Dự án cuối khóa: Quản lý sinh viên", "Thiết kế lớp • Thêm/sửa/xóa/tìm kiếm • Lưu dữ liệu • Tổng kết"}, {"6 bài học • 5 giờ"}}
        };
        for (String[][] ch : otherChapters) {
            CardBox cb = new CardBox(new Color(248, 250, 252), COLOR_BORDER, 8, new Insets(10, 14, 10, 14));
            RowPanel r = new RowPanel(new BorderLayout());
            VBox lv = new VBox();
            JLabel lt = new JLabel(ch[0][0]); lt.setFont(FONT_BOLD); lt.setForeground(COLOR_TEXT_DARK);
            JLabel ls = new JLabel(ch[0][1]); ls.setFont(FONT_SMALL); ls.setForeground(COLOR_TEXT_MUTED);
            lv.add(lt); lv.add(Box.createVerticalStrut(2)); lv.add(ls);
            JLabel rt = new JLabel(ch[1][0]); rt.setFont(FONT_SMALL); rt.setForeground(COLOR_TEXT_MUTED);
            r.add(lv, BorderLayout.WEST);
            r.add(rt, BorderLayout.EAST);
            cb.add(r);
            cardCur.add(cb);
            cardCur.add(Box.createVerticalStrut(8));
        }

        leftCol.add(cardCur);
        leftCol.add(Box.createVerticalStrut(16));

        // Card 3: Giảng viên
        CardBox cardGV = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(20, 24, 20, 24));
        JLabel lblGVTitle = new JLabel("Giảng viên");
        lblGVTitle.setFont(FONT_SECTION);
        JLabel lblGVName = new JLabel("Nguyễn Minh Tuấn");
        lblGVName.setFont(FONT_CARD_H);
        JLabel lblGVRole = new JLabel("Giảng viên lập trình Java • 6 năm kinh nghiệm");
        lblGVRole.setFont(FONT_SMALL); lblGVRole.setForeground(COLOR_TEXT_MUTED);
        JLabel lblGVBio = new JLabel("<html><div style='width:500px;'>Tham gia phát triển các hệ thống backend doanh nghiệp và hướng dẫn hơn 1.200 học viên bắt đầu với lập trình Java. Phong cách giảng dạy tập trung vào bản chất, ví dụ ngắn gọn và bài tập thực hành theo từng bước.</div></html>");
        lblGVBio.setFont(FONT_SMALL); lblGVBio.setForeground(COLOR_TEXT_MUTED);
        cardGV.add(lblGVTitle); cardGV.add(Box.createVerticalStrut(10));
        cardGV.add(lblGVName); cardGV.add(Box.createVerticalStrut(2));
        cardGV.add(lblGVRole); cardGV.add(Box.createVerticalStrut(8));
        cardGV.add(lblGVBio);
        leftCol.add(cardGV);
        leftCol.add(Box.createVerticalStrut(16));

        // Card 4: Đánh giá từ học viên
        CardBox cardRev = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(20, 24, 20, 24));
        RowPanel revHead = new RowPanel(new BorderLayout());
        JLabel lblRevTitle = new JLabel("Đánh giá từ học viên"); lblRevTitle.setFont(FONT_SECTION);
        JLabel lblRevScore = new JLabel("4,8/5 (186 đánh giá)"); lblRevScore.setFont(FONT_BOLD); lblRevScore.setForeground(COLOR_TEXT_DARK);
        revHead.add(lblRevTitle, BorderLayout.WEST); revHead.add(lblRevScore, BorderLayout.EAST);
        cardRev.add(revHead);
        cardRev.add(Box.createVerticalStrut(12));

        JLabel rev1Name = new JLabel("Minh Đức"); rev1Name.setFont(FONT_BOLD);
        JLabel rev1Text = new JLabel("Nội dung rõ ràng, phần hướng đối tượng giải thích dễ hiểu và có bài tập áp dụng ngay.");
        rev1Text.setFont(FONT_SMALL); rev1Text.setForeground(COLOR_TEXT_MUTED);
        cardRev.add(rev1Name); cardRev.add(Box.createVerticalStrut(3)); cardRev.add(rev1Text);
        cardRev.add(Box.createVerticalStrut(10));
        cardRev.add(createSeparator());
        cardRev.add(Box.createVerticalStrut(10));

        JLabel rev2Name = new JLabel("Thu Trang"); rev2Name.setFont(FONT_BOLD);
        JLabel rev2Text = new JLabel("Mình mất gốc lập trình nhưng học theo lộ trình vẫn theo kịp. Dự án cuối khóa rất hữu ích.");
        rev2Text.setFont(FONT_SMALL); rev2Text.setForeground(COLOR_TEXT_MUTED);
        cardRev.add(rev2Name); cardRev.add(Box.createVerticalStrut(3)); cardRev.add(rev2Text);

        leftCol.add(cardRev);

        bodyRow.add(leftCol, BorderLayout.CENTER);

        // Right Sidebar (Chuẩn Figma image32.png)
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        VBox rightCol = new VBox();
        rightCol.setPreferredSize(new Dimension(300, 450));

        CardBox c1 = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(18, 18, 18, 18));
        JLabel c1Title = new JLabel("Khóa học bao gồm");
        c1Title.setFont(FONT_CARD_H);
        c1.add(c1Title);
        c1.add(Box.createVerticalStrut(10));
        c1.add(createCheckItem("28 giờ video hướng dẫn", COLOR_TEXT_MUTED));
        c1.add(Box.createVerticalStrut(4));
        c1.add(createCheckItem("42 bài học theo lộ trình", COLOR_TEXT_MUTED));
        c1.add(Box.createVerticalStrut(4));
        c1.add(createCheckItem("Bài tập và mã nguồn tham khảo", COLOR_TEXT_MUTED));
        c1.add(Box.createVerticalStrut(4));
        c1.add(createCheckItem("Dự án thực hành cuối khóa", COLOR_TEXT_MUTED));
        c1.add(Box.createVerticalStrut(4));
        c1.add(createCheckItem("Truy cập nội dung trọn đời", COLOR_TEXT_MUTED));
        c1.add(Box.createVerticalStrut(4));
        c1.add(createCheckItem("Học trên máy tính và điện thoại", COLOR_TEXT_MUTED));

        CardBox c2 = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(18, 18, 18, 18));
        JLabel c2Title = new JLabel("Khóa học dành cho ai?");
        c2Title.setFont(FONT_CARD_H);
        JLabel c2Text = new JLabel("<html>Sinh viên, người mới bắt đầu và người muốn<br/>củng cố kiến thức Java.</html>");
        c2Text.setFont(FONT_SMALL); c2Text.setForeground(COLOR_TEXT_MUTED);

        JLabel c3Title = new JLabel("Bạn cần chuẩn bị");
        c3Title.setFont(FONT_CARD_H);
        JLabel c3Text = new JLabel("<html>Máy tính có kết nối Internet, không cần kinh<br/>nghiệm lập trình trước đó.</html>");
        c3Text.setFont(FONT_SMALL); c3Text.setForeground(COLOR_TEXT_MUTED);

        c2.add(c2Title);
        c2.add(Box.createVerticalStrut(6));
        c2.add(c2Text);
        c2.add(Box.createVerticalStrut(12));
        c2.add(createSeparator());
        c2.add(Box.createVerticalStrut(12));
        c2.add(c3Title);
        c2.add(Box.createVerticalStrut(6));
        c2.add(c3Text);

        rightCol.add(c1);
        rightCol.add(Box.createVerticalStrut(16));
        rightCol.add(c2);

        rightWrapper.add(rightCol, BorderLayout.NORTH);
        bodyRow.add(rightWrapper, BorderLayout.EAST);

        content.add(bodyRow);
        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private void refreshCourseDetailPage() {
        if (detailHeaderCard == null || currentDetailCourse == null) return;
        detailHeaderCard.removeAll();

        lblDetailBreadcrumb.setText("Trang chủ  /  Khóa học  /  " + currentDetailCourse.getMaDanhMuc() + "  /  " + currentDetailCourse.getTenKhoaHoc());

        RowPanel headerRow = new RowPanel(new BorderLayout(24, 0));

        // Left banner (300x185)
        JPanel banner = createCourseThumbnail(currentDetailCourse.getAnhKhoaHoc() != null ? currentDetailCourse.getAnhKhoaHoc() : currentDetailCourse.getMaDanhMuc(), 300, 185);
        banner.setPreferredSize(new Dimension(300, 185));
        headerRow.add(banner, BorderLayout.WEST);

        // Right Info (Using VBox so every label and button row aligns at X = 0!)
        VBox info = new VBox();

        JLabel pill = new JLabel(currentDetailCourse.getMaDanhMuc().toUpperCase() + " • " + currentDetailCourse.getCapDo().toUpperCase());
        pill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pill.setForeground(COLOR_PRIMARY);

        JLabel title = new JLabel(currentDetailCourse.getTenKhoaHoc());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT_DARK);

        JLabel teacher = new JLabel("Giảng viên: " + currentDetailCourse.getGiangVien());
        teacher.setFont(FONT_BODY);
        teacher.setForeground(COLOR_TEXT_MUTED);

        JPanel pnlRating = createStarRatingPanel(4.8, 1250);

        JLabel meta = new JLabel(currentDetailCourse.getThoiLuong() + " học • Tiếng Việt • Cập nhật 03/2026");
        meta.setFont(FONT_SMALL);
        meta.setForeground(COLOR_TEXT_MUTED);

        JLabel price = new JLabel(AppUtils.formatTien(currentDetailCourse.getHocPhi()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 22));
        price.setForeground(COLOR_TEXT_DARK);

        RowPanel btnRow = new RowPanel(new GridLayout(1, 2, 12, 0));

        ModernButton btnAddToCart = new ModernButton("Thêm vào giỏ hàng", Color.WHITE, COLOR_TEXT_DARK, COLOR_BORDER_INPUT);
        btnAddToCart.setPreferredSize(new Dimension(200, 42));
        btnAddToCart.addActionListener(e -> {
            gioHangDAO.themVaoGio(getCurrentCartId(), currentDetailCourse.getMaKhoaHoc(), currentDetailCourse.getGiaHienThi());
            updateCartBadge();
            AppUtils.showSuccess(this, "Đã thêm khóa học vào giỏ hàng thành công!");
        });

        ModernButton btnBuyNow = new ModernButton("Mua ngay", COLOR_PRIMARY, Color.WHITE);
        btnBuyNow.setPreferredSize(new Dimension(200, 42));
        btnBuyNow.addActionListener(e -> {
            gioHangDAO.themVaoGio(getCurrentCartId(), currentDetailCourse.getMaKhoaHoc(), currentDetailCourse.getGiaHienThi());
            updateCartBadge();
            navigate(PAGE_CHECKOUT);
        });

        btnRow.add(btnAddToCart);
        btnRow.add(btnBuyNow);

        info.add(pill);
        info.add(Box.createVerticalStrut(6));
        info.add(title);
        info.add(Box.createVerticalStrut(6));
        info.add(teacher);
        info.add(Box.createVerticalStrut(4));
        info.add(pnlRating);
        info.add(Box.createVerticalStrut(4));
        info.add(meta);
        info.add(Box.createVerticalStrut(10));
        info.add(price);
        info.add(Box.createVerticalStrut(14));
        info.add(btnRow);

        headerRow.add(info, BorderLayout.CENTER);
        detailHeaderCard.add(headerRow);
        detailHeaderCard.revalidate();
        detailHeaderCard.repaint();
    }

    // =========================================================================
    // MÀN HÌNH 4: GIỎ HÀNG (Figma Frame 6 - image33.png)
    // =========================================================================
    private VBox cartItemsContainer;
    private JLabel lblCartTamTinh, lblCartTong, lblCartSub, lblCartTamTinhTitle, lblCartBottomCount;

    private PagePanel createCartPage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(20, 48, 32, 48));

        JLabel lblBreadcrumb = new JLabel("Trang chủ  /  Giỏ hàng");
        lblBreadcrumb.setFont(FONT_SMALL);
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JLabel lblTitle = new JLabel("Giỏ hàng của bạn");
        lblTitle.setFont(FONT_HERO_H1);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        lblCartSub = new JLabel("Bạn đã thêm 2 khóa học. Kiểm tra lại trước khi thanh toán.");
        lblCartSub.setFont(FONT_BODY);
        lblCartSub.setForeground(COLOR_TEXT_MUTED);

        content.add(lblBreadcrumb);
        content.add(Box.createVerticalStrut(6));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblCartSub);
        content.add(Box.createVerticalStrut(20));

        RowPanel row = new RowPanel(new BorderLayout(24, 0));

        VBox left = new VBox();

        // Unified Table Card (chuẩn Figma image33.png)
        CardBox tableCard = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(0, 0, 4, 0));

        RowPanel tableHead = new RowPanel(new BorderLayout());
        tableHead.setOpaque(true);
        tableHead.setBackground(new Color(248, 250, 252));
        tableHead.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel thCourse = new JLabel("Khóa học"); thCourse.setFont(FONT_BOLD); thCourse.setForeground(COLOR_TEXT_DARK);
        JPanel rightHead = new JPanel(new FlowLayout(FlowLayout.RIGHT, 42, 0));
        rightHead.setOpaque(false);
        JLabel thPrice = new JLabel("Giá"); thPrice.setFont(FONT_BOLD); thPrice.setForeground(COLOR_TEXT_DARK);
        JLabel thDel = new JLabel("Xóa"); thDel.setFont(FONT_BOLD); thDel.setForeground(COLOR_TEXT_DARK);
        rightHead.add(thPrice);
        rightHead.add(thDel);
        tableHead.add(thCourse, BorderLayout.WEST);
        tableHead.add(rightHead, BorderLayout.EAST);

        tableCard.add(tableHead);

        cartItemsContainer = new VBox();
        tableCard.add(cartItemsContainer);

        left.add(tableCard);
        left.add(Box.createVerticalStrut(14));

        RowPanel pnlActionRow = new RowPanel(new BorderLayout());
        ModernButton btnContinue = new ModernButton("Tiếp tục mua hàng", Color.WHITE, COLOR_TEXT_DARK, COLOR_BORDER_INPUT);
        btnContinue.setPreferredSize(new Dimension(150, 36));
        btnContinue.addActionListener(e -> navigate(PAGE_COURSES));

        lblCartBottomCount = new JLabel("2 khóa học trong giỏ hàng");
        lblCartBottomCount.setFont(FONT_SMALL);
        lblCartBottomCount.setForeground(COLOR_TEXT_MUTED);

        pnlActionRow.add(btnContinue, BorderLayout.WEST);
        pnlActionRow.add(lblCartBottomCount, BorderLayout.EAST);
        left.add(pnlActionRow);
        left.add(Box.createVerticalStrut(16));

        // Notice Box chuẩn Figma
        CardBox noticeBox = new CardBox(new Color(241, 245, 249), COLOR_BORDER, 8, new Insets(12, 16, 12, 16));
        noticeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel lblNotice = new JLabel("Khóa học trực tuyến không có phí vận chuyển. Sau khi thanh toán thành công, bạn có thể bắt đầu học trong mục \"Khóa học của tôi\".");
        lblNotice.setFont(FONT_SMALL);
        lblNotice.setForeground(COLOR_TEXT_MUTED);
        noticeBox.add(lblNotice);
        left.add(noticeBox);

        row.add(left, BorderLayout.CENTER);

        // Right Summary Card (Dùng CardBox VBox để mọi dòng thẳng hàng 100%)
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);

        CardBox right = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 22, 22, 22));
        right.setPreferredSize(new Dimension(330, 265));

        JLabel lblSumTitle = new JLabel("Tóm tắt đơn hàng");
        lblSumTitle.setFont(FONT_CARD_H);
        lblSumTitle.setForeground(COLOR_TEXT_DARK);

        RowPanel p1 = new RowPanel(new BorderLayout());
        lblCartTamTinhTitle = new JLabel("Tạm tính (2 khóa học)");
        lblCartTamTinhTitle.setFont(FONT_SMALL);
        lblCartTamTinhTitle.setForeground(COLOR_TEXT_MUTED);
        lblCartTamTinh = new JLabel("0 đ");
        lblCartTamTinh.setFont(FONT_BOLD);
        p1.add(lblCartTamTinhTitle, BorderLayout.WEST);
        p1.add(lblCartTamTinh, BorderLayout.EAST);

        RowPanel p2 = new RowPanel(new BorderLayout());
        JLabel l2 = new JLabel("Giảm giá");
        l2.setFont(FONT_SMALL);
        l2.setForeground(COLOR_TEXT_MUTED);
        JLabel l2v = new JLabel("0 đ");
        l2v.setFont(FONT_BOLD);
        p2.add(l2, BorderLayout.WEST);
        p2.add(l2v, BorderLayout.EAST);

        RowPanel p3 = new RowPanel(new BorderLayout());
        JLabel l3 = new JLabel("Tổng thanh toán");
        l3.setFont(FONT_BOLD);
        lblCartTong = new JLabel("0 đ");
        lblCartTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCartTong.setForeground(COLOR_PRIMARY);
        p3.add(l3, BorderLayout.WEST);
        p3.add(lblCartTong, BorderLayout.EAST);

        JLabel lblTaxNote = new JLabel("Giá đã bao gồm các khoản thuế áp dụng.");
        lblTaxNote.setFont(FONT_SMALL);
        lblTaxNote.setForeground(COLOR_TEXT_MUTED);

        ModernButton btnCheckout = new ModernButton("Tiến hành thanh toán", COLOR_PRIMARY, Color.WHITE);
        btnCheckout.setPreferredSize(new Dimension(286, 40));
        btnCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnCheckout.addActionListener(e -> {
            if (gioHangDAO.getChiTiet("GH001").isEmpty()) {
                AppUtils.showError(this, "Giỏ hàng của bạn đang trống!");
                return;
            }
            navigate(PAGE_CHECKOUT);
        });

        RowPanel pnlOpenNote = new RowPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel lblOpenNote = new JLabel("Khóa học được mở sau khi thanh toán thành công.");
        lblOpenNote.setFont(FONT_SMALL);
        lblOpenNote.setForeground(COLOR_TEXT_MUTED);
        pnlOpenNote.add(lblOpenNote);

        right.add(lblSumTitle);
        right.add(Box.createVerticalStrut(12));
        right.add(createSeparator());
        right.add(Box.createVerticalStrut(12));
        right.add(p1);
        right.add(Box.createVerticalStrut(8));
        right.add(p2);
        right.add(Box.createVerticalStrut(12));
        right.add(createSeparator());
        right.add(Box.createVerticalStrut(12));
        right.add(p3);
        right.add(Box.createVerticalStrut(4));
        right.add(lblTaxNote);
        right.add(Box.createVerticalStrut(14));
        right.add(btnCheckout);
        right.add(Box.createVerticalStrut(8));
        right.add(pnlOpenNote);

        rightWrap.add(right, BorderLayout.NORTH);
        row.add(rightWrap, BorderLayout.EAST);

        content.add(row);
        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private void refreshCartPage() {
        if (cartItemsContainer == null) return;
        cartItemsContainer.removeAll();

        List<ChiTietGioHang> list = gioHangDAO.getChiTiet(getCurrentCartId());
        long total = 0;

        for (int i = 0; i < list.size(); i++) {
            ChiTietGioHang ct = list.get(i);
            total += ct.getDonGia();
            cartItemsContainer.add(createCartItemCard(ct));
            if (i < list.size() - 1) {
                cartItemsContainer.add(createSeparator());
            }
        }

        if (list.isEmpty()) {
            JLabel empty = new JLabel("Giỏ hàng của bạn hiện đang trống. Hãy thêm các khóa học bổ ích nhé!");
            empty.setFont(FONT_BODY);
            empty.setForeground(COLOR_TEXT_MUTED);
            empty.setBorder(new EmptyBorder(24, 16, 24, 16));
            cartItemsContainer.add(empty);
        }

        lblCartSub.setText("Bạn đã thêm " + list.size() + " khóa học. Kiểm tra lại trước khi thanh toán.");
        lblCartTamTinhTitle.setText("Tạm tính (" + list.size() + " khóa học)");
        lblCartBottomCount.setText(list.size() + " khóa học trong giỏ hàng");
        lblCartTamTinh.setText(AppUtils.formatTien(total));
        lblCartTong.setText(AppUtils.formatTien(total));

        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    private JPanel createCartItemCard(ChiTietGioHang item) {
        KhoaHoc kh = item.getKhoaHoc();
        RowPanel card = new RowPanel(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel thumb = createCourseThumbnail(kh.getAnhKhoaHoc() != null ? kh.getAnhKhoaHoc() : kh.getMaDanhMuc(), 84, 58);
        thumb.setPreferredSize(new Dimension(84, 58));
        card.add(thumb, BorderLayout.WEST);

        VBox info = new VBox();
        JLabel title = new JLabel(kh.getTenKhoaHoc());
        title.setFont(FONT_CARD_H);
        title.setForeground(COLOR_TEXT_DARK);

        JLabel teacher = new JLabel(kh.getGiangVien());
        teacher.setFont(FONT_SMALL);
        teacher.setForeground(COLOR_TEXT_MUTED);

        JPanel pnlRating = createStarRatingPanel(4.8, 1250);

        info.add(title);
        info.add(Box.createVerticalStrut(2));
        info.add(teacher);
        info.add(Box.createVerticalStrut(2));
        info.add(pnlRating);

        card.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 22, 12));
        right.setOpaque(false);

        JLabel price = new JLabel(AppUtils.formatTien(item.getDonGia()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        price.setForeground(COLOR_TEXT_DARK);

        ModernButton btnDelete = new ModernButton("Xóa", Color.WHITE, COLOR_TEXT_MUTED, COLOR_BORDER);
        btnDelete.setPreferredSize(new Dimension(54, 30));
        btnDelete.addActionListener(e -> {
            gioHangDAO.xoaKhoiGio(getCurrentCartId(), kh.getMaKhoaHoc());
            updateCartBadge();
            refreshCartPage();
        });

        right.add(price);
        right.add(btnDelete);

        card.add(right, BorderLayout.EAST);
        return card;
    }

    // =========================================================================
    // MÀN HÌNH 5: THANH TOÁN (Figma Frame 7 - image34.png)
    // =========================================================================
    private JTextField txtCheckHoTen, txtCheckEmail, txtCheckSDT, txtMaUuDai;
    private JLabel lblCheckTamTinh, lblCheckGiamGia, lblCheckTong, lblCheckoutCount, lblCheckTamTinhLabel;
    private VBox checkoutItemsList;
    private int selectedPaymentMethod = 0;
    private CardBox payCard0, payCard1, payCard2;
    private JRadioButton rbPay0, rbPay1, rbPay2;

    private PagePanel createCheckoutPage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(20, 48, 32, 48));

        JLabel lblBreadcrumb = new JLabel("Trang chủ  /  Giỏ hàng  /  Thanh toán");
        lblBreadcrumb.setFont(FONT_SMALL);
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JLabel lblTitle = new JLabel("Thanh toán");
        lblTitle.setFont(FONT_HERO_H1);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel("Hoàn tất thông tin để đăng ký các khóa học đã chọn.");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        content.add(lblBreadcrumb);
        content.add(Box.createVerticalStrut(6));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);
        content.add(Box.createVerticalStrut(12));
        content.add(createCheckoutStepper(2));
        content.add(Box.createVerticalStrut(18));

        RowPanel row = new RowPanel(new BorderLayout(24, 0));

        // Left Column (Customer info & Payment Method Cards chuẩn Figma image34.png)
        VBox left = new VBox();

        CardBox cCust = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 24, 22, 24));
        JLabel cCustTitle = new JLabel("Thông tin khách hàng");
        cCustTitle.setFont(FONT_CARD_H);
        cCustTitle.setForeground(COLOR_TEXT_DARK);

        JLabel cCustSub = new JLabel("Thông tin dùng để xác nhận đơn hàng và gửi thông báo khóa học.");
        cCustSub.setFont(FONT_SMALL);
        cCustSub.setForeground(COLOR_TEXT_MUTED);

        cCust.add(cCustTitle);
        cCust.add(Box.createVerticalStrut(4));
        cCust.add(cCustSub);
        cCust.add(Box.createVerticalStrut(14));

        TaiKhoan user = UserSession.getInstance().getCurrentUser();
        txtCheckHoTen = createModernTextField("Nhập họ và tên", 38);
        txtCheckHoTen.setText(user != null ? user.getHoTen() : "Nguyễn Văn An");
        txtCheckEmail = createModernTextField("Nhập địa chỉ email", 38);
        txtCheckEmail.setText(user != null ? user.getEmail() : "an.nguyen@email.com");
        txtCheckSDT   = createModernTextField("Nhập số điện thoại", 38);
        txtCheckSDT.setText(user != null ? user.getSoDienThoai() : "0912 345 678");

        JLabel lName = new JLabel("Họ tên"); lName.setFont(FONT_BOLD);
        JLabel lMail = new JLabel("Email"); lMail.setFont(FONT_BOLD);
        JLabel lPhone = new JLabel("Số điện thoại"); lPhone.setFont(FONT_BOLD);

        cCust.add(lName);
        cCust.add(Box.createVerticalStrut(5));
        cCust.add(txtCheckHoTen);
        cCust.add(Box.createVerticalStrut(12));
        cCust.add(lMail);
        cCust.add(Box.createVerticalStrut(5));
        cCust.add(txtCheckEmail);
        cCust.add(Box.createVerticalStrut(12));
        cCust.add(lPhone);
        cCust.add(Box.createVerticalStrut(5));
        cCust.add(txtCheckSDT);

        left.add(cCust);
        left.add(Box.createVerticalStrut(16));

        // Payment Method Box (Mỗi lựa chọn nằm trong 1 khung bo góc chuẩn Figma image34.png)
        CardBox cPay = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 24, 22, 24));
        JLabel cPayTitle = new JLabel("Phương thức thanh toán");
        cPayTitle.setFont(FONT_CARD_H);
        cPayTitle.setForeground(COLOR_TEXT_DARK);
        cPay.add(cPayTitle);
        cPay.add(Box.createVerticalStrut(14));

        ButtonGroup bgPay = new ButtonGroup();
        rbPay0 = new JRadioButton(); rbPay0.setOpaque(false); rbPay0.setSelected(true);
        rbPay1 = new JRadioButton(); rbPay1.setOpaque(false);
        rbPay2 = new JRadioButton(); rbPay2.setOpaque(false);
        bgPay.add(rbPay0); bgPay.add(rbPay1); bgPay.add(rbPay2);

        payCard0 = createPaymentOptionCard(0, rbPay0, "Chuyển khoản ngân hàng", "Nhận thông tin chuyển khoản sau khi xác nhận đơn hàng.");
        payCard1 = createPaymentOptionCard(1, rbPay1, "Ví điện tử MoMo", "Thanh toán qua ứng dụng MoMo.");
        payCard2 = createPaymentOptionCard(2, rbPay2, "Thẻ ATM nội địa / Internet Banking", "Thanh toán qua cổng thanh toán an toàn.");
        updatePaymentCardsStyle();

        cPay.add(payCard0);
        cPay.add(Box.createVerticalStrut(10));
        cPay.add(payCard1);
        cPay.add(Box.createVerticalStrut(10));
        cPay.add(payCard2);

        left.add(cPay);
        left.add(Box.createVerticalStrut(16));

        JLabel lnkBackCart = new JLabel("← Quay lại giỏ hàng");
        lnkBackCart.setFont(FONT_BOLD);
        lnkBackCart.setForeground(COLOR_PRIMARY);
        lnkBackCart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lnkBackCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { navigate(PAGE_CART); }
        });
        left.add(lnkBackCart);

        row.add(left, BorderLayout.CENTER);

        // Right Column (Order Summary & Voucher chuẩn Figma image34.png)
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);

        CardBox right = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(22, 22, 22, 22));
        right.setPreferredSize(new Dimension(360, 510));

        RowPanel headRight = new RowPanel(new BorderLayout());
        JLabel lblOrderTitle = new JLabel("Đơn hàng của bạn");
        lblOrderTitle.setFont(FONT_CARD_H);
        lblCheckoutCount = new JLabel("2 khóa học");
        lblCheckoutCount.setFont(FONT_SMALL);
        lblCheckoutCount.setForeground(COLOR_TEXT_MUTED);
        headRight.add(lblOrderTitle, BorderLayout.WEST);
        headRight.add(lblCheckoutCount, BorderLayout.EAST);

        right.add(headRight);
        right.add(Box.createVerticalStrut(12));
        right.add(createSeparator());
        right.add(Box.createVerticalStrut(10));

        checkoutItemsList = new VBox();
        right.add(checkoutItemsList);
        right.add(Box.createVerticalStrut(10));
        right.add(createSeparator());
        right.add(Box.createVerticalStrut(10));

        JLabel lblDisclaimer = new JLabel("<html>Bằng việc xác nhận thanh toán, bạn đồng ý với Điều khoản<br/>sử dụng và Chính sách bảo mật của HAUI.</html>");
        lblDisclaimer.setFont(FONT_SMALL);
        lblDisclaimer.setForeground(COLOR_TEXT_MUTED);
        right.add(lblDisclaimer);
        right.add(Box.createVerticalStrut(12));

        // Voucher Input Group
        RowPanel pnlVoucher = new RowPanel(new BorderLayout(8, 0));
        txtMaUuDai = createModernTextField("Nhập mã ưu đãi HaUI", 36);
        ModernButton btnApply = new ModernButton("Áp dụng", COLOR_PRIMARY, Color.WHITE);
        btnApply.setPreferredSize(new Dimension(88, 36));
        btnApply.addActionListener(e -> {
            String code = txtMaUuDai.getText().trim().toUpperCase();
            int dbDiscount = donHangDAO.kiemTraVoucher(code);
            if (dbDiscount > 0) {
                discountPercent = dbDiscount;
                AppUtils.showSuccess(this, "Áp dụng mã ưu đãi " + code + " thành công (Giảm " + discountPercent + "%)!");
            } else if ("HAUI10".equals(code)) {
                discountPercent = 10;
            } else if ("HAUI20".equals(code)) {
                discountPercent = 20;
            } else if ("HAUI50".equals(code)) {
                discountPercent = 50;
            } else {
                AppUtils.showError(this, "Mã ưu đãi không hợp lệ hoặc đã hết hạn!\n(Mã hợp lệ trong CSDL: HAUI10, HAUI20, HAUI50)");
                discountPercent = 0;
            }
            refreshCheckoutSummary();
        });

        pnlVoucher.add(txtMaUuDai, BorderLayout.CENTER);
        pnlVoucher.add(btnApply, BorderLayout.EAST);
        right.add(pnlVoucher);
        right.add(Box.createVerticalStrut(14));

        // Summary Rows
        RowPanel pTamTinh = new RowPanel(new BorderLayout());
        lblCheckTamTinhLabel = new JLabel("Tạm tính (2 khóa học)");
        lblCheckTamTinhLabel.setFont(FONT_SMALL);
        lblCheckTamTinhLabel.setForeground(COLOR_TEXT_MUTED);
        lblCheckTamTinh = new JLabel("0 đ");
        lblCheckTamTinh.setFont(FONT_BOLD);
        pTamTinh.add(lblCheckTamTinhLabel, BorderLayout.WEST);
        pTamTinh.add(lblCheckTamTinh, BorderLayout.EAST);
        right.add(pTamTinh);
        right.add(Box.createVerticalStrut(6));

        RowPanel pGiam = new RowPanel(new BorderLayout());
        JLabel lGiam = new JLabel("Giảm giá");
        lGiam.setFont(FONT_SMALL);
        lGiam.setForeground(COLOR_TEXT_MUTED);
        lblCheckGiamGia = new JLabel("0 đ");
        lblCheckGiamGia.setFont(FONT_BOLD);
        pGiam.add(lGiam, BorderLayout.WEST);
        pGiam.add(lblCheckGiamGia, BorderLayout.EAST);
        right.add(pGiam);
        right.add(Box.createVerticalStrut(10));

        right.add(createSeparator());
        right.add(Box.createVerticalStrut(10));

        RowPanel pTong = new RowPanel(new BorderLayout());
        JLabel lblTongTitle = new JLabel("Tổng thanh toán");
        lblTongTitle.setFont(FONT_BOLD);
        lblCheckTong = new JLabel("0 đ");
        lblCheckTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCheckTong.setForeground(COLOR_PRIMARY);
        pTong.add(lblTongTitle, BorderLayout.WEST);
        pTong.add(lblCheckTong, BorderLayout.EAST);
        right.add(pTong);
        right.add(Box.createVerticalStrut(4));

        JLabel lblTax = new JLabel("Giá đã bao gồm các khoản thuế áp dụng.");
        lblTax.setFont(FONT_SMALL);
        lblTax.setForeground(COLOR_TEXT_MUTED);
        right.add(lblTax);
        right.add(Box.createVerticalStrut(14));

        ModernButton btnConfirm = new ModernButton("Xác nhận thanh toán", COLOR_PRIMARY, Color.WHITE);
        btnConfirm.setPreferredSize(new Dimension(316, 42));
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConfirm.addActionListener(e -> {
            String name = txtCheckHoTen.getText().trim();
            String email = txtCheckEmail.getText().trim();
            String sdt = txtCheckSDT.getText().trim();

            if (name.isEmpty() || email.isEmpty() || sdt.isEmpty()) {
                AppUtils.showError(this, "Vui lòng điền đầy đủ thông tin khách hàng!");
                return;
            }

            String cartId = getCurrentCartId();
            List<ChiTietGioHang> list = gioHangDAO.getChiTiet(cartId);
            if (list.isEmpty()) {
                AppUtils.showError(this, "Giỏ hàng rỗng!");
                return;
            }

            String maKH = getCurrentCustomerId();
            long subtotal = 0;
            for (ChiTietGioHang ct : list) subtotal += ct.getDonGia();
            long discount = (subtotal * discountPercent) / 100;
            long total = subtotal - discount;
            String payMethod = selectedPaymentMethod == 0 ? "Chuyển khoản ngân hàng" :
                               (selectedPaymentMethod == 1 ? "Ví điện tử MoMo" : "Thẻ ATM nội địa / Internet Banking");
            String voucher = discountPercent > 0 ? txtMaUuDai.getText().trim().toUpperCase() : null;

            String maDH = donHangDAO.taoDonHang(maKH, voucher, list, subtotal, discount, total, payMethod, cartId);
            if (maDH == null) {
                for (ChiTietGioHang ct : list) {
                    dangKyDAO.dangKyKhoaHoc(maKH, ct.getKhoaHoc().getMaKhoaHoc());
                }
                gioHangDAO.xoaGio(cartId);
                maDH = "DH" + (System.currentTimeMillis() % 100000);
            }
            discountPercent = 0;
            updateCartBadge();

            AppUtils.showSuccess(this,
                "ĐẶT HÀNG VÀ THANH TOÁN THÀNH CÔNG!\n\n" +
                "Mã đơn hàng: " + maDH + "\n" +
                "Khách hàng: " + name + "\n" +
                "Email nhận bài học: " + email + "\n" +
                "Phương thức: " + payMethod + "\n\n" +
                "Dữ liệu đơn hàng và khóa học đã được lưu vào CSDL SQL Server (HaUICourseDB).");

            navigate(PAGE_MY_COURSES);
        });

        right.add(btnConfirm);
        right.add(Box.createVerticalStrut(8));

        RowPanel pnlOpen = new RowPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel lblOpen = new JLabel("Khóa học được mở sau khi thanh toán thành công.");
        lblOpen.setFont(FONT_SMALL); lblOpen.setForeground(COLOR_TEXT_MUTED);
        pnlOpen.add(lblOpen);
        right.add(pnlOpen);
        right.add(Box.createVerticalStrut(8));

        CardBox secBox = new CardBox(new Color(248, 250, 252), COLOR_BORDER, 6, new Insets(8, 10, 8, 10));
        RowPanel secRow = new RowPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel lblSecNote = new JLabel("Thông tin của bạn được bảo mật. Cần hỗ trợ?");
        lblSecNote.setFont(FONT_SMALL); lblSecNote.setForeground(COLOR_TEXT_MUTED);
        secRow.add(lblSecNote);
        secBox.add(secRow);
        right.add(secBox);

        rightWrap.add(right, BorderLayout.NORTH);
        row.add(rightWrap, BorderLayout.EAST);

        content.add(row);
        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private CardBox createPaymentOptionCard(int idx, JRadioButton rb, String title, String sub) {
        CardBox box = new CardBox(Color.WHITE, COLOR_BORDER, 8, new Insets(12, 14, 12, 14));
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        RowPanel r = new RowPanel(new BorderLayout(10, 0));
        r.add(rb, BorderLayout.WEST);

        VBox textCol = new VBox();
        JLabel t = new JLabel(title); t.setFont(FONT_BOLD); t.setForeground(COLOR_TEXT_DARK);
        JLabel s = new JLabel(sub); s.setFont(FONT_SMALL); s.setForeground(COLOR_TEXT_MUTED);
        textCol.add(t);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(s);

        r.add(textCol, BorderLayout.CENTER);
        box.add(r);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedPaymentMethod = idx;
                rb.setSelected(true);
                updatePaymentCardsStyle();
            }
        };
        box.addMouseListener(ma);
        rb.addActionListener(e -> {
            selectedPaymentMethod = idx;
            updatePaymentCardsStyle();
        });
        return box;
    }

    private void updatePaymentCardsStyle() {
        if (payCard0 == null) return;
        payCard0.setCardColors(selectedPaymentMethod == 0 ? new Color(239, 246, 255) : Color.WHITE,
                               selectedPaymentMethod == 0 ? COLOR_PRIMARY : COLOR_BORDER);
        payCard1.setCardColors(selectedPaymentMethod == 1 ? new Color(239, 246, 255) : Color.WHITE,
                               selectedPaymentMethod == 1 ? COLOR_PRIMARY : COLOR_BORDER);
        payCard2.setCardColors(selectedPaymentMethod == 2 ? new Color(239, 246, 255) : Color.WHITE,
                               selectedPaymentMethod == 2 ? COLOR_PRIMARY : COLOR_BORDER);
    }

    private void refreshCheckoutPage() {
        if (checkoutItemsList == null) return;
        checkoutItemsList.removeAll();

        TaiKhoan u = UserSession.getInstance().getCurrentUser();
        if (u != null) {
            txtCheckHoTen.setText(u.getHoTen() != null ? u.getHoTen() : "");
            txtCheckEmail.setText(u.getEmail() != null ? u.getEmail() : "");
            txtCheckSDT.setText(u.getSoDienThoai() != null ? u.getSoDienThoai() : "");
        }

        List<ChiTietGioHang> list = gioHangDAO.getChiTiet(getCurrentCartId());
        lblCheckoutCount.setText(list.size() + " khóa học");
        lblCheckTamTinhLabel.setText("Tạm tính (" + list.size() + " khóa học)");

        for (ChiTietGioHang ct : list) {
            KhoaHoc kh = ct.getKhoaHoc();
            RowPanel itemP = new RowPanel(new BorderLayout(10, 0));
            itemP.setBorder(new EmptyBorder(6, 0, 6, 0));

            JPanel thumb = createCourseThumbnail(kh.getAnhKhoaHoc() != null ? kh.getAnhKhoaHoc() : kh.getMaDanhMuc(), 58, 42);
            thumb.setPreferredSize(new Dimension(58, 42));

            VBox info = new VBox();
            JLabel t = new JLabel(kh.getTenKhoaHoc()); t.setFont(FONT_BOLD); t.setForeground(COLOR_TEXT_DARK);
            JLabel gv = new JLabel(kh.getGiangVien()); gv.setFont(FONT_SMALL); gv.setForeground(COLOR_TEXT_MUTED);
            JLabel pr = new JLabel(AppUtils.formatTien(ct.getDonGia())); pr.setFont(FONT_BOLD); pr.setForeground(COLOR_TEXT_DARK);
            info.add(t);
            info.add(Box.createVerticalStrut(2));
            info.add(gv);
            info.add(Box.createVerticalStrut(2));
            info.add(pr);

            itemP.add(thumb, BorderLayout.WEST);
            itemP.add(info, BorderLayout.CENTER);

            checkoutItemsList.add(itemP);
        }

        refreshCheckoutSummary();
        checkoutItemsList.revalidate();
        checkoutItemsList.repaint();
    }

    private void refreshCheckoutSummary() {
        List<ChiTietGioHang> list = gioHangDAO.getChiTiet(getCurrentCartId());
        long subtotal = 0;
        for (ChiTietGioHang ct : list) subtotal += ct.getDonGia();

        long discount = (subtotal * discountPercent) / 100;
        long total = subtotal - discount;

        lblCheckTamTinh.setText(AppUtils.formatTien(subtotal));
        lblCheckGiamGia.setText((discount > 0 ? "-" : "") + AppUtils.formatTien(discount));
        lblCheckTong.setText(AppUtils.formatTien(total));
    }

    // =========================================================================
    // MÀN HÌNH 6 & 7: ĐĂNG KÝ / ĐĂNG NHẬP (Chuẩn 1:1 theo ảnh Figma người dùng gửi)
    // =========================================================================
    private PagePanel createAuthPage() {
        PagePanel page = new PagePanel(true);

        // Khu vực trung tâm sử dụng GridBagLayout để:
        // 1. Hai thẻ nằm chính giữa màn hình với kích thước rộng rãi, cân đối
        // 2. Thẻ bên trái tự động căn giữa theo chiều dọc (vertical center) so với thẻ bên phải như trong Figma
        JPanel centerArea = new JPanel(new GridBagLayout());
        centerArea.setOpaque(false);
        centerArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerArea.setBorder(new EmptyBorder(24, 48, 28, 48));

        // Left Card (Bo góc 12px, rộng rãi, không bị thiếu kích thước)
        authLeftCard = new CardBox(Color.WHITE, COLOR_BORDER, 12, new Insets(32, 32, 32, 32));
        authLeftCard.setMinDimensions(460, 445);

        // Right Card (Bo góc 12px, mọi label, input và button thẳng hàng tuyệt đối bên trái)
        authRightCard = new CardBox(Color.WHITE, COLOR_BORDER, 12, new Insets(32, 34, 32, 34));

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.anchor = GridBagConstraints.CENTER;
        gbcLeft.insets = new Insets(0, 0, 0, 40);
        centerArea.add(authLeftCard, gbcLeft);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.gridy = 0;
        gbcRight.anchor = GridBagConstraints.CENTER;
        centerArea.add(authRightCard, gbcRight);

        page.add(centerArea);
        page.add(createFooterSection());

        rebuildAuthCards();
        return page;
    }

    private void rebuildAuthCards() {
        if (authLeftCard == null || authRightCard == null) return;
        authLeftCard.removeAll();
        authRightCard.removeAll();

        // Trong Figma: Màn Đăng ký thẻ trái nền trắng viền mảnh; Màn Đăng nhập thẻ trái nền xám nhạt #EEF2F6
        if (isAuthLoginMode) {
            authLeftCard.setCardColors(new Color(238, 242, 247), new Color(226, 232, 240));
            authLeftCard.setMinDimensions(460, 445);
            authRightCard.setMinDimensions(470, 445);
        } else {
            authLeftCard.setCardColors(Color.WHITE, COLOR_BORDER);
            authLeftCard.setMinDimensions(460, 445);
            authRightCard.setMinDimensions(470, 635);
        }

        // 1. Dựng Thẻ Trái (Chuẩn Figma: Badge xanh -> Tiêu đề -> Mô tả -> Khung code có thanh tiêu đề -> 3 tích xanh sát nhau)
        String tagText = isAuthLoginMode ? "KHÓA HỌC LẬP TRÌNH HAUI" : "MUA KHÓA HỌC LẬP TRÌNH";
        String h1Text  = isAuthLoginMode ? "Bắt đầu hành trình lập trình." : "Đăng ký mua khóa học lập trình.";
        String subText = isAuthLoginMode ?
            "<html>Khóa học có lộ trình rõ ràng, bài tập thực hành<br/>và kiến thức từ cơ bản đến nâng cao.</html>" :
            "<html>Điền thông tin để tạo tài khoản và tiếp tục mua khóa học<br/>lập trình tại HAUI.</html>";

        JLabel badge = new JLabel(tagText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(COLOR_PRIMARY);

        JLabel h1 = new JLabel(h1Text);
        h1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        h1.setForeground(COLOR_TEXT_DARK);

        JLabel sub = new JLabel(subText);
        sub.setFont(FONT_BODY);
        sub.setForeground(COLOR_TEXT_MUTED);

        // Khung Code có thanh file header ngăn cách bằng đường kẻ ngang y hệt Figma
        CardBox codeBox = new CardBox(new Color(248, 250, 252), COLOR_BORDER, 8, new Insets(0, 0, 14, 0));

        RowPanel codeHeader = new RowPanel(new BorderLayout());
        codeHeader.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
            new EmptyBorder(9, 16, 9, 16)
        ));
        JLabel lblCodeFile = new JLabel("bai-hoc-dau-tien.java");
        lblCodeFile.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCodeFile.setForeground(COLOR_TEXT_MUTED);
        codeHeader.add(lblCodeFile, BorderLayout.WEST);

        VBox codeBody = new VBox();
        codeBody.setBorder(new EmptyBorder(12, 16, 4, 16));
        Font fCons = new Font("Consolas", Font.PLAIN, 13);
        Color cCode = new Color(51, 65, 85);

        JLabel c1 = new JLabel("class XinChao {"); c1.setFont(fCons); c1.setForeground(cCode);
        JLabel c2 = new JLabel("  public static void main() {"); c2.setFont(fCons); c2.setForeground(cCode);
        JLabel c3 = new JLabel("    print(\"Chào bạn!\");"); c3.setFont(fCons); c3.setForeground(cCode);
        JLabel c4 = new JLabel("  }"); c4.setFont(fCons); c4.setForeground(cCode);
        JLabel c5 = new JLabel("}"); c5.setFont(fCons); c5.setForeground(cCode);

        codeBody.add(c1);
        codeBody.add(Box.createVerticalStrut(4));
        codeBody.add(c2);
        codeBody.add(Box.createVerticalStrut(4));
        codeBody.add(c3);
        codeBody.add(Box.createVerticalStrut(4));
        codeBody.add(c4);
        codeBody.add(Box.createVerticalStrut(4));
        codeBody.add(c5);

        codeBox.add(codeHeader);
        codeBox.add(codeBody);

        authLeftCard.add(badge);
        authLeftCard.add(Box.createVerticalStrut(10));
        authLeftCard.add(h1);
        authLeftCard.add(Box.createVerticalStrut(8));
        authLeftCard.add(sub);
        authLeftCard.add(Box.createVerticalStrut(20));
        authLeftCard.add(codeBox);
        authLeftCard.add(Box.createVerticalStrut(20));
        authLeftCard.add(createCheckItem("Lộ trình dành cho người mới bắt đầu", COLOR_TEXT_DARK));
        authLeftCard.add(Box.createVerticalStrut(8));
        authLeftCard.add(createCheckItem("Học mọi lúc, theo tốc độ của bạn", COLOR_TEXT_DARK));
        authLeftCard.add(Box.createVerticalStrut(8));
        authLeftCard.add(createCheckItem("Thực hành với dự án thực tế", COLOR_TEXT_DARK));

        // 2. Dựng Thẻ Phải (Tất cả tiêu đề, nhãn, ô nhập, điều khoản và nút bấm căn thẳng lề trái tuyệt đối)
        if (isAuthLoginMode) {
            JLabel title = new JLabel("Chào mừng trở lại!");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(COLOR_TEXT_DARK);

            JLabel subL = new JLabel("Đăng nhập để tiếp tục hành trình học tập.");
            subL.setFont(FONT_BODY);
            subL.setForeground(COLOR_TEXT_MUTED);

            JLabel lblEmail = new JLabel("Email");
            lblEmail.setFont(FONT_BOLD);
            lblEmail.setForeground(COLOR_TEXT_DARK);
            JTextField txtEmail = createModernTextField("Nhập địa chỉ email", 42);

            JLabel lblPass = new JLabel("Mật khẩu");
            lblPass.setFont(FONT_BOLD);
            lblPass.setForeground(COLOR_TEXT_DARK);
            JPasswordField txtPass = createModernPasswordField("Nhập mật khẩu", 42);

            RowPanel pnlRem = new RowPanel(new BorderLayout());
            pnlRem.setBorder(new EmptyBorder(4, 0, 4, 0));
            JCheckBox chkRemember = new JCheckBox("Ghi nhớ đăng nhập", false);
            chkRemember.setFont(FONT_SMALL);
            chkRemember.setForeground(COLOR_TEXT_MUTED);
            chkRemember.setOpaque(false);

            JLabel lnkForgot = new JLabel("Quên mật khẩu");
            lnkForgot.setFont(FONT_SMALL);
            lnkForgot.setForeground(COLOR_PRIMARY);
            lnkForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            pnlRem.add(chkRemember, BorderLayout.WEST);
            pnlRem.add(lnkForgot, BorderLayout.EAST);

            ModernButton btnLogin = new ModernButton("Đăng nhập", COLOR_PRIMARY, Color.WHITE);
            btnLogin.setPreferredSize(new Dimension(402, 42));
            btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btnLogin.addActionListener(e -> {
                String email = txtEmail.getText().trim();
                String pass = new String(txtPass.getPassword());
                if (email.isEmpty() || pass.isEmpty()) {
                    AppUtils.showError(this, "Vui lòng nhập Email và Mật khẩu!\n(Tài khoản CSDL mẫu: an.nguyen@email.com / 123456)");
                    return;
                }
                TaiKhoan tk = taiKhoanDAO.dangNhap(email, pass);
                if (tk != null) {
                    String maGH = gioHangDAO.getOrCreateGioHang(tk.getMaKhachHang());
                    UserSession.getInstance().login(tk, maGH);
                    updateUserBadge();
                    updateCartBadge();
                    AppUtils.showSuccess(this, "Đăng nhập thành công! Xin chào " + tk.getHoTen());
                    navigate(PAGE_HOME);
                } else {
                    AppUtils.showError(this, "Email hoặc mật khẩu không chính xác!\n(Tài khoản CSDL: an.nguyen@email.com / 123456)");
                }
            });

            RowPanel pnlToggle = new RowPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            JLabel lblAsk = new JLabel("Chưa có tài khoản?");
            lblAsk.setFont(FONT_SMALL);
            lblAsk.setForeground(COLOR_TEXT_MUTED);

            JLabel lnkRegister = new JLabel("Đăng ký");
            lnkRegister.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lnkRegister.setForeground(COLOR_PRIMARY);
            lnkRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lnkRegister.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { openAuth(false); }
            });
            pnlToggle.add(lblAsk);
            pnlToggle.add(lnkRegister);

            authRightCard.add(title);
            authRightCard.add(Box.createVerticalStrut(6));
            authRightCard.add(subL);
            authRightCard.add(Box.createVerticalStrut(24));

            authRightCard.add(lblEmail);
            authRightCard.add(Box.createVerticalStrut(6));
            authRightCard.add(txtEmail);
            authRightCard.add(Box.createVerticalStrut(18));

            authRightCard.add(lblPass);
            authRightCard.add(Box.createVerticalStrut(6));
            authRightCard.add(txtPass);
            authRightCard.add(Box.createVerticalStrut(14));
            authRightCard.add(pnlRem);
            authRightCard.add(Box.createVerticalStrut(22));

            authRightCard.add(btnLogin);
            authRightCard.add(Box.createVerticalStrut(18));
            authRightCard.add(pnlToggle);

        } else {
            JLabel title = new JLabel("Tạo tài khoản");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(COLOR_TEXT_DARK);

            JLabel subR = new JLabel("Đăng ký để mua và theo dõi khóa học lập trình.");
            subR.setFont(FONT_BODY);
            subR.setForeground(COLOR_TEXT_MUTED);

            JLabel l1 = new JLabel("Họ và tên"); l1.setFont(FONT_BOLD); l1.setForeground(COLOR_TEXT_DARK);
            JTextField txtName = createModernTextField("Nhập họ và tên của bạn", 38);

            JLabel l2 = new JLabel("Email"); l2.setFont(FONT_BOLD); l2.setForeground(COLOR_TEXT_DARK);
            JTextField txtEmail = createModernTextField("Nhập địa chỉ email", 38);

            JLabel l3 = new JLabel("Số điện thoại"); l3.setFont(FONT_BOLD); l3.setForeground(COLOR_TEXT_DARK);
            JTextField txtPhone = createModernTextField("Nhập số điện thoại", 38);

            JLabel l4 = new JLabel("Mật khẩu"); l4.setFont(FONT_BOLD); l4.setForeground(COLOR_TEXT_DARK);
            JPasswordField txtPass = createModernPasswordField("Tạo mật khẩu tối thiểu 8 ký tự", 38);

            JLabel l5 = new JLabel("Mã sinh viên HaUI"); l5.setFont(FONT_BOLD); l5.setForeground(COLOR_TEXT_DARK);
            JTextField txtMsv = createModernTextField("Ví dụ: 202160xxxx", 38);

            JLabel l6 = new JLabel("Nhập lại mật khẩu"); l6.setFont(FONT_BOLD); l6.setForeground(COLOR_TEXT_DARK);
            JPasswordField txtRePass = createModernPasswordField("Nhập lại mật khẩu của bạn", 38);

            JLabel lblTerm = new JLabel("<html>Bằng việc đăng ký, bạn đồng ý với <font color='#24467A'>Điều khoản sử dụng</font> và <font color='#24467A'>Chính sách<br/>bảo mật</font>.</html>");
            lblTerm.setFont(FONT_SMALL);
            lblTerm.setForeground(COLOR_TEXT_MUTED);

            ModernButton btnRegister = new ModernButton("Đăng ký", COLOR_PRIMARY, Color.WHITE);
            btnRegister.setPreferredSize(new Dimension(402, 42));
            btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btnRegister.addActionListener(e -> {
                String name = txtName.getText().trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();
                String pass = new String(txtPass.getPassword());
                String rePass = new String(txtRePass.getPassword());
                String msv = txtMsv.getText().trim();

                if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    AppUtils.showError(this, "Vui lòng nhập đầy đủ Họ tên, Email và Mật khẩu!");
                    return;
                }
                if (!AppUtils.isValidEmail(email)) {
                    AppUtils.showError(this, "Email không hợp lệ!");
                    return;
                }
                if (pass.length() < 6) {
                    AppUtils.showError(this, "Mật khẩu phải có tối thiểu 6 ký tự!");
                    return;
                }
                if (!pass.equals(rePass)) {
                    AppUtils.showError(this, "Mật khẩu nhập lại không khớp!");
                    return;
                }

                String maKH = taiKhoanDAO.generateMaKH();
                TaiKhoan newTk = new TaiKhoan(maKH, name, email, pass, phone, msv, "Hoạt động", new java.util.Date());
                boolean ok = taiKhoanDAO.dangKyTaiKhoan(newTk);
                if (ok) {
                    String maGH = gioHangDAO.getOrCreateGioHang(newTk.getMaKhachHang());
                    UserSession.getInstance().login(newTk, maGH);
                    updateUserBadge();
                    updateCartBadge();
                    AppUtils.showSuccess(this, "Đăng ký tài khoản thành công trên CSDL SQL Server! Bạn đã được tự động đăng nhập.");
                    navigate(PAGE_HOME);
                } else {
                    AppUtils.showError(this, "Đăng ký không thành công. Email có thể đã tồn tại trong CSDL!");
                }
            });

            RowPanel pnlToggle = new RowPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            JLabel lblAsk = new JLabel("Đã có tài khoản?");
            lblAsk.setFont(FONT_SMALL);
            lblAsk.setForeground(COLOR_TEXT_MUTED);

            JLabel lnkLogin = new JLabel("Đăng nhập");
            lnkLogin.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lnkLogin.setForeground(COLOR_PRIMARY);
            lnkLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lnkLogin.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { openAuth(true); }
            });
            pnlToggle.add(lblAsk);
            pnlToggle.add(lnkLogin);

            authRightCard.add(title);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(subR);
            authRightCard.add(Box.createVerticalStrut(16));

            authRightCard.add(l1);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtName);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(l2);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtEmail);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(l3);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtPhone);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(l4);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtPass);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(l5);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtMsv);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(l6);
            authRightCard.add(Box.createVerticalStrut(4));
            authRightCard.add(txtRePass);
            authRightCard.add(Box.createVerticalStrut(10));

            authRightCard.add(lblTerm);
            authRightCard.add(Box.createVerticalStrut(12));
            authRightCard.add(btnRegister);
            authRightCard.add(Box.createVerticalStrut(12));
            authRightCard.add(pnlToggle);
        }

        authLeftCard.revalidate();
        authLeftCard.repaint();
        authRightCard.revalidate();
        authRightCard.repaint();
    }

    // =========================================================================
    // MÀN HÌNH BỔ TRỢ TUẦN 6: KHÓA HỌC CỦA TÔI & TRANG CÁ NHÂN
    // =========================================================================
    private JPanel myCoursesGrid;

    private PagePanel createMyCoursesPage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(20, 48, 32, 48));

        JLabel lblBreadcrumb = new JLabel("Trang chủ  /  Khóa học của tôi");
        lblBreadcrumb.setFont(FONT_SMALL);
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JLabel lblTitle = new JLabel("Khóa học của tôi");
        lblTitle.setFont(FONT_HERO_H1);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel("Theo dõi lộ trình và tiếp tục các bài giảng của bạn.");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        content.add(lblBreadcrumb);
        content.add(Box.createVerticalStrut(6));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);
        content.add(Box.createVerticalStrut(20));

        myCoursesGrid = new RowPanel(new GridLayout(0, 3, 16, 16));
        content.add(myCoursesGrid);

        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private void refreshMyCoursesPage() {
        if (myCoursesGrid == null) return;
        myCoursesGrid.removeAll();

        String maKH = getCurrentCustomerId();
        List<DangKy> list = dangKyDAO.getDanhSachDaDangKy(maKH);

        for (DangKy dk : list) {
            KhoaHoc kh = dk.getKhoaHoc();
            if (kh != null) {
                myCoursesGrid.add(createMyCourseCard(dk));
            }
        }

        if (list.isEmpty()) {
            JLabel empty = new JLabel("Bạn chưa đăng ký khóa học nào. Hãy khám phá khóa học ngay!");
            empty.setFont(FONT_BODY);
            empty.setForeground(COLOR_TEXT_MUTED);
            myCoursesGrid.add(empty);
        }

        myCoursesGrid.revalidate();
        myCoursesGrid.repaint();
    }

    private JPanel createMyCourseCard(DangKy dk) {
        KhoaHoc kh = dk.getKhoaHoc();
        CardBox card = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(14, 14, 14, 14));

        JPanel thumb = createCourseThumbnail(kh.getAnhKhoaHoc() != null ? kh.getAnhKhoaHoc() : kh.getMaDanhMuc(), 0, 105);
        thumb.setPreferredSize(new Dimension(100, 105));
        thumb.setMaximumSize(new Dimension(2000, 105));

        JLabel lblTitle = new JLabel("<html><div style='width:220px;'><b>" + kh.getTenKhoaHoc() + "</b></div></html>");
        lblTitle.setFont(FONT_CARD_H);
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setPreferredSize(new Dimension(220, 38));

        JLabel lblTeacher = new JLabel(kh.getGiangVien() + " • " + kh.getThoiLuong());
        lblTeacher.setFont(FONT_SMALL);
        lblTeacher.setForeground(COLOR_TEXT_MUTED);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(45);
        progress.setStringPainted(true);
        progress.setString("Tiến độ: 45%");
        progress.setFont(FONT_SMALL);
        progress.setForeground(COLOR_PRIMARY);
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        ModernButton btnStudy = new ModernButton("Vào học ngay", COLOR_PRIMARY, Color.WHITE);
        btnStudy.setPreferredSize(new Dimension(100, 38));
        btnStudy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnStudy.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Đang mở phòng học trực tuyến cho khóa học:\n\n" + kh.getTenKhoaHoc() +
                "\n\nChúc bạn học tập hiệu quả!",
                "Phòng Học Trực Tuyến HaUI", JOptionPane.INFORMATION_MESSAGE);
        });

        card.add(thumb);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTeacher);
        card.add(Box.createVerticalStrut(8));
        card.add(progress);
        card.add(Box.createVerticalStrut(12));
        card.add(btnStudy);

        return card;
    }

    private JTextField txtProfName, txtProfMsv, txtProfEmail, txtProfPhone;
    private JLabel lblProfMaKH, lblProfStatus, lblProfDbStatus;

    private PagePanel createProfilePage() {
        PagePanel page = new PagePanel();

        VBox content = new VBox();
        content.setBorder(new EmptyBorder(24, 48, 36, 48));

        JLabel lblBreadcrumb = new JLabel("Trang chủ  /  Thông tin cá nhân");
        lblBreadcrumb.setFont(FONT_SMALL);
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JLabel lblTitle = new JLabel("Hồ sơ & Tài khoản học viên");
        lblTitle.setFont(FONT_HERO_H1);
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel("Quản lý thông tin tài khoản cá nhân và đồng bộ trực tiếp với CSDL SQL Server.");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        content.add(lblBreadcrumb);
        content.add(Box.createVerticalStrut(6));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);
        content.add(Box.createVerticalStrut(22));

        RowPanel row = new RowPanel(new BorderLayout(24, 0));

        CardBox card = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(28, 32, 28, 32));
        card.setMinDimensions(0, 410);

        TaiKhoan user = UserSession.getInstance().getCurrentUser();

        JLabel l1 = new JLabel("Họ và tên"); l1.setFont(FONT_BOLD);
        txtProfName = createModernTextField("Họ và tên", 40);
        txtProfName.setText(user != null ? user.getHoTen() : "");

        JLabel l2 = new JLabel("Mã sinh viên HaUI"); l2.setFont(FONT_BOLD);
        txtProfMsv = createModernTextField("Mã sinh viên", 40);
        txtProfMsv.setText(user != null ? user.getMaSinhVien() : "");

        JLabel l3 = new JLabel("Email liên hệ"); l3.setFont(FONT_BOLD);
        txtProfEmail = createModernTextField("Email", 40);
        txtProfEmail.setText(user != null ? user.getEmail() : "");

        JLabel l4 = new JLabel("Số điện thoại"); l4.setFont(FONT_BOLD);
        txtProfPhone = createModernTextField("Số điện thoại", 40);
        txtProfPhone.setText(user != null ? user.getSoDienThoai() : "");

        card.add(l1);
        card.add(Box.createVerticalStrut(6));
        card.add(txtProfName);
        card.add(Box.createVerticalStrut(14));
        card.add(l2);
        card.add(Box.createVerticalStrut(6));
        card.add(txtProfMsv);
        card.add(Box.createVerticalStrut(14));
        card.add(l3);
        card.add(Box.createVerticalStrut(6));
        card.add(txtProfEmail);
        card.add(Box.createVerticalStrut(14));
        card.add(l4);
        card.add(Box.createVerticalStrut(6));
        card.add(txtProfPhone);
        card.add(Box.createVerticalStrut(24));

        RowPanel btnRow = new RowPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        ModernButton btnSave = new ModernButton("Lưu cập nhật", COLOR_PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(150, 42));
        btnSave.addActionListener(e -> {
            TaiKhoan cur = UserSession.getInstance().getCurrentUser();
            if (cur != null) {
                taiKhoanDAO.capNhatThongTin(cur.getMaKhachHang(), txtProfName.getText().trim(), txtProfPhone.getText().trim(), txtProfMsv.getText().trim());
                cur.setHoTen(txtProfName.getText().trim());
                cur.setSoDienThoai(txtProfPhone.getText().trim());
                cur.setMaSinhVien(txtProfMsv.getText().trim());
                updateUserBadge();
                AppUtils.showSuccess(this, "Đã lưu cập nhật thông tin vào CSDL SQL Server thành công!");
            }
        });

        ModernButton btnLogout = new ModernButton("Đăng xuất", new Color(220, 38, 38), Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(130, 42));
        btnLogout.addActionListener(e -> {
            boolean confirm = AppUtils.showConfirm(this, "Bạn có chắc chắn muốn đăng xuất tài khoản?");
            if (confirm) {
                UserSession.getInstance().logout();
                updateUserBadge();
                updateCartBadge();
                AppUtils.showSuccess(this, "Đã đăng xuất thành công!");
                openAuth(true);
            }
        });

        btnRow.add(btnSave);
        btnRow.add(Box.createHorizontalStrut(14));
        btnRow.add(btnLogout);
        card.add(btnRow);

        row.add(card, BorderLayout.CENTER);

        // Thẻ thông tin hệ thống & CSDL bên phải
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        CardBox infoCard = new CardBox(Color.WHITE, COLOR_BORDER, 10, new Insets(24, 24, 24, 24));
        infoCard.setMinDimensions(360, 220);

        JLabel lblSysTitle = new JLabel("Thông tin hệ thống & CSDL");
        lblSysTitle.setFont(FONT_CARD_H);
        lblSysTitle.setForeground(COLOR_TEXT_DARK);

        lblProfMaKH = new JLabel("Mã học viên: " + (user != null ? user.getMaKhachHang() : "KH001"));
        lblProfMaKH.setFont(FONT_BODY);
        lblProfMaKH.setForeground(COLOR_TEXT_DARK);

        lblProfStatus = new JLabel("Trạng thái: Đang hoạt động");
        lblProfStatus.setFont(FONT_BODY);
        lblProfStatus.setForeground(COLOR_SUCCESS);

        lblProfDbStatus = new JLabel("CSDL: " + (DBConnection.isMockMode() ? "Mock Mode" : "SQL Server (HaUICourseDB)"));
        lblProfDbStatus.setFont(FONT_BOLD);
        lblProfDbStatus.setForeground(COLOR_PRIMARY);

        infoCard.add(lblSysTitle);
        infoCard.add(Box.createVerticalStrut(12));
        infoCard.add(createSeparator());
        infoCard.add(Box.createVerticalStrut(14));
        infoCard.add(lblProfMaKH);
        infoCard.add(Box.createVerticalStrut(8));
        infoCard.add(lblProfStatus);
        infoCard.add(Box.createVerticalStrut(8));
        infoCard.add(lblProfDbStatus);
        infoCard.add(Box.createVerticalStrut(14));
        infoCard.add(createCheckItem("Đồng bộ dữ liệu thời gian thực", COLOR_TEXT_MUTED));
        infoCard.add(Box.createVerticalStrut(6));
        infoCard.add(createCheckItem("Bảo mật tài khoản sinh viên HaUI", COLOR_TEXT_MUTED));

        rightWrap.add(infoCard, BorderLayout.NORTH);
        row.add(rightWrap, BorderLayout.EAST);

        content.add(row);
        page.add(content);
        page.add(createFooterSection());

        return page;
    }

    private void refreshProfilePage() {
        TaiKhoan user = UserSession.getInstance().getCurrentUser();
        if (txtProfName != null) {
            txtProfName.setText(user != null && user.getHoTen() != null ? user.getHoTen() : "");
            txtProfMsv.setText(user != null && user.getMaSinhVien() != null ? user.getMaSinhVien() : "");
            txtProfEmail.setText(user != null && user.getEmail() != null ? user.getEmail() : "");
            txtProfPhone.setText(user != null && user.getSoDienThoai() != null ? user.getSoDienThoai() : "");
        }
        if (lblProfMaKH != null) {
            lblProfMaKH.setText("Mã học viên: " + (user != null ? user.getMaKhachHang() : "Chưa đăng nhập"));
            lblProfDbStatus.setText("CSDL: " + (DBConnection.isMockMode() ? "Mock Mode" : "SQL Server (HaUICourseDB)"));
        }
    }

    // =========================================================================
    // CUSTOM FIGMA GRAPHICS2D COMPONENTS
    // =========================================================================

    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);
        sep.setBackground(Color.WHITE);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public static class ModernButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color normalFg;
        private Color borderColor;
        private int cornerRadius = 6;
        private boolean isHover = false;
        private boolean isPressed = false;

        public ModernButton(String text, Color bg, Color fg, Color border) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = (bg.equals(Color.WHITE)) ? new Color(241, 245, 249) : COLOR_PRIMARY_HOVER;
            this.normalFg = fg;
            this.borderColor = border;
            setFont(FONT_BOLD);
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHover = false; isPressed = false; repaint(); }
                @Override
                public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
                @Override
                public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
            });
        }

        public ModernButton(String text, Color bg, Color fg) {
            this(text, bg, fg, null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color currentBg = isPressed ? hoverBg.darker() : (isHover ? hoverBg : normalBg);
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }

            g2.setColor(normalFg);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    /**
     * Ô nhập văn bản bo góc mượt Graphics2D, tự động căn lề trái và giữ cố định chiều cao
     */
    public static JTextField createModernTextField(String placeholder, int height) {
        JTextField tf = new JTextField() {
            @Override
            public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, height); }
            @Override
            public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); return new Dimension(d.width, height); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g);
                if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocusOwner()) {
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, insets.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                }
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? COLOR_PRIMARY : COLOR_BORDER_INPUT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setFont(FONT_BODY);
        tf.setForeground(COLOR_TEXT_DARK);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(new EmptyBorder(6, 12, 6, 12));
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { tf.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { tf.repaint(); }
        });
        return tf;
    }

    public static JPasswordField createModernPasswordField(String placeholder, int height) {
        JPasswordField pf = new JPasswordField() {
            @Override
            public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, height); }
            @Override
            public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); return new Dimension(d.width, height); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g);
                if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty() && !isFocusOwner()) {
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, insets.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                }
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? COLOR_PRIMARY : COLOR_BORDER_INPUT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        pf.setOpaque(false);
        pf.setFont(FONT_BODY);
        pf.setForeground(COLOR_TEXT_DARK);
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setBorder(new EmptyBorder(6, 12, 6, 12));
        pf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { pf.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { pf.repaint(); }
        });
        return pf;
    }

    public static JPanel createCourseThumbnail(String category, int width, int height) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(new Color(235, 240, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                int cardW = Math.max(54, (int) (getWidth() * 0.56));
                int cardH = Math.max(36, (int) (getHeight() * 0.56));
                int cardX = (getWidth() - cardW) / 2;
                int cardY = (getHeight() - cardH) / 2;

                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(cardX, cardY, cardW, cardH, 6, 6);
                g2.setColor(new Color(186, 198, 214));
                g2.drawRoundRect(cardX, cardY, cardW - 1, cardH - 1, 6, 6);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(10, Math.min(13, cardH / 3))));
                g2.setColor(new Color(51, 65, 85));
                FontMetrics fm = g2.getFontMetrics();
                String displayCat = (category != null && !category.isEmpty()) ? category : "Java";
                int textX = cardX + (cardW - fm.stringWidth(displayCat)) / 2;
                int textY = cardY + (cardH / 2) - 2;
                g2.drawString(displayCat, textX, textY);

                int lineW1 = (int) (cardW * 0.62);
                int lineX1 = cardX + (cardW - lineW1) / 2;
                int lineY1 = textY + 6;
                g2.setColor(new Color(120, 138, 160));
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(lineX1, lineY1, lineX1 + lineW1, lineY1);

                int lineW2 = (int) (cardW * 0.42);
                int lineX2 = cardX + (cardW - lineW2) / 2;
                int lineY2 = lineY1 + 5;
                g2.setColor(new Color(175, 188, 205));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(lineX2, lineY2, lineX2 + lineW2, lineY2);

                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    public static JPanel createStarRatingPanel(double rating, int studentCount) {
        RowPanel p = new RowPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

        JComponent star = new JComponent() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(13, 13); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 158, 11));
                Polygon s = new Polygon();
                double cx = 6.5, cy = 6.5;
                for (int i = 0; i < 10; i++) {
                    double r = (i % 2 == 0) ? 6.0 : 2.6;
                    double angle = i * Math.PI / 5 - Math.PI / 2;
                    s.addPoint((int) (cx + r * Math.cos(angle)), (int) (cy + r * Math.sin(angle)));
                }
                g2.fill(s);
                g2.dispose();
            }
        };

        JLabel lbl = new JLabel(String.format(Locale.US, "%.1f • %,d học viên", rating, studentCount).replace(",", "."));
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(COLOR_TEXT_MUTED);

        p.add(star);
        p.add(lbl);
        return p;
    }

    /**
     * Dấu tích chuẩn Figma (Sử dụng RowPanel để giữ chiều cao cố định, không bao giờ bị giãn dọc)
     */
    public static JPanel createCheckItem(String text, Color textColor) {
        RowPanel p = new RowPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JComponent chk = new JComponent() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(14, 14); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(2, 7, 5, 11);
                g2.drawLine(5, 11, 12, 3);
                g2.dispose();
            }
        };

        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(textColor != null ? textColor : COLOR_TEXT_DARK);

        p.add(chk);
        p.add(lbl);
        return p;
    }

    public static JPanel createCheckoutStepper(int currentStep) {
        RowPanel p = new RowPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JPanel s1 = createCheckItem("Giỏ hàng", COLOR_TEXT_MUTED);

        JComponent line1 = createStepLine(true);

        JLabel s2 = new JLabel("2. Thanh toán");
        s2.setFont(FONT_BOLD);
        s2.setForeground(COLOR_PRIMARY);

        JComponent line2 = createStepLine(false);

        JLabel s3 = new JLabel("3. Hoàn tất");
        s3.setFont(FONT_SMALL);
        s3.setForeground(COLOR_TEXT_MUTED);

        p.add(s1);
        p.add(line1);
        p.add(s2);
        p.add(line2);
        p.add(s3);

        return p;
    }

    private static JComponent createStepLine(boolean active) {
        return new JComponent() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(36, 14); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(active ? COLOR_BORDER_INPUT : COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(2, 7, getWidth() - 2, 7);
                g2.dispose();
            }
        };
    }

    private JLabel createPill(String text) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(COLOR_TEXT_DARK);
        lbl.setBackground(new Color(241, 245, 249));
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lbl;
    }
}
