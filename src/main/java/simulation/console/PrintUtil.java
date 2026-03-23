package simulation.console;

final public class PrintUtil {

    private PrintUtil() {
    }

    public static void mapPreviewMsg() {
        System.out.println("Карта при начальной расстановке: ");
    }

    public static void printHelp() {
        System.out.printf("""
                Управление [русская раскладка WASD]:
                  %s = пауза
                  %s = продолжить, пока не будет сделана пауза или выход
                  %s = сделать ровно один ход и ожидать команды (игнорирует бесконтрольный беспаузный режим) 
                  %s = выход
                """,
                ConsoleSymbols.PAUSE_BUTTON,
                ConsoleSymbols.RESUME_BUTTON,
                ConsoleSymbols.STEP_BUTTON,
                ConsoleSymbols.STOP_BUTTON);
    }

    public static void printCommandPrompt() {
        System.out.printf("Команда (%c=пауза, %c=продолжить, %c=ход, %c=выход)%n",
                ConsoleSymbols.PAUSE_BUTTON,
                ConsoleSymbols.RESUME_BUTTON,
                ConsoleSymbols.STEP_BUTTON,
                ConsoleSymbols.STOP_BUTTON
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
                ConsoleSymbols.YES_BUTTON,
                ConsoleSymbols.NO_BUTTON
        );
    }

    public static void printYesNoAtSimulationStart() {
        System.out.printf("Начать новую симуляцию? Введите '%c' для начала или '%c' для выхода.%n",
                ConsoleSymbols.YES_BUTTON,
                ConsoleSymbols.NO_BUTTON
        );
    }

    public static void printAskNumberOrEnter() {
        System.out.println("\"Нужно ввести число (enter/пробел = оставить среднюю карту по-умолчанию)\"");
    }

    public static void printOutOfRange(int min, int max) {
        System.out.printf("Число вне диапазона (%d..%d). Повторите ввод.%n", min, max);
    }

    public static void printSpecificCommand(SimulationCommand cmd) {
        String separator = System.lineSeparator();
        switch (cmd) {
            case STOP -> System.out.println("⏹ stop" + separator);
            case PAUSE -> System.out.println("⏸ pause" + separator);
            case RESUME -> System.out.println("▶ resume" + separator);
            case STEP -> System.out.println("⏭ step" + separator);
        }
    }

    public static void printMapInfo() {
        System.out.printf("""
            
            Выберите пресет карты (нужно ввести строго одно число):
              %d = маленькая 
              %d = средняя
              %d = большая
              Enter/пробел = средняя по умолчанию
              
            """,
                ConsoleSymbols.SMALL_PRESET_KEY,
                ConsoleSymbols.MEDIUM_PRESET_KEY,
                ConsoleSymbols.LARGE_PRESET_KEY);
    }

    public static void printGreetings() {
        System.out.println("""
                
                Добро пожаловать в программу симуляция!
                            
                Здесь вы сможете наблюдать за эмуляцией животного мира в режиме реального времени.
                
                Отображение - в консоле буквами:
                P - хищник, H - травоядное, g - трава, t - дерево, r - камень
                                
                Камни, деревья, трава - неподвижные объекты.
                Есть двое видов существ которые двигаются - это травоядные и хищники.
                
                Травоядные - питаются травой (она постепенно растет), хищники - травоядными и - 
                движутся по карте к своим целям по кратчайшему маршруту.
                                
                Чтобы избежать ситуации, когда травоядных будет мало и/или они съедят всю траву - трава растет, 
                а травоядные попрождаются.
                
                ***
                                                            
                В начале игры вы можете выбрать размер карты (количество объектов запрограммировано заранее).
                            
                Желаю нескучно провести время!
                                
                ****************************************************
                
                """);
    }

    public static void printBye() {
        System.out.println("""
               
                До свидания!
                
                """);
    }
}
