
package com.yehor.bankapi;

import com.yehor.bankapi.exception.InsufficientFundsException;
import com.yehor.bankapi.service.BankService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BankConcurrencyTest {

    @Autowired
    private BankService bankService;

    private static final Integer ACCOUNT_ID = 3;

    @Test
    void testConcurrentWithdraw() throws Exception {


        BigDecimal currentBalance = bankService
                .getAccountById(ACCOUNT_ID)
                .getBalance();

        BigDecimal targetBalance = new BigDecimal("1000.00");
        int comparison = currentBalance.compareTo(targetBalance);

        if (comparison < 0) {
            bankService.deposit(
                    ACCOUNT_ID,
                    targetBalance.subtract(currentBalance)
            );
        } else if (comparison > 0) {
            bankService.withdraw(
                    ACCOUNT_ID,
                    currentBalance.subtract(targetBalance)
            );
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        try {
            Callable<Boolean> withdrawTask = () -> {
                readyLatch.countDown();
                startLatch.await();

                try {
                    bankService.withdraw(
                            ACCOUNT_ID,
                            new BigDecimal("800.00")
                    );
                    return true;

                } catch (InsufficientFundsException e) {
                    return false;
                }
            };

            Future<Boolean> future1 = executor.submit(withdrawTask);
            Future<Boolean> future2 = executor.submit(withdrawTask);

            assertTrue(readyLatch.await(10, TimeUnit.SECONDS));

            startLatch.countDown();

            boolean result1 = future1.get(30, TimeUnit.SECONDS);
            boolean result2 = future2.get(30, TimeUnit.SECONDS);

            int successfulWithdrawals = 0;

            if (result1) successfulWithdrawals++;
            if (result2) successfulWithdrawals++;

            assertEquals(1, successfulWithdrawals);

            BigDecimal finalBalance = bankService
                    .getAccountById(ACCOUNT_ID)
                    .getBalance();

            assertEquals(
                    0,
                    new BigDecimal("200.00").compareTo(finalBalance)
            );

        } finally {
            startLatch.countDown();
            executor.shutdownNow();
        }
    }
}
