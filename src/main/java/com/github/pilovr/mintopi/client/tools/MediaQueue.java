package com.github.pilovr.mintopi.client.tools;

import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.MessageType;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentBuilder;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.util.MediaConverter;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MediaQueue {
    private final Set<Pair<AttachmentType, AttachmentType>> supportedConversions;
    private final Queue<MediaQueueObject> queue;
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

    public void addToQueue(MediaQueueObject mediaQueueObject) {
        if (isSupportedConversion(mediaQueueObject.originType(), mediaQueueObject.targetType())) {
            queue.add(mediaQueueObject);
            if (!isProcessing.get()) {
                startProcessing();
            }
        } else {
            throw new IllegalArgumentException("Unsupported conversion: " + mediaQueueObject.originType().toString() + " to " + mediaQueueObject.targetType().toString());
        }
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
            MediaQueueObject mediaQueueObject = queue.poll();
            if (mediaQueueObject == null) {
                // Queue is empty, wait before checking again
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            try {
                // Process this media object (actual conversion implementation)
                processMediaConversion(mediaQueueObject); //todo send this shit? Make sure client provides runnable?!?

                // Respect additionalTimeout before processing the next item
                if (mediaQueueObject.additionalTimeout() > 0) {
                    Thread.sleep(mediaQueueObject.additionalTimeout());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Log the error but continue processing the queue
                System.err.println("Error processing media conversion: " + e.getMessage());
            }
        }
        isProcessing.set(false);
    }

    private byte[] processMediaConversion(MediaQueueObject mediaQueueObject) {
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
        return queue.size();
    }
}