import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        GameProgress game1 = new GameProgress(94, 3, 25, 24.5);
        GameProgress game2 = new GameProgress(32, 8, 12, 13.7);
        GameProgress game3 = new GameProgress(61, 5, 38, 172.1);

        String path1 = "/Users/yuryshakhmatov/Games/savegames/save1.dat";
        String path2 = "/Users/yuryshakhmatov/Games/savegames/save2.dat";
        String path3 = "/Users/yuryshakhmatov/Games/savegames/save3.dat";

        String zipPath = "/Users/yuryshakhmatov/Games/savegames/zip.zip";

        String[] filesToZipPaths = {path1, path2, path3};

        saveGame(path1, game1);
        saveGame(path2, game2);
        saveGame(path3, game3);

        zipFiles(zipPath, filesToZipPaths);

        for (String fileToDelete : filesToZipPaths) {
            File file = new File(fileToDelete);
            file.delete();
        }

        openZip(zipPath, zipPath.substring(0, zipPath.lastIndexOf('/')));

        System.out.println(openProgress(path1));
        System.out.println(openProgress(path2));
        System.out.println(openProgress(path3));
    }

    public static void saveGame(String path, GameProgress game) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(game);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void zipFiles(String zipPath, String[] filesToZipPaths) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (String fileToZipPath : filesToZipPaths) {
                try (FileInputStream fis = new FileInputStream(fileToZipPath)) {
                    ZipEntry entry = new ZipEntry(fileToZipPath.substring(fileToZipPath.lastIndexOf('/') + 1));
                    zos.putNextEntry(entry);

                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);

                    zos.closeEntry();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void openZip(String zipPath, String unzipPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            String name;

            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fos = new FileOutputStream(unzipPath + "/" + name);

                for (int c = zis.read(); c != -1 ; c = zis.read()) {
                    fos.write(c);
                }

                fos.flush();
                zis.closeEntry();
                fos.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static GameProgress openProgress(String path) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return gameProgress;
    }
}