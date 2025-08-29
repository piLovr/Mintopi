package com.github.pilovr.mintopi.tools;

import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.util.MediaConverter;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MediaQueue {
    private final Queue<Triplet<Attachment, String, FluxSink<MediaConversionEvent>>> queue;
    private final AtomicBoolean isProcessing;
    private Thread processingThread;

    public MediaQueue() {
        this.queue = new LinkedList<>();
        this.isProcessing = new AtomicBoolean(false);
    }

    public Flux<MediaConversionEvent> addToQueue(Attachment attachment, String targetMimeType) {
        if (!MediaConverter.isSupportedConversion(attachment.getMimeType(), targetMimeType)) {
            return Flux.create(emitter -> {
                emitter.next(new MediaConversionEvent(MediaConversionEvent.EventType.CONVERSION_FAILED,getQueueSize(),null));
                emitter.complete();
            });
        }

        Flux<MediaConversionEvent> flux = Flux.create(emitter -> {
            queue.add(Triplet.with(attachment, targetMimeType, emitter));
            emitter.next(new MediaConversionEvent(MediaConversionEvent.EventType.CONVERSION_STARTED,getQueueSize(),null));
        });

        synchronized (queue) {
            if (!isProcessing.get()) {
                startProcessing();
            }
        }
        return flux;
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
            Triplet<Attachment, String, FluxSink<MediaConversionEvent>> element;
            synchronized (queue) {
                element = queue.poll();
                updatePositions();
            }

            if (element == null) {
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
                element.getValue2().next(new MediaConversionEvent(MediaConversionEvent.EventType.CONVERSION_STARTED,0,null));
                byte[] convertedMedia = processMediaConversion(element);
                element.getValue2().next(new MediaConversionEvent(MediaConversionEvent.EventType.CONVERSION_SUCCEEDED, 0, convertedMedia));
            } catch (Exception e) {
                element.getValue2().next(new MediaConversionEvent(MediaConversionEvent.EventType.CONVERSION_FAILED, 0, null));
            }
        }
        isProcessing.set(false);
    }

    private byte[] processMediaConversion(Triplet<Attachment, String, FluxSink<MediaConversionEvent>> element) {
        return MediaConverter.convert(element.getValue0().getData(), element.getValue0().getMimeType(), element.getValue1());
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

    private void updatePositions(){
        int pos = getQueueSize();
        for(Triplet<Attachment, String, FluxSink<MediaConversionEvent>> item : queue){
            item.getValue2().next(new MediaConversionEvent(MediaConversionEvent.EventType.POS_UPDATED, pos, null));
            pos--;
        }
    }
}