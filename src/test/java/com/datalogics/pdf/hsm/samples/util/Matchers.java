/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.hsm.samples.util;

import static org.hamcrest.CoreMatchers.equalTo;

import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.pdf.graphics.xobject.PDFXObjectImage;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Extended Hamcrest matchers for the sample tests.
 */
public class Matchers {
    private Matchers() {}

    /**
     * Check that a {@link PDFXObjectImage} has a particular SHA-1 checksum.
     *
     * @param checksum the image checksum to check for
     * @return a {@link Matcher}
     */
    public static Matcher<PDFXObjectImage> hasChecksum(final String checksum) {
        // see http://www.planetgeek.ch/2012/03/07/create-your-own-matcher/ for an explanation
        return new FeatureMatcher<PDFXObjectImage, String>(equalTo(checksum), "has checksum", "checksum") {
            private void throwChecksumError(final Throwable exc) {
                throw new IllegalStateException("Getting an image checksum threw " + exc, exc);
            }

            @Override
            protected String featureValueOf(final PDFXObjectImage image) {
                final PDFXObjectImage xObjectImage = image;
                try {
                    return Checksum.getSha1Checksum(xObjectImage.getImageStreamData());
                } catch (final PDFInvalidDocumentException e) {
                    throwChecksumError(e);
                } catch (final PDFIOException e) {
                    throwChecksumError(e);
                } catch (final PDFSecurityException e) {
                    throwChecksumError(e);
                } catch (final NoSuchAlgorithmException e) {
                    throwChecksumError(e);
                } catch (final IOException e) {
                    throwChecksumError(e);
                }
                return null;
            }
        };
    }

    /**
     * Check that a {@link BufferedImage} has a particular SHA-1 checksum.
     *
     * @param checksum the image checksum to check for
     * @return a {@link Matcher}
     */
    public static Matcher<BufferedImage> bufferedImageHasChecksum(final String checksum) {
        // see http://www.planetgeek.ch/2012/03/07/create-your-own-matcher/ for an explanation
        return new FeatureMatcher<BufferedImage, String>(equalTo(checksum), "has checksum", "checksum") {
            private void throwChecksumError(final Throwable exc) {
                throw new IllegalStateException("Getting an image checksum threw " + exc, exc);
            }

            @Override
            protected String featureValueOf(final BufferedImage image) {
                final DataBufferByte imageData = (DataBufferByte) image.getRaster().getDataBuffer();
                final InputStream imageStream = new ByteArrayInputStream(imageData.getData(0));
                try {
                    return Checksum.getSha1Checksum(imageStream);
                } catch (final NoSuchAlgorithmException e) {
                    throwChecksumError(e);
                } catch (final IOException e) {
                    throwChecksumError(e);
                }
                return null;
            }
        };
    }
}
