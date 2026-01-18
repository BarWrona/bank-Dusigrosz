package pl.edu.pjwstk.dusigrosz.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pjwstk.dusigrosz.domain.model.*;
import pl.edu.pjwstk.dusigrosz.domain.repository.AccountRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.CurrencyRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.UserRepository;
import pl.edu.pjwstk.dusigrosz.domain.repository.VisorRepository;
import pl.edu.pjwstk.dusigrosz.service.util.BankAccountsIbanGenerator;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VisorRepository visorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final BankAccountsIbanGenerator ibanGenerator;

    @Override
    public void run(String... args) throws Exception {
        if (visorRepository.count() == 0) {
            System.out.println("Seeding initial Visor...");
            Visor visor = new Visor();
            visor.setFirstName("Jan");
            visor.setLastName("Nadzorca");
            visor.setUsername("visor");
            visor.setPassword(passwordEncoder.encode("visor"));
            visor.setRole(Role.VISOR);
            visor.setPesel("90010100146");
            visor.setPhoneNumber("555111222");
            visorRepository.save(visor);
        }

        if (userRepository.count() == 0) {
            System.out.println("Seeding initial Users...");

            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setPesel("80010100154");
            admin.setPhoneNumber("555000000");

            userRepository.save(admin);

            Visor visor = visorRepository.findAll().get(0);
            User user = new User();
            user.setFirstName("Zwykly");
            user.setLastName("Uzytkownik");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(Role.USER);
            user.setPesel("70010100162");
            user.setPhoneNumber("555333444");
            user.setVisor(visor);
            user = userRepository.save(user);

            Currency pln = new Currency();
            if (currencyRepository.count() == 0) {
                pln.setCode("PLN");
                pln.setName("Polski Złoty");
                pln.setExchangeRate(BigDecimal.ONE);
                pln = currencyRepository.save(pln);
            } else {
                pln = currencyRepository.findAll().get(0);
            }

            Account account = new Account();
            String iban = ibanGenerator.generateIban();
            account.setIban(iban);
            account.setCurrency(pln);
            account.setBalance(new BigDecimal("1000.00"));
            account.getUsers().add(user);
            account = accountRepository.save(account);

            user.getAccounts().add(account);
            userRepository.save(user);

            System.out.println("Seeding completed.");
        }
    }
}
