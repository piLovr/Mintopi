package com.github.pilovr.mintopi.starter.util;

import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.message.ExtendedMessage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import it.auties.qr.QrTerminal;
import it.auties.whatsapp.api.WhatsappVerificationHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class QrHandler implements WhatsappVerificationHandler.Web.QrCode {

    public static QrHandler toRemoteAction(Client sock, ExtendedMessage extendedMessage) {
        return new QrHandler() {
            @Override
            public void handle(String qr) {
                try {
                    BitMatrix matrix = QrCode.createMatrix(qr, 500, 5);
                    Path tempFile = Files.createTempFile("qr", ".png");
                    MatrixToImageWriter.writeToPath(matrix, "PNG", tempFile);

                    // You need to implement sending an image with your MessageBuilder
                    // Example: sock.sendMessage(message.getChatId(), MessageBuilder.withImage(tempFile.toFile()));
                    //message.reply((ExtendedMessage) null); //TODO Build qr Message

                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to create or send QR image", e);
                }
            }
        };
    }
    public static QrHandler toTerminal() {
        return new QrHandler() {
            @Override
            public void handle(String qr) {
                //it.auties.whatsapp.api.QrHandler.toTerminal().accept(qr);
                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix matrix = null;
                try {
                    matrix = writer.encode(qr, BarcodeFormat.QR_CODE, 10, 10, Map.of(EncodeHintType.MARGIN, 0, EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L));
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(QrTerminal.toString(matrix, true));
            }
        };
    }

    @Override
    public void handle(String s) {

    }
}
