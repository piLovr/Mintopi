package com.github.pilovr.mintopi.client.tools;

import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.util.MediaConverter;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MediaQueue {
    private final Set<Pair<AttachmentType, AttachmentType>> supportedConversions;
    private final Queue<com.github.pilovr.mintopi.client.tools.MediaQueueObjectWithFuture> queue;
    private final AtomicBoolean isProcessing;
    private Thread processingThread;

    public MediaQueue() {
        this.supportedConversions = Set.of(
                Pair.with(AttachmentType.IMAGE, AttachmentType.STICKER),
                Pair.with(AttachmentType.STICKER, AttachmentType.IMAGE)
        );
        this.queue = new LinkedList<>();
        this.isProcessing = new AtomicBoolean(false);
    }

    public boolean isSupportedConversion(AttachmentType from, AttachmentType to) {
        return supportedConversions.contains(Pair.with(from, to));
    }

    public CompletableFuture<byte[]> addToQueue(ExtendedMessageEvent origin, AttachmentType target, int attachmentIndex, int timeout) {
        CompletableFuture<byte[]> resultFuture = new CompletableFuture<>();
        MediaQueueObjectWithFuture mediaQueueObject = new MediaQueueObjectWithFuture(target, origin, attachmentIndex, timeout, resultFuture);


        if (!isSupportedConversion(mediaQueueObject.originType(), mediaQueueObject.targetType())) {
            CompletableFuture<byte[]> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException(
                    "Unsupported conversion: " + mediaQueueObject.originType() + " to " + mediaQueueObject.targetType()));
            return future;
        }

        synchronized (queue) {
            queue.add(mediaQueueObject);
            if (!isProcessing.get()) {
                startProcessing();
            }
        }

        return resultFuture;
    }

    public synchronized void startProcessing() {
        if (isProcessing.compareAndSet(false, true)) {
            processingThread = new Thread(this::processQueue);
            processingThread.setDaemon(true);
            processingThread.start();
        }
    }

    private void processQueue() {
        while (isProcessing.get() && !Thread.currentThread().isInterrupted()) {
            MediaQueueObjectWithFuture mediaQueueObject;
            synchronized (queue) {
                mediaQueueObject = (MediaQueueObjectWithFuture) queue.poll();
            }

            if (mediaQueueObject == null) {
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            try {
                // Process this media object and complete the future
                byte[] convertedMedia = processMediaConversion(mediaQueueObject);
                mediaQueueObject.future().complete(convertedMedia);

                // Respect additionalTimeout before processing the next item
                if (mediaQueueObject.additionalTimeout() > 0) {
                    Thread.sleep(mediaQueueObject.additionalTimeout());
                }
            } catch (InterruptedException e) {
                mediaQueueObject.future().completeExceptionally(e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                mediaQueueObject.future().completeExceptionally(e);
                System.err.println("Error processing media conversion: " + e.getMessage());
            }
        }
        isProcessing.set(false);
    }

    private byte[] processMediaConversion(com.github.pilovr.mintopi.client.tools.MediaQueueObjectWithFuture mediaQueueObject) {
        byte[] origin = mediaQueueObject.messageEvent().downloadAttachments().get(mediaQueueObject.attachmentIndex());
        return MediaConverter.convert(origin, mediaQueueObject.originType(), mediaQueueObject.targetType());
    }

    public void stopProcessing() {
        if (processingThread != null) {
            processingThread.interrupt();
        }
        isProcessing.set(false);
    }

    public boolean isProcessing() {
        return isProcessing.get();
    }

    public int getQueueSize() {
        synchronized (queue) {
            return queue.size();
        }
    }
}