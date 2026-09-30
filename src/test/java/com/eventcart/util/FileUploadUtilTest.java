package com.eventcart.util;

import com.eventcart.exception.ValidationException;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadUtilTest {

    @Test
    @DisplayName("Handles null or empty parts safely without throwing exceptions")
    void testNullPartReturnsNull() {
        assertNull(FileUploadUtil.uploadEventBanner(null, "somePath"));

        MockPart emptyPart = new MockPart("banner.jpg", "image/jpeg", 0, new byte[0]);
        assertNull(FileUploadUtil.uploadEventBanner(emptyPart, "somePath"));
    }

    @Test
    @DisplayName("Rejects oversized files beyond 5MB")
    void testRejectsOversizedFile() {
        long oversized = 6 * 1024 * 1024; // 6 MB
        MockPart oversizedPart = new MockPart("large.jpg", "image/jpeg", oversized, new byte[10]);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FileUploadUtil.uploadEventBanner(oversizedPart, "somePath"));
        assertTrue(ex.getMessage().contains("exceeds the maximum permitted size"));
    }

    @Test
    @DisplayName("Rejects disallowed MIME types and disallowed file extensions")
    void testRejectsDisallowedMimeAndExtension() {
        MockPart exePart = new MockPart("exploit.exe", "application/x-msdownload", 100, "sample".getBytes());
        assertThrows(ValidationException.class, () -> FileUploadUtil.uploadEventBanner(exePart, "somePath"));

        MockPart pdfPart = new MockPart("document.pdf", "application/pdf", 100, "sample".getBytes());
        assertThrows(ValidationException.class, () -> FileUploadUtil.uploadEventBanner(pdfPart, "somePath"));
    }

    static class MockPart implements Part {
        private final String fileName;
        private final String contentType;
        private final long size;
        private final byte[] content;

        MockPart(String fileName, String contentType, long size, byte[] content) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.size = size;
            this.content = content;
        }

        @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public String getContentType() { return contentType; }
        @Override public String getName() { return "bannerImage"; }
        @Override public String getSubmittedFileName() { return fileName; }
        @Override public long getSize() { return size; }
        @Override public void write(String s) {}
        @Override public void delete() {}
        @Override public String getHeader(String s) {
            if ("content-disposition".equalsIgnoreCase(s)) {
                return "form-data; name=\"bannerImage\"; filename=\"" + fileName + "\"";
            }
            return null;
        }
        @Override public Collection<String> getHeaders(String s) { return Collections.emptyList(); }
        @Override public Collection<String> getHeaderNames() { return Collections.emptyList(); }
    }
}
