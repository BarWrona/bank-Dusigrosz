package pl.edu.pjwstk.dusigrosz.service.util;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class BankAccountsIbanGenerator {

    private static final String BANK_CODE = "21376769";
    private static final SecureRandom random = new SecureRandom();

    public String generateIban() {
        String randomAccountNumber = generateRandomDigits(16);

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode(BANK_CODE)
                .branchCode("")
                .nationalCheckDigit("")
                .accountNumber(randomAccountNumber)
                .build();

        return iban.toString();
    }

    public String generateRandomDigits(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
    }
}
