package com.serenia.platform.socialcompanionship.infrastructure.storage.azure.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Stores the files in a private Azure Blob Storage container, under a random key.
 *
 * <p>The container is created without public access, so the files can only be obtained
 * through the API, after the requester's access has been checked.</p>
 */
@Service
@ConditionalOnProperty(prefix = "social-companionship.media", name = "storage-provider", havingValue = "azure")
public class AzureBlobMediaStorageService implements MediaStorageService {

    private final BlobContainerClient containerClient;

    public AzureBlobMediaStorageService(
            @Value("${social-companionship.media.azure.connection-string}") String connectionString,
            @Value("${social-companionship.media.azure.container}") String containerName) {
        this.containerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient(containerName);
        // Without an access type the container is private
        this.containerClient.createIfNotExists();
    }

    @Override
    public MediaKey store(MediaFile mediaFile) {
        var key = new MediaKey(UUID.randomUUID() + "." + mediaFile.extension());
        var options = new BlobParallelUploadOptions(BinaryData.fromBytes(mediaFile.content()))
                .setHeaders(new BlobHttpHeaders().setContentType(mediaFile.contentType()));
        containerClient.getBlobClient(key.value()).uploadWithResponse(options, null, null);
        return key;
    }

    @Override
    public Optional<MediaFile> load(MediaKey mediaKey) {
        var blobClient = containerClient.getBlobClient(mediaKey.value());
        if (!blobClient.exists()) return Optional.empty();
        var contentType = MediaFile.contentTypeOf(mediaKey).orElse("application/octet-stream");
        return Optional.of(new MediaFile(blobClient.downloadContent().toBytes(), contentType));
    }

    @Override
    public void delete(MediaKey mediaKey) {
        containerClient.getBlobClient(mediaKey.value()).deleteIfExists();
    }
}
