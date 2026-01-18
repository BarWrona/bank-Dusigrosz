import { A } from "@solidjs/router";
import styles from '../assets/LandingPage.module.css';

const LandingPage = () => {
    return (
        <div class={styles.pageWrapper}>
            <nav class={styles.navbar}>
                <div class={styles.navContainer}>
                    <div class={styles.logo}>Dusigrosz Bank</div>
                    <div class={styles.navLinks}>
                        <a href="#" class={styles.navLink}>Oferta</a>
                        <a href="#" class={styles.navLink}>Pomoc</a>
                    </div>
                    <A href="/login" class={styles.loginButton}>
                        Zaloguj
                    </A>
                    <A href="/register" class={styles.primaryButton} style="margin-left: 10px; text-decoration: none; padding: 0.5rem 1.5rem; font-size: 0.9rem;">
                        Zarejestruj się
                    </A>
                </div>
            </nav>

            <header class={styles.heroSection}>
                <div class={styles.heroContent}>
                    <h1 class={styles.heroTitle}>Twoje finanse<br />w najlepszych rękach</h1>
                    <p class={styles.heroSubtitle}>Nowoczesna bankowość dostosowana do Twoich potrzeb. Dołącz do tysięcy zadowolonych klientów.</p>
                    <div class={styles.heroButtons}>
                        <button class={styles.primaryButton}>Zostań klientem</button>
                        <button class={styles.secondaryButton}>Dowiedz się więcej</button>
                    </div>
                </div>
                <div class={styles.heroImage}>

                    <div class={styles.decorativeCircle}></div>
                </div>
            </header>

            <section class={styles.offersSection}>
                <h2 class={styles.sectionTitle}>Dlaczego warto?</h2>
                <div class={styles.offersGrid}>
                    <div class={styles.offerCard}>
                        <div class={styles.iconWrapper}>💎</div>
                        <h3 class={styles.offerTitle}>Konto za 0 zł</h3>
                        <p class={styles.offerText}>Bezwarunkowo darmowe prowadzenie konta i karty. Wypłaty ze wszystkich bankomatów w Polsce.</p>
                    </div>
                    <div class={styles.offerCard}>
                        <div class={styles.iconWrapper}>🏠</div>
                        <h3 class={styles.offerTitle}>Kredyt Hipoteczny</h3>
                        <p class={styles.offerText}>Niskie oprocentowanie RRSO 5.5%. Decyzja nawet w 24 godziny. Spełnij marzenia o własnym M.</p>
                    </div>
                    <div class={styles.offerCard}>
                        <div class={styles.iconWrapper}>📈</div>
                        <h3 class={styles.offerTitle}>Lokata 8%</h3>
                        <p class={styles.offerText}>Pomnażaj swoje oszczędności bezpiecznie. Gwarancja zysku i elastyczne warunki wypłaty.</p>
                    </div>
                </div>
            </section>

            <footer class={styles.footer}>
                <div class={styles.footerContent}>
                    <p>&copy; 2026 Dusigrosz Bank S.A. Wszystkie prawa zastrzeżone.</p>
                    <div class={styles.footerLinks}>
                        <a href="#">Regulamin</a>
                        <a href="#">Prywatność</a>
                        <a href="#">Kontakt</a>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default LandingPage;
