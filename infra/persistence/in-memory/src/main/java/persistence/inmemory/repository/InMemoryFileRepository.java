package persistence.inmemory.repository;

import domain.filemanager.api.entity.Permission;
import persistence.inmemory.entity.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryFileRepository implements domain.filemanager.spi.FileRepository {

    private final Map<String, File> filesInMemory = new HashMap<>();

    public Map<String, File> getAllfiles() {
        return filesInMemory;
    }

    public Optional<File> findByName(String fileName) {
        return filesInMemory.values().stream()
            .filter(mockFile -> hasFileName(mockFile, fileName))
            .findFirst();
    }

    private boolean hasFileName(File file, String fileName) {
        return fileName.equals(file.getName());
    }

    @Override
    public List<domain.filemanager.api.entity.File> findFilesBySharedUser(String userId) {
        return filesInMemory.values()
                .stream()
                .filter(f -> isSharedToUser(f, userId))
                .collect(Collectors.toList());
    }

    private boolean isSharedToUser(domain.filemanager.api.entity.File file, String userId) {
        return file.getSharedUsersIdWithPermission()
                .keySet()
                .contains(userId);
    }


    @Override
    public File findFileById(String fileId) {
        return filesInMemory.get(fileId);
    }

    @Override
    public List<domain.filemanager.api.entity.File> findFilesByUserId(String ownerId) {
        return filesInMemory.values()
                .stream()
                .filter(file -> ownerId.equals(file.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public File addFile(String name, byte[] data, String ownerId) {
        File fileToSave = new File(autoGeneratedId(), name, data, ownerId);
        filesInMemory.put(fileToSave.getId(), fileToSave);
        return fileToSave;
    }

    @Override
    public void shareFile(String fileId, Map<String, Permission> usersIdToShareWithPermission) {
        File fileToAddShareUsers = filesInMemory.get(fileId);
        fileToAddShareUsers.setSharedUsersIdWithPermission(usersIdToShareWithPermission);
    }

    @Override
    public void deleteFile(String name) {
        filesInMemory.remove(name);
    }

    private String autoGeneratedId() {
        return String.valueOf(filesInMemory.size());
    }
}
