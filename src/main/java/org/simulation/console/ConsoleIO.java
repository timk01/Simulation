package org.simulation.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class ConsoleIO {
    private static final Logger log = LoggerFactory.getLogger(ConsoleIO.class);
    private final Scanner scanner;
    private final boolean ownsStream;
    private final BlockingQueue<String> inbox = new LinkedBlockingQueue<>();
    private volatile boolean readerStarted = false;
    private volatile boolean eof = false;

    public ConsoleIO(InputStream in, Charset cs) {
        this(in, cs, true);
    }

    public ConsoleIO(InputStream in, Charset cs, boolean ownsStream) {
        this.scanner = new Scanner(new InputStreamReader(in, cs));
        this.ownsStream = ownsStream;
    }

    /**
     * Мягко блокирующее чтение следующей строки из очереди, наполняемой
     * фоновым reader-потоком. Возвращает null при EOF/ошибке ввода.
     *
     * Внутри — бесконечный цикл с poll(queue, NON_BLOCKING_POLL_MS):
     * это позволяет не зависать навечно и корректно завершаться при EOF,
     * сохраняя поведение близким к блокирующему Scanner.nextLine().
     *
     * И это - тоже костыль (см. ниже).
     */
    public String readLineOrNull() {
        ensureReaderStarted();
        try {
            while (true) {
                if (eof && inbox.isEmpty()) {
                    return null;
                }
                String s = inbox.poll(ConsoleConfig.NON_BLOCKING_POLL_MS, TimeUnit.MILLISECONDS);
                if (s != null) {
                    return s;
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (RuntimeException e) {
            log.warn("Scanner read failed (blocking via queue)", e);
            return null;
        }
    }

    private void ensureReaderStarted() {
        if (readerStarted) {
            return;
        }
        synchronized (this) {
            if (readerStarted) {
                return;
            }
            readerStarted = true;
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        if (!scanner.hasNextLine()) {
                            eof = true;
                            break;
                        }
                        String line = scanner.nextLine();
                        inbox.offer(line);
                        log.trace("Reader queued line: '{}'", line);
                    }
                } catch (Exception e) {
                    log.warn("Background console reader failed", e);
                    eof = true;
                }
            }, "console-reader");
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Псевдо-неблокирующее чтение с опросом очереди из фонового reader-потока.
     * Возвращает строку, когда она появляется; возвращает null, если:
     *  - наступил EOF/ошибка ввода (scanner исчерпан), ИЛИ
     *  - сработал предикат abort.getAsBoolean() (например, симуляция остановлена).
     *
     * Реализация использует бесконечный цикл с периодическим poll из BlockingQueue
     * (см. NON_BLOCKING_POLL_MS).
     *
     * Это - костыль. Но рабочий костыль.
     */
    public String readLineOrNullNonBlockingUntil(BooleanSupplier abort) {
        ensureReaderStarted();
        try {
            while(true) {
                if (abort != null && abort.getAsBoolean()) {
                    return null;
                }
                if (eof && inbox.isEmpty()) {
                    return null;
                }
                String s = inbox.poll(ConsoleConfig.NON_BLOCKING_POLL_MS, TimeUnit.MILLISECONDS);
                if (s != null) {
                    return s;
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Старое API без предиката — оставляем, но делаем через очередь.
     */
    @Deprecated
    public String readLineOrNullNonBlocking() {
        return readLineOrNullNonBlockingUntil(() -> false);
    }

    /**
     * Корректно закрыть Scanner.
     */
    public void close() {
        try {
            if (ownsStream) {
                scanner.close();
                log.debug("Scanner closed");
            } else {
                log.debug("Scanner not closed (ownsStream=false)");
            }
        } catch (Exception e) {
            log.warn("Failed to close console scanner", e);
        }
    }
}
