package com.example.ocr;

public class OCRResult {
    public String stringIndexType;
    public String content;
    public String modelVersion;

    public String getStringIndexType() {
        return stringIndexType;
    }

    public void setStringIndexType(String stringIndexType) {
        this.stringIndexType = stringIndexType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}