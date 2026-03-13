package v2.dialogue;

final public class PrintUtil {

    private PrintUtil() {
    }

    public static void mapPreviewMsg() {
        System.out.println("Карта при первой расстановке: ");
    }

    public static void printHelp() {
        System.out.printf("""
                
                Управление [русская раскладка WASD]:
                  %s = пауза
                  %s = продолжить, пока не будет сделана пауза или выход
                  %s = сделать ровно один ход и ожидать команды (игнорирует бесконтрольный беспаузный режим) 
                  %s = выход
                """,
                ConsoleConfig.PAUSE_BUTTON,
                ConsoleConfig.RESUME_BUTTON,
                ConsoleConfig.STEP_BUTTON,
                ConsoleConfig.STOP_BUTTON);
    }

    public static void printCommandPrompt() {
        System.out.printf("Команда (%c=пауза, %c=продолжить, %c=ход, %c=выход)%n",
                ConsoleConfig.PAUSE_BUTTON,
                ConsoleConfig.RESUME_BUTTON,
                ConsoleConfig.STEP_BUTTON,
                ConsoleConfig.STOP_BUTTON
        );
    }

    public static void printStatus(int moves) {
        System.out.printf("""
                ────═ Ход: %d ═────
                """, moves);
    }

    public static void printInvalidInput() {
        System.out.println("Некорректный ввод ->");
    }

    public static void printInvalidYesNoInput() {
        System.out.printf("Некорректный ввод. Пожалуйста, введите '%c' или '%c'.%n",
                ConsoleConfig.YES_BUTTON,
                ConsoleConfig.NO_BUTTON
        );
    }

    public static void printYesNoAtSimulStart() {
        System.out.printf("Начать новую симуляцию? Введите '%c' для начала или '%c' для выхода.%n",
                ConsoleConfig.YES_BUTTON,
                ConsoleConfig.NO_BUTTON
        );
    }

    public static void printAskNumberOrEnter() {
        System.out.println("\"Нужно ввести число (enter = оставить по-умолчанию)\"");
    }

    public static void printOutOfRange(int min, int max) {
        System.out.printf("Число вне диапазона (%d..%d). Повторите ввод.%n", min, max);
    }

    public static void printSpecificCommand(ChosenCommand cmd) {
        String separator = System.lineSeparator();
        switch (cmd) {
            case STOP -> System.out.println("⏹ stop" + separator);
            case PAUSE -> System.out.println("⏸ pause" + separator);
            case RESUME -> System.out.println("▶ resume" + separator);
            case STEP -> System.out.println("⏭ step" + separator);
        }
    }

    public static void printMapInfo() {
        System.out.println("""
                
                Выберите пресет карты (нужно ввести строго 1 число):
                  1 = маленькая (12*12)
                  2 = средняя  (20*20)
                  3 = большая  (30*30)
                  Enter/пробел = средняя по умолчанию (20*20)
                  
                """);
    }

    public static void greetings() {
        System.out.println("""
                
                Добро пожаловать в программу симуляция!
                            
                Здесь вы сможете наблюдать за эмуляцией (хотя и ограниченной) животного мира:
                демонстрационное окно откроется справа.
                                
                Помимо неподвижных объектов: камней, деревьев, есть двое видов существ - это травоядные и хищники.
                Травоядные - питаются травой (она постепенно растет), хищники - травоядными и - 
                движутся по карте к своим целям по кратчайшему маршртуру.
                                
                У всех животных постепенно уменьшается здоровье, но трава растет.
                                
                А в конце симуляции - вы можете увидеть статистику.
                            
                В начале игры вы можете выбрать режим и настройки
                - карты, количество движимых/недвижимых объектов на ней, паузы и ходов (см. ниже)
                            
                Желаю нескучно провести время!
                                
                ****************************************************
                
                """);
    }
}
