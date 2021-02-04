package lk.utils.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManager {

    private final Charset UTF8 = StandardCharsets.UTF_8;

    //----------------------------String----------------------------
    public String readString(String path) throws IOException {
        File file = existAndCreate(path);
        return FileUtils.readFileToString(file, UTF8);
    }

    public void writeString(String path, String write) throws IOException {
        File file = existAndCreate(path);
        FileUtils.write(file, write, UTF8);
    }

    public String addString(String path, String write) throws IOException {
        String text = readString(path) + write;
        writeString(path, text);
        return text;
    }

    public String removeString(String path, String remove) throws IOException {
        String text = readString(path).replace(remove, "");
        writeString(path, text);
        return text;
    }

    public boolean containsString(String path, String contains) throws IOException {
        String text = readString(path);
        return text.contains(contains);
    }

    //----------------------------Lines----------------------------
    public List<String> readLines(String path) throws IOException {
        File file = existAndCreate(path);
        return FileUtils.readLines(file, UTF8);
    }

    public void writeLines(String path, List<String> lines) throws IOException {
        File file = existAndCreate(path);
        FileUtils.writeLines(file, lines);
    }

    public List<String> addLine(String path, String line) throws IOException {
        List<String> lines = readLines(path);
        lines.add(line);
        writeLines(path, lines);
        return lines;
    }

    public List<String> addLines(String path, List<String> addLines) throws IOException {
        List<String> lines = readLines(path);
        lines.addAll(addLines);
        writeLines(path, lines);
        return lines;
    }

    public List<String> removeLine(String path, String line) throws IOException {
        List<String> lines = readLines(path);
        lines.remove(line);
        writeLines(path, lines);
        return lines;
    }

    public List<String> removeLines(String path, List<String> removeLines) throws IOException {
        List<String> lines = readLines(path);
        lines.removeAll(removeLines);
        writeLines(path, lines);
        return lines;
    }

    public boolean containsLine(String path, String line) throws IOException {
        List<String> lines = readLines(path);
        return lines.contains(line);
    }

    public boolean containsLines(String path, List<String> containsLines) throws IOException {
        List<String> lines = readLines(path);
        return lines.containsAll(containsLines);
    }

    //----------------------------File--------------------------------
    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public FileTime getFileTime(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime();
    }

    public boolean isEmpty(String path) {
        File file = new File(path);
        return file.length() == 0;
    }

    //----------------------------Directory----------------------------
    public File clearDirectory(String directory) throws IOException {
        File file = createDirectory(directory);
        FileUtils.cleanDirectory(file);
        return file;
    }

    public File deleteDirectory(String path) throws IOException {
        File dir = getNormalizeFile(path);
        FileUtils.deleteDirectory(dir);
        return dir;
    }

    public File createDirectory(String path) throws IOException {
        File dir = getNormalizeFile(path);
        FileUtils.forceMkdir(dir);
        return dir;
    }

    public List<Path> getFilesNameFromDirectory(String path) throws IOException {
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .collect(Collectors.toList());
    }

    public void copyDirectory(String fromPath, String toPath) throws IOException {
        File fromFile = createDirectory(fromPath);
        File toFile = createDirectory(toPath);
        FileUtils.copyDirectory(fromFile, toFile);
    }

    //----------------------------Zip----------------------------
    public void writeDirectoryToZip(String dirPath, String zipPath) throws IOException {
        File zipFile = existAndCreate(zipPath);
        existAndCreate(dirPath);
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos, UTF8);
        doWriteDirectoryToZip(dirPath, zos);
        zos.close();
    }

    private void doWriteDirectoryToZip(String path, ZipOutputStream zos) throws IOException {
        File[] files = getNormalizeFile(path).listFiles();
        List<File> fileList = (files == null) ? new ArrayList<>() : Arrays.asList(files);
        for (File file : fileList) {
            String filePathWithName = path + File.separator + file.getName();
            if (file.isDirectory()) {
                doWriteDirectoryToZip(filePathWithName, zos);
            } else {
                zos.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream in = new FileInputStream(filePathWithName);
                IOUtils.copy(in, zos);
                in.close();
            }
        }
    }

    //----------------------------Utils----------------------------
    public File getNormalizeFile(String path) {
        String normalized = FilenameUtils.normalize(path);
        return new File(normalized);
    }

    public File existAndCreate(String path) throws IOException {
        File file = getNormalizeFile(path);
        FileUtils.touch(file);
        return file;
    }

    public boolean isExist(String path) {
        File file = getNormalizeFile(path);
        return file.exists();
    }

}
