package bio.singa.mathematics.algorithms.primes;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for prime numbers.
 */
public final class Primes {

    private Primes() {
    }

    /**
     * Checks for the given number if it is a prime number.
     *
     * @param number The number to check.
     * @return True if prime number.
     */
    public static boolean checkPrime(int number) {
        int limit = ((int) Math.sqrt(number)) + 1;
        for (int i = 3; i < limit; i = i + 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a list of ascending prime numbers of the given length.
     *
     * @param length The length of the list of prime numbers, i.e. their count.
     * @return List of prime numbers of requested length.
     */
    public static List<Integer> getPrimeList(int length) {
        ArrayList<Integer> primeList = new ArrayList<>();
        primeList.add(2);
        int n = 3;
        while (primeList.size() < length) {
            if (checkPrime(n)) {
                primeList.add(n);
            }
            n += 2;
        }
        return primeList;
    }
}
