package cn.littlelory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 01/09/2017.
 */
class WorkSpace {
    private final String basePath;

    WorkSpace(String basePath) {
        this.basePath = basePath;
    }

    List<FileEntry> list() {
        FileVisitor fileVisitor = new FileVisitor();
        try {
            Files.walkFileTree(Paths.get(basePath), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileVisitor.getResult().stream()
                .map(filePath -> new FileEntry(filePath.substring(basePath.length() + 1), Fingerprint.generate(FileUtil.readBytes(filePath))))
                .sorted()
                .collect(Collectors.toList());
    }

    Map<String, FileEntry> map() {
        return list().stream().collect(Collectors.toMap(FileEntry::getPathname, (fileEntry -> fileEntry)));
    }

    private static final class FileVisitor implements java.nio.file.FileVisitor<Path> {
        private List<String> list;

        FileVisitor() {
            this.list = new ArrayList<>();
        }

        List<String> getResult() {
            return list;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            list.add(file.toUri().getPath());
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return null;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
