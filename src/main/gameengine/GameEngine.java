package main.gameengine;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.common.Camera;
import main.common.Input;
import main.common.OBJLoader;
import main.models.*;
import main.renderengine.WindowManager;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import static org.lwjgl.opengl.GL40.*;

/**
 *
 * @authors Humam Tlay and Lubna Alhalabi
 */
public class GameEngine {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static MainCharacter mainCharachter;
    private static final List<Model> models = new ArrayList<>();
    private static final List<Shot> shots = new ArrayList<>();
    private static final List<Fire> fires = new ArrayList<>();
    private static final List<Monstor> monstors = new ArrayList<>();
    private static Camera camera;
    public static Camera getCamera() {
        return camera;
    }
    private static final Random random = new Random();
    private static final Set<Float> generatedNumbers = new HashSet<>();
    private static int shouldClose = 0;
    public static float getRandom(float min, float max) {
        float randomNumber;
        do {
            randomNumber = random.nextFloat((max - min) + 1) + min;
        } while (generatedNumbers.contains(randomNumber));

        generatedNumbers.add(randomNumber);
        return randomNumber;
    }
    private static void generateModels() throws Exception{
        Model qube = new SkyBox();
        Terrain terrain = new Terrain("terrain", "heightmap.png");
        mainCharachter = new MainCharacter(OBJLoader.loadObjModel("plan.obj", "plan.png"));
        camera = new Camera(mainCharachter);

        models.add(qube);
        models.add(terrain);
        models.add(qube);
        models.add(mainCharachter);

        models.forEach((model) -> {
            try {
                model.load();
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    private static void generateMonistors() throws Exception{
        for (int i = 0; i < 8; i++) {
            monstors.add(new Monstor(OBJLoader.loadObjModel("monster.obj", "monster.jpg"),
                    0.01f,
                    new Vector3f(getRandom(-10000.0f, 10000.0f), getRandom(0, 1000.0f), getRandom(-10000.0f, 10000.0f)),
                    mainCharachter,
                    new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(1.0f, 1.0f, 1.0f)));
        }

        monstors.forEach((model) -> {
            try {
                model.load();
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    private static void generateFire(Vector3f position) throws Exception{
        for (int i = 0 ; i< 20; i++) {
            fires.add(new Fire(System.currentTimeMillis()));
        }

        fires.forEach((Fire) -> {
            try {
                Fire.load();
                Fire.increasePosition(position.x, position.y, position.z);
                Fire.increaseRotation(1.55f,0.0f,0.0f);
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    private static void processMonitors() throws Exception{
        AtomicBoolean isTooClose = new AtomicBoolean(false);

        if (monstors.size() == 0) {
            shouldClose = 2;
            return;
        }

        if (monstors.size() < 6) {
            Monstor monstor = new Monstor(OBJLoader.loadObjModel("monster.obj", "monster.jpg"),
                    0.02f,
                    new Vector3f(getRandom(-10000.0f, 10000.0f), getRandom(0, 1000.0f), getRandom(-10000.0f, 10000.0f)),
                    mainCharachter,
                    new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(1.0f, 1.0f, 1.0f));
            monstor.load();
            monstors.add(monstor);
        }

        monstors.forEach((monstor) -> {
            float distance = (monstor.getPosition().x - mainCharachter.getPosition().x) * (monstor.getPosition().x - mainCharachter.getPosition().x);
            distance += (monstor.getPosition().y - mainCharachter.getPosition().y) * (monstor.getPosition().y - mainCharachter.getPosition().y);
            distance += (monstor.getPosition().z - mainCharachter.getPosition().z) * (monstor.getPosition().z - mainCharachter.getPosition().z);

            if (distance < 40) {
                shouldClose = 1;
            }
            else if (distance < 500) {
                isTooClose.set(true);
            }
            monstor.render();
        });

        if(isTooClose.get()){
            try {
                runMonistorSound();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void processShots() throws Exception{
        if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_1) && shots.size() < 5) {
            Vector3f position1 = new Vector3f(camera.getPosition());
            Vector3f position2 = new Vector3f(mainCharachter.getPosition());
            Vector3f rotation = new Vector3f(mainCharachter.getRotation());
            rotation.x += 1.55f;
            Vector3f scale = new Vector3f(0.1f, 0.1f, 0.1f);
            Shot shot = new Shot(OBJLoader.loadObjModel("missile.obj", "missile.png"),
                    0.8f, position1, position2, rotation, scale);
            shots.add(shot);
            shot.load();
            runRocketSound();
        }

        Iterator<Shot> shotsIterator = shots.iterator();
        while (shotsIterator.hasNext()) {
            Shot shot = shotsIterator.next();
            if (shot.t > 500) {
                shot.distroy();
                shotsIterator.remove();
            }
            else if (shot.getPosition().y <= -3){
                shot.distroy();
                shotsIterator.remove();

                runExplosionSound();
                generateFire(shot.getPosition());
            }
            else {
                Iterator<Monstor> monstorsIterator = monstors.iterator();
                boolean isDeleted = false;
                while (monstorsIterator.hasNext()) {
                    Monstor monstor = monstorsIterator.next();
                    float distance = (monstor.getPosition().x - shot.getPosition().x) * (monstor.getPosition().x - shot.getPosition().x);
                    distance += (monstor.getPosition().y - shot.getPosition().y) * (monstor.getPosition().y - shot.getPosition().y);
                    distance += (monstor.getPosition().z - shot.getPosition().z) * (monstor.getPosition().z - shot.getPosition().z);

                    if (distance < 10) {
                        isDeleted = true;
                        monstor.distroy();
                        monstorsIterator.remove();
                    }
                }
                if (isDeleted) {
                    runExplosionSound();
                    generateFire(shot.getPosition());

                    shot.distroy();
                    shotsIterator.remove();
                } else
                    shot.render();
            }
        }
    }
    private static void processFire() throws Exception{
        Iterator<Fire> fireIterator = fires.iterator();

        while (fireIterator.hasNext()) {
            Fire fire = fireIterator.next();
            long currentTimeMillis = System.currentTimeMillis();

            if((currentTimeMillis - fire.startTime) > 5 * 1000){ // 5 seconds only
                fire.distroy();
                fireIterator.remove();
            }
            else{
                fire.increaseRotation(0.0f,getRandom(0.0f,0.000002f),getRandom(0.0f,0.000002f));
                fire.setScale(new Vector3f(getRandom(1.0f,10.0f),getRandom(1.0f,10.0f),getRandom(1.0f,10.0f)));

                fire.render();
            }
        }
    }
    private static void runPlanSound() throws Exception{
        Thread planeSoundthread = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                File file = new File("src/resources/sounds/AirplaneTaxiLong.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                while (true){
                    clip.start();
                    int minutes = 3;
                    int seconds = 39;
                    long durationInMillis = (minutes * 60 + seconds) * 1000;
                    Thread.sleep(durationInMillis);
                }
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        planeSoundthread.start();
    }
    private static void runRocketSound() throws Exception{
        Thread planeSoundthread = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                File file = new File("src/resources/sounds/AirplaneRocket.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                // Sleep for 1 second
                Thread.sleep(1500);

                // Stop the clip
                clip.stop();
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        planeSoundthread.start();
    }
    private static void runExplosionSound() throws Exception{
        Thread planeSoundthread = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                File file = new File("src/resources/sounds/BigExplosionCutOff.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        planeSoundthread.start();
    }
    private static boolean isMonistorSoundRunning = false;
    private static void runMonistorSound() throws Exception{
        if(isMonistorSoundRunning)
            return;

        Thread planeSoundthread = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                File file = new File("src/resources/sounds/PropPlaneFly.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                isMonistorSoundRunning = true;
                int minutes = 0;
                int seconds = 50;
                long durationInMillis = (minutes * 60 + seconds) * 1000;
                Thread.sleep(durationInMillis);
                clip.stop();
                isMonistorSoundRunning = false;
            } catch (Exception ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        planeSoundthread.start();
    }

    public static void main(String[] args) {
        try {
            WindowManager.createWindow();
            generateModels();
            generateMonistors();
            runPlanSound();
            while (!WindowManager.shouldClose() && shouldClose == 0) {
                glEnable(GL_DEPTH_TEST);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0, 0, 0, 1);
                camera.move();

                processShots();
                processMonitors();
                processFire();

                if(mainCharachter.getPosition().y <= -3.0) {
                    shouldClose = 1;
                }

                models.forEach((model) -> {
                    model.render();
                });

                WindowManager.updateWindow();
                Thread.sleep(1000 / 60);
            }

            models.forEach((model) -> {
                model.distroy();
            });
            shots.forEach((shot) -> {
                shot.distroy();
            });
            monstors.forEach((monstor) -> {
                monstor.distroy();
            });

            if (shouldClose == 1) {
                System.out.println("You Lost!!!");
                runExplosionSound();
            }
            else if (shouldClose == 2)
                System.out.println(ANSI_GREEN + "You Win!" + ANSI_RESET);
            WindowManager.closeWindow();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
