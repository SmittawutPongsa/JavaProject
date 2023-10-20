package Fix;

public class Main {

    public Main(String directory, String mapName){
        MainWindow window = new MainWindow(directory, mapName);
    }
    public static void main(String[] args) {
        MainWindow window = new MainWindow("C:\\Project HSRPG\\7-project", "map1");
    }
}
