package saldo.app.email.counter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmailDomainCounter {

    private static final String AT_SIGN = "@";

    public static void main(String[] args) {
        System.out.println(findTopNEmailDomains(generateMockEmailList(), 10));
    }

    /**
     * Method for fining top-n email domains
     *
     * @param emails                    - collection of email addresses
     * @param maxAmountOfReturnedEmails - max number of emails that should be returned
     * @return list of top maxAmountOfReturnedEmails domains that appear the most often with a
     * count of the number of times it appears after each domain.
     */
    private static List<String> findTopNEmailDomains(Collection<String> emails, int maxAmountOfReturnedEmails) {
        return emails.stream()
                // optional filter, if we are sure that data validation happens before - filter can be removed
                .filter(email -> email != null && email.contains(AT_SIGN))
                .collect(Collectors.groupingBy(EmailDomainCounter::extractDomainFromEmail, Collectors.counting()))
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(maxAmountOfReturnedEmails)
                .map(entry -> {
                    final String emailDomain = entry.getKey();
                    final Long count = entry.getValue();
                    return String.format("%s %s", emailDomain, count);
                }).toList();

    }

    /**
     * Helper method for extracting only domain from email
     *
     * @return domain from email
     */
    private static String extractDomainFromEmail(String email) {
        String[] split = email.split(AT_SIGN);
        return split[1];
    }

    /**
     * @return list of mock data
     */
    private static List<String> generateMockEmailList() {
        List<String> joeSaldo = Collections.nCopies(500, "joeblogs@saldoaaps.com");
        List<String> andrewGmail = Collections.nCopies(300, "andrew.smith@gmail.com");
        List<String> andrewSaldo = Collections.nCopies(450, "andrew.smith@saldoaaps.com");
        List<String> alexYahoo = Collections.nCopies(200, "alex.smith@yahoo.com");
        List<String> johnCiklum = Collections.nCopies(100, "john.smith@ciklum.com");
        return Stream.of(johnCiklum, joeSaldo, andrewSaldo, andrewGmail, alexYahoo)
                .flatMap(Collection::stream)
                .toList();
    }
}