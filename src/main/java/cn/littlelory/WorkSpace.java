package cn.littlelory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 01/09/2017.
 */
class WorkSpace {
    private final String basePath;
    WorkSpace(String basePath) {
        this.basePath = basePath;
    }

    byte[] search(String pathname) {
        String absolutePath = basePath + "/" + pathname;
        return FileUtil.exist(absolutePath) ? FileUtil.readBytes(absolutePath) : null;
    }

    List<FileEntry> list() {
        FileVisitor fileVisitor = new FileVisitor(basePath + "/.jit");
        try {
            Files.walkFileTree(Paths.get(basePath), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileVisitor.getResult().stream()
                .map(filePath -> {
                    String pathname = filePath.substring(basePath.length() + 1);
                    return new FileEntry(pathname, Fingerprint.generate(new JitObject(pathname, FileUtil.readBytes(filePath)).encode()));
                })
                .sorted()
                .collect(Collectors.toList());
    }

    Map<String, FileEntry> map() {
        return list().stream().collect(Collectors.toMap(FileEntry::getPathname, (fileEntry -> fileEntry)));
    }

    void flush(List<JitObject> objects) {
        for (JitObject object : objects) {
            String pathname = this.basePath + "/" + object.getPathname();
            String dirPath = pathname.substring(0, pathname.lastIndexOf('/'));
            byte[] data = object.getData();
            FileUtil.mkdirs(dirPath);
            FileUtil.writeBytes(pathname, data);
        }
    }

    private static final class FileVisitor implements java.nio.file.FileVisitor<Path> {
        private String skipPath;
        private Set<String> list;

        FileVisitor(String skipPath) {
            this.skipPath = skipPath;
            this.list = new HashSet<>();
        }

        Set<String> getResult() {
            return list;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String pathname = file.toUri().getPath();
//            System.out.println("visit:" + pathname);
            if (!pathname.startsWith(skipPath))
                list.add(pathname);
            else
                System.out.println("skip:" + pathname);
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
