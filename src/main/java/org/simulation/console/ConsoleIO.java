package org.simulation.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;
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

    public static ConsoleIO autodetect() {
        Console console = System.console();
        Charset cs = (console != null && console.charset() != null)
                ? console.charset()
                : Charset.defaultCharset();
        return new ConsoleIO(System.in, cs, false);
    }

    /**
     * Блокирующее чтение следующей строки; null при EOF/ошибке.
     */
    public String readLineOrNull() {
        if (!scanner.hasNextLine()) {
            log.debug("Scanner has no next line (EOF)");
            return null;
        }
        try {
            String line = scanner.nextLine();
            log.trace("Read line: '{}'", line);
            return line;
        } catch (NoSuchElementException | IllegalStateException e) {
            log.warn("Scanner read failed", e);
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
     * Псевдо-неблокирующее чтение с опросом очереди.
     * Возвращает строку когда она появляется; возвращает null если:
     * - наступил EOF/ошибка ввода, или
     * - сработал предикат abort.getAsBoolean() (например, симуляция остановлена).
     */
    public String readLineOrNullNonBlockingUntil(BooleanSupplier abort) {
        ensureReaderStarted();
        try {
            while (true) {
                if (abort != null && abort.getAsBoolean()) {
                    return null;
                }
                if (eof) {
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
